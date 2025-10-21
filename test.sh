#!/bin/bash

# Script para executar testes

echo "🧪 Executando testes..."
echo ""

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Executar testes
mvn test

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Todos os testes passaram!"
else
    echo ""
    echo "❌ Alguns testes falharam"
    exit 1
fi

