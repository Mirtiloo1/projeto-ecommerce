package backend.backend.controller;

import backend.backend.dto.UsuarioLoginDTO;
import backend.backend.dto.UsuarioRegisterDTO;
import backend.backend.dto.AuthResponseDTO;
import backend.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UsuarioRegisterDTO registerDTO) {
        AuthResponseDTO response = usuarioService.register(registerDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody UsuarioLoginDTO loginDTO) {
        AuthResponseDTO response = usuarioService.login(loginDTO);
        return ResponseEntity.ok(response);
    }
}