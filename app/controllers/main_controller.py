from flask import Blueprint, render_template, redirect, url_for

# Criar Blueprint para rotas principais
bp = Blueprint('main', __name__)


@bp.route('/')
def index():
    """Página inicial - redireciona para dashboard se logado"""
    from flask import session
    
    # Se usuário estiver logado, redirecionar para dashboard
    if session.get('user_id'):
        return redirect(url_for('dashboard.dashboard'))
    
    return render_template('main/index.html', title='Home')


@bp.route('/about')
def about():
    """Página sobre"""
    return render_template('main/about.html', title='Sobre')

