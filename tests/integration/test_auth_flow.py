"""
Testes de integração para fluxo de autenticação
"""
import pytest
from app.models.user import db
from tests.factories.user_factory import UserFactory


class TestAuthFlow:
    """Testes de integração para fluxo de autenticação"""
    
    def test_complete_auth_flow(self, client, app):
        """Testa fluxo completo de autenticação"""
        with app.app_context():
            # 1. Acessar página inicial (deve redirecionar para login se não autenticado)
            response = client.get('/')
            assert response.status_code in [200, 302]  # Pode redirecionar ou mostrar página
            
            # 2. Registrar novo usuário
            response = client.post('/auth/register', data={
                'name': 'Maria Silva',
                'email': 'maria@test.com',
                'username': 'maria123',
                'password': 'senha123',
                'password_confirm': 'senha123',
                'phone': '11999999999'
            }, follow_redirects=True)
            
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'bem-vindo' in page or 'home' in page
            
            # 3. Fazer logout
            response = client.get('/auth/logout', follow_redirects=True)
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            # mensagem de despedida deve estar presente
            assert 'até logo' in page or 'ate logo' in page
            
            # 4. Fazer login novamente
            response = client.post('/auth/login', data={
                'username_or_email': 'maria123',
                'password': 'senha123'
            }, follow_redirects=True)
            
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'bem-vindo' in page or 'home' in page
    
    def test_protected_route_access(self, client, app):
        """Testa acesso a rotas protegidas"""
        with app.app_context():
            # Tentar acessar dashboard sem autenticação
            response = client.get('/dashboard', follow_redirects=True)
            # Deve redirecionar para login e exibir mensagem
            assert response.status_code in [200, 302, 401, 403]
            page = response.data.decode('utf-8').lower()
            assert 'você precisa estar logado' in page or 'voce precisa estar logado' in page
            
            # Criar usuário e fazer login
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            response = client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            }, follow_redirects=True)
            
            # Agora deve conseguir acessar dashboard
            response = client.get('/dashboard')
            assert response.status_code == 200
    
    def test_session_persistence(self, client, app):
        """Testa persistência de sessão"""
        with app.app_context():
            # Criar usuário e fazer login
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            response = client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            }, follow_redirects=True)
            
            # Fazer nova requisição (sessão deve persistir)
            response = client.get('/dashboard')
            assert response.status_code == 200
    
    def test_multiple_user_sessions(self, client, app):
        """Testa múltiplas sessões de usuários"""
        with app.app_context():
            # Criar dois usuários
            user1 = UserFactory(username='user1')
            user1.set_password('senha123')
            user2 = UserFactory(username='user2')
            user2.set_password('senha123')
            
            db.session.add_all([user1, user2])
            db.session.commit()
            
            # Login do primeiro usuário
            response = client.post('/auth/login', data={
                'username_or_email': 'user1',
                'password': 'senha123'
            }, follow_redirects=True)
            
            # Verificar se está logado
            response = client.get('/dashboard')
            assert response.status_code == 200
            
            # Logout
            response = client.get('/auth/logout', follow_redirects=True)
            
            # Login do segundo usuário
            response = client.post('/auth/login', data={
                'username_or_email': 'user2',
                'password': 'senha123'
            }, follow_redirects=True)
            
            # Verificar se está logado
            response = client.get('/dashboard')
            assert response.status_code == 200
