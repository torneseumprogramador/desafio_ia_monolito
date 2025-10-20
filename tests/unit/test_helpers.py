"""
Testes unitários para utilitários em app/utils/helpers.py
"""
import pytest
from datetime import datetime

from app.utils.helpers import format_date, validate_email, slugify


class TestHelpers:
    """Testes para helpers utilitários"""

    # format_date
    def test_format_date_with_datetime(self):
        date = datetime(2025, 10, 20)
        assert format_date(date) == '20/10/2025'

    def test_format_date_with_string_iso(self):
        assert format_date('2025-10-20T15:30:00') == '20/10/2025'

    def test_format_date_custom_format(self):
        date = datetime(2025, 1, 5)
        assert format_date(date, '%Y-%m-%d') == '2025-01-05'

    def test_format_date_invalid_string_raises(self):
        with pytest.raises(ValueError):
            format_date('20/10/2025')  # não é ISO, deve falhar

    # validate_email
    @pytest.mark.parametrize(
        'email',
        [
            'user@example.com',
            'USER+label@sub.domain.co',
            'a_b.c-d@domain.io',
        ],
    )
    def test_validate_email_true(self, email):
        assert validate_email(email) is True

    @pytest.mark.parametrize(
        'email',
        [
            'plainaddress',
            'user@',
            '@domain.com',
            'user@domain',
            'user@domain.c',  # TLD muito curto
            'user@@domain.com',
            'user domain.com',
        ],
    )
    def test_validate_email_false(self, email):
        assert validate_email(email) is False

    # slugify
    @pytest.mark.parametrize(
        'text,expected',
        [
            ('Hello World', 'hello-world'),
            ('  múltiplos   espaços  ', 'm-ltiplos-espa-os'),  # acentos viram separadores
            ('Café & Chá!', 'caf-ch'),
            ('---Slug__IFY---', 'slug-ify'),
            ('Título com Ácentos e Çedilha', 't-tulo-com-centos-e-edilha'),
            ('123 abc XYZ', '123-abc-xyz'),
            ('__', ''),
        ],
    )
    def test_slugify(self, text, expected):
        assert slugify(text) == expected


