from flask import Blueprint, render_template

# Criar Blueprint para rotas principais
bp = Blueprint('main', __name__)


@bp.route('/')
def index():
    """Página inicial"""
    return render_template('main/index.html', title='Home')


@bp.route('/about')
def about():
    """Página sobre"""
    return render_template('main/about.html', title='Sobre')

