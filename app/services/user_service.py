"""
Serviço de Usuários
Camada de lógica de negócios para usuários
"""
from typing import List, Optional, Dict, Any
from app.models.user import User
from app.repositories.user_repository import UserRepository


class UserService:
    """Serviço para lógica de negócios relacionada a usuários"""
    
    def __init__(self):
        self.repository = UserRepository()
    
    def create_user(self, data: Dict[str, Any]) -> tuple[Optional[User], Optional[str]]:
        """
        Cria um novo usuário
        Retorna: (usuario, erro)
        """
        # Validações
        if not data.get('name'):
            return None, 'Nome é obrigatório'
        
        if not data.get('email'):
            return None, 'Email é obrigatório'
        
        if not data.get('username'):
            return None, 'Username é obrigatório'
        
        if not data.get('password'):
            return None, 'Senha é obrigatória'
        
        # Verificar se email já existe
        if self.repository.exists_by_email(data['email']):
            return None, 'Email já cadastrado'
        
        # Verificar se username já existe
        if self.repository.exists_by_username(data['username']):
            return None, 'Username já cadastrado'
        
        # Criar usuário
        try:
            user = User(
                name=data['name'],
                email=data['email'],
                username=data['username'],
                phone=data.get('phone', ''),
                is_active=data.get('is_active', True)
            )
            user.set_password(data['password'])
            
            created_user = self.repository.create(user)
            return created_user, None
        except Exception as e:
            return None, f'Erro ao criar usuário: {str(e)}'
    
    def get_user_by_id(self, user_id: int) -> Optional[User]:
        """Busca usuário por ID"""
        return self.repository.find_by_id(user_id)
    
    def get_user_by_email(self, email: str) -> Optional[User]:
        """Busca usuário por email"""
        return self.repository.find_by_email(email)
    
    def get_user_by_username(self, username: str) -> Optional[User]:
        """Busca usuário por username"""
        return self.repository.find_by_username(username)
    
    def get_all_users(self, page: int = 1, per_page: int = 10) -> tuple:
        """
        Retorna todos os usuários com paginação
        Retorna: (lista_usuarios, total_usuarios)
        """
        return self.repository.find_all(page, per_page)
    
    def get_active_users(self) -> List[User]:
        """Retorna todos os usuários ativos"""
        return self.repository.find_active_users()
    
    def update_user(self, user_id: int, data: Dict[str, Any]) -> tuple[Optional[User], Optional[str]]:
        """
        Atualiza um usuário existente
        Retorna: (usuario, erro)
        """
        user = self.repository.find_by_id(user_id)
        if not user:
            return None, 'Usuário não encontrado'
        
        # Validações
        if 'email' in data and data['email'] != user.email:
            if self.repository.exists_by_email(data['email'], exclude_id=user_id):
                return None, 'Email já cadastrado'
        
        if 'username' in data and data['username'] != user.username:
            if self.repository.exists_by_username(data['username'], exclude_id=user_id):
                return None, 'Username já cadastrado'
        
        # Atualizar dados
        try:
            user.update_from_dict(data)
            updated_user = self.repository.update(user)
            return updated_user, None
        except Exception as e:
            return None, f'Erro ao atualizar usuário: {str(e)}'
    
    def delete_user(self, user_id: int) -> tuple[bool, Optional[str]]:
        """
        Remove um usuário
        Retorna: (sucesso, erro)
        """
        user = self.repository.find_by_id(user_id)
        if not user:
            return False, 'Usuário não encontrado'
        
        try:
            self.repository.delete(user)
            return True, None
        except Exception as e:
            return False, f'Erro ao deletar usuário: {str(e)}'
    
    def toggle_user_status(self, user_id: int) -> tuple[Optional[User], Optional[str]]:
        """
        Ativa/Desativa um usuário
        Retorna: (usuario, erro)
        """
        user = self.repository.find_by_id(user_id)
        if not user:
            return None, 'Usuário não encontrado'
        
        try:
            user.is_active = not user.is_active
            updated_user = self.repository.update(user)
            return updated_user, None
        except Exception as e:
            return None, f'Erro ao alterar status: {str(e)}'
    
    def count_users(self) -> int:
        """Retorna o total de usuários cadastrados"""
        return self.repository.count()
    
    def authenticate(self, username_or_email: str, password: str) -> tuple[Optional[User], Optional[str]]:
        """
        Autentica um usuário com username/email e senha
        Retorna: (usuario, erro)
        """
        # Tentar buscar por username ou email
        user = self.repository.find_by_username(username_or_email)
        if not user:
            user = self.repository.find_by_email(username_or_email)
        
        if not user:
            return None, 'Usuário ou senha inválidos'
        
        if not user.is_active:
            return None, 'Usuário inativo. Entre em contato com o administrador.'
        
        if not user.check_password(password):
            return None, 'Usuário ou senha inválidos'
        
        return user, None
    
    def get_user_statistics(self) -> Dict[str, Any]:
        """
        Retorna estatísticas dos usuários para o dashboard
        """
        from app.models.user import db
        from sqlalchemy import func
        from datetime import datetime, timedelta
        
        try:
            # Total de usuários
            total_users = self.repository.count()
            
            # Usuários ativos
            active_users = db.session.query(User).filter(User.is_active == True).count()
            
            # Usuários inativos
            inactive_users = total_users - active_users
            
            # Usuários criados hoje
            today = datetime.utcnow().date()
            users_today = db.session.query(User).filter(
                func.date(User.created_at) == today
            ).count()
            
            # Usuários criados na última semana
            week_ago = datetime.utcnow() - timedelta(days=7)
            users_week = db.session.query(User).filter(
                User.created_at >= week_ago
            ).count()
            
            # Usuários criados no último mês
            month_ago = datetime.utcnow() - timedelta(days=30)
            users_month = db.session.query(User).filter(
                User.created_at >= month_ago
            ).count()
            
            return {
                'total_users': total_users,
                'active_users': active_users,
                'inactive_users': inactive_users,
                'users_today': users_today,
                'users_week': users_week,
                'users_month': users_month,
                'active_percentage': round((active_users / total_users * 100), 1) if total_users > 0 else 0
            }
        except Exception as e:
            return {
                'total_users': 0,
                'active_users': 0,
                'inactive_users': 0,
                'users_today': 0,
                'users_week': 0,
                'users_month': 0,
                'active_percentage': 0
            }
    
    def get_monthly_user_registrations(self, months: int = 6) -> List[Dict[str, Any]]:
        """
        Retorna dados de registros mensais para gráficos
        """
        from app.models.user import db
        from sqlalchemy import func
        from datetime import datetime, timedelta
        import calendar
        
        try:
            # Calcular data inicial (X meses atrás)
            end_date = datetime.utcnow()
            start_date = end_date - timedelta(days=months * 30)
            
            # Query para contar usuários por mês
            monthly_counts = db.session.query(
                func.extract('year', User.created_at).label('year'),
                func.extract('month', User.created_at).label('month'),
                func.count(User.id).label('count')
            ).filter(
                User.created_at >= start_date
            ).group_by(
                func.extract('year', User.created_at),
                func.extract('month', User.created_at)
            ).order_by('year', 'month').all()
            
            # Criar dicionário com os dados
            monthly_data = {}
            for row in monthly_counts:
                key = f"{int(row.year)}-{int(row.month):02d}"
                monthly_data[key] = int(row.count)
            
            # Preencher meses sem dados com 0
            result = []
            current_date = start_date.replace(day=1)
            
            while current_date <= end_date.replace(day=1):
                key = current_date.strftime('%Y-%m')
                month_name = calendar.month_name[current_date.month][:3]  # Jan, Feb, etc.
                
                result.append({
                    'month': month_name,
                    'year': current_date.year,
                    'count': monthly_data.get(key, 0),
                    'full_date': current_date.strftime('%Y-%m')
                })
                
                # Próximo mês
                if current_date.month == 12:
                    current_date = current_date.replace(year=current_date.year + 1, month=1)
                else:
                    current_date = current_date.replace(month=current_date.month + 1)
            
            return result
            
        except Exception as e:
            return []