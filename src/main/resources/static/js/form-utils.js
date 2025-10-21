/**
 * Utilitários para Formulários
 * Funções auxiliares para melhorar a experiência do usuário em formulários
 */

/**
 * Toggle de Visibilidade de Senha
 */
const PasswordToggle = {
    /**
     * Inicializa o toggle de senha
     * @param {string} passwordFieldId - ID do campo de senha
     * @param {string} toggleButtonId - ID do botão de toggle
     */
    init: function(passwordFieldId, toggleButtonId) {
        const passwordField = document.getElementById(passwordFieldId);
        const toggleButton = document.getElementById(toggleButtonId);
        
        if (!passwordField || !toggleButton) {
            console.warn('Campo de senha ou botão de toggle não encontrado');
            return;
        }

        toggleButton.addEventListener('click', () => {
            this.toggle(passwordFieldId, toggleButtonId);
        });
    },

    /**
     * Alterna a visibilidade da senha
     * @param {string} passwordFieldId - ID do campo de senha
     * @param {string} toggleButtonId - ID do botão de toggle
     */
    toggle: function(passwordFieldId, toggleButtonId) {
        const passwordField = document.getElementById(passwordFieldId);
        const iconElement = document.querySelector(`#${toggleButtonId} i`);
        
        if (!passwordField || !iconElement) return;

        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            iconElement.classList.remove('fa-eye');
            iconElement.classList.add('fa-eye-slash');
        } else {
            passwordField.type = 'password';
            iconElement.classList.remove('fa-eye-slash');
            iconElement.classList.add('fa-eye');
        }
    }
};

/**
 * Validação de Formulários
 */
const FormValidation = {
    /**
     * Inicializa a validação Bootstrap em todos os formulários
     */
    init: function() {
        const forms = document.querySelectorAll('form[novalidate]');
        
        Array.from(forms).forEach((form) => {
            form.addEventListener('submit', (event) => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    },

    /**
     * Remove a classe de validação de um formulário
     * @param {string} formId - ID do formulário
     */
    reset: function(formId) {
        const form = document.getElementById(formId);
        if (form) {
            form.classList.remove('was-validated');
        }
    },

    /**
     * Valida um campo específico
     * @param {string} fieldId - ID do campo
     * @returns {boolean} True se válido
     */
    validateField: function(fieldId) {
        const field = document.getElementById(fieldId);
        return field ? field.checkValidity() : false;
    },

    /**
     * Adiciona mensagem de erro customizada
     * @param {string} fieldId - ID do campo
     * @param {string} message - Mensagem de erro
     */
    setCustomError: function(fieldId, message) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.setCustomValidity(message);
            field.reportValidity();
        }
    },

    /**
     * Remove mensagem de erro customizada
     * @param {string} fieldId - ID do campo
     */
    clearCustomError: function(fieldId) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.setCustomValidity('');
        }
    }
};

/**
 * Confirmação de Ações
 */
const ConfirmAction = {
    /**
     * Adiciona confirmação antes de submit em forms de delete
     * @param {string} selector - Seletor CSS dos formulários
     * @param {string} message - Mensagem de confirmação
     */
    init: function(selector = 'form[data-confirm]', message = 'Tem certeza?') {
        const forms = document.querySelectorAll(selector);
        
        forms.forEach((form) => {
            form.addEventListener('submit', (event) => {
                const customMessage = form.dataset.confirm || message;
                if (!confirm(customMessage)) {
                    event.preventDefault();
                }
            });
        });
    }
};

/**
 * Auto-save de Formulário
 */
const AutoSave = {
    /**
     * Salva dados do formulário no localStorage
     * @param {string} formId - ID do formulário
     * @param {string} storageKey - Chave no localStorage
     */
    save: function(formId, storageKey) {
        const form = document.getElementById(formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = {};
        
        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }
        
        localStorage.setItem(storageKey, JSON.stringify(data));
    },

    /**
     * Restaura dados do formulário do localStorage
     * @param {string} formId - ID do formulário
     * @param {string} storageKey - Chave no localStorage
     */
    restore: function(formId, storageKey) {
        const form = document.getElementById(formId);
        if (!form) return;

        const savedData = localStorage.getItem(storageKey);
        if (!savedData) return;

        try {
            const data = JSON.parse(savedData);
            
            for (let [key, value] of Object.entries(data)) {
                const field = form.elements[key];
                if (field) {
                    if (field.type === 'checkbox' || field.type === 'radio') {
                        field.checked = value === 'on' || value === true;
                    } else {
                        field.value = value;
                    }
                }
            }
        } catch (e) {
            console.error('Erro ao restaurar dados do formulário:', e);
        }
    },

    /**
     * Limpa dados salvos do localStorage
     * @param {string} storageKey - Chave no localStorage
     */
    clear: function(storageKey) {
        localStorage.removeItem(storageKey);
    }
};

// Exportar para uso global
window.PasswordToggle = PasswordToggle;
window.FormValidation = FormValidation;
window.ConfirmAction = ConfirmAction;
window.AutoSave = AutoSave;

