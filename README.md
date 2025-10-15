# Aplicação Java com Spring Boot - Arquitetura MVC

Uma aplicação web monolítica desenvolvida com **Java** e **Spring Boot** seguindo o padrão **MVC (Model-View-Controller)**.

## 📋 Estrutura do Projeto (MVC)

```
monolito/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/torneseumprogramador/desafioiamonolito/
│       │       ├── App.java             # Classe principal
│       │       ├── controllers/         # Controllers (Rotas/Lógica de controle)
│       │       │   └── MainController.java
│       │       ├── models/              # Models (Modelos de dados)
│       │       │   └── User.java
│       │       └── services/            # Services (Lógica de negócio)
│       │           └── UserService.java
│       └── resources/
│           └── templates/               # Views (Templates HTML)
│               ├── layouts/
│               │   └── base.html        # Layout base para uso nas outras views
│               └── main/
│                   ├── index.html       # Página inicial do site
│                   └── about.html       # Página sobre
├── run.sh                               # Script de execução
├── pom.xml                              # Configuração do Maven
└── README.md                            # Este arquivo
```

## 🏗️ Arquitetura MVC

### **Models** (Modelos)
- Representam a estrutura de dados
- Localização: `src/main/java/com/torneseumprogramador/desafioiamonolito/models/`

### **Views** (Visualizações)
- Templates HTML (Thymeleaf)
- Interface com o usuário
- Localização: `src/main/resources/templates/`

### **Controllers** (Controladores)
- Recebem requisições HTTP
- Processam dados
- Chamam services quando necessário
- Retornam respostas
- Localização: `src/main/java/com/torneseumprogramador/desafioiamonolito/controllers/`

### **Services** (Serviços)
- Contêm a lógica de negócio
- São chamados pelos controllers
- Localização: `src/main/java/com/torneseumprogramador/desafioiamonolito/services/`

## 🚀 Como Executar

### 1. Executar a aplicação

**Opção 1: Usando o script**
```bash
./run.sh
```

**Opção 2: Com Maven**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.0** - Framework web
- **Thymeleaf** - Engine de templates

## 📦 Padrões Utilizados

- **MVC Architecture** - Separação de responsabilidades

## 🔄 Fluxo de uma Requisição

```
1. Usuário acessa URL
   ↓
2. Controller recebe a requisição
   ↓
3. Controller chama Service (se necessário)
   ↓
4. Service processa lógica de negócio
   ↓
5. Controller renderiza View (template)
   ↓
6. HTML é retornado ao usuário
```

## 📄 Licença

Este projeto está sob a licença MIT.
