package com.darwin.authservice.infrastructure.r2dbc.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "user")
public class UserEntity {
    @Id
    private Long id;
    private String email;
    private String password;
    private String role;
}
