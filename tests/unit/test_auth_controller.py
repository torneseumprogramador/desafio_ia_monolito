"""
Testes unitários para o AuthController
"""
import pytest
from app.controllers.auth_controller import bp
from app.models.user import db
from tests.factories.user_factory import UserFactory


class TestAuthController:
    """Testes para o AuthController"""
    
    def test_login_get(self, client):
        """Testa página de login GET"""
        response = client.get('/auth/login')
        
        assert response.status_code == 200
        assert b'login' in response.data.lower()
    
    def test_login_post_success(self, client, app):
        """Testa login POST com sucesso"""
        with app.app_context():
            # Criar usuário de teste
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            response = client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            }, follow_redirects=True)
            
            assert response.status_code == 200
    
    def test_login_post_invalid_credentials(self, client, app):
        """Testa login POST com credenciais inválidas"""
        with app.app_context():
            response = client.post('/auth/login', data={
                'username_or_email': 'usuarioinexistente',
                'password': 'senha123'
            })
            
            assert response.status_code == 200
    
    def test_login_post_empty_fields(self, client):
        """Testa login POST com campos vazios"""
        response = client.post('/auth/login', data={
            'username_or_email': '',
            'password': ''
        })
        
        assert response.status_code == 200
    
    def test_register_get(self, client):
        """Testa página de registro GET"""
        response = client.get('/auth/register')
        
        assert response.status_code == 200
        assert b'register' in response.data.lower() or b'cadastro' in response.data.lower()
    
    def test_register_post_success(self, client, app):
        """Testa registro POST com sucesso"""
        with app.app_context():
            response = client.post('/auth/register', data={
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123',
                'password_confirm': 'senha123',
                'phone': '11999999999'
            }, follow_redirects=True)
            
            assert response.status_code == 200
    
    def test_register_post_password_mismatch(self, client):
        """Testa registro POST com senhas diferentes"""
        response = client.post('/auth/register', data={
            'name': 'Joao Silva',
            'email': 'joao@test.com',
            'username': 'joao123',
            'password': 'senha123',
            'password_confirm': 'senhadiferente',
            'phone': '11999999999'
        })
        
        assert response.status_code == 200
    
    def test_register_post_short_password(self, client):
        """Testa registro POST com senha muito curta"""
        response = client.post('/auth/register', data={
            'name': 'Joao Silva',
            'email': 'joao@test.com',
            'username': 'joao123',
            'password': '123',
            'password_confirm': '123',
            'phone': '11999999999'
        })
        
        assert response.status_code == 200
    
    def test_register_post_duplicate_email(self, client, app):
        """Testa registro POST com email duplicado"""
        with app.app_context():
            # Criar usuário existente
            existing_user = UserFactory(email='joao@test.com')
            db.session.add(existing_user)
            db.session.commit()
            
            response = client.post('/auth/register', data={
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123',
                'password_confirm': 'senha123',
                'phone': '11999999999'
            })
            
            assert response.status_code == 200
    
    def test_register_post_duplicate_username(self, client, app):
        """Testa registro POST com username duplicado"""
        with app.app_context():
            # Criar usuário existente
            existing_user = UserFactory(username='joao123')
            db.session.add(existing_user)
            db.session.commit()
            
            response = client.post('/auth/register', data={
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123',
                'password_confirm': 'senha123',
                'phone': '11999999999'
            })
            
            assert response.status_code == 200
    
    def test_logout(self, client, app):
        """Testa logout"""
        with app.app_context():
            # Criar usuário e fazer login
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': user.username,
                'password': 'senha123'
            })
            
            # Fazer logout
            response = client.get('/auth/logout', follow_redirects=True)
            
            assert response.status_code == 200