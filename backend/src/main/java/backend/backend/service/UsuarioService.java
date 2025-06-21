package backend.backend.service;

import backend.backend.dto.AuthResponseDTO;
import backend.backend.dto.UsuarioLoginDTO;
import backend.backend.dto.UsuarioRegisterDTO;
import backend.backend.dto.UsuarioResponseDTO;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.model.Role;
import backend.backend.model.Usuario;
import backend.backend.repository.RoleRepository;
import backend.backend.repository.UsuarioRepository;
import backend.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public AuthResponseDTO register(UsuarioRegisterDTO registerDTO) {
        if (usuarioRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email já registrado!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registerDTO.getNome());
        novoUsuario.setEmail(registerDTO.getEmail());

        novoUsuario.setSenha(passwordEncoder.encode(registerDTO.getSenha()));

        Role userRole = roleRepository.findByNomeRole("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'ROLE_USER' não encontrada!"));
        novoUsuario.setRoles(Collections.singleton(userRole));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        String jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        usuarioSalvo.getEmail(),
                        usuarioSalvo.getSenha(),
                        usuarioSalvo.getRoles().stream()
                                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getNomeRole()))
                                .collect(Collectors.toList())
                )
        );

        return new AuthResponseDTO(jwtToken, "Bearer", jwtService.getJwtExpiration(), usuarioSalvo.getEmail(), usuarioSalvo.getId());
    }

    public AuthResponseDTO login(UsuarioLoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado no banco de dados."));

        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(jwtToken, "Bearer", jwtService.getJwtExpiration(), usuarioAutenticado.getEmail(), usuarioAutenticado.getId());
    }

    public Optional<UsuarioResponseDTO> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getRoles()
                ));
    }

    public List<UsuarioResponseDTO> buscarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getRoles()
                ))
                .collect(Collectors.toList());
    }
}