# 🚀 Guia Rápido de Execução

## Executar a Aplicação

### Opção 1: Script Shell (Recomendado)
```bash
./run.sh
```

### Opção 2: Maven
```bash
mvn spring-boot:run
```

### Opção 3: Make
```bash
make run
```

## Acessar a Aplicação

Abra o navegador em: **http://localhost:5000**

## Primeiro Acesso

1. **Criar Conta**
   - Acesse: http://localhost:5000/auth/register
   - Preencha os dados:
     - Nome: Seu Nome
     - Email: seu@email.com
     - Username: seuusername
     - Senha: suasenha (mínimo 6 caracteres)
     - Telefone: (opcional)
   - Clique em "Registrar"

2. **Fazer Login**
   - Acesse: http://localhost:5000/auth/login
   - Use seu username ou email
   - Digite sua senha
   - Clique em "Entrar"

## Funcionalidades Disponíveis

### 📊 Dashboard
- **URL**: http://localhost:5000/dashboard
- Estatísticas de usuários
- Gráficos de registros mensais
- Métricas em tempo real

### 👥 Gerenciar Usuários
- **URL**: http://localhost:5000/users
- Listar todos os usuários (com paginação)
- Criar novo usuário
- Editar usuário
- Visualizar detalhes
- Ativar/Desativar usuário
- Deletar usuário

### 👤 Meu Perfil
- **URL**: http://localhost:5000/profile
- Visualizar perfil
- Editar dados pessoais
- Alterar senha

### 🔌 API REST (JSON)
- `GET /api/stats` - Estatísticas
- `GET /api/monthly-data` - Dados mensais
- `GET /users/api` - Lista de usuários
- `GET /users/api/{id}` - Usuário específico

## Parar a Aplicação

Pressione `Ctrl+C` no terminal onde a aplicação está rodando.

## Logs

Os logs são exibidos no console durante a execução. Para debug detalhado:

```bash
mvn spring-boot:run -Ddebug=true
```

## Banco de Dados

Por padrão, usa **H2 Database** (arquivo em `./instance/app.mv.db`)

Para visualizar o banco:
- Acesse: http://localhost:5000/h2-console (se configurado)
- Ou use um cliente SQL apontando para o arquivo

## Troubleshooting

### Porta 5000 já em uso?
```bash
# Matar processo na porta 5000
lsof -ti:5000 | xargs kill -9

# Ou alterar porta em src/main/resources/application.properties
server.port=8080
```

### Erro de compilação?
```bash
./clean.sh
./build.sh
```

### Limpar banco de dados?
```bash
rm -rf instance/
```

## Comandos Úteis

```bash
# Compilar
./build.sh

# Executar testes
./test.sh

# Limpar projeto
./clean.sh

# Ver todos os comandos
make help
```

## Credenciais de Teste

Após registrar seu primeiro usuário, você pode criar mais usuários via interface:
- Dashboard → Menu → Usuários → Criar Novo

Aproveite! 🎉

