# 🚀 Guia de Configuração e Uso

## 📋 Pré-requisitos

- Python 3.8+
- Docker e Docker Compose
- Make (opcional, mas recomendado)

## ⚙️ Configuração Inicial

### 1. Clone o Repositório

```bash
git clone <url-do-repositorio>
cd monolito
```

### 2. Crie o Ambiente Virtual

```bash
python3 -m venv venv
source venv/bin/activate  # Linux/Mac
# ou
venv\Scripts\activate  # Windows
```

### 3. Configure as Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```bash
# Configurações Flask
SECRET_KEY=dev-secret-key-change-in-production
DEBUG=True
FLASK_ENV=development

# Configurações do Banco de Dados PostgreSQL
DATABASE_URL=postgresql://monolito_user:monolito_password@localhost:5432/monolito_db

# Configurações do PostgreSQL (Docker)
POSTGRES_DB=monolito_db
POSTGRES_USER=monolito_user
POSTGRES_PASSWORD=monolito_password
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
```

## 🐳 Banco de Dados PostgreSQL

### Iniciar o PostgreSQL com Docker

```bash
# Usando Makefile
make db-start

# Ou diretamente com Docker Compose
docker-compose up -d
```

### Credenciais do Banco

- **Database**: monolito_db
- **Usuário**: monolito_user
- **Senha**: monolito_password
- **Host**: localhost
- **Porta**: 5432

### pgAdmin (Interface Gráfica)

Acesse: http://localhost:5050

- **Email**: admin@monolito.com
- **Senha**: admin123

**Conectar ao servidor PostgreSQL no pgAdmin:**

1. Clique em "Add New Server"
2. Na aba "General", defina um nome (ex: "Monolito")
3. Na aba "Connection":
   - Host: postgres (se estiver no Docker) ou localhost
   - Port: 5432
   - Database: monolito_db
   - Username: monolito_user
   - Password: monolito_password

### Comandos Úteis do Banco

```bash
# Iniciar
make db-start

# Parar
make db-stop

# Reiniciar
make db-restart

# Ver logs
make db-logs

# Ver status
make db-status
```

## 📦 Instalação de Dependências

```bash
# Usando Makefile
make install

# Ou manualmente
pip install -r requirements.txt
```

## 🎯 Executar a Aplicação

```bash
# Usando Makefile
make app-start

# Ou manualmente
python run.py
```

Acesse: http://localhost:5000

## 🛠️ Comandos do Makefile

### Ver Todos os Comandos

```bash
make help
```

### Setup Completo

```bash
make setup  # Instala dependências + inicia banco
```

### Gerenciar Banco de Dados

```bash
make db-start      # Iniciar PostgreSQL
make db-stop       # Parar PostgreSQL
make db-restart    # Reiniciar PostgreSQL
make db-logs       # Ver logs
make db-status     # Status dos containers
```

### Gerenciar Aplicação

```bash
make app-start     # Iniciar aplicação Flask
make app-stop      # Informações sobre como parar
```

### Manutenção

```bash
make clean         # Limpar arquivos temporários e cache
```

## 👥 CRUD de Usuários

### Funcionalidades Disponíveis

- ✅ Listar usuários (com paginação)
- ✅ Criar novo usuário
- ✅ Visualizar detalhes do usuário
- ✅ Editar usuário
- ✅ Excluir usuário
- ✅ Ativar/Desativar usuário
- ✅ Validações de email e username únicos
- ✅ Hash de senhas com Werkzeug

### Campos do Usuário

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| id | Integer | Auto | ID único |
| name | String(100) | Sim | Nome completo |
| username | String(80) | Sim | Username (único) |
| email | String(120) | Sim | Email (único) |
| phone | String(20) | Não | Telefone |
| password_hash | String(255) | Sim | Senha criptografada |
| is_active | Boolean | Sim | Status ativo/inativo |
| created_at | DateTime | Auto | Data de criação |
| updated_at | DateTime | Auto | Data de atualização |

### Rotas Web

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/users/` | Lista todos os usuários |
| GET | `/users/create` | Formulário de criação |
| POST | `/users/create` | Cria um novo usuário |
| GET | `/users/<id>` | Detalhes do usuário |
| GET | `/users/<id>/edit` | Formulário de edição |
| POST | `/users/<id>/edit` | Atualiza o usuário |
| POST | `/users/<id>/delete` | Remove o usuário |
| POST | `/users/<id>/toggle-status` | Ativa/Desativa usuário |

### Endpoints da API REST

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/users/api` | Lista usuários (JSON) |
| GET | `/users/api/<id>` | Busca usuário por ID (JSON) |

### Exemplo de Uso da API

```bash
# Listar todos os usuários
curl http://localhost:5000/users/api

# Buscar usuário específico
curl http://localhost:5000/users/api/1
```

## 📚 Arquitetura em Camadas

### Fluxo de uma Requisição

```
Cliente (Browser/API)
    ↓
Controller (user_controller.py)
    ↓
Service (user_service.py)
    ↓
Repository (user_repository.py)
    ↓
Model (user.py)
    ↓
Banco de Dados (PostgreSQL)
```

### Camadas

1. **Controller**: Recebe requisições HTTP, valida entrada
2. **Service**: Aplica regras de negócio, validações
3. **Repository**: Acessa o banco de dados
4. **Model**: Define a estrutura da tabela

## 🔧 Desenvolvimento

### Adicionar Nova Entidade

1. **Criar Model** (`app/models/`)
2. **Criar Repository** (`app/repositories/`)
3. **Criar Service** (`app/services/`)
4. **Criar Controller** (`app/controllers/`)
5. **Criar Templates** (`app/templates/`)
6. **Registrar Blueprint** em `app/__init__.py`

### Estrutura de Código

```python
# 1. Model (app/models/produto.py)
class Produto(db.Model):
    __tablename__ = 'produtos'
    id = db.Column(db.Integer, primary_key=True)
    nome = db.Column(db.String(100), nullable=False)

# 2. Repository (app/repositories/produto_repository.py)
class ProdutoRepository:
    @staticmethod
    def find_all():
        return Produto.query.all()

# 3. Service (app/services/produto_service.py)
class ProdutoService:
    def __init__(self):
        self.repository = ProdutoRepository()
    
    def get_all_produtos(self):
        return self.repository.find_all()

# 4. Controller (app/controllers/produto_controller.py)
@bp.route('/')
def index():
    produtos = produto_service.get_all_produtos()
    return render_template('produtos/index.html', produtos=produtos)
```

## 🐛 Troubleshooting

### Erro de conexão com o banco

```bash
# Verifique se o PostgreSQL está rodando
make db-status

# Verifique os logs
make db-logs

# Reinicie o banco
make db-restart
```

### Erro "ModuleNotFoundError"

```bash
# Verifique se o ambiente virtual está ativado
source venv/bin/activate

# Reinstale as dependências
pip install -r requirements.txt
```

### Porta 5000 já em uso

```bash
# No arquivo run.py, altere a porta:
app.run(debug=True, port=5001)
```

## 📝 Notas Importantes

1. **Nunca commite o arquivo `.env`** - ele contém informações sensíveis
2. **Sempre use o ambiente virtual** - evita conflitos de dependências
3. **Sempre faça backup do banco de dados** antes de fazer alterações estruturais
4. **Use senhas fortes** em produção
5. **Desative o DEBUG** em produção

## 🎓 Próximos Passos

- [ ] Implementar autenticação e autorização
- [ ] Adicionar testes unitários
- [ ] Configurar CI/CD
- [ ] Adicionar mais validações de formulários
- [ ] Implementar sistema de logs
- [ ] Adicionar paginação em todas as listagens
- [ ] Criar dashboard administrativo

## 📞 Suporte

Para dúvidas ou problemas, consulte a documentação do Flask:
- https://flask.palletsprojects.com/
- https://flask-sqlalchemy.palletsprojects.com/

