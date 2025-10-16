# ⚡ Comandos Rápidos

## 🚀 Setup Inicial (Primeira Vez)

```bash
# 1. Criar ambiente virtual
python3 -m venv venv
source venv/bin/activate

# 2. Setup completo (faz tudo automaticamente)
make setup

# 3. Iniciar aplicação
make app-start
```

**Acesse**: http://localhost:5000

---

## 📊 Banco de Dados

```bash
make db-start        # Iniciar PostgreSQL
make db-stop         # Parar PostgreSQL
make db-restart      # Reiniciar PostgreSQL
make db-status       # Ver status
make db-logs         # Ver logs
```

---

## 🔄 Migrations

```bash
make db-init                              # Inicializar (primeira vez)
make db-migrate MSG="Descrição"          # Criar migration
make db-upgrade                           # Aplicar migrations
make db-downgrade                         # Reverter última
```

---

## 💻 Aplicação

```bash
make app-start       # Iniciar aplicação Flask
make app-stop        # Parar aplicação (Ctrl+C)
```

---

## 🛠️ Manutenção

```bash
make install         # Instalar dependências
make clean          # Limpar cache
make help           # Ver todos os comandos
```

---

## 🌐 URLs Úteis

- **Aplicação**: http://localhost:5000
- **Usuários**: http://localhost:5000/users/
- **pgAdmin**: http://localhost:5050
  - Email: `admin@monolito.com`
  - Senha: `admin123`

---

## 📝 Workflow Diário

```bash
# 1. Ativar ambiente
source venv/bin/activate

# 2. Garantir que o banco está rodando
make db-status

# 3. Se não estiver, iniciar
make db-start

# 4. Iniciar aplicação
make app-start
```

---

## 🆕 Adicionar Nova Entidade

```bash
# 1. Criar model em app/models/
# 2. Importar em app/__init__.py
# 3. Criar migration
make db-migrate MSG="Adicionar tabela X"

# 4. Aplicar
make db-upgrade

# 5. Verificar
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db -c "\dt"
```

---

## 🐛 Resolver Problemas

```bash
# PostgreSQL não conecta
make db-restart

# Migration com erro
make db-downgrade

# Reinstalar dependências
pip install -r requirements.txt --force-reinstall

# Resetar tudo (⚠️ APAGA DADOS!)
make db-stop
docker-compose down -v
rm -rf migrations/
make setup
```

---

## 📦 Comandos Docker Úteis

```bash
# Ver containers rodando
docker ps

# Acessar PostgreSQL
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db

# Ver logs do PostgreSQL
docker logs monolito_postgres

# Parar tudo
docker-compose down

# Parar e remover volumes (apaga dados)
docker-compose down -v
```

---

## 💾 Backup e Restore

```bash
# Backup
docker exec monolito_postgres pg_dump -U monolito_user monolito_db > backup_$(date +%Y%m%d).sql

# Restore
docker exec -i monolito_postgres psql -U monolito_user monolito_db < backup_20241016.sql
```

---

## 🧪 Testes Rápidos

```bash
# Testar conexão com banco
docker exec monolito_postgres pg_isready -U monolito_user

# Ver tabelas
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db -c "\dt"

# Ver usuários cadastrados
docker exec -it monolito_postgres psql -U monolito_user -d monolito_db -c "SELECT id, name, email, username FROM users;"
```

---

## 🎯 Comandos Mais Usados

```bash
make setup          # Setup inicial completo
make db-start       # Iniciar PostgreSQL
make app-start      # Iniciar aplicação
make db-migrate     # Criar migration
make db-upgrade     # Aplicar migration
```

---

## 📚 Documentação Completa

- `POSTGRES_SETUP.md` - Setup do PostgreSQL
- `MIGRATIONS.md` - Guia completo de migrations
- `SETUP.md` - Configuração geral
- `README.md` - Visão geral do projeto

