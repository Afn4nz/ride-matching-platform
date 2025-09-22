package com.ridematch.auth.entity;

import com.ridematch.auth.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends AuditEntity {

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
