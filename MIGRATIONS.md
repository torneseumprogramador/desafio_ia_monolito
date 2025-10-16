# 📊 Guia de Migrations - PostgreSQL

Este documento explica como gerenciar as migrations do banco de dados usando Flask-Migrate e Alembic.

## 🔧 Configuração Inicial

### 1. Certifique-se de que o PostgreSQL está rodando

```bash
make db-start
```

### 2. Instale as dependências (se ainda não instalou)

```bash
make install
```

### 3. Configure o arquivo .env

Certifique-se de que seu arquivo `.env` contém a URL de conexão do PostgreSQL:

```env
DATABASE_URL=postgresql://monolito_user:monolito_password@localhost:5432/monolito_db
```

## 🚀 Comandos de Migration

### Usando o Makefile (Recomendado)

#### Inicializar o sistema de migrations
```bash
make db-init
```
Este comando cria a pasta `migrations/` com toda a estrutura necessária.

#### Criar uma nova migration
```bash
# Migration automática (detecta mudanças nos models)
make db-migrate MSG="Criar tabela de usuários"

# Ou sem mensagem (usará mensagem padrão)
make db-migrate
```

#### Aplicar migrations pendentes
```bash
make db-upgrade
```

#### Reverter última migration
```bash
make db-downgrade
```

### Usando o script migrate.sh

Você também pode usar o script interativo:

```bash
# Menu interativo
./migrate.sh

# Ou diretamente por comando
./migrate.sh init
./migrate.sh migrate "Mensagem da migration"
./migrate.sh upgrade
./migrate.sh downgrade
./migrate.sh status
```

### Usando Flask CLI diretamente

```bash
# Ativar o ambiente virtual
source venv/bin/activate

# Inicializar migrations
flask db init

# Criar migration
flask db migrate -m "Descrição da mudança"

# Aplicar migrations
flask db upgrade

# Reverter migration
flask db downgrade

# Ver histórico
flask db history

# Ver migration atual
flask db current
```

## 📝 Fluxo de Trabalho Completo

### Setup Inicial do Projeto

```bash
# 1. Clonar e configurar
git clone <repositorio>
cd monolito
python3 -m venv venv
source venv/bin/activate

# 2. Usar o comando setup (faz tudo automaticamente)
make setup
```

O comando `make setup` faz:
- Instala dependências
- Inicia PostgreSQL
- Inicializa migrations
- Aplica migrations

### Criando uma Nova Entidade

Exemplo: Adicionar uma entidade "Produto"

**1. Criar o Model** (`app/models/produto.py`):

```python
from app.models.user import db
from datetime import datetime

class Produto(db.Model):
    __tablename__ = 'produtos'
    
    id = db.Column(db.Integer, primary_key=True)
    nome = db.Column(db.String(100), nullable=False)
    preco = db.Column(db.Numeric(10, 2), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<Produto {self.nome}>'
```

**2. Importar o Model** em `app/__init__.py`:

```python
# Adicione no topo do arquivo
from app.models.produto import Produto
```

**3. Criar a Migration**:

```bash
make db-migrate MSG="Adicionar tabela produtos"
```

**4. Verificar o arquivo de migration** gerado em `migrations/versions/`:

```python
# O Alembic gera automaticamente algo como:
def upgrade():
    op.create_table('produtos',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('nome', sa.String(length=100), nullable=False),
        sa.Column('preco', sa.Numeric(precision=10, scale=2), nullable=False),
        sa.Column('created_at', sa.DateTime(), nullable=True),
        sa.PrimaryKeyConstraint('id')
    )
```

**5. Aplicar a Migration**:

```bash
make db-upgrade
```

### Modificando uma Entidade Existente

Exemplo: Adicionar campo "estoque" ao Produto

**1. Atualizar o Model**:

```python
class Produto(db.Model):
    # ... campos existentes ...
    estoque = db.Column(db.Integer, default=0, nullable=False)
```

**2. Criar a Migration**:

```bash
make db-migrate MSG="Adicionar campo estoque em produtos"
```

**3. Aplicar a Migration**:

```bash
make db-upgrade
```

## 🔄 Reverter Mudanças

Se algo der errado ou você precisar reverter:

```bash
# Reverter a última migration
make db-downgrade

# Ver histórico de migrations
flask db history

# Reverter para uma versão específica
flask db downgrade <revision_id>
```

## 📋 Boas Práticas

### 1. Sempre Revisar Migrations Automáticas

O Alembic detecta mudanças automaticamente, mas **sempre revise** o arquivo gerado antes de aplicar:

```bash
# Após criar a migration
cat migrations/versions/<arquivo>.py
```

### 2. Mensagens Descritivas

Use mensagens claras e descritivas:

```bash
# ✅ Bom
make db-migrate MSG="Adicionar campo email único na tabela users"

# ❌ Ruim
make db-migrate MSG="update"
```

### 3. Teste Antes de Fazer Commit

```bash
# Criar migration
make db-migrate MSG="Minha alteração"

# Aplicar
make db-upgrade

# Testar a aplicação
make app-start

# Se tudo ok, commit
git add migrations/
git commit -m "Migration: Minha alteração"
```

### 4. Backup Antes de Migrations Importantes

```bash
# Backup do banco (usando Docker)
docker exec monolito_postgres pg_dump -U monolito_user monolito_db > backup.sql

# Restaurar se necessário
docker exec -i monolito_postgres psql -U monolito_user monolito_db < backup.sql
```

### 5. Não Edite Migrations Já Aplicadas

- ❌ **NUNCA** edite uma migration que já foi aplicada em produção
- ✅ Crie uma nova migration para corrigir

## 🐛 Troubleshooting

### Erro: "Can't locate revision identified by..."

```bash
# Limpar o estado das migrations
flask db stamp head
```

### Erro: "Target database is not up to date"

```bash
# Aplicar todas as migrations pendentes
make db-upgrade
```

### Migration não detecta mudanças

```bash
# Verificar se o model está sendo importado
# Adicione o import em app/__init__.py

# Criar migration manualmente
flask db revision -m "Descrição manual"
# Edite o arquivo gerado em migrations/versions/
```

### Resetar completamente o banco

```bash
# ⚠️ CUIDADO: Isso apaga TODOS os dados!

# Parar o banco
make db-stop

# Remover volumes do Docker
docker-compose down -v

# Remover pasta de migrations
rm -rf migrations/

# Iniciar novamente
make db-start
make db-init
make db-migrate MSG="Initial migration"
make db-upgrade
```

## 📚 Estrutura de Arquivos

```
monolito/
├── migrations/                  # Pasta de migrations (gerada)
│   ├── versions/               # Arquivos de migration
│   │   └── xxxx_descrição.py  # Cada migration
│   ├── alembic.ini            # Configuração do Alembic
│   ├── env.py                 # Script de ambiente
│   ├── README                 # README do Alembic
│   └── script.py.mako         # Template para novas migrations
├── app/
│   ├── models/                # Models (entidades)
│   │   └── user.py           # Model de usuário
│   └── __init__.py           # Inicialização do Flask-Migrate
└── migrate.sh                # Script auxiliar de migrations
```

## 🎯 Comandos Rápidos

```bash
# Setup inicial completo
make setup

# Workflow diário
make db-migrate MSG="Minha mudança"
make db-upgrade

# Ver status
flask db current
flask db history

# Emergência (reverter)
make db-downgrade
```

## 🔗 Referências

- [Flask-Migrate Documentation](https://flask-migrate.readthedocs.io/)
- [Alembic Documentation](https://alembic.sqlalchemy.org/)
- [SQLAlchemy Documentation](https://docs.sqlalchemy.org/)

