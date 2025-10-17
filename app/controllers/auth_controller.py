"""
Controller de Autenticação
Responsável pelas rotas de login, logout e registro
"""
from flask import Blueprint, render_template, request, redirect, url_for, flash, session
from app.services.user_service import UserService
from app.middleware import guest_only

bp = Blueprint('auth', __name__, url_prefix='/auth')
user_service = UserService()


@bp.route('/login', methods=['GET', 'POST'])
@guest_only
def login():
    """Página e ação de login"""
    if request.method == 'POST':
        username_or_email = request.form.get('username_or_email', '').strip()
        password = request.form.get('password', '')
        
        if not username_or_email or not password:
            flash('Por favor, preencha todos os campos.', 'danger')
            return render_template('auth/login.html', username_or_email=username_or_email)
        
        # Autenticar usuário
        user, error = user_service.authenticate(username_or_email, password)
        
        if error:
            flash(error, 'danger')
            return render_template('auth/login.html', username_or_email=username_or_email)
        
        # Login bem-sucedido - criar sessão
        session['user_id'] = user.id
        session['username'] = user.username
        session['name'] = user.name
        session.permanent = True  # Usar cookie permanente
        
        flash(f'Bem-vindo(a), {user.name}!', 'success')
        
        # Redirecionar para a página que o usuário tentou acessar ou para home
        next_page = session.pop('next', None)
        if next_page:
            return redirect(next_page)
        return redirect(url_for('main.index'))
    
    return render_template('auth/login.html')


@bp.route('/register', methods=['GET', 'POST'])
@guest_only
def register():
    """Página e ação de registro de novos usuários"""
    if request.method == 'POST':
        data = {
            'name': request.form.get('name', '').strip(),
            'email': request.form.get('email', '').strip(),
            'username': request.form.get('username', '').strip(),
            'password': request.form.get('password', ''),
            'phone': request.form.get('phone', '').strip(),
            'is_active': True  # Novos usuários são ativados por padrão
        }
        
        # Validar confirmação de senha
        password_confirm = request.form.get('password_confirm', '')
        if data['password'] != password_confirm:
            flash('As senhas não coincidem.', 'danger')
            return render_template('auth/register.html', data=data)
        
        # Validar tamanho da senha
        if len(data['password']) < 6:
            flash('A senha deve ter no mínimo 6 caracteres.', 'danger')
            return render_template('auth/register.html', data=data)
        
        # Criar usuário
        user, error = user_service.create_user(data)
        
        if error:
            flash(error, 'danger')
            return render_template('auth/register.html', data=data)
        
        # Registro bem-sucedido - fazer login automático
        session['user_id'] = user.id
        session['username'] = user.username
        session['name'] = user.name
        session.permanent = True
        
        flash('Conta criada com sucesso! Bem-vindo(a)!', 'success')
        return redirect(url_for('main.index'))
    
    return render_template('auth/register.html')


@bp.route('/logout')
def logout():
    """Ação de logout"""
    username = session.get('name', 'Usuário')
    
    # Limpar sessão
    session.clear()
    
    flash(f'Até logo, {username}!', 'info')
    return redirect(url_for('main.index'))

