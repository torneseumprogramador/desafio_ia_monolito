"""
Testes unitários para o UserService
"""
import pytest
from app.services.user_service import UserService
from app.models.user import User, db
from tests.factories.user_factory import UserFactory


class TestUserService:
    """Testes para o UserService"""
    
    def test_create_user_success(self, app):
        """Testa criação de usuário com sucesso"""
        with app.app_context():
            service = UserService()
            user_data = {
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123',
                'phone': '11999999999',
                'is_active': True
            }
            
            user, error = service.create_user(user_data)
            
            assert user is not None
            assert error is None
            assert user.name == 'Joao Silva'
            assert user.email == 'joao@test.com'
            assert user.username == 'joao123'
            assert user.check_password('senha123')
    
    def test_create_user_missing_name(self, app):
        """Testa criação de usuário sem nome"""
        with app.app_context():
            service = UserService()
            user_data = {
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123'
            }
            
            user, error = service.create_user(user_data)
            
            assert user is None
            assert error == 'Nome é obrigatório'
    
    def test_create_user_missing_email(self, app):
        """Testa criação de usuário sem email"""
        with app.app_context():
            service = UserService()
            user_data = {
                'name': 'Joao Silva',
                'username': 'joao123',
                'password': 'senha123'
            }
            
            user, error = service.create_user(user_data)
            
            assert user is None
            assert error == 'Email é obrigatório'
    
    def test_create_user_missing_username(self, app):
        """Testa criação de usuário sem username"""
        with app.app_context():
            service = UserService()
            user_data = {
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'password': 'senha123'
            }
            
            user, error = service.create_user(user_data)
            
            assert user is None
            assert error == 'Username é obrigatório'
    
    def test_create_user_missing_password(self, app):
        """Testa criação de usuário sem senha"""
        with app.app_context():
            service = UserService()
            user_data = {
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123'
            }
            
            user, error = service.create_user(user_data)
            
            assert user is None
            assert error == 'Senha é obrigatória'
    
    def test_create_user_duplicate_email(self, app):
        """Testa criação de usuário com email duplicado"""
        with app.app_context():
            # Criar usuário existente
            existing_user = UserFactory(email='joao@test.com')
            db.session.add(existing_user)
            db.session.commit()
            
            service = UserService()
            user_data = {
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123'
            }
            
            user, error = service.create_user(user_data)
            
            assert user is None
            assert error == 'Email já cadastrado'
    
    def test_create_user_duplicate_username(self, app):
        """Testa criação de usuário com username duplicado"""
        with app.app_context():
            # Criar usuário existente
            existing_user = UserFactory(username='joao123')
            db.session.add(existing_user)
            db.session.commit()
            
            service = UserService()
            user_data = {
                'name': 'Joao Silva',
                'email': 'joao@test.com',
                'username': 'joao123',
                'password': 'senha123'
            }
            
            user, error = service.create_user(user_data)
            
            assert user is None
            assert error == 'Username já cadastrado'
    
    def test_authenticate_success(self, app):
        """Testa autenticação bem-sucedida"""
        with app.app_context():
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            authenticated_user, error = service.authenticate(user.username, 'senha123')
            
            assert authenticated_user is not None
            assert error is None
            assert authenticated_user.id == user.id
    
    def test_authenticate_by_email(self, app):
        """Testa autenticação por email"""
        with app.app_context():
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            authenticated_user, error = service.authenticate(user.email, 'senha123')
            
            assert authenticated_user is not None
            assert error is None
            assert authenticated_user.id == user.id
    
    def test_authenticate_wrong_password(self, app):
        """Testa autenticação com senha incorreta"""
        with app.app_context():
            user = UserFactory()
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            authenticated_user, error = service.authenticate(user.username, 'senhaerrada')
            
            assert authenticated_user is None
            assert error == 'Usuário ou senha inválidos'
    
    def test_authenticate_inactive_user(self, app):
        """Testa autenticação de usuário inativo"""
        with app.app_context():
            user = UserFactory(is_active=False)
            user.set_password('senha123')
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            authenticated_user, error = service.authenticate(user.username, 'senha123')
            
            assert authenticated_user is None
            assert error == 'Usuário inativo. Entre em contato com o administrador.'
    
    def test_authenticate_nonexistent_user(self, app):
        """Testa autenticação de usuário inexistente"""
        with app.app_context():
            service = UserService()
            authenticated_user, error = service.authenticate('usuarioinexistente', 'senha123')
            
            assert authenticated_user is None
            assert error == 'Usuário ou senha inválidos'
    
    def test_get_user_by_id(self, app):
        """Testa busca de usuário por ID"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            found_user = service.get_user_by_id(user.id)
            
            assert found_user is not None
            assert found_user.id == user.id
    
    def test_get_user_by_id_not_found(self, app):
        """Testa busca de usuário por ID inexistente"""
        with app.app_context():
            service = UserService()
            found_user = service.get_user_by_id(999)
            
            assert found_user is None
    
    def test_update_user_success(self, app):
        """Testa atualização de usuário com sucesso"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            update_data = {
                'name': 'Nome Atualizado',
                'phone': '987654321'
            }
            
            updated_user, error = service.update_user(user.id, update_data)
            
            assert updated_user is not None
            assert error is None
            assert updated_user.name == 'Nome Atualizado'
            assert updated_user.phone == '987654321'
    
    def test_update_user_not_found(self, app):
        """Testa atualização de usuário inexistente"""
        with app.app_context():
            service = UserService()
            update_data = {'name': 'Nome Atualizado'}
            
            updated_user, error = service.update_user(999, update_data)
            
            assert updated_user is None
            assert error == 'Usuário não encontrado'
    
    def test_delete_user_success(self, app):
        """Testa exclusão de usuário com sucesso"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            user_id = user.id
            
            service = UserService()
            success, error = service.delete_user(user_id)
            
            assert success is True
            assert error is None
            
            # Verificar se usuário foi removido
            deleted_user = service.get_user_by_id(user_id)
            assert deleted_user is None
    
    def test_delete_user_not_found(self, app):
        """Testa exclusão de usuário inexistente"""
        with app.app_context():
            service = UserService()
            success, error = service.delete_user(999)
            
            assert success is False
            assert error == 'Usuário não encontrado'
    
    def test_toggle_user_status(self, app):
        """Testa alternância de status do usuário"""
        with app.app_context():
            user = UserFactory(is_active=True)
            db.session.add(user)
            db.session.commit()
            
            service = UserService()
            
            # Desativar usuário
            updated_user, error = service.toggle_user_status(user.id)
            assert updated_user is not None
            assert error is None
            assert updated_user.is_active is False
            
            # Ativar usuário novamente
            updated_user, error = service.toggle_user_status(user.id)
            assert updated_user is not None
            assert error is None
            assert updated_user.is_active is True
    
    def test_count_users(self, app):
        """Testa contagem de usuários"""
        with app.app_context():
            # Criar alguns usuários
            for _ in range(3):
                user = UserFactory()
                db.session.add(user)
            db.session.commit()
            
            service = UserService()
            count = service.count_users()
            
            assert count == 3