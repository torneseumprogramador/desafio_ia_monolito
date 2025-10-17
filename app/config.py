import os
from datetime import timedelta


class Config:
    """Configurações da aplicação Flask"""
    
    # Secret key para sessões
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'dev-secret-key-change-in-production'
    
    # Configurações de sessão
    PERMANENT_SESSION_LIFETIME = timedelta(days=7)  # Sessão dura 7 dias
    
    # Configurações de banco de dados
    # Usar PostgreSQL se DATABASE_URL estiver definido, caso contrário usar SQLite
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL') or 'sqlite:///app.db'
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ECHO = os.environ.get('DEBUG', 'True') == 'True'  # Log das queries SQL
    
    # Outras configurações
    DEBUG = os.environ.get('DEBUG', 'True') == 'True'
    FLASK_ENV = os.environ.get('FLASK_ENV', 'development')

