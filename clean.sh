#!/bin/bash

# Script para limpar o projeto

echo "🧹 Limpando projeto..."
echo ""

# Limpar build do Maven
if command -v mvn &> /dev/null; then
    echo "📦 Limpando build do Maven..."
    mvn clean
fi

# Remover banco de dados local
if [ -d "instance" ]; then
    echo "🗑️  Removendo banco de dados local..."
    rm -rf instance
fi

# Remover logs
if [ -d "logs" ]; then
    echo "🗑️  Removendo logs..."
    rm -rf logs
fi

echo ""
echo "✅ Projeto limpo com sucesso!"

