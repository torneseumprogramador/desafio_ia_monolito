package com.torneseumprogramador.desafioiamonolito.services;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Serviço para lógica de negócios relacionada a usuários
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário
     * @param data Map com os dados do usuário
     * @return Array com [User criado ou null, mensagem de erro ou null]
     */
    @Transactional
    public Object[] createUser(Map<String, Object> data) {
        try {
            // Validações
            if (!data.containsKey("name") || ((String) data.get("name")).trim().isEmpty()) {
                return new Object[]{null, "Nome é obrigatório"};
            }

            if (!data.containsKey("email") || ((String) data.get("email")).trim().isEmpty()) {
                return new Object[]{null, "Email é obrigatório"};
            }

            if (!data.containsKey("username") || ((String) data.get("username")).trim().isEmpty()) {
                return new Object[]{null, "Username é obrigatório"};
            }

            if (!data.containsKey("password") || ((String) data.get("password")).trim().isEmpty()) {
                return new Object[]{null, "Senha é obrigatória"};
            }

            String email = ((String) data.get("email")).trim();
            String username = ((String) data.get("username")).trim();
            String password = (String) data.get("password");

            // Verificar se email já existe
            if (repository.existsByEmail(email)) {
                return new Object[]{null, "Email já cadastrado"};
            }

            // Verificar se username já existe
            if (repository.existsByUsername(username)) {
                return new Object[]{null, "Username já cadastrado"};
            }

            // Criar usuário
            User user = new User();
            user.setName(((String) data.get("name")).trim());
            user.setEmail(email);
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setPhone(data.containsKey("phone") ? ((String) data.get("phone")).trim() : "");
            user.setIsActive(data.containsKey("isActive") ? (Boolean) data.get("isActive") : true);

            User createdUser = repository.save(user);
            return new Object[]{createdUser, null};
        } catch (Exception e) {
            return new Object[]{null, "Erro ao criar usuário: " + e.getMessage()};
        }
    }

    /**
     * Busca usuário por ID
     * @param userId ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> getUserById(Long userId) {
        return repository.findById(userId);
    }

    /**
     * Busca usuário por email
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Busca usuário por username
     * @param username Username do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    /**
     * Retorna todos os usuários com paginação
     * @param page Número da página (1-based)
     * @param perPage Itens por página
     * @return Array com [Lista de usuários, Total de usuários]
     */
    public Object[] getAllUsers(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userPage = repository.findAll(pageable);
        return new Object[]{userPage.getContent(), userPage.getTotalElements()};
    }

    /**
     * Retorna todos os usuários ativos
     * @return Lista de usuários ativos
     */
    public List<User> getActiveUsers() {
        return repository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    /**
     * Atualiza um usuário existente
     * @param userId ID do usuário
     * @param data Map com os dados a atualizar
     * @return Array com [User atualizado ou null, mensagem de erro ou null]
     */
    @Transactional
    public Object[] updateUser(Long userId, Map<String, Object> data) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) {
            return new Object[]{null, "Usuário não encontrado"};
        }

        User user = optionalUser.get();

        try {
            // Validar email se foi alterado
            if (data.containsKey("email")) {
                String newEmail = ((String) data.get("email")).trim();
                if (!newEmail.equals(user.getEmail())) {
                    if (repository.existsByEmailExcludingId(newEmail, userId)) {
                        return new Object[]{null, "Email já cadastrado"};
                    }
                    user.setEmail(newEmail);
                }
            }

            // Validar username se foi alterado
            if (data.containsKey("username")) {
                String newUsername = ((String) data.get("username")).trim();
                if (!newUsername.equals(user.getUsername())) {
                    if (repository.existsByUsernameExcludingId(newUsername, userId)) {
                        return new Object[]{null, "Username já cadastrado"};
                    }
                    user.setUsername(newUsername);
                }
            }

            // Atualizar outros campos
            if (data.containsKey("name")) {
                user.setName(((String) data.get("name")).trim());
            }
            if (data.containsKey("phone")) {
                user.setPhone(((String) data.get("phone")).trim());
            }
            if (data.containsKey("isActive")) {
                user.setIsActive((Boolean) data.get("isActive"));
            }
            if (data.containsKey("password") && !((String) data.get("password")).trim().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode((String) data.get("password")));
            }

            User updatedUser = repository.save(user);
            return new Object[]{updatedUser, null};
        } catch (Exception e) {
            return new Object[]{null, "Erro ao atualizar usuário: " + e.getMessage()};
        }
    }

    /**
     * Remove um usuário
     * @param userId ID do usuário
     * @return Array com [sucesso (boolean), mensagem de erro ou null]
     */
    @Transactional
    public Object[] deleteUser(Long userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) {
            return new Object[]{false, "Usuário não encontrado"};
        }

        try {
            repository.delete(optionalUser.get());
            return new Object[]{true, null};
        } catch (Exception e) {
            return new Object[]{false, "Erro ao deletar usuário: " + e.getMessage()};
        }
    }

    /**
     * Ativa/Desativa um usuário
     * @param userId ID do usuário
     * @return Array com [User atualizado ou null, mensagem de erro ou null]
     */
    @Transactional
    public Object[] toggleUserStatus(Long userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) {
            return new Object[]{null, "Usuário não encontrado"};
        }

        try {
            User user = optionalUser.get();
            user.setIsActive(!user.getIsActive());
            User updatedUser = repository.save(user);
            return new Object[]{updatedUser, null};
        } catch (Exception e) {
            return new Object[]{null, "Erro ao alterar status: " + e.getMessage()};
        }
    }

    /**
     * Retorna o total de usuários cadastrados
     * @return Número total de usuários
     */
    public long countUsers() {
        return repository.count();
    }

    /**
     * Autentica um usuário com username/email e senha
     * @param usernameOrEmail Username ou email
     * @param password Senha
     * @return Array com [User autenticado ou null, mensagem de erro ou null]
     */
    public Object[] authenticate(String usernameOrEmail, String password) {
        // Tentar buscar por username ou email
        Optional<User> optionalUser = repository.findByUsername(usernameOrEmail);
        if (!optionalUser.isPresent()) {
            optionalUser = repository.findByEmail(usernameOrEmail);
        }

        if (!optionalUser.isPresent()) {
            return new Object[]{null, "Usuário ou senha inválidos"};
        }

        User user = optionalUser.get();

        if (!user.getIsActive()) {
            return new Object[]{null, "Usuário inativo. Entre em contato com o administrador."};
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new Object[]{null, "Usuário ou senha inválidos"};
        }

        return new Object[]{user, null};
    }

    /**
     * Retorna estatísticas dos usuários para o dashboard
     * @return Map com as estatísticas
     */
    public Map<String, Object> getUserStatistics() {
        try {
            long totalUsers = repository.count();
            long activeUsers = repository.countByIsActiveTrue();
            long inactiveUsers = totalUsers - activeUsers;

            LocalDateTime today = LocalDate.now().atStartOfDay();
            LocalDateTime tomorrow = today.plusDays(1);
            long usersToday = repository.countByCreatedAtBetween(today, tomorrow);

            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            long usersWeek = repository.countByCreatedAtAfter(weekAgo);

            LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
            long usersMonth = repository.countByCreatedAtAfter(monthAgo);

            double activePercentage = totalUsers > 0 ? Math.round((double) activeUsers / totalUsers * 1000.0) / 10.0 : 0.0;

            Map<String, Object> stats = new HashMap<>();
            stats.put("total_users", totalUsers);
            stats.put("active_users", activeUsers);
            stats.put("inactive_users", inactiveUsers);
            stats.put("users_today", usersToday);
            stats.put("users_week", usersWeek);
            stats.put("users_month", usersMonth);
            stats.put("active_percentage", activePercentage);

            return stats;
        } catch (Exception e) {
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("total_users", 0);
            emptyStats.put("active_users", 0);
            emptyStats.put("inactive_users", 0);
            emptyStats.put("users_today", 0);
            emptyStats.put("users_week", 0);
            emptyStats.put("users_month", 0);
            emptyStats.put("active_percentage", 0.0);
            return emptyStats;
        }
    }

    /**
     * Retorna dados de registros mensais para gráficos
     * @param months Número de meses a retornar
     * @return Lista de mapas com os dados mensais
     */
    public List<Map<String, Object>> getMonthlyUserRegistrations(int months) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(months);

            YearMonth currentMonth = YearMonth.from(startDate.withDayOfMonth(1));
            YearMonth lastMonth = YearMonth.from(endDate);

            while (!currentMonth.isAfter(lastMonth)) {
                LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
                LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

                long count = repository.countByCreatedAtBetween(monthStart, monthEnd);

                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", currentMonth.getMonth().toString().substring(0, 3));
                monthData.put("year", currentMonth.getYear());
                monthData.put("count", count);
                monthData.put("full_date", currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));

                result.add(monthData);
                currentMonth = currentMonth.plusMonths(1);
            }

            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
