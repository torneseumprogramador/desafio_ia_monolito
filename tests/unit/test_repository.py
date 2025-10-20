"""
Testes unitários para repositórios
"""
import pytest
from app.repositories.user_repository import UserRepository
from app.models.user import User, db
from tests.factories.user_factory import UserFactory


class TestUserRepository:
    """Testes para UserRepository"""
    
    def test_create_user(self, app):
        """Testa criação de usuário no repositório"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory()
            
            created_user = repository.create(user)
            
            assert created_user.id is not None
            assert created_user.name == user.name
            assert created_user.email == user.email
    
    def test_find_by_id(self, app):
        """Testa busca por ID"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            found_user = repository.find_by_id(user.id)
            
            assert found_user is not None
            assert found_user.id == user.id
    
    def test_find_by_id_not_found(self, app):
        """Testa busca por ID inexistente"""
        with app.app_context():
            repository = UserRepository()
            found_user = repository.find_by_id(999)
            
            assert found_user is None
    
    def test_find_by_email(self, app):
        """Testa busca por email"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory(email='test@example.com')
            db.session.add(user)
            db.session.commit()
            
            found_user = repository.find_by_email('test@example.com')
            
            assert found_user is not None
            assert found_user.email == 'test@example.com'
    
    def test_find_by_username(self, app):
        """Testa busca por username"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory(username='testuser')
            db.session.add(user)
            db.session.commit()
            
            found_user = repository.find_by_username('testuser')
            
            assert found_user is not None
            assert found_user.username == 'testuser'
    
    def test_exists_by_email(self, app):
        """Testa verificação de existência por email"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory(email='test@example.com')
            db.session.add(user)
            db.session.commit()
            
            assert repository.exists_by_email('test@example.com') is True
            assert repository.exists_by_email('nonexistent@example.com') is False
    
    def test_exists_by_username(self, app):
        """Testa verificação de existência por username"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory(username='testuser')
            db.session.add(user)
            db.session.commit()
            
            assert repository.exists_by_username('testuser') is True
            assert repository.exists_by_username('nonexistent') is False
    
    def test_update_user(self, app):
        """Testa atualização de usuário"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            user.name = 'Nome Atualizado'
            updated_user = repository.update(user)
            
            assert updated_user.name == 'Nome Atualizado'
    
    def test_delete_user(self, app):
        """Testa exclusão de usuário"""
        with app.app_context():
            repository = UserRepository()
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            user_id = user.id
            
            repository.delete(user)
            
            # Verificar se foi removido
            found_user = repository.find_by_id(user_id)
            assert found_user is None
    
    def test_count_users(self, app):
        """Testa contagem de usuários"""
        with app.app_context():
            repository = UserRepository()
            
            # Criar alguns usuários
            for _ in range(3):
                user = UserFactory()
                db.session.add(user)
            db.session.commit()
            
            count = repository.count()
            assert count == 3
