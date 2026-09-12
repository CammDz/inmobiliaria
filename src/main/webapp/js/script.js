// ============================================================
// InmoVaIn Soluciones - JavaScript global
// ============================================================

(function () {
    'use strict';

    // Autocierre de alertas de mensajes flash después de 6 segundos.
    document.addEventListener('DOMContentLoaded', function () {
        setTimeout(function () {
            var alertas = document.querySelectorAll('.alert-dismissible');
            alertas.forEach(function (a) {
                var bs = new bootstrap.Alert(a);
                bs.close();
            });
        }, 6000);
    });

    // Confirmación genérica en botones con data-confirm.
    document.addEventListener('click', function (e) {
        var btn = e.target.closest('[data-confirm]');
        if (btn && !window.confirm(btn.getAttribute('data-confirm'))) {
            e.preventDefault();
        }
    });

    // Mostrar/ocultar contraseña.
    window.togglePassword = function (nombreId) {
        var campo = document.getElementById(nombreId);
        if (campo) {
            campo.type = campo.type === 'password' ? 'text' : 'password';
        }
    };
})();