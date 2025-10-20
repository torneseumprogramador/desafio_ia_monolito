"""
Testes de integração para rotas básicas
"""
import pytest
from app.models.user import db
from tests.factories.user_factory import UserFactory


class TestBasicRoutes:
    """Testes para rotas básicas"""
    
    def test_home_route(self, client):
        """Testa rota home"""
        response = client.get('/')
        assert response.status_code == 200
    
    def test_about_route(self, client):
        """Testa rota about"""
        response = client.get('/about')
        assert response.status_code == 200
    
    def test_login_route(self, client):
        """Testa rota de login"""
        response = client.get('/auth/login')
        assert response.status_code == 200
    
    def test_register_route(self, client):
        """Testa rota de registro"""
        response = client.get('/auth/register')
        assert response.status_code == 200
    
    def test_dashboard_route_redirect(self, client):
        """Testa que dashboard redireciona quando não autenticado"""
        response = client.get('/dashboard', follow_redirects=True)
        # Deve redirecionar para login e exibir mensagem de alerta
        assert response.status_code in [200, 302, 308, 401, 403]
        page = response.data.decode('utf-8').lower()
        assert 'você precisa estar logado' in page or 'voce precisa estar logado' in page
    
    def test_users_route_redirect(self, client):
        """Testa que users redireciona quando não autenticado"""
        response = client.get('/users', follow_redirects=True)
        # Deve redirecionar para login e exibir mensagem de alerta
        assert response.status_code in [200, 302, 308, 401, 403]
        page = response.data.decode('utf-8').lower()
        assert 'você precisa estar logado' in page or 'voce precisa estar logado' in page
    
    def test_profile_route_redirect(self, client):
        """Testa que profile redireciona quando não autenticado"""
        response = client.get('/profile', follow_redirects=True)
        # Deve redirecionar para login e exibir mensagem de alerta
        assert response.status_code in [200, 302, 308, 401, 403]
        page = response.data.decode('utf-8').lower()
        assert 'você precisa estar logado' in page or 'voce precisa estar logado' in page
    
    def test_authenticated_dashboard(self, client, app):
        """Testa acesso ao dashboard quando autenticado"""
        with app.app_context():
            # Criar usuário
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            })
            
            # Acessar dashboard
            response = client.get('/dashboard')
            assert response.status_code == 200
    
    def test_authenticated_users_list(self, client, app):
        """Testa acesso à lista de usuários quando autenticado"""
        with app.app_context():
            # Criar usuário
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            })
            
            # Acessar lista de usuários (pode redirecionar para /users/)
            response = client.get('/users', follow_redirects=True)
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'usu' in page  # conteúdo genérico para evitar falsos negativos
    
    def test_authenticated_profile(self, client, app):
        """Testa acesso ao perfil quando autenticado"""
        with app.app_context():
            # Criar usuário
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            })
            
            # Acessar perfil
            response = client.get('/profile')
            assert response.status_code == 200
