package com.veckos.VECKOS_Backend.security.service;

import com.veckos.VECKOS_Backend.dtos.MessageResponseDto;
import com.veckos.VECKOS_Backend.entities.Rol;
import com.veckos.VECKOS_Backend.entities.UsuarioSistema;
import com.veckos.VECKOS_Backend.security.dtos.LoginDto;
import com.veckos.VECKOS_Backend.security.dtos.RegisterDto;
import com.veckos.VECKOS_Backend.security.jwt.JwtProvider;
import com.veckos.VECKOS_Backend.security.repositories.RolRepository;
import com.veckos.VECKOS_Backend.security.repositories.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    public Map<String, Object> login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");
        response.put("username", userDetails.getUsername());
        response.put("roles", userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList()));

        return response;
    }

    public ResponseEntity<?> register(RegisterDto registerDto){
        if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("Error: El nombre de usuario ya existe"));
        }

        if (usuarioRepository.existsByEmail(registerDto.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("Error: El correo electrónico ya está en uso"));
        }

        // Crear nuevo usuario
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setNombre(registerDto.getNombre());
        usuario.setApellido(registerDto.getApellido());
        usuario.setUsername(registerDto.getUsername());
        usuario.setEmail(registerDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Rol operatorRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_OPERADOR)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        Set<Rol> roles = Set.of(operatorRole);

        usuario.setRoles(roles);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponseDto("Usuario registrado exitosamente"));
    }

}
