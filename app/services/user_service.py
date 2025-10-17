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