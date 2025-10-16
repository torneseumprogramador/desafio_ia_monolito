#!/bin/bash

# Script para gerenciar migrations do banco de dados

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para exibir mensagens coloridas
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar se o virtual environment está ativado
check_venv() {
    if [ -z "$VIRTUAL_ENV" ]; then
        print_error "Virtual environment não está ativado!"
        print_info "Ativando virtual environment..."
        source venv/bin/activate || {
            print_error "Falha ao ativar o virtual environment"
            exit 1
        }
    fi
}

# Função para inicializar migrations
init_migrations() {
    print_info "Inicializando sistema de migrations..."
    
    if [ -d "migrations" ]; then
        print_warning "Sistema de migrations já existe"
        return 1
    fi
    
    flask db init
    print_success "Sistema de migrations inicializado!"
}

# Função para criar uma nova migration
create_migration() {
    local message=$1
    
    if [ -z "$message" ]; then
        message="Auto migration"
    fi
    
    print_info "Criando nova migration: $message"
    flask db migrate -m "$message"
    print_success "Migration criada com sucesso!"
}

# Função para aplicar migrations
upgrade_database() {
    print_info "Aplicando migrations pendentes..."
    flask db upgrade
    print_success "Migrations aplicadas com sucesso!"
}

# Função para reverter migration
downgrade_database() {
    print_warning "Revertendo última migration..."
    flask db downgrade
    print_success "Migration revertida!"
}

# Função para exibir o status das migrations
show_status() {
    print_info "Status das migrations:"
    flask db current
    echo ""
    print_info "Histórico de migrations:"
    flask db history
}

# Menu principal
show_menu() {
    echo ""
    echo "================================"
    echo "  Gerenciador de Migrations"
    echo "================================"
    echo "1. Inicializar migrations (db init)"
    echo "2. Criar nova migration (db migrate)"
    echo "3. Aplicar migrations (db upgrade)"
    echo "4. Reverter última migration (db downgrade)"
    echo "5. Ver status das migrations"
    echo "6. Sair"
    echo "================================"
}

# Processar argumentos de linha de comando
if [ $# -gt 0 ]; then
    check_venv
    
    case "$1" in
        init)
            init_migrations
            ;;
        migrate)
            create_migration "$2"
            ;;
        upgrade)
            upgrade_database
            ;;
        downgrade)
            downgrade_database
            ;;
        status)
            show_status
            ;;
        *)
            print_error "Comando inválido: $1"
            echo "Uso: $0 {init|migrate|upgrade|downgrade|status} [mensagem]"
            exit 1
            ;;
    esac
    exit 0
fi

# Menu interativo
while true; do
    show_menu
    read -p "Escolha uma opção: " option
    
    check_venv
    
    case $option in
        1)
            init_migrations
            ;;
        2)
            read -p "Digite a mensagem da migration: " msg
            create_migration "$msg"
            ;;
        3)
            upgrade_database
            ;;
        4)
            downgrade_database
            ;;
        5)
            show_status
            ;;
        6)
            print_info "Saindo..."
            exit 0
            ;;
        *)
            print_error "Opção inválida!"
            ;;
    esac
    
    echo ""
    read -p "Pressione ENTER para continuar..."
done

