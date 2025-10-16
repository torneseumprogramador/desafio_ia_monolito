/**
 * Máscaras de Formatação
 * Funções para aplicar máscaras em campos de formulário
 */

/**
 * Máscara de Telefone Brasileiro
 * Formatos: (00) 0000-0000 ou (00) 00000-0000
 */
const PhoneMask = {
    /**
     * Inicializa a máscara no campo de telefone
     * @param {string} fieldId - ID do campo input
     */
    init: function(fieldId) {
        const phoneInput = document.getElementById(fieldId);
        
        if (!phoneInput) {
            console.warn(`Campo ${fieldId} não encontrado`);
            return;
        }

        // Aplicar máscara ao carregar a página (se houver valor)
        if (phoneInput.value) {
            phoneInput.value = this.format(phoneInput.value);
        }

        // Aplicar máscara enquanto digita
        phoneInput.addEventListener('input', (e) => {
            e.target.value = this.format(e.target.value);
        });

        // Prevenir entrada de caracteres não numéricos
        phoneInput.addEventListener('keypress', (e) => {
            const char = String.fromCharCode(e.which);
            if (!/[0-9]/.test(char)) {
                e.preventDefault();
            }
        });
    },

    /**
     * Formata o valor do telefone
     * @param {string} value - Valor a ser formatado
     * @returns {string} Valor formatado
     */
    format: function(value) {
        // Remove tudo que não é número
        let numbers = value.replace(/\D/g, '');
        
        // Limita a 11 dígitos (2 DDD + 9 número)
        numbers = numbers.substring(0, 11);
        
        // Aplica a máscara conforme o tamanho
        if (numbers.length === 0) {
            return '';
        } else if (numbers.length <= 2) {
            // (00
            return `(${numbers}`;
        } else if (numbers.length <= 6) {
            // (00) 0000
            return `(${numbers.substring(0, 2)}) ${numbers.substring(2)}`;
        } else if (numbers.length <= 10) {
            // (00) 0000-0000 (telefone fixo)
            return `(${numbers.substring(0, 2)}) ${numbers.substring(2, 6)}-${numbers.substring(6)}`;
        } else {
            // (00) 00000-0000 (celular)
            return `(${numbers.substring(0, 2)}) ${numbers.substring(2, 7)}-${numbers.substring(7, 11)}`;
        }
    },

    /**
     * Remove a formatação do telefone
     * @param {string} value - Valor formatado
     * @returns {string} Apenas números
     */
    unmask: function(value) {
        return value.replace(/\D/g, '');
    }
};

/**
 * Máscara de CPF
 * Formato: 000.000.000-00
 */
const CPFMask = {
    init: function(fieldId) {
        const cpfInput = document.getElementById(fieldId);
        
        if (!cpfInput) {
            console.warn(`Campo ${fieldId} não encontrado`);
            return;
        }

        if (cpfInput.value) {
            cpfInput.value = this.format(cpfInput.value);
        }

        cpfInput.addEventListener('input', (e) => {
            e.target.value = this.format(e.target.value);
        });

        cpfInput.addEventListener('keypress', (e) => {
            const char = String.fromCharCode(e.which);
            if (!/[0-9]/.test(char)) {
                e.preventDefault();
            }
        });
    },

    format: function(value) {
        let numbers = value.replace(/\D/g, '');
        numbers = numbers.substring(0, 11);
        
        if (numbers.length === 0) {
            return '';
        } else if (numbers.length <= 3) {
            return numbers;
        } else if (numbers.length <= 6) {
            return `${numbers.substring(0, 3)}.${numbers.substring(3)}`;
        } else if (numbers.length <= 9) {
            return `${numbers.substring(0, 3)}.${numbers.substring(3, 6)}.${numbers.substring(6)}`;
        } else {
            return `${numbers.substring(0, 3)}.${numbers.substring(3, 6)}.${numbers.substring(6, 9)}-${numbers.substring(9)}`;
        }
    },

    unmask: function(value) {
        return value.replace(/\D/g, '');
    }
};

/**
 * Máscara de CEP
 * Formato: 00000-000
 */
const CEPMask = {
    init: function(fieldId) {
        const cepInput = document.getElementById(fieldId);
        
        if (!cepInput) {
            console.warn(`Campo ${fieldId} não encontrado`);
            return;
        }

        if (cepInput.value) {
            cepInput.value = this.format(cepInput.value);
        }

        cepInput.addEventListener('input', (e) => {
            e.target.value = this.format(e.target.value);
        });

        cepInput.addEventListener('keypress', (e) => {
            const char = String.fromCharCode(e.which);
            if (!/[0-9]/.test(char)) {
                e.preventDefault();
            }
        });
    },

    format: function(value) {
        let numbers = value.replace(/\D/g, '');
        numbers = numbers.substring(0, 8);
        
        if (numbers.length === 0) {
            return '';
        } else if (numbers.length <= 5) {
            return numbers;
        } else {
            return `${numbers.substring(0, 5)}-${numbers.substring(5)}`;
        }
    },

    unmask: function(value) {
        return value.replace(/\D/g, '');
    }
};

// Exportar para uso global
window.PhoneMask = PhoneMask;
window.CPFMask = CPFMask;
window.CEPMask = CEPMask;

