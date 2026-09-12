package com.inmobiliaria.controller;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.PerfilDAO;
import com.inmobiliaria.dao.RolDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.model.Perfil;
import com.inmobiliaria.model.Usuario;
import com.inmobiliaria.util.AuthUtil;
import com.inmobiliaria.util.PasswordUtil;
import com.inmobiliaria.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String contrasena = request.getParameter("contrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        // Validaciones del servidor
        if (ValidationUtil.estaVacio(nombre) || ValidationUtil.estaVacio(apellido)
                || ValidationUtil.estaVacio(correo) || ValidationUtil.estaVacio(contrasena)) {
            request.setAttribute("error", "Todos los campos obligatorios deben ser diligenciados.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        if (!ValidationUtil.esCorreoValido(correo)) {
            request.setAttribute("error", "El formato del correo no es válido.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        if (!ValidationUtil.esTelefonoValido(telefono)) {
            request.setAttribute("error", "El formato del teléfono no es válido.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        if (!ValidationUtil.esContrasenaValida(contrasena)) {
            request.setAttribute("error", "La contraseña debe tener al menos 4 caracteres.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        if (usuarioDAO.existeCorreo(correo)) {
            request.setAttribute("error", "No fue posible registrar el usuario porque el correo ya está registrado.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono != null ? telefono.trim() : "");
        usuario.setContrasena(PasswordUtil.hashPassword(contrasena));
        usuario.setActivo(true);

        try {
            int idUsuario = usuarioDAO.insertar(usuario);
            if (idUsuario == -1) {
                request.setAttribute("error", "No fue posible registrar el usuario porque el correo ya está registrado.");
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
                return;
            }

            // Asignar rol Cliente por defecto
            com.inmobiliaria.model.Rol rolCliente = rolDAO.buscarPorNombre("Cliente");
            if (rolCliente != null) {
                rolDAO.asignarRol(idUsuario, rolCliente.getIdRol());
            }

            // Crear perfil (relación 1:1)
            Perfil perfil = new Perfil();
            perfil.setIdUsuario(idUsuario);
            perfil.setDireccion("");
            perfil.setDocumentoIdentidad("");
            perfilDAO.crear(perfil);

            auditoriaDAO.registrar(idUsuario, "CREACIÓN DE USUARIO",
                    "usuario", idUsuario, "Registro realizado por el propio usuario", AuthUtil.getIp(request));

            // Autenticar automáticamente. Protección anti-fijación de sesión:
            // se descarta la sesión previa (posiblemente fijada) antes de
            // crear la sesión autenticada.
            HttpSession sesionPrevia = request.getSession(false);
            if (sesionPrevia != null) {
                sesionPrevia.invalidate();
            }
            java.util.Set<String> roles = rolDAO.obtenerRolesDeUsuario(idUsuario);
            HttpSession sesion = request.getSession(true);
            AuthUtil.guardarSesion(sesion, idUsuario,
                    usuario.getNombreCompleto(), usuario.getCorreo(), roles);

            AuthUtil.setMensajeExito(sesion,
                    "¡Registro exitoso! Bienvenido a la inmobiliaria.");
            response.sendRedirect(request.getContextPath() + "/cliente/dashboard.jsp");
        } catch (Exception e) {
            System.err.println("Error en RegistroServlet: " + e.getMessage());
            request.setAttribute("error",
                    "Ocurrió un error al registrar el usuario. Intente nuevamente.");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        }
    }
}