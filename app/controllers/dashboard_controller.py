"""
Controller do Dashboard e Perfil
Responsável pelas rotas do dashboard e gerenciamento do perfil do usuário logado
"""
from flask import Blueprint, render_template, request, redirect, url_for, flash, session, jsonify
from app.services.user_service import UserService
from app.middleware import login_required

bp = Blueprint('dashboard', __name__)
user_service = UserService()


@bp.route('/dashboard')
@login_required
def dashboard():
    """Dashboard principal para usuários logados"""
    try:
        # Obter estatísticas dos usuários
        stats = user_service.get_user_statistics()
        
        # Obter dados para gráfico de usuários por mês (últimos 6 meses)
        monthly_data = user_service.get_monthly_user_registrations()
        
        return render_template(
            'dashboard/dashboard.html',
            stats=stats,
            monthly_data=monthly_data
        )
    except Exception as e:
        flash(f'Erro ao carregar dashboard: {str(e)}', 'danger')
        return redirect(url_for('main.index'))


@bp.route('/profile')
@login_required
def profile():
    """Exibe o perfil do usuário logado"""
    user_id = session.get('user_id')
    user = user_service.get_user_by_id(user_id)
    
    if not user:
        flash('Usuário não encontrado', 'danger')
        return redirect(url_for('dashboard.dashboard'))
    
    return render_template('dashboard/profile.html', user=user)


@bp.route('/profile/edit', methods=['GET', 'POST'])
@login_required
def edit_profile():
    """Edita o perfil do usuário logado"""
    user_id = session.get('user_id')
    user = user_service.get_user_by_id(user_id)
    
    if not user:
        flash('Usuário não encontrado', 'danger')
        return redirect(url_for('dashboard.dashboard'))
    
    if request.method == 'POST':
        data = {
            'name': request.form.get('name', '').strip(),
            'email': request.form.get('email', '').strip(),
            'username': request.form.get('username', '').strip(),
            'phone': request.form.get('phone', '').strip(),
        }
        
        # Atualizar dados
        updated_user, error = user_service.update_user(user_id, data)
        
        if error:
            flash(error, 'danger')
            return render_template('dashboard/edit_profile.html', user=user, data=data)
        
        # Atualizar dados da sessão
        session['name'] = updated_user.name
        session['username'] = updated_user.username
        
        flash('Perfil atualizado com sucesso!', 'success')
        return redirect(url_for('dashboard.profile'))
    
    return render_template('dashboard/edit_profile.html', user=user)


@bp.route('/profile/change-password', methods=['POST'])
@login_required
def change_password():
    """Altera a senha do usuário logado"""
    user_id = session.get('user_id')
    user = user_service.get_user_by_id(user_id)
    
    if not user:
        return jsonify({'success': False, 'message': 'Usuário não encontrado'}), 404
    
    current_password = request.form.get('current_password', '')
    new_password = request.form.get('new_password', '')
    confirm_password = request.form.get('confirm_password', '')
    
    # Validar senha atual
    if not user.check_password(current_password):
        return jsonify({'success': False, 'message': 'Senha atual incorreta'}), 400
    
    # Validar nova senha
    if len(new_password) < 6:
        return jsonify({'success': False, 'message': 'Nova senha deve ter no mínimo 6 caracteres'}), 400
    
    if new_password != confirm_password:
        return jsonify({'success': False, 'message': 'Confirmação de senha não confere'}), 400
    
    # Atualizar senha
    data = {'password': new_password}
    updated_user, error = user_service.update_user(user_id, data)
    
    if error:
        return jsonify({'success': False, 'message': error}), 400
    
    return jsonify({'success': True, 'message': 'Senha alterada com sucesso!'})


# API endpoints para dashboard
@bp.route('/api/stats')
@login_required
def api_stats():
    """API: Retorna estatísticas do dashboard"""
    try:
        stats = user_service.get_user_statistics()
        return jsonify(stats)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@bp.route('/api/monthly-data')
@login_required
def api_monthly_data():
    """API: Retorna dados mensais para gráficos"""
    try:
        monthly_data = user_service.get_monthly_user_registrations()
        return jsonify(monthly_data)
    except Exception as e:
        return jsonify({'error': str(e)}), 500
