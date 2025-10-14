# Aplicação Flask - Arquitetura MVC

Uma aplicação web monolítica desenvolvida com Flask seguindo o padrão **MVC (Model-View-Controller)**.

## 📋 Estrutura do Projeto (MVC)

```
monolito/
├── app/                      # Pasta principal da aplicação
│   ├── __init__.py          # Application Factory
│   ├── controllers/         # Controllers (Rotas/Lógica de controle)
│   │   ├── __init__.py
│   │   └── main_controller.py
│   ├── models/              # Models (Modelos de dados)
│   │   ├── __init__.py
│   │   └── user.py
│   ├── services/            # Services (Lógica de negócio)
│   │   ├── __init__.py
│   │   └── user_service.py
│   ├── utils/               # Utils (Funções auxiliares)
│   │   ├── __init__.py
│   │   └── helpers.py
│   ├── templates/           # Views (Templates HTML)
│   │   ├── layouts/
│   │   │   └── base.html    # Layout base para uso nas outras views
│   │   └── main/
│   │       ├── index.html   # Página inicial do site
│   │       └── about.html   # Página sobre
│   └── static/              # Arquivos estáticos
│       ├── css/
│       │   └── style.css
│       └── js/
│           └── main.js
├── run.py                   # Arquivo principal para executar
├── requirements.txt         # Dependências
└── README.md                # Este arquivo
```

## 🏗️ Arquitetura MVC

### **Models** (Modelos)
- Representam a estrutura de dados
- Interagem com o banco de dados
- Localização: `app/models/`

### **Views** (Visualizações)
- Templates HTML (Jinja2)
- Interface com o usuário
- Localização: `app/templates/`

### **Controllers** (Controladores)
- Recebem requisições HTTP
- Processam dados
- Chamam services quando necessário
- Retornam respostas
- Localização: `app/controllers/`

### **Services** (Serviços)
- Contêm a lógica de negócio
- São chamados pelos controllers
- Interagem com models
- Localização: `app/services/`

### **Utils** (Utilitários)
- Funções auxiliares
- Helpers e validações
- Localização: `app/utils/`

## 🚀 Como Executar

### 1. Criar ambiente virtual (se ainda não existe)

```bash
python3 -m venv venv
source venv/bin/activate  # No Windows: venv\Scripts\activate
```

### 2. Instalar dependências

```bash
pip install -r requirements.txt
```

### 3. Executar a aplicação

**Opção 1: Usando o script**
```bash
./run.sh
```

**Opção 2: Manualmente**
```bash
source venv/bin/activate
python run.py
```

**Opção 3: Com Flask CLI**
```bash
source venv/bin/activate
export FLASK_APP=run.py
flask run
```

A aplicação estará disponível em: `http://localhost:5000`

## 🛠️ Tecnologias Utilizadas

- **Python 3.13+**
- **Flask 3.0** - Framework web
- **Jinja2** - Engine de templates
- **SQLAlchemy** - ORM (opcional, já configurado)

## 📦 Padrões Utilizados

- **Application Factory Pattern** - Para criar a aplicação
- **Blueprints** - Para organizar rotas
- **MVC Architecture** - Separação de responsabilidades
- **Service Layer** - Lógica de negócio separada

## 🔄 Fluxo de uma Requisição

```
1. Usuário acessa URL
   ↓
2. Controller recebe a requisição (routes)
   ↓
3. Controller chama Service (se necessário)
   ↓
4. Service processa lógica de negócio
   ↓
5. Service interage com Model (banco de dados)
   ↓
6. Controller renderiza View (template)
   ↓
7. HTML é retornado ao usuário
```

## 📝 Como Adicionar Novas Funcionalidades

### 1. Criar um novo Controller

```python
# app/controllers/user_controller.py
from flask import Blueprint, render_template

bp = Blueprint('users', __name__, url_prefix='/users')

@bp.route('/')
def list_users():
    return render_template('users/list.html')
```

### 2. Registrar o Blueprint

```python
# app/__init__.py
from app.controllers import user_controller

app.register_blueprint(user_controller.bp)
```

### 3. Criar um Service (opcional)

```python
# app/services/user_service.py
class UserService:
    @staticmethod
    def get_all_users():
        # Lógica para buscar usuários
        pass
```

### 4. Criar um Model (opcional)

```python
# app/models/user.py
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80))
```

## 🧪 Testes

Para adicionar testes:

```bash
pip install pytest
pytest tests/
```

## 📄 Licença

Este projeto está sob a licença MIT.
