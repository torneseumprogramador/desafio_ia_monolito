from flask import Flask
from flask_migrate import Migrate
from .config import Config
from .models.user import db

migrate = Migrate()


def create_app(config_class=Config):
    """Application Factory Pattern"""
    app = Flask(__name__)
    app.config.from_object(config_class)
    
    # Inicializar banco de dados
    db.init_app(app)
    
    # Inicializar Flask-Migrate
    migrate.init_app(app, db)
    
    # Registrar Blueprints (Controllers)
    from app.controllers import main_controller, user_controller, auth_controller
    
    app.register_blueprint(main_controller.bp)
    app.register_blueprint(user_controller.bp)
    app.register_blueprint(auth_controller.bp)
    
    return app

