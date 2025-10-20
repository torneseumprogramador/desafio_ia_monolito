.PHONY: help db-start db-stop db-restart db-logs db-status db-init db-migrate db-upgrade db-downgrade app-start app-stop install setup clean test test-unit test-integration test-cov test-cov-html

# Variáveis
PYTHON = python3
VENV = venv
FLASK_APP = run.py

# Comando padrão
help:
	@echo "Comandos disponíveis:"
	@echo "  make install       - Instala as dependências do projeto"
	@echo "  make setup         - Configura o ambiente completo (instala dependências + inicia banco + migrations)"
	@echo ""
	@echo "Banco de Dados:"
	@echo "  make db-start      - Inicia o banco de dados PostgreSQL"
	@echo "  make db-stop       - Para o banco de dados PostgreSQL"
	@echo "  make db-restart    - Reinicia o banco de dados PostgreSQL"
	@echo "  make db-logs       - Mostra os logs do banco de dados"
	@echo "  make db-status     - Verifica o status dos containers"
	@echo ""
	@echo "Migrations:"
	@echo "  make db-init       - Inicializa o sistema de migrations"
	@echo "  make db-migrate    - Cria uma nova migration (use MSG='mensagem')"
	@echo "  make db-upgrade    - Aplica as migrations pendentes"
	@echo "  make db-downgrade  - Desfaz a última migration"
	@echo ""
	@echo "Aplicação:"
	@echo "  make app-start     - Inicia a aplicação Flask"
	@echo "  make app-stop      - Para a aplicação Flask"
	@echo ""
	@echo "Manutenção:"
	@echo "  make clean         - Remove arquivos temporários e cache"
	@echo ""
	@echo "Testes:"
	@echo "  make test          - Roda testes unitários e de integração"
	@echo "  make test-unit     - Roda apenas testes unitários"
	@echo "  make test-integration - Roda apenas testes de integração"
	@echo "  make test-cov      - Roda testes com cobertura no terminal"
	@echo "  make test-cov-html - Roda testes com cobertura e gera htmlcov/"

# Instalar dependências
install:
	@echo "📦 Instalando dependências..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && pip install --trusted-host pypi.org --trusted-host pypi.python.org --trusted-host files.pythonhosted.org -r requirements.txt
	@echo "✅ Dependências instaladas com sucesso!"

# Setup completo
setup: install db-start db-init db-upgrade
	@echo "✅ Ambiente configurado com sucesso!"
	@echo "📊 Banco de dados migrado e pronto para uso"
	@echo "Acesse pgAdmin em: http://localhost:5050"
	@echo "  Email: admin@monolito.com"
	@echo "  Senha: admin123"

# Iniciar banco de dados
db-start:
	@echo "🚀 Iniciando banco de dados PostgreSQL..."
	@docker-compose up -d
	@echo "✅ PostgreSQL iniciado!"
	@echo "📊 Banco disponível em: localhost:5432"
	@echo "🔧 pgAdmin disponível em: http://localhost:5050"

# Parar banco de dados
db-stop:
	@echo "🛑 Parando banco de dados PostgreSQL..."
	@docker-compose down
	@echo "✅ PostgreSQL parado!"

# Reiniciar banco de dados
db-restart:
	@echo "🔄 Reiniciando banco de dados PostgreSQL..."
	@docker-compose restart
	@echo "✅ PostgreSQL reiniciado!"

# Ver logs do banco de dados
db-logs:
	@docker-compose logs -f postgres

# Status dos containers
db-status:
	@docker-compose ps

# Inicializar migrations
db-init:
	@echo "🔧 Inicializando sistema de migrations..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@if [ ! -d "migrations" ]; then \
		. $(VENV)/bin/activate && flask db init; \
		echo "✅ Sistema de migrations inicializado!"; \
	else \
		echo "⚠️  Sistema de migrations já existe"; \
	fi

# Criar nova migration
db-migrate:
	@echo "📝 Criando nova migration..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && flask db migrate -m "$(or $(MSG),Auto migration)"
	@echo "✅ Migration criada com sucesso!"

# Aplicar migrations
db-upgrade:
	@echo "⬆️  Aplicando migrations..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && flask db upgrade
	@echo "✅ Migrations aplicadas com sucesso!"

# Reverter última migration
db-downgrade:
	@echo "⬇️  Revertendo última migration..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && flask db downgrade
	@echo "✅ Migration revertida com sucesso!"

# Iniciar aplicação
app-start:
	@echo "🚀 Iniciando aplicação Flask..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && $(PYTHON) $(FLASK_APP)

# Parar aplicação (informativo)
app-stop:
	@echo "Para parar a aplicação, pressione Ctrl+C no terminal onde ela está rodando"

# Limpar arquivos temporários
clean:
	@echo "🧹 Limpando arquivos temporários..."
	@find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
	@find . -type f -name "*.pyc" -delete 2>/dev/null || true
	@find . -type f -name "*.pyo" -delete 2>/dev/null || true
	@find . -type d -name "*.egg-info" -exec rm -rf {} + 2>/dev/null || true
	@echo "✅ Limpeza concluída!"

# Testes
test:
	@echo "🧪 Rodando toda a suíte de testes (unit + integration)..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && python -m pytest tests/ -v

test-unit:
	@echo "🧪 Rodando testes unitários..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && python -m pytest tests/unit/ -v

test-integration:
	@echo "🧪 Rodando testes de integração..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && python -m pytest tests/integration/ -v

test-cov:
	@echo "🧪 Rodando testes com cobertura (terminal)..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && python -m pytest tests/ -v --cov=app --cov-report=term-missing

test-cov-html:
	@echo "🧪 Rodando testes com cobertura (HTML em htmlcov/)..."
	@if [ ! -d "$(VENV)" ]; then \
		echo "⚠️  Virtual environment não encontrado. Execute: python3 -m venv venv"; \
		exit 1; \
	fi
	@. $(VENV)/bin/activate && python -m pytest tests/ -v --cov=app --cov-report=html --cov-report=term-missing

