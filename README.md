# Desafio IA - Monolito (Java/Spring Boot)

Aplicação monolítica desenvolvida em Java com Spring Boot para gerenciamento de usuários.

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL / H2 Database
- Maven
- JUnit 5
- Mockito
- Lombok

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+ (opcional - usa H2 em desenvolvimento)

## 🔧 Instalação

### 1. Clone o repositório

```bash
git clone <repository-url>
cd monolito
git checkout migrate-to-java
```

### 2. Configure o banco de dados

#### Desenvolvimento (H2 - padrão)
Não é necessária configuração adicional. O H2 está configurado por padrão.

#### Produção (PostgreSQL)
Crie um arquivo `.env` ou configure as variáveis de ambiente:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/monolito
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha
```

### 3. Instale as dependências

```bash
mvn clean install
```

## 🎮 Executando a aplicação

### Usando scripts

```bash
# Executar em modo desenvolvimento
./run.sh

# Ou usando Make
make run
```

### Usando Maven diretamente

```bash
mvn spring-boot:run
```

### Acessar a aplicação

Abra o navegador em: `http://localhost:5000`

## 🧪 Testes

### Executar todos os testes

```bash
./test.sh
# ou
make test
# ou
mvn test
```

### Cobertura de testes

```bash
mvn test jacoco:report
```

Relatório disponível em: `target/site/jacoco/index.html`

## 📦 Build

### Gerar JAR da aplicação

```bash
./build.sh
# ou
make build
# ou
mvn package
```

O JAR será gerado em: `target/desafio-ia-monolito-1.0-SNAPSHOT.jar`

### Executar JAR em produção

```bash
java -jar target/desafio-ia-monolito-1.0-SNAPSHOT.jar --spring.profiles.active=production
# ou
make prod
```

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/torneseumprogramador/desafioiamonolito/
│   │   ├── config/           # Configurações do Spring
│   │   ├── controllers/      # Controllers REST/Web
│   │   ├── interceptor/      # Interceptors de autenticação
│   │   ├── models/          # Entidades JPA
│   │   ├── repositories/    # Repositórios Spring Data
│   │   ├── services/        # Lógica de negócios
│   │   └── utils/           # Classes utilitárias
│   └── resources/
│       ├── static/          # Arquivos estáticos (CSS, JS)
│       ├── templates/       # Templates Thymeleaf
│       └── application.properties
└── test/
    └── java/com/torneseumprogramador/desafioiamonolito/
        ├── integration/     # Testes de integração
        ├── models/          # Testes de modelos
        ├── services/        # Testes de serviços
        └── utils/           # Testes de utilitários
```

## 🔑 Funcionalidades

### Autenticação
- ✅ Login
- ✅ Registro de usuário
- ✅ Logout
- ✅ Sessões com cookie (7 dias)

### Gerenciamento de Usuários
- ✅ CRUD completo de usuários
- ✅ Listagem com paginação
- ✅ Ativar/Desativar usuários
- ✅ Edição de perfil
- ✅ Alteração de senha

### Dashboard
- ✅ Estatísticas de usuários
- ✅ Gráficos de registros mensais
- ✅ Métricas em tempo real

### Segurança
- ✅ Hash de senhas com BCrypt
- ✅ Proteção de rotas com interceptors
- ✅ Validação de dados
- ✅ Sessões seguras

## 📚 Documentação da API

### Endpoints Principais

#### Públicos
- `GET /` - Página inicial
- `GET /about` - Sobre
- `GET /auth/login` - Página de login
- `POST /auth/login` - Autenticar usuário
- `GET /auth/register` - Página de registro
- `POST /auth/register` - Registrar novo usuário

#### Autenticados
- `GET /dashboard` - Dashboard com estatísticas
- `GET /users` - Listar usuários (paginado)
- `GET /users/{id}` - Ver detalhes do usuário
- `POST /users/create` - Criar usuário
- `POST /users/{id}/edit` - Editar usuário
- `POST /users/{id}/delete` - Deletar usuário
- `GET /profile` - Ver perfil
- `POST /profile/edit` - Editar perfil
- `POST /profile/change-password` - Alterar senha

#### API (JSON)
- `GET /api/stats` - Estatísticas (JSON)
- `GET /api/monthly-data` - Dados mensais (JSON)
- `GET /users/api` - Lista de usuários (JSON)
- `GET /users/api/{id}` - Usuário específico (JSON)

## 🐳 Docker

### Build da imagem

```bash
docker build -t desafio-ia-monolito .
# ou
make docker-build
```

### Executar com Docker Compose

```bash
docker-compose up -d
# ou
make docker-run
```

### Ver logs

```bash
docker-compose logs -f
# ou
make docker-logs
```

### Parar containers

```bash
docker-compose down
# ou
make docker-stop
```

## 🛠️ Comandos Make

```bash
make help          # Mostra todos os comandos disponíveis
make install       # Instala dependências
make run           # Executa a aplicação
make build         # Compila a aplicação
make test          # Executa testes
make clean         # Limpa o projeto
make dev           # Executa em modo desenvolvimento
make prod          # Executa em modo produção
make package       # Gera JAR
make docker-build  # Cria imagem Docker
make docker-run    # Executa com Docker
make docker-stop   # Para Docker
```

## 🔄 Migração do Python

Esta aplicação é uma migração da versão Python (Flask) para Java (Spring Boot), mantendo:

✅ Mesma estrutura de arquitetura (MVC)  
✅ Mesmas funcionalidades  
✅ Mesmos endpoints  
✅ Mesma interface visual  
✅ Mesma lógica de negócios  
✅ Mesmos testes (adaptados)  

### Equivalências

| Python/Flask | Java/Spring Boot |
|-------------|------------------|
| Flask Blueprint | @Controller/@RestController |
| SQLAlchemy | Spring Data JPA |
| Jinja2 | Thymeleaf |
| @login_required | AuthInterceptor |
| Flask session | HttpSession |
| pytest | JUnit 5 + Mockito |
| requirements.txt | pom.xml |

## 📝 Variáveis de Ambiente

```properties
# Servidor
SERVER_PORT=5000

# Banco de Dados
DATABASE_URL=jdbc:postgresql://localhost:5432/monolito
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=senha
DATABASE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Debug
DEBUG=false

# Segurança
SECRET_KEY=sua-chave-secreta
```

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

## 👥 Autores

- Danilo - [GitHub](https://github.com/seu-usuario)

## 🙏 Agradecimentos

- Spring Boot Team
- Thymeleaf Team
- Comunidade Java
