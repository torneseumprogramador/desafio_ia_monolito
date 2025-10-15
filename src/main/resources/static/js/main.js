// JavaScript principal da aplicação

document.addEventListener('DOMContentLoaded', function() {
    console.log('Aplicação Spring Boot carregada com sucesso!');
    
    // Adicionar classe active no link da página atual
    const currentLocation = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-links a');
    
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentLocation) {
            link.style.color = 'var(--primary-color)';
        }
    });
});

// Função de exemplo para futuras funcionalidades
function showMessage(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
}
