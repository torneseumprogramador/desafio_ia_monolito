# Sistema de Autenticação

Este documento descreve o sistema de autenticação implementado na aplicação.

## 🔐 Visão Geral

O sistema de autenticação utiliza sessões do Flask para gerenciar o login de usuários. As rotas estão protegidas por middlewares (decoradores) que verificam se o usuário está autenticado.

## 📋 Funcionalidades

### 1. Login
- **Rota**: `/auth/login`
- **Descrição**: Permite que usuários façam login com username/email e senha
- **Características**:
  - Aceita tanto username quanto email como identificador
  - Verifica se o usuário está ativo
  - Cria uma sessão permanente (duração de 7 dias)
  - Redireciona para a página que o usuário tentava acessar antes do login

### 2. Registro/Cadastro
- **Rota**: `/auth/register`
- **Descrição**: Permite que novos usuários criem uma conta
- **Validações**:
  - Senha mínima de 6 caracteres
  - Confirmação de senha
  - Email e username únicos
  - Todos os campos obrigatórios preenchidos
- **Características**:
  - Novos usuários são ativados automaticamente
  - Login automático após registro bem-sucedido

### 3. Logout
- **Rota**: `/auth/logout`
- **Descrição**: Encerra a sessão do usuário
- **Características**:
  - Limpa todos os dados da sessão
  - Exibe mensagem de despedida
  - Redireciona para a página inicial

## 🛡️ Middlewares de Autenticação

### @login_required
Protege rotas que requerem autenticação.

**Uso**:
```python
from app.middleware import login_required

@bp.route('/protected')
@login_required
def protected_route():
    return "Esta rota está protegida"
```

**Comportamento**:
- Verifica se existe `user_id` na sessão
- Se não estiver logado, redireciona para `/auth/login`
- Salva a URL original para redirecionar após o login
- Exibe mensagem informando que é necessário login

### @guest_only
Protege rotas que só devem ser acessadas por usuários NÃO autenticados (ex: login, registro).

**Uso**:
```python
from app.middleware import guest_only

@bp.route('/login')
@guest_only
def login():
    return "Página de login"
```

**Comportamento**:
- Verifica se o usuário já está logado
- Se estiver logado, redireciona para a home
- Evita que usuários logados acessem páginas de login/registro

## 🔒 Rotas Protegidas

Todas as rotas do CRUD de usuários estão protegidas:

- `GET /users/` - Lista de usuários
- `GET /users/create` - Formulário de criação
- `POST /users/create` - Criar usuário
- `GET /users/<id>` - Detalhes do usuário
- `GET /users/<id>/edit` - Formulário de edição
- `POST /users/<id>/edit` - Atualizar usuário
- `POST /users/<id>/delete` - Deletar usuário
- `POST /users/<id>/toggle-status` - Ativar/desativar usuário
- `GET /users/api` - API: listar usuários
- `GET /users/api/<id>` - API: buscar usuário

## 🌍 Rotas Públicas

As seguintes rotas estão acessíveis sem autenticação:

- `GET /` - Página inicial (Home)
- `GET /about` - Página sobre
- `GET /auth/login` - Login
- `POST /auth/login` - Processar login
- `GET /auth/register` - Cadastro
- `POST /auth/register` - Processar cadastro
- `GET /auth/logout` - Logout

## 💾 Dados da Sessão

Quando um usuário faz login, os seguintes dados são armazenados na sessão:

```python
session['user_id'] = user.id
session['username'] = user.username
session['name'] = user.name
session.permanent = True  # Sessão permanente (7 dias)
```

### Acessando Dados da Sessão nos Templates

```html
{% if session.get('user_id') %}
    <p>Olá, {{ session.get('name') }}!</p>
{% endif %}
```

### Acessando Dados da Sessão nos Controllers

```python
from flask import session

@bp.route('/profile')
@login_required
def profile():
    user_id = session.get('user_id')
    username = session.get('username')
    name = session.get('name')
    # ...
```

## ⚙️ Configurações

As configurações de sessão estão em `app/config.py`:

```python
# Configurações de sessão
PERMANENT_SESSION_LIFETIME = timedelta(days=7)  # Sessão dura 7 dias
```

Para alterar a duração da sessão, modifique o valor de `PERMANENT_SESSION_LIFETIME`.

## 🎨 Interface do Usuário

### Layouts Separados

A aplicação possui dois layouts distintos para melhor experiência do usuário:

**Layout Guest** (`layouts/guest.html`):
- Usado para usuários não autenticados
- Menu simplificado com opções públicas
- Botão de cadastro em destaque

**Layout Authenticated** (`layouts/authenticated.html`):
- Usado para usuários autenticados
- Menu completo com acesso ao CRUD
- Dropdown com informações do usuário

📚 **Veja mais detalhes em**: [LAYOUTS.md](LAYOUTS.md)

### Menu de Navegação

O menu de navegação adapta-se automaticamente baseado no estado de autenticação:

**Usuário NÃO autenticado** (Layout Guest):
- Home
- Sobre
- Login

**Usuário autenticado** (Layout Authenticated):
- Home
- Sobre
- Usuários
- [Nome do Usuário] (dropdown com username e opção de Sair)

## 🔧 Como Adicionar Novas Rotas Protegidas

1. Importe o middleware:
```python
from app.middleware import login_required
```

2. Adicione o middleware à rota:
```python
@bp.route('/nova-rota')
@login_required
def nova_rota():
    return "Conteúdo protegido"
```

## 🧪 Testando o Sistema

### Criar um usuário de teste

1. Acesse `/auth/register`
2. Preencha os dados:
   - Nome: Teste Admin
   - Username: admin
   - Email: admin@example.com
   - Senha: admin123
   - Confirmação: admin123
3. Clique em "Criar Conta"

### Fazer login

1. Acesse `/auth/login`
2. Digite: admin (ou admin@example.com)
3. Senha: admin123
4. Clique em "Entrar"

### Testar proteção de rotas

1. Faça logout
2. Tente acessar `/users/`
3. Você será redirecionado para a página de login
4. Após fazer login, será redirecionado automaticamente para `/users/`

## 🔐 Segurança

### Senhas

- As senhas são armazenadas como hash usando `werkzeug.security`
- Função: `generate_password_hash()` para criar o hash
- Função: `check_password_hash()` para verificar a senha
- Nunca armazenamos senhas em texto puro

### Sessões

- Utiliza a SECRET_KEY definida em `app/config.py`
- Em produção, defina uma SECRET_KEY forte usando variável de ambiente:
  ```bash
  export SECRET_KEY="sua-chave-secreta-aleatoria-e-forte"
  ```

### Validações

- Usuários inativos não podem fazer login
- Validação de email único
- Validação de username único
- Senha mínima de 6 caracteres

## 📝 Mensagens Flash

O sistema utiliza mensagens flash para feedback ao usuário:

- **success** (verde): Operação bem-sucedida
- **danger** (vermelho): Erro ou falha
- **warning** (amarelo): Aviso
- **info** (azul): Informação

Exemplos:
```python
flash('Login realizado com sucesso!', 'success')
flash('Usuário ou senha inválidos', 'danger')
flash('Você precisa estar logado', 'warning')
flash('Até logo!', 'info')
```

## 🚀 Próximos Passos (Melhorias Futuras)

- [ ] Implementar "Lembrar-me" no login
- [ ] Adicionar recuperação de senha
- [ ] Implementar verificação de email
- [ ] Adicionar autenticação de dois fatores (2FA)
- [ ] Criar roles/permissões de usuários
- [ ] Implementar limite de tentativas de login
- [ ] Adicionar logs de atividades de login
- [ ] OAuth/Social Login (Google, Facebook, etc.)

