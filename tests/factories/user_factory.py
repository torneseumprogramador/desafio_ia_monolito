"""
Factory para criação de usuários de teste
"""
import factory
from app.models.user import User


class UserFactory(factory.Factory):
    """Factory para criar usuários de teste"""
    
    class Meta:
        model = User
    
    name = factory.Faker('name')
    email = factory.Faker('email')
    username = factory.Faker('user_name')
    phone = factory.Faker('phone_number')
    is_active = True
    
    @factory.post_generation
    def password(obj, create, extracted, **kwargs):
        """Define senha padrão se não fornecida"""
        if extracted:
            obj.set_password(extracted)
        else:
            obj.set_password('testpassword')
