# Sistema de Layouts

Este documento explica a estrutura de layouts da aplicação.

## 📐 Estrutura de Layouts

A aplicação possui 3 layouts diferentes para atender diferentes contextos de uso:

### 1. `layouts/base.html` - Layout Base

O layout base contém toda a estrutura HTML comum:

- **Propósito**: Serve como layout pai para todos os outros layouts
- **Conteúdo**:
  - Tag `<html>`, `<head>` e `<body>`
  - Importação de CSS (Bootstrap, Font Awesome, CSS customizado)
  - Block `navbar` (vazio, preenchido pelos layouts filhos)
  - Block `content` para conteúdo principal
  - Exibição global de Flash Messages
  - Footer
  - Importação de JavaScript (Bootstrap, scripts customizados)
  - Block `extra_css` e `extra_js` para customizações

### 2. `layouts/guest.html` - Layout para Usuários NÃO Logados

**Estende**: `layouts/base.html`

**Usado em**:
- Páginas de autenticação (login, registro)
- Páginas públicas quando o usuário não está logado

**Características do Navbar**:
- Logo do sistema
- Link para **Home**
- Link para **Sobre**
- Link para **Login** (destaque como link normal)
- Link para **Cadastrar** (destaque com botão verde)

**Exemplo de uso**:
```jinja
{% extends "layouts/guest.html" %}

{% block title %}Minha Página{% endblock %}

{% block content %}
  <!-- Seu conteúdo aqui -->
{% endblock %}
```

### 3. `layouts/authenticated.html` - Layout para Usuários Logados

**Estende**: `layouts/base.html`

**Usado em**:
- Todas as páginas do CRUD de usuários
- Páginas protegidas que requerem autenticação
- Páginas públicas quando o usuário está logado

**Características do Navbar**:
- Logo do sistema
- Links principais:
  - **Home**
  - **Sobre**
  - **Usuários** (acesso ao CRUD)
- Menu dropdown do usuário (lado direito):
  - Nome do usuário logado
  - Username exibido no dropdown
  - Opção **Sair**

**Exemplo de uso**:
```jinja
{% extends "layouts/authenticated.html" %}

{% block title %}Página Protegida{% endblock %}

{% block content %}
  <!-- Seu conteúdo aqui -->
{% endblock %}
```

## 🔄 Layouts Dinâmicos

Para páginas que podem ser acessadas tanto por usuários logados quanto não logados (ex: Home, Sobre), usamos uma expressão condicional no `extends`:

```jinja
{% extends "layouts/authenticated.html" if session.get('user_id') else "layouts/guest.html" %}
```

Isso faz com que:
- **Se o usuário estiver logado**: Use o layout `authenticated.html`
- **Se o usuário NÃO estiver logado**: Use o layout `guest.html`

## 📋 Mapeamento de Templates

### Páginas Públicas (Layout Dinâmico)
| Página | Template | Layout |
|--------|----------|--------|
| Home | `main/index.html` | `guest.html` ou `authenticated.html` |
| Sobre | `main/about.html` | `guest.html` ou `authenticated.html` |

### Páginas de Autenticação (Apenas Não Logados)
| Página | Template | Layout |
|--------|----------|--------|
| Login | `auth/login.html` | `guest.html` |
| Registro | `auth/register.html` | `guest.html` |

### Páginas Protegidas (Apenas Logados)
| Página | Template | Layout |
|--------|----------|--------|
| Lista de Usuários | `users/index.html` | `authenticated.html` |
| Criar Usuário | `users/create.html` | `authenticated.html` |
| Detalhes do Usuário | `users/show.html` | `authenticated.html` |
| Editar Usuário | `users/edit.html` | `authenticated.html` |

## 🎨 Customizando Layouts

### Adicionando CSS Customizado

Qualquer template pode adicionar CSS adicional:

```jinja
{% block extra_css %}
<style>
  .minha-classe {
    color: red;
  }
</style>
{% endblock %}
```

### Adicionando JavaScript Customizado

Qualquer template pode adicionar JavaScript adicional:

```jinja
{% block extra_js %}
<script>
  console.log('Meu script customizado');
</script>
{% endblock %}
```

## 📱 Navbar Features

### Indicador de Página Ativa

Ambos os layouts incluem destaque automático da página atual:

```jinja
<a href="{{ url_for('main.index') }}" 
   class="nav-link {% if request.endpoint == 'main.index' %}active{% endif %}">
  Home
</a>
```

### Dropdown do Usuário (Layout Authenticated)

O dropdown exibe:
- Ícone de usuário
- Nome completo do usuário
- Username (dentro do dropdown)
- Opção de logout em vermelho

```jinja
<li class="nav-item dropdown">
  <a class="nav-link dropdown-toggle" href="#" id="userDropdown" 
     role="button" data-bs-toggle="dropdown">
    <i class="fas fa-user-circle"></i>
    {{ session.get('name', 'Usuário') }}
  </a>
  <ul class="dropdown-menu">
    <li>
      <div class="px-3 py-2 text-muted small">
        <i class="fas fa-at"></i> {{ session.get('username', '') }}
      </div>
    </li>
    <li><hr class="dropdown-divider"></li>
    <li>
      <a class="dropdown-item text-danger" href="{{ url_for('auth.logout') }}">
        <i class="fas fa-sign-out-alt"></i> Sair
      </a>
    </li>
  </ul>
</li>
```

## 📩 Flash Messages

As mensagens flash são exibidas automaticamente no `base.html`, logo após a navbar e antes do conteúdo:

```python
# No controller
flash('Operação realizada com sucesso!', 'success')
flash('Erro ao processar requisição', 'danger')
flash('Atenção: verifique os dados', 'warning')
flash('Informação importante', 'info')
```

**Categorias disponíveis**:
- `success` - Verde (sucesso)
- `danger` - Vermelho (erro)
- `warning` - Amarelo (aviso)
- `info` - Azul (informação)

**Nota**: Como as mensagens flash já são exibidas no layout base, **não é necessário** incluí-las nos templates individuais.

## 🔧 Criando um Novo Layout

Se precisar criar um novo layout (ex: para uma área administrativa):

1. Crie o arquivo em `app/templates/layouts/`:
```jinja
{% extends "layouts/base.html" %}

{% block navbar %}
  <!-- Seu navbar customizado aqui -->
{% endblock %}
```

2. Use o novo layout nos templates:
```jinja
{% extends "layouts/meu_novo_layout.html" %}
```

## 📝 Boas Práticas

1. **Sempre estenda um layout**: Nunca crie templates sem layout pai
2. **Use o layout correto**: 
   - `guest.html` para páginas públicas/não autenticadas
   - `authenticated.html` para páginas protegidas
   - Layout dinâmico para páginas híbridas
3. **Não duplique mensagens flash**: O layout base já as exibe
4. **Use blocks apropriados**: `title`, `content`, `extra_css`, `extra_js`
5. **Mantenha consistência**: Use a mesma estrutura HTML em todos os templates

## 🎯 Vantagens dessa Arquitetura

✅ **Separação clara** entre usuários logados e não logados  
✅ **Manutenção facilitada** - mudanças no navbar afetam todos os templates  
✅ **Melhor UX** - interfaces apropriadas para cada contexto  
✅ **DRY** (Don't Repeat Yourself) - código reutilizável  
✅ **Escalabilidade** - fácil adicionar novos layouts se necessário  
✅ **Mensagens centralizadas** - flash messages em um único lugar  

## 🔍 Troubleshooting

### O navbar não está aparecendo
- Verifique se o template estende o layout correto (`guest.html` ou `authenticated.html`)
- Não estenda `base.html` diretamente (a menos que esteja criando um novo layout)

### As mensagens flash aparecem duplicadas
- Remova blocos `{% with messages = get_flashed_messages() %}` dos templates individuais
- O layout base já exibe as mensagens automaticamente

### O usuário logado não vê o nome dele no navbar
- Verifique se `session['name']` está sendo definido no login
- Certifique-se de que o template está usando `authenticated.html`

### Páginas públicas não mudam o layout ao fazer login
- Verifique se o template usa a expressão condicional:
  ```jinja
  {% extends "layouts/authenticated.html" if session.get('user_id') else "layouts/guest.html" %}
  ```

