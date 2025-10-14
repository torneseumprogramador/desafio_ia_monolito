from datetime import datetime


def format_date(date, format='%d/%m/%Y'):
    """Formata uma data"""
    if isinstance(date, str):
        date = datetime.fromisoformat(date)
    return date.strftime(format)


def validate_email(email):
    """Valida formato de email básico"""
    import re
    pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
    return re.match(pattern, email) is not None


def slugify(text):
    """Converte texto em slug URL-friendly"""
    import re
    text = text.lower()
    text = re.sub(r'[^a-z0-9]+', '-', text)
    return text.strip('-')

