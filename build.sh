#!/bin/bash

# Script para compilar a aplicação Spring Boot

echo "🔨 Compilando aplicação Spring Boot..."
echo ""

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Limpar e compilar
echo "📦 Limpando build anterior..."
mvn clean

echo ""
echo "⚙️  Compilando aplicação..."
mvn compile

echo ""
echo "📦 Gerando JAR..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build concluído com sucesso!"
    echo "📦 JAR gerado em: target/desafio-ia-monolito-1.0-SNAPSHOT.jar"
else
    echo ""
    echo "❌ Erro durante o build"
    exit 1
fi

