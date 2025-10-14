# Exemplo de Model - User
# 
# from app.models import db
# from datetime import datetime
# 
# 
# class User(db.Model):
#     """Modelo de Usuário"""
#     __tablename__ = 'users'
#     
#     id = db.Column(db.Integer, primary_key=True)
#     username = db.Column(db.String(80), unique=True, nullable=False)
#     email = db.Column(db.String(120), unique=True, nullable=False)
#     password_hash = db.Column(db.String(255), nullable=False)
#     created_at = db.Column(db.DateTime, default=datetime.utcnow)
#     
#     def __repr__(self):
#         return f'<User {self.username}>'
#     
#     def to_dict(self):
#         """Converte o modelo para dicionário"""
#         return {
#             'id': self.id,
#             'username': self.username,
#             'email': self.email,
#             'created_at': self.created_at.isoformat()
#         }

