"""
Decoradores e Middlewares da Aplicação
"""
from functools import wraps
from flask import session, redirect, url_for, flash, request


def login_required(f):
    """
    Decorator para proteger rotas que requerem autenticação.
    Redireciona para a página de login se o usuário não estiver autenticado.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            flash('Você precisa estar logado para acessar esta página.', 'warning')
            # Salva a URL que o usuário estava tentando acessar
            session['next'] = request.url
            return redirect(url_for('auth.login'))
        return f(*args, **kwargs)
    return decorated_function


def guest_only(f):
    """
    Decorator para rotas que só devem ser acessadas por usuários NÃO autenticados.
    Redireciona para a home se o usuário já estiver logado.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' in session:
            flash('Você já está logado!', 'info')
            return redirect(url_for('main.index'))
        return f(*args, **kwargs)
    return decorated_function

