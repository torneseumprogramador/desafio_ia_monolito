"""
Testes unitários para middleware
"""
import pytest
from app.middleware.auth_middleware import login_required, guest_only
from app.models.user import db
from tests.factories.user_factory import UserFactory


class TestAuthMiddleware:
    """Testes para middleware de autenticação"""
    
    def test_login_required_decorator(self, client, app):
        """Testa decorator login_required"""
        with app.app_context():
            # Testar acesso sem autenticação
            response = client.get('/dashboard')
            # Deve redirecionar
            assert response.status_code in [302, 308]
    
    def test_guest_only_decorator(self, client, app):
        """Testa decorator guest_only"""
        with app.app_context():
            # Testar acesso sem autenticação (deve permitir)
            response = client.get('/auth/login')
            assert response.status_code == 200
            
            # Testar acesso com autenticação
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            })
            
            # Tentar acessar página de login (deve redirecionar)
            response = client.get('/auth/login')
            assert response.status_code in [302, 308]
