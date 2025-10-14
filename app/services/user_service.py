# Exemplo de Service - UserService
# 
# from app.models.user import User
# from app.models import db
# from werkzeug.security import generate_password_hash, check_password_hash
# 
# 
# class UserService:
#     """Service para gerenciar usuários"""
#     
#     @staticmethod
#     def create_user(username, email, password):
#         """Cria um novo usuário"""
#         password_hash = generate_password_hash(password)
#         user = User(
#             username=username,
#             email=email,
#             password_hash=password_hash
#         )
#         db.session.add(user)
#         db.session.commit()
#         return user
#     
#     @staticmethod
#     def get_user_by_id(user_id):
#         """Busca usuário por ID"""
#         return User.query.get(user_id)
#     
#     @staticmethod
#     def get_user_by_email(email):
#         """Busca usuário por email"""
#         return User.query.filter_by(email=email).first()
#     
#     @staticmethod
#     def verify_password(user, password):
#         """Verifica se a senha está correta"""
#         return check_password_hash(user.password_hash, password)

