# 📁 JavaScript Assets

Esta pasta contém os scripts JavaScript organizados da aplicação.

## 📂 Estrutura de Arquivos

```
js/
├── main.js           # Script principal da aplicação
├── masks.js          # Máscaras de formatação (telefone, CPF, CEP)
├── form-utils.js     # Utilitários para formulários
├── user-form.js      # Script específico para formulários de usuário
└── README.md         # Este arquivo
```

## 📝 Descrição dos Arquivos

### `main.js`
Script principal que é carregado em todas as páginas da aplicação.

**Uso:**
```html
<script src="{{ url_for('static', filename='js/main.js') }}"></script>
```

### `masks.js`
Contém funções para aplicar máscaras de formatação em campos de formulário.

**Máscaras disponíveis:**
- **PhoneMask**: Telefone brasileiro (00) 00000-0000
- **CPFMask**: CPF 000.000.000-00
- **CEPMask**: CEP 00000-000

**Exemplo de uso:**
```javascript
// Inicializar máscara de telefone
PhoneMask.init('phone');

// Formatar valor
const formatted = PhoneMask.format('11987654321');
// Retorna: (11) 98765-4321

// Remover formatação
const numbers = PhoneMask.unmask('(11) 98765-4321');
// Retorna: 11987654321
```

### `form-utils.js`
Utilitários para melhorar a experiência do usuário em formulários.

**Funcionalidades disponíveis:**

#### 1. PasswordToggle
Toggle de visibilidade de senha.

```javascript
PasswordToggle.init('password', 'togglePassword');
```

#### 2. FormValidation
Validação de formulários com Bootstrap.

```javascript
// Inicializar validação em todos os forms
FormValidation.init();

// Validar campo específico
FormValidation.validateField('email');

// Adicionar erro customizado
FormValidation.setCustomError('username', 'Username já existe');

// Limpar erro
FormValidation.clearCustomError('username');
```

#### 3. ConfirmAction
Confirmação antes de ações perigosas.

```javascript
ConfirmAction.init('form[data-confirm]', 'Tem certeza?');
```

#### 4. AutoSave
Salvar formulário no localStorage.

```javascript
// Salvar
AutoSave.save('myForm', 'form-data');

// Restaurar
AutoSave.restore('myForm', 'form-data');

// Limpar
AutoSave.clear('form-data');
```

### `user-form.js`
Script específico para formulários de criação e edição de usuários.

**Funcionalidades:**
- Inicializa máscara de telefone
- Inicializa toggle de senha
- Aplica validações customizadas
- Auto-focus no primeiro campo

**Como usar:**
```html
{% block extra_js %}
<script src="{{ url_for('static', filename='js/masks.js') }}"></script>
<script src="{{ url_for('static', filename='js/form-utils.js') }}"></script>
<script src="{{ url_for('static', filename='js/user-form.js') }}"></script>
{% endblock %}
```

## 🎯 Boas Práticas

### 1. Ordem de Carregamento
Sempre carregue os scripts na ordem correta:
1. Bibliotecas (Bootstrap, jQuery, etc)
2. Utilitários (masks.js, form-utils.js)
3. Scripts específicos (user-form.js)

### 2. Dependências
- `user-form.js` depende de `masks.js` e `form-utils.js`
- Sempre carregue as dependências antes

### 3. Namespace Global
Todos os módulos são expostos no objeto `window`:
- `window.PhoneMask`
- `window.CPFMask`
- `window.CEPMask`
- `window.PasswordToggle`
- `window.FormValidation`
- `window.ConfirmAction`
- `window.AutoSave`

### 4. Event Listeners
Use `DOMContentLoaded` para garantir que o DOM está pronto:

```javascript
document.addEventListener('DOMContentLoaded', function() {
    // Seu código aqui
});
```

## 🔧 Exemplos de Uso

### Formulário com Máscara de Telefone
```html
<input type="tel" id="phone" name="phone">

<script src="{{ url_for('static', filename='js/masks.js') }}"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        PhoneMask.init('phone');
    });
</script>
```

### Formulário com Validação
```html
<form novalidate>
    <input type="email" id="email" required>
    <button type="submit">Enviar</button>
</form>

<script src="{{ url_for('static', filename='js/form-utils.js') }}"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        FormValidation.init();
    });
</script>
```

### Toggle de Senha
```html
<input type="password" id="password">
<button type="button" id="togglePassword">
    <i class="fas fa-eye"></i>
</button>

<script src="{{ url_for('static', filename='js/form-utils.js') }}"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        PasswordToggle.init('password', 'togglePassword');
    });
</script>
```

## 🚀 Extensão

Para adicionar novas máscaras ou utilitários:

1. Adicione a função no arquivo apropriado
2. Exponha no objeto `window`
3. Documente aqui no README
4. Atualize os testes (se houver)

### Exemplo de Nova Máscara
```javascript
// Em masks.js
const CNPJMask = {
    init: function(fieldId) {
        // implementação
    },
    format: function(value) {
        // implementação
    },
    unmask: function(value) {
        // implementação
    }
};

window.CNPJMask = CNPJMask;
```

## 📊 Performance

- Scripts são carregados de forma assíncrona quando possível
- Use `defer` ou `async` para scripts não críticos
- Minifique em produção
- Considere usar um bundler (Webpack, Vite, etc)

## 🐛 Debug

Para ativar logs de debug, abra o console do navegador:

```javascript
// Ver se os módulos estão carregados
console.log(window.PhoneMask);
console.log(window.FormValidation);

// Testar formatação
PhoneMask.format('11987654321');
```

## 📚 Referências

- [Bootstrap Documentation](https://getbootstrap.com/docs/)
- [JavaScript Best Practices](https://developer.mozilla.org/pt-BR/docs/Web/JavaScript)
- [Web APIs](https://developer.mozilla.org/pt-BR/docs/Web/API)

