package com.torneseumprogramador.desafioiamonolito.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entidade de Usuário
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 20)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Define a senha do usuário (hash)
     * @param passwordHash Hash da senha
     */
    public void setPassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Converte o modelo para Map (equivalente ao to_dict do Python)
     * @return Map com os dados do usuário
     */
    public Map<String, Object> toDict() {
        Map<String, Object> dict = new HashMap<>();
        dict.put("id", this.id);
        dict.put("name", this.name);
        dict.put("email", this.email);
        dict.put("username", this.username);
        dict.put("phone", this.phone);
        dict.put("isActive", this.isActive);
        dict.put("createdAt", this.createdAt != null ? this.createdAt.toString() : null);
        dict.put("updatedAt", this.updatedAt != null ? this.updatedAt.toString() : null);
        return dict;
    }

    /**
     * Atualiza os campos do usuário a partir de um Map
     * @param data Map com os dados a serem atualizados
     */
    public void updateFromMap(Map<String, Object> data) {
        if (data.containsKey("name")) {
            this.name = (String) data.get("name");
        }
        if (data.containsKey("email")) {
            this.email = (String) data.get("email");
        }
        if (data.containsKey("username")) {
            this.username = (String) data.get("username");
        }
        if (data.containsKey("phone")) {
            this.phone = (String) data.get("phone");
        }
        if (data.containsKey("isActive")) {
            this.isActive = (Boolean) data.get("isActive");
        }
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s'}", id, username);
    }
}
