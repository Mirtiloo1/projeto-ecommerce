package backend.backend.dto;

import backend.backend.model.Role;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;

    private Set<String> roles;

    public UsuarioResponseDTO(Long id, String nome, String email, Set<Role> roles) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.roles = roles.stream()
                .map(Role::getNomeRole)
                .collect(Collectors.toSet());
    }
}
