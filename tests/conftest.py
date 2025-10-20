"""
Configurações globais para os testes
"""
import pytest
import os
import tempfile
from app import create_app
from app.models.user import db


@pytest.fixture
def app():
    """Cria uma instância da aplicação para testes"""
    # Criar arquivo temporário para banco de dados
    db_fd, db_path = tempfile.mkstemp()
    
    # Configurações de teste
    test_config = {
        'TESTING': True,
        'SQLALCHEMY_DATABASE_URI': f'sqlite:///{db_path}',
        'SQLALCHEMY_TRACK_MODIFICATIONS': False,
        'SECRET_KEY': 'test-secret-key',
        'WTF_CSRF_ENABLED': False
    }
    
    app = create_app()
    app.config.update(test_config)
    
    with app.app_context():
        db.create_all()
        yield app
        db.drop_all()
    
    os.close(db_fd)
    os.unlink(db_path)


@pytest.fixture
def client(app):
    """Cliente de teste para fazer requisições"""
    return app.test_client()


@pytest.fixture
def runner(app):
    """Runner para comandos CLI"""
    return app.test_cli_runner()


@pytest.fixture
def auth_headers(client):
    """Headers de autenticação para testes"""
    # Criar usuário de teste
    from tests.factories.user_factory import UserFactory
    
    user = UserFactory()
    user.set_password('testpassword')
    db.session.add(user)
    db.session.commit()
    
    # Fazer login
    response = client.post('/auth/login', data={
        'username_or_email': user.username,
        'password': 'testpassword'
    })
    
    # Retornar headers de autenticação
    return {
        'Cookie': response.headers.get('Set-Cookie', '')
    }
