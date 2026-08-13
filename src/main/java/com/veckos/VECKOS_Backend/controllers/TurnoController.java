package com.veckos.VECKOS_Backend.controllers;

import com.veckos.VECKOS_Backend.dtos.turno.TurnoConUsuariosDto;
import com.veckos.VECKOS_Backend.dtos.turno.TurnoDto;
import com.veckos.VECKOS_Backend.dtos.usuario.UsuarioInfoDto;
import com.veckos.VECKOS_Backend.entities.Turno;
import com.veckos.VECKOS_Backend.services.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<TurnoDto>> getAllTurnos() {
        List<Turno> turnos = turnoService.findAll();
        List<TurnoDto> turnosDto = turnos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(turnosDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<TurnoDto> getTurnoById(@PathVariable Long id) {
        Turno turno = turnoService.findById(id);
        return ResponseEntity.ok(convertToDto(turno));
    }

    @GetMapping("/por-dia/{diaSemana}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<TurnoDto>> getTurnosByDiaSemana(@PathVariable DayOfWeek diaSemana) {
        List<Turno> turnos = turnoService.findByDiaSemanaOrderByHoraAsc(diaSemana);
        List<TurnoDto> turnosDto = turnos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(turnosDto);
    }

    @GetMapping("/con-usuarios/{diaSemana}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<TurnoConUsuariosDto>> getTurnosConUsuariosByDiaSemana(@PathVariable DayOfWeek diaSemana) {
        List<Turno> turnos = turnoService.findTurnosByDiaSemanaConUsuarios(diaSemana);
        List<TurnoConUsuariosDto> response = turnos.stream().map(TurnoConUsuariosDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-ocupacion")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<TurnoDto>> getTurnosByOcupacion() {
        List<Turno> turnos = turnoService.findAllOrderByOcupacion();
        List<TurnoDto> turnosDto = turnos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(turnosDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTurno(@Valid @RequestBody TurnoDto turnoDto) {
        try {
            Turno turno = convertToEntity(turnoDto);
            Turno savedTurno = turnoService.save(turno);
            return new ResponseEntity<>(convertToDto(savedTurno), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Capturar la excepción si ya existe un turno en el mismo día y hora
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTurno(
            @PathVariable Long id,
            @Valid @RequestBody TurnoDto turnoDto) {

        try {
            Turno turno = convertToEntity(turnoDto);
            Turno updatedTurno = turnoService.update(id, turno);
            return ResponseEntity.ok(convertToDto(updatedTurno));
        } catch (IllegalStateException e) {
            // Capturar la excepción si ya existe un turno en el mismo día y hora
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTurno(@PathVariable Long id) {
        try {
            turnoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // Capturar la excepción si el turno tiene inscripciones o clases asociadas
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    /**
     * Convierte una entidad Turno a su DTO correspondiente
     */
    private TurnoDto convertToDto(Turno turno) {
        return new TurnoDto(turno);
    }

    /**
     * Convierte un DTO de Turno a su entidad correspondiente
     */
    private Turno convertToEntity(TurnoDto turnoDto) {
        Turno turno = new Turno();
        turno.setId(turnoDto.getId());
        turno.setDiaSemana(turnoDto.getDiaSemana());
        turno.setHora(turnoDto.getHora());
        turno.setDescripcion(turnoDto.getDescripcion());
        return turno;
    }
}
