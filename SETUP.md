# Guia de Configuração - Aplicação Java Spring Boot

## Instalação do Ambiente

### 1. Instalar Java 17+

#### macOS
```bash
brew install openjdk@17
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Windows
Baixe e instale do site oficial da Oracle ou use:
```bash
choco install openjdk17
```

### 2. Instalar Maven

#### macOS
```bash
brew install maven
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install maven
```

#### Windows
```bash
choco install maven
```

### 3. Verificar Instalação

```bash
java -version
# Deve mostrar: openjdk version "17.x.x"

mvn -version
# Deve mostrar: Apache Maven 3.x.x
```

## Configuração do Banco de Dados

### Desenvolvimento (H2)
Não requer configuração. O H2 será criado automaticamente em `./instance/app.mv.db`

### Produção (PostgreSQL)

#### 1. Instalar PostgreSQL

##### macOS
```bash
brew install postgresql
brew services start postgresql
```

##### Linux (Ubuntu/Debian)
```bash
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

##### Windows
Baixe do site oficial: https://www.postgresql.org/download/windows/

#### 2. Criar Banco de Dados

```bash
# Conectar ao PostgreSQL
psql -U postgres

# Criar banco de dados
CREATE DATABASE monolito;

# Criar usuário (opcional)
CREATE USER monolito_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE monolito TO monolito_user;

# Sair
\q
```

#### 3. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/monolito
DATABASE_USERNAME=monolito_user
DATABASE_PASSWORD=sua_senha
DATABASE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

## Executar Aplicação

### 1. Primeira Execução

```bash
# Instalar dependências
mvn clean install

# Executar aplicação
./run.sh
# ou
mvn spring-boot:run
```

### 2. Acessar Aplicação

Abra o navegador em: http://localhost:5000

### 3. Criar Primeiro Usuário

1. Acesse: http://localhost:5000/auth/register
2. Preencha os dados do formulário
3. Clique em "Registrar"

## Configurações Avançadas

### Alterar Porta

Edite `src/main/resources/application.properties`:

```properties
server.port=8080
```

### Modo Debug

```bash
mvn spring-boot:run -Ddebug=true
```

### Perfis de Ambiente

#### Desenvolvimento
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Produção
```bash
java -jar target/desafio-ia-monolito-1.0-SNAPSHOT.jar --spring.profiles.active=production
```

## Troubleshooting

### Erro: "Port 5000 already in use"

```bash
# Encontrar processo usando a porta
lsof -ti:5000

# Matar processo
kill -9 $(lsof -ti:5000)

# Ou alterar a porta no application.properties
```

### Erro: "Could not find or load main class"

```bash
# Limpar e recompilar
mvn clean install
```

### Erro: "Connection refused" (PostgreSQL)

```bash
# Verificar se PostgreSQL está rodando
# macOS
brew services list

# Linux
sudo systemctl status postgresql

# Se não estiver rodando, iniciar
brew services start postgresql  # macOS
sudo systemctl start postgresql # Linux
```

### Erro: "Table doesn't exist"

```bash
# Verificar configuração do Hibernate
# application.properties deve ter:
spring.jpa.hibernate.ddl-auto=update
```

## Logs

### Ver logs da aplicação

```bash
# Durante execução
mvn spring-boot:run

# Arquivo de log (se configurado)
tail -f logs/application.log
```

### Aumentar nível de log

Edite `application.properties`:

```properties
logging.level.root=DEBUG
logging.level.com.torneseumprogramador=TRACE
```

## Performance

### Aumentar memória da JVM

```bash
export MAVEN_OPTS="-Xmx2048m"
mvn spring-boot:run
```

### Produção - Otimizações

```bash
java -Xmx1024m -Xms512m -jar target/desafio-ia-monolito-1.0-SNAPSHOT.jar
```

## Docker (Opcional)

### Build da imagem

```bash
docker build -t desafio-ia-monolito .
```

### Executar com Docker Compose

```bash
# Iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar
docker-compose down
```

## Proximos Passos

1. ✅ Configurar ambiente
2. ✅ Executar aplicação
3. ✅ Criar primeiro usuário
4. 📚 Ler documentação das rotas (README.md)
5. 🧪 Executar testes (`make test`)
6. 🚀 Deploy em produção

