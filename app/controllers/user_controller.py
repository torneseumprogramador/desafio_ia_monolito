"""
Controller de Usuários
Responsável pelas rotas e requisições HTTP relacionadas a usuários
"""
from flask import Blueprint, render_template, request, redirect, url_for, flash, jsonify
from app.services.user_service import UserService
from app.utils.decorators import login_required

bp = Blueprint('users', __name__, url_prefix='/users')
user_service = UserService()


@bp.route('/')
@login_required
def index():
    """Lista todos os usuários"""
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    
    users, total = user_service.get_all_users(page=page, per_page=per_page)
    
    # Calcular informações de paginação
    total_pages = (total + per_page - 1) // per_page
    
    return render_template(
        'users/index.html',
        users=users,
        page=page,
        per_page=per_page,
        total=total,
        total_pages=total_pages
    )


@bp.route('/create', methods=['GET', 'POST'])
@login_required
def create():
    """Cria um novo usuário"""
    if request.method == 'POST':
        data = {
            'name': request.form.get('name'),
            'email': request.form.get('email'),
            'username': request.form.get('username'),
            'password': request.form.get('password'),
            'phone': request.form.get('phone'),
            'is_active': request.form.get('is_active') == 'on'
        }
        
        user, error = user_service.create_user(data)
        
        if error:
            flash(error, 'danger')
            return render_template('users/create.html', data=data)
        
        flash('Usuário criado com sucesso!', 'success')
        return redirect(url_for('users.index'))
    
    return render_template('users/create.html')


@bp.route('/<int:user_id>')
@login_required
def show(user_id):
    """Exibe detalhes de um usuário"""
    user = user_service.get_user_by_id(user_id)
    
    if not user:
        flash('Usuário não encontrado', 'danger')
        return redirect(url_for('users.index'))
    
    return render_template('users/show.html', user=user)


@bp.route('/<int:user_id>/edit', methods=['GET', 'POST'])
@login_required
def edit(user_id):
    """Edita um usuário existente"""
    user = user_service.get_user_by_id(user_id)
    
    if not user:
        flash('Usuário não encontrado', 'danger')
        return redirect(url_for('users.index'))
    
    if request.method == 'POST':
        data = {
            'name': request.form.get('name'),
            'email': request.form.get('email'),
            'username': request.form.get('username'),
            'phone': request.form.get('phone'),
            'is_active': request.form.get('is_active') == 'on'
        }
        
        # Só atualiza a senha se foi informada
        password = request.form.get('password')
        if password:
            data['password'] = password
        
        updated_user, error = user_service.update_user(user_id, data)
        
        if error:
            flash(error, 'danger')
            return render_template('users/edit.html', user=user)
        
        flash('Usuário atualizado com sucesso!', 'success')
        return redirect(url_for('users.show', user_id=user_id))
    
    return render_template('users/edit.html', user=user)


@bp.route('/<int:user_id>/delete', methods=['POST'])
@login_required
def delete(user_id):
    """Remove um usuário"""
    success, error = user_service.delete_user(user_id)
    
    if error:
        flash(error, 'danger')
    else:
        flash('Usuário removido com sucesso!', 'success')
    
    return redirect(url_for('users.index'))


@bp.route('/<int:user_id>/toggle-status', methods=['POST'])
@login_required
def toggle_status(user_id):
    """Ativa/Desativa um usuário"""
    user, error = user_service.toggle_user_status(user_id)
    
    if error:
        flash(error, 'danger')
    else:
        status = 'ativado' if user.is_active else 'desativado'
        flash(f'Usuário {status} com sucesso!', 'success')
    
    return redirect(url_for('users.index'))


# API Endpoints (opcional)
@bp.route('/api', methods=['GET'])
@login_required
def api_list():
    """API: Lista todos os usuários"""
    users, total = user_service.get_all_users(page=1, per_page=100)
    return jsonify({
        'users': [user.to_dict() for user in users],
        'total': total
    })


@bp.route('/api/<int:user_id>', methods=['GET'])
@login_required
def api_get(user_id):
    """API: Busca um usuário por ID"""
    user = user_service.get_user_by_id(user_id)
    
    if not user:
        return jsonify({'error': 'Usuário não encontrado'}), 404
    
    return jsonify(user.to_dict())

