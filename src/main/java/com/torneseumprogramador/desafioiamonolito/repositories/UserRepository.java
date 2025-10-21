package com.torneseumprogramador.desafioiamonolito.repositories;

import com.torneseumprogramador.desafioiamonolito.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados relacionadas a usuários
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário por email
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário por username
     * @param username Username do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca usuários por email ou username
     * @param email Email
     * @param username Username
     * @return Optional contendo o usuário se encontrado
     */
    @Query("SELECT u FROM User u WHERE u.email = :email OR u.username = :username")
    Optional<User> findByEmailOrUsername(@Param("email") String email, @Param("username") String username);

    /**
     * Retorna todos os usuários ativos ordenados por data de criação
     * @return Lista de usuários ativos
     */
    List<User> findByIsActiveTrueOrderByCreatedAtDesc();

    /**
     * Retorna todos os usuários com paginação ordenados por data de criação
     * @param pageable Configuração de paginação
     * @return Página de usuários
     */
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Verifica se existe um usuário com o email informado
     * @param email Email a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe um usuário com o username informado
     * @param username Username a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByUsername(String username);

    /**
     * Verifica se existe um email (excluindo um ID específico)
     * @param email Email a verificar
     * @param excludeId ID a excluir da verificação
     * @return true se existe, false caso contrário
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * Verifica se existe um username (excluindo um ID específico)
     * @param username Username a verificar
     * @param excludeId ID a excluir da verificação
     * @return true se existe, false caso contrário
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.id != :excludeId")
    boolean existsByUsernameExcludingId(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * Conta usuários ativos
     * @return Número de usuários ativos
     */
    long countByIsActiveTrue();

    /**
     * Conta usuários criados após uma data específica
     * @param date Data de referência
     * @return Número de usuários
     */
    long countByCreatedAtAfter(LocalDateTime date);

    /**
     * Conta usuários criados em uma data específica
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Número de usuários
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt < :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

