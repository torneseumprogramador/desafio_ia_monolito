"""
Controller do Dashboard
Responsável pelas rotas do dashboard com estatísticas e gráficos
"""
from flask import Blueprint, render_template, jsonify
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
        from flask import flash, redirect, url_for
        flash(f'Erro ao carregar dashboard: {str(e)}', 'danger')
        return redirect(url_for('main.index'))


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
