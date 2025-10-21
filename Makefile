.PHONY: help install run build test clean migrate

help: ## Mostra esta mensagem de ajuda
	@echo "Comandos disponíveis:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

install: ## Instala as dependências do projeto
	@echo "📦 Instalando dependências..."
	mvn clean install

run: ## Executa a aplicação
	@./run.sh

build: ## Compila a aplicação
	@./build.sh

test: ## Executa os testes
	@./test.sh

clean: ## Limpa o projeto
	@./clean.sh

dev: ## Executa em modo de desenvolvimento
	@echo "🚀 Executando em modo de desenvolvimento..."
	mvn spring-boot:run -Dspring-boot.run.profiles=dev

prod: ## Executa em modo de produção
	@echo "🚀 Executando em modo de produção..."
	java -jar target/desafio-ia-monolito-1.0-SNAPSHOT.jar --spring.profiles.active=production

package: ## Gera o JAR da aplicação
	@echo "📦 Gerando JAR..."
	mvn package -DskipTests

docker-build: ## Cria imagem Docker
	@echo "🐳 Criando imagem Docker..."
	docker build -t desafio-ia-monolito .

docker-run: ## Executa container Docker
	@echo "🐳 Executando container Docker..."
	docker-compose up -d

docker-stop: ## Para container Docker
	@echo "🛑 Parando container Docker..."
	docker-compose down

docker-logs: ## Mostra logs do Docker
	@echo "📋 Logs do Docker..."
	docker-compose logs -f

lint: ## Executa verificação de código
	@echo "🔍 Verificando código..."
	mvn checkstyle:check

format: ## Formata o código
	@echo "✨ Formatando código..."
	mvn fmt:format

