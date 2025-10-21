/**
 * Inicialização do Formulário de Usuário
 * Script específico para os formulários de criação e edição de usuários
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('Inicializando formulário de usuário...');
    
    // Inicializar máscara de telefone
    if (document.getElementById('phone')) {
        PhoneMask.init('phone');
        console.log('✓ Máscara de telefone inicializada');
    }

    // Inicializar toggle de senha
    if (document.getElementById('password') && document.getElementById('togglePassword')) {
        PasswordToggle.init('password', 'togglePassword');
        console.log('✓ Toggle de senha inicializado');
    }

    // Inicializar validação de formulários
    FormValidation.init();
    console.log('✓ Validação de formulários inicializada');

    // Validação customizada para username
    const usernameField = document.getElementById('username');
    if (usernameField) {
        usernameField.addEventListener('blur', function() {
            const value = this.value.trim();
            
            // Validar mínimo de 3 caracteres
            if (value.length > 0 && value.length < 3) {
                FormValidation.setCustomError('username', 'Username deve ter no mínimo 3 caracteres');
            } else {
                FormValidation.clearCustomError('username');
            }
        });
    }

    // Validação customizada para senha
    const passwordField = document.getElementById('password');
    if (passwordField) {
        passwordField.addEventListener('blur', function() {
            const value = this.value;
            
            // Se o campo é obrigatório e está vazio
            if (this.required && value.length === 0) {
                FormValidation.setCustomError('password', 'Senha é obrigatória');
            }
            // Se tem conteúdo mas é muito curto
            else if (value.length > 0 && value.length < 6) {
                FormValidation.setCustomError('password', 'Senha deve ter no mínimo 6 caracteres');
            } else {
                FormValidation.clearCustomError('password');
            }
        });
    }

    // Auto-focus no primeiro campo
    const firstField = document.querySelector('input[autofocus]');
    if (firstField) {
        firstField.focus();
    }

    console.log('Formulário de usuário pronto!');
});

