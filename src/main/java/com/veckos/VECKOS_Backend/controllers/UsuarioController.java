package com.veckos.VECKOS_Backend.controllers;

import com.veckos.VECKOS_Backend.dtos.inscripcion.InscripcionInfoDto;
import com.veckos.VECKOS_Backend.dtos.usuario.UsuarioDetalleDto;
import com.veckos.VECKOS_Backend.dtos.usuario.UsuarioDto;
import com.veckos.VECKOS_Backend.dtos.usuario.UsuarioListItemDto;
import com.veckos.VECKOS_Backend.entities.Inscripcion;
import com.veckos.VECKOS_Backend.entities.Usuario;
import com.veckos.VECKOS_Backend.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<UsuarioListItemDto>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        List<UsuarioListItemDto> usuariosDto = usuarios.stream()
                .map(UsuarioListItemDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDto);
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<UsuarioDto>> getUsuariosActivos() {
        List<Usuario> usuarios = usuarioService.buscarUsuariosActivos();
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDto);
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<UsuarioDto>> getUsuariosPendientes() {
        List<Usuario> usuarios = usuarioService.buscarUsuariosPendientes();
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<UsuarioDetalleDto> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if(usuario.getInscripciones().size() > 0){
            Inscripcion inscripcion = usuario.obtenerInscripcionActiva();
            if(Objects.nonNull(inscripcion)){
                InscripcionInfoDto inscripcionInfoDto = new InscripcionInfoDto(inscripcion);
                UsuarioDetalleDto response = new UsuarioDetalleDto(usuario,inscripcionInfoDto);
                return ResponseEntity.ok(response);
            }else{
                inscripcion =usuario.getInscripciones().get(usuario.getInscripciones().size() - 1);
                InscripcionInfoDto inscripcionInfoDto = new InscripcionInfoDto(inscripcion);
                UsuarioDetalleDto response = new UsuarioDetalleDto(usuario,inscripcionInfoDto);
                return ResponseEntity.ok(response);
            }
        }else{
            UsuarioDetalleDto response = new UsuarioDetalleDto(usuario,null);
            return ResponseEntity.ok(response);
        }


    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<UsuarioDto>> buscarUsuarios(@RequestParam String termino) {
        List<Usuario> usuarios = usuarioService.buscarPorTermino(termino);
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDto> createUsuario(@Valid @RequestBody UsuarioDto usuarioDto) {
        // Verificar si ya existe un usuario con el mismo DNI
        if (usuarioService.existsByDni(usuarioDto.getDni())) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = convertToEntity(usuarioDto);
        Usuario savedUsuario = usuarioService.save(usuario);
        return new ResponseEntity<>(convertToDto(savedUsuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<UsuarioDto> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto usuarioDto) {

        Usuario usuario = convertToEntity(usuarioDto);
        Usuario updatedUsuario = usuarioService.update(id, usuario);
        return ResponseEntity.ok(convertToDto(updatedUsuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte una entidad Usuario a su DTO correspondiente
     */
    private UsuarioDto convertToDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setDni(usuario.getDni());
        dto.setCuil(usuario.getCuil());
        dto.setTelefono(usuario.getTelefono());
        dto.setCorreo(usuario.getCorreo());
        dto.setEstado(usuario.obtenerEstado());
        return dto;
    }

    /**
     * Convierte un DTO de Usuario a su entidad correspondiente
     */
    private Usuario convertToEntity(UsuarioDto usuarioDto) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioDto.getId());
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuarioDto.getApellido());
        usuario.setFechaNacimiento(usuarioDto.getFechaNacimiento());
        usuario.setDni(usuarioDto.getDni());
        usuario.setCuil(usuarioDto.getCuil());
        usuario.setTelefono(usuarioDto.getTelefono());
        usuario.setCorreo(usuarioDto.getCorreo());
        //usuario.setEstado(usuarioDto.getEstado());
        return usuario;
    }
}
