from flask import Flask
from .config import Config


def create_app(config_class=Config):
    """Application Factory Pattern"""
    app = Flask(__name__)
    app.config.from_object(config_class)
    
    # Registrar Blueprints (Controllers)
    from app.controllers import main_controller
    
    app.register_blueprint(main_controller.bp)
    
    return app

