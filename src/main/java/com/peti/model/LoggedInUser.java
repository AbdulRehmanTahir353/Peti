package com.peti.model;

import com.peti.constants.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUser
{
    private String jwtToken;
    private String email;
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String username;
    private boolean enabled;
}
