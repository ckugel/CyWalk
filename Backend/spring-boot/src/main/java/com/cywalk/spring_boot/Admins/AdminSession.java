package com.cywalk.spring_boot.Admins;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
public class AdminSession {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Schema(description = "Admin session key", example = "1")
    private Long key;

    @JoinColumn(name = "admin_id")
    @ManyToOne
    @Schema(description = "Admin entity pointer")
    private Admin admin;


    public AdminSession(Long key, Admin admin) {
        this.key = key;
        this.admin = admin;
    }

    public AdminSession(Admin admin) {
        this.admin = admin;
    }

    public AdminSession() {

    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}