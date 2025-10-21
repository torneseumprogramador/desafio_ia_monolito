#!/bin/bash

# Script para executar a aplicação Spring Boot

echo "🚀 Iniciando aplicação Spring Boot..."
echo ""

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não está instalado. Por favor, instale o Java 17 ou superior."
    exit 1
fi

# Verificar versão do Java
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$java_version" -lt 17 ]; then
    echo "❌ Java 17 ou superior é necessário. Versão atual: $java_version"
    exit 1
fi

# Executar a aplicação
echo "✅ Executando aplicação na porta 5000..."
echo ""
mvn spring-boot:run
