"""
Testes unitários para o modelo User
"""
import pytest
from app.models.user import User, db
from tests.factories.user_factory import UserFactory


class TestUserModel:
    """Testes para o modelo User"""
    
    def test_user_creation(self, app):
        """Testa criação de usuário"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            assert user.id is not None
            assert user.name is not None
            assert user.email is not None
            assert user.username is not None
            assert user.is_active is True
    
    def test_user_password_hashing(self, app):
        """Testa hash de senha"""
        with app.app_context():
            user = UserFactory()
            password = 'testpassword123'
            user.set_password(password)
            
            assert user.password_hash != password
            assert user.check_password(password)
            assert not user.check_password('wrongpassword')
    
    def test_user_to_dict(self, app):
        """Testa conversão para dicionário"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            user_dict = user.to_dict()
            
            assert user_dict['id'] == user.id
            assert user_dict['name'] == user.name
            assert user_dict['email'] == user.email
            assert user_dict['username'] == user.username
            assert user_dict['is_active'] == user.is_active
            assert 'password_hash' not in user_dict
    
    def test_user_update_from_dict(self, app):
        """Testa atualização a partir de dicionário"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            original_name = user.name
            original_email = user.email
            
            update_data = {
                'name': 'Novo Nome',
                'email': 'novo@email.com',
                'phone': '123456789'
            }
            
            user.update_from_dict(update_data)
            
            assert user.name == 'Novo Nome'
            assert user.email == 'novo@email.com'
            assert user.phone == '123456789'
            assert user.username != 'Novo Nome'  # Username não foi alterado
    
    def test_user_password_update(self, app):
        """Testa atualização de senha"""
        with app.app_context():
            user = UserFactory()
            user.set_password('oldpassword')
            db.session.add(user)
            db.session.commit()
            
            old_hash = user.password_hash
            
            update_data = {'password': 'newpassword123'}
            user.update_from_dict(update_data)
            
            assert user.password_hash != old_hash
            assert user.check_password('newpassword123')
            assert not user.check_password('oldpassword')
    
    def test_user_repr(self, app):
        """Testa representação string do usuário"""
        with app.app_context():
            user = UserFactory()
            db.session.add(user)
            db.session.commit()
            
            repr_str = repr(user)
            assert f'<User {user.username}>' == repr_str