"""
Middleware de Autenticação
Responsável por proteger rotas que requerem autenticação ou que devem ser acessadas apenas por visitantes
"""
from functools import wraps
from flask import session, redirect, url_for, flash, request


def login_required(f):
    """
    Middleware para proteger rotas que requerem autenticação.
    Redireciona para a página de login se o usuário não estiver autenticado.
    
    Args:
        f: Função da rota a ser protegida
        
    Returns:
        Função decorada que verifica autenticação antes de executar a rota
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
    Middleware para rotas que só devem ser acessadas por usuários NÃO autenticados.
    Redireciona para a home se o usuário já estiver logado.
    
    Args:
        f: Função da rota a ser protegida
        
    Returns:
        Função decorada que verifica se o usuário não está autenticado
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' in session:
            flash('Você já está logado!', 'info')
            return redirect(url_for('main.index'))
        return f(*args, **kwargs)
    return decorated_function
