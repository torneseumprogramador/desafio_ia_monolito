-- Script de inicialização do banco de dados
-- Este arquivo será executado automaticamente quando o container for criado pela primeira vez

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Mensagem de sucesso
SELECT 'Database initialized successfully!' as message;

