package com.finance.dto;

import com.finance.enums.Role;
import com.finance.enums.UserStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
