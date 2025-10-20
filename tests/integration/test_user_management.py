"""
Testes de integração para gerenciamento de usuários
"""
import pytest
from app.models.user import db
from tests.factories.user_factory import UserFactory


class TestUserManagement:
    """Testes de integração para gerenciamento de usuários"""
    
    def test_user_crud_operations(self, client, app):
        """Testa operações CRUD de usuários"""
        with app.app_context():
            # Criar usuário admin
            admin = UserFactory()
            admin.set_password('admin123')
            db.session.add(admin)
            db.session.commit()
            
            # Fazer login como admin
            client.post('/auth/login', data={
                'username_or_email': admin.username,
                'password': 'admin123'
            })
            
            # 1. Criar novo usuário
            response = client.post('/users/create', data={
                'name': 'Novo Usuario',
                'email': 'novo@test.com',
                'username': 'novo123',
                'password': 'senha123',
                'phone': '11999999999'
            }, follow_redirects=True)
            
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'criado' in page or 'sucesso' in page or 'novo usuario' in page
            
            # 2. Listar usuários
            response = client.get('/users', follow_redirects=True)
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'novo usuario' in page
            
            # 3. Visualizar usuário
            # Assumindo que o ID do usuário criado é 2 (admin é 1)
            response = client.get('/users/2')
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'novo@test.com' in page
            
            # 4. Editar usuário
            response = client.post('/users/2/edit', data={
                'name': 'Usuario Atualizado',
                'email': 'atualizado@test.com',
                'username': 'atualizado123',
                'phone': '11888888888'
            }, follow_redirects=True)
            
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'atualizado' in page or 'sucesso' in page
            
            # 5. Verificar atualização
            response = client.get('/users/2')
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'usuario atualizado' in page
    
    def test_user_search_and_filter(self, client, app):
        """Testa busca e filtros de usuários"""
        with app.app_context():
            # Criar usuários de teste
            users = [
                UserFactory(name='Joao Silva', email='joao@test.com'),
                UserFactory(name='Maria Santos', email='maria@test.com'),
                UserFactory(name='Pedro Costa', email='pedro@test.com')
            ]
            
            for user in users:
                user.set_password('senha123')
                db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': users[0].username,
                'password': 'senha123'
            })
            
            # Buscar usuários
            response = client.get('/users?search=Joao', follow_redirects=True)
            assert response.status_code == 200
            page = response.data.decode('utf-8').lower()
            assert 'joao silva' in page
            
            # Filtrar por status ativo (segue redirects)
            response = client.get('/users?status=active', follow_redirects=True)
            assert response.status_code == 200
    
    def test_user_pagination(self, client, app):
        """Testa paginação de usuários"""
        with app.app_context():
            # Criar múltiplos usuários
            users = []
            for i in range(15):  # Criar mais usuários que o limite por página
                user = UserFactory(name=f'Usuario {i}')
                user.set_password('senha123')
                users.append(user)
                db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': users[0].username,
                'password': 'senha123'
            })
            
            # Testar primeira página
            response = client.get('/users?page=1', follow_redirects=True)
            assert response.status_code == 200
            
            # Testar segunda página
            response = client.get('/users?page=2', follow_redirects=True)
            assert response.status_code == 200
    
    def test_user_statistics(self, client, app):
        """Testa estatísticas de usuários"""
        with app.app_context():
            # Criar usuários com diferentes status
            active_user = UserFactory(is_active=True)
            inactive_user = UserFactory(is_active=False)
            
            for user in [active_user, inactive_user]:
                user.set_password('senha123')
                db.session.add(user)
            db.session.commit()
            
            # Fazer login
            client.post('/auth/login', data={
                'username_or_email': active_user.username,
                'password': 'senha123'
            })
            
            # Acessar dashboard (que deve mostrar estatísticas)
            response = client.get('/dashboard')
            assert response.status_code == 200
            # Verificar se contém informações de estatísticas
            page = response.data.decode('utf-8').lower()
            assert 'usuarios' in page or 'users' in page