# 🐘 Setup PostgreSQL e Migrations

## 🎯 Início Rápido

### Opção 1: Setup Automático (Recomendado)

```bash
# 1. Criar e ativar ambiente virtual
python3 -m venv venv
source venv/bin/activate  # Linux/Mac
# ou: venv\Scripts\activate  # Windows

# 2. Setup completo (instala tudo, inicia banco, cria migrations)
make setup

# 3. Iniciar aplicação
make app-start
```

Pronto! Acesse http://localhost:5000

### Opção 2: Passo a Passo Manual

```bash
# 1. Ativar ambiente virtual
source venv/bin/activate

# 2. Instalar dependências com PostgreSQL
pip install -r requirements.txt

# 3. Iniciar PostgreSQL via Docker
make db-start

# 4. Inicializar sistema de migrations
make db-init

# 5. Criar migration inicial
make db-migrate MSG="Initial migration - criar tabela users"

# 6. Aplicar migration
make db-upgrade

# 7. Iniciar aplicação
make app-start
```

## 📦 Dependências Instaladas

As seguintes dependências foram adicionadas ao `requirements.txt`:

```txt
Flask==3.0.3
Flask-SQLAlchemy==3.1.1
Flask-Migrate==4.0.5          # ← Sistema de migrations
python-dotenv==1.0.1
psycopg2-binary==2.9.9        # ← Driver PostgreSQL
alembic==1.13.1               # ← Engine de migrations
```

## 🔧 Configuração do PostgreSQL

### Variáveis de Ambiente (.env)

Crie um arquivo `.env` na raiz do projeto:

```env
# Flask
SECRET_KEY=dev-secret-key-change-in-production
DEBUG=True
FLASK_ENV=development

# PostgreSQL
DATABASE_URL=postgresql://monolito_user:monolito_password@localhost:5432/monolito_db
```

### Credenciais do Banco

Definidas no `docker-compose.yml`:

- **Database**: `monolito_db`
- **User**: `monolito_user`
- **Password**: `monolito_password`
- **Host**: `localhost`
- **Port**: `5432`

### pgAdmin (Interface Gráfica)

Acesse: http://localhost:5050

- **Email**: `admin@monolito.com`
- **Password**: `admin123`

## 📊 Comandos de Migrations

### Comandos Make (Mais Fácil)

```bash
make db-init          # Inicializa migrations (primeira vez)
make db-migrate       # Cria nova migration
make db-upgrade       # Aplica migrations
make db-downgrade     # Reverte última migration
```

### Com Mensagem Personalizada

```bash
make db-migrate MSG="Adicionar campo telefone"
```

### Comandos Flask CLI (Direto)

```bash
flask db init                           # Inicializa
flask db migrate -m "Mensagem"         # Cria migration
flask db upgrade                        # Aplica
flask db downgrade                      # Reverte
flask db current                        # Versão atual
flask db history                        # Histórico
```

## 🗄️ Estrutura Criada

Após executar `make db-init`, será criada a estrutura:

```
monolito/
├── migrations/              # ← Sistema de migrations
│   ├── versions/           # ← Arquivos de migration (.py)
│   ├── alembic.ini
│   ├── env.py
│   ├── README
│   └── script.py.mako
├── app/
│   ├── models/
│   │   └── user.py        # ← Entidade User
│   └── __init__.py        # ← Flask-Migrate configurado
└── docker-compose.yml     # ← PostgreSQL configurado
```

## ✅ Verificar se Está Funcionando

### 1. Verificar PostgreSQL

```bash
make db-status

# Deve mostrar:
# NAME                  COMMAND                  SERVICE    STATUS
# monolito_postgres    "docker-entrypoint.s…"   postgres   Up
# monolito_pgadmin     "/entrypoint.sh"         pgadmin    Up
```

### 2. Verificar Conexão

```bash
# Testar conexão com o banco
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db -c "\dt"

# Deve listar as tabelas (incluindo 'users' após migration)
```

### 3. Verificar Migrations

```bash
flask db current

# Deve mostrar a versão atual da migration
```

### 4. Verificar Tabelas no Banco

```bash
# Via Docker
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db

# No prompt do PostgreSQL:
\dt              # Listar tabelas
\d users         # Descrever tabela users
SELECT * FROM users;  # Ver dados
\q               # Sair
```

## 🔄 Workflow Completo

### Ao Criar uma Nova Entidade

```bash
# 1. Criar o model em app/models/produto.py
# 2. Importar em app/__init__.py
# 3. Criar migration
make db-migrate MSG="Adicionar tabela produtos"

# 4. Verificar o arquivo gerado
cat migrations/versions/<arquivo>.py

# 5. Aplicar
make db-upgrade

# 6. Verificar no banco
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db -c "\d produtos"
```

## 🐛 Problemas Comuns

### Erro: "relation does not exist"

```bash
# Aplicar migrations
make db-upgrade
```

### Erro: "Can't locate revision"

```bash
# Marcar como atualizado
flask db stamp head
```

### PostgreSQL não inicia

```bash
# Ver logs
make db-logs

# Reiniciar
make db-restart

# Recriar (apaga dados!)
make db-stop
docker-compose down -v
make db-start
```

### Migration não detecta mudanças

```bash
# Verificar se o model está importado em app/__init__.py
# Criar migration manualmente
flask db revision -m "Descrição"
```

## 📝 Arquivos Modificados

Os seguintes arquivos foram criados/modificados para suportar PostgreSQL:

### Criados:
- ✅ `docker-compose.yml` - PostgreSQL + pgAdmin
- ✅ `init.sql` - Script de inicialização
- ✅ `Makefile` - Comandos facilitadores
- ✅ `migrate.sh` - Script de migrations
- ✅ `MIGRATIONS.md` - Documentação completa
- ✅ `POSTGRES_SETUP.md` - Este arquivo

### Modificados:
- ✅ `requirements.txt` - Adicionadas dependências
- ✅ `app/__init__.py` - Configurado Flask-Migrate
- ✅ `app/config.py` - Suporte a PostgreSQL
- ✅ `app/models/user.py` - Entidade User completa
- ✅ `app/repositories/user_repository.py` - Repositório
- ✅ `app/services/user_service.py` - Serviço

## 🎓 Próximos Passos

1. **Executar o setup**: `make setup`
2. **Acessar a aplicação**: http://localhost:5000/users/
3. **Criar usuários** pelo formulário
4. **Ver no pgAdmin**: http://localhost:5050
5. **Ler MIGRATIONS.md** para entender migrations em detalhes

## 📚 Documentação Adicional

- **MIGRATIONS.md** - Guia completo de migrations
- **SETUP.md** - Guia geral da aplicação
- **README.md** - Visão geral do projeto

## 💡 Dicas

1. Use `make help` para ver todos os comandos
2. Sempre revise migrations antes de aplicar
3. Faça backup antes de migrations importantes
4. Use mensagens descritivas nas migrations
5. Teste localmente antes de fazer commit

## 🚀 Comando Único

Se quiser apenas rodar tudo de uma vez:

```bash
# Iniciar do zero
python3 -m venv venv && \
source venv/bin/activate && \
make setup && \
make app-start
```

Pronto! Sistema completo com PostgreSQL rodando! 🎉

