// Aplicação Java Spring Boot - Scripts Principais

document.addEventListener('DOMContentLoaded', function() {
    console.log('Aplicação Java Spring Boot carregada com sucesso! 🚀');
    
    // Adiciona classe active ao link da página atual
    highlightActiveLink();
    
    // Adiciona efeitos de scroll suaves
    enableSmoothScroll();
    
    // Log de informações da aplicação
    logAppInfo();
});

/**
 * Destaca o link ativo na navegação
 */
function highlightActiveLink() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-links a');
    
    navLinks.forEach(link => {
        const linkPath = new URL(link.href).pathname;
        if (linkPath === currentPath) {
            link.style.color = 'var(--primary-color)';
            link.style.backgroundColor = 'var(--background)';
        }
    });
}

/**
 * Habilita scroll suave para âncoras
 */
function enableSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

/**
 * Loga informações da aplicação no console
 */
function logAppInfo() {
    console.log('%c Monolito Java Spring Boot ', 'background: #2563eb; color: white; font-size: 16px; padding: 8px;');
    console.log('%c Tecnologias: ', 'font-weight: bold');
    console.log('- Java 17');
    console.log('- Spring Boot 3.0');
    console.log('- Thymeleaf Templates');
    console.log('- Maven');
}

/**
 * Utilitário para fazer requisições AJAX (exemplo)
 */
async function fetchData(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Erro ao buscar dados:', error);
        throw error;
    }
}

// Exporta funções para uso global
window.appUtils = {
    fetchData,
    highlightActiveLink,
    enableSmoothScroll
};

