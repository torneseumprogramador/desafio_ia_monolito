"""
Repositório de Usuários
Camada responsável pelo acesso direto aos dados de usuários
"""
from typing import List, Optional
from app.models.user import User, db


class UserRepository:
    """Repositório para operações de banco de dados relacionadas a usuários"""
    
    @staticmethod
    def create(user: User) -> User:
        """Cria um novo usuário no banco de dados"""
        db.session.add(user)
        db.session.commit()
        return user
    
    @staticmethod
    def find_by_id(user_id: int) -> Optional[User]:
        """Busca um usuário por ID"""
        return User.query.get(user_id)
    
    @staticmethod
    def find_by_email(email: str) -> Optional[User]:
        """Busca um usuário por email"""
        return User.query.filter_by(email=email).first()
    
    @staticmethod
    def find_by_username(username: str) -> Optional[User]:
        """Busca um usuário por username"""
        return User.query.filter_by(username=username).first()
    
    @staticmethod
    def find_all(page: int = 1, per_page: int = 10) -> tuple:
        """
        Retorna todos os usuários com paginação
        Retorna: (lista_usuarios, total_usuarios)
        """
        pagination = User.query.order_by(User.created_at.desc()).paginate(
            page=page, 
            per_page=per_page, 
            error_out=False
        )
        return pagination.items, pagination.total
    
    @staticmethod
    def find_active_users() -> List[User]:
        """Retorna todos os usuários ativos"""
        return User.query.filter_by(is_active=True).order_by(User.created_at.desc()).all()
    
    @staticmethod
    def update(user: User) -> User:
        """Atualiza um usuário existente"""
        db.session.add(user)
        db.session.commit()
        db.session.refresh(user)
        return user
    
    @staticmethod
    def delete(user: User) -> None:
        """Remove um usuário do banco de dados"""
        db.session.delete(user)
        db.session.commit()
    
    @staticmethod
    def exists_by_email(email: str, exclude_id: Optional[int] = None) -> bool:
        """Verifica se já existe um usuário com o email informado"""
        query = User.query.filter_by(email=email)
        if exclude_id:
            query = query.filter(User.id != exclude_id)
        return query.first() is not None
    
    @staticmethod
    def exists_by_username(username: str, exclude_id: Optional[int] = None) -> bool:
        """Verifica se já existe um usuário com o username informado"""
        query = User.query.filter_by(username=username)
        if exclude_id:
            query = query.filter(User.id != exclude_id)
        return query.first() is not None
    
    @staticmethod
    def count() -> int:
        """Retorna o total de usuários cadastrados"""
        return User.query.count()

