package com.veckos.VECKOS_Backend.controllers;

import com.veckos.VECKOS_Backend.dtos.asistencia.AsistenciaInfoDto;
import com.veckos.VECKOS_Backend.dtos.asistencia.AsistenciaRegistrarDto;
import com.veckos.VECKOS_Backend.entities.Asistencia;
import com.veckos.VECKOS_Backend.services.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<AsistenciaInfoDto>> getAllAsistencias() {
        List<Asistencia> asistencias = asistenciaService.findAll();
        List<AsistenciaInfoDto> response = asistencias.stream()
                .map(AsistenciaInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<AsistenciaInfoDto> getAsistenciaById(@PathVariable Long id) {
        Asistencia asistencia = asistenciaService.findById(id);
        AsistenciaInfoDto asistenciaInfoDto = new AsistenciaInfoDto(asistencia);
        return ResponseEntity.ok(asistenciaInfoDto);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<AsistenciaInfoDto>> getAsistenciasByUsuarioId(@PathVariable Long usuarioId) {
        List<Asistencia> asistencias = asistenciaService.findByUsuarioId(usuarioId);
        List<AsistenciaInfoDto> response = asistencias.stream()
                .map(AsistenciaInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clase/{claseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<AsistenciaInfoDto>> getAsistenciasByClaseId(@PathVariable Long claseId) {
        List<Asistencia> asistencias = asistenciaService.findByClaseId(claseId);
        List<AsistenciaInfoDto> response = asistencias.stream()
                .map(AsistenciaInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/fecha")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<AsistenciaInfoDto>> getAsistenciasByUsuarioIdAndFecha(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Asistencia> asistencias = asistenciaService.findByUsuarioIdAndFechaBetween(usuarioId, fechaInicio, fechaFin);
        List<AsistenciaInfoDto> response = asistencias.stream()
                .map(AsistenciaInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<AsistenciaInfoDto> registrarAsistencia(@Valid @RequestBody AsistenciaRegistrarDto asistenciaDto) {
        Asistencia asistencia = asistenciaService.registrarAsistencia(
                asistenciaDto.getClaseId(),
                asistenciaDto.getUsuarioId(),
                asistenciaDto.getPresente());
        AsistenciaInfoDto asistenciaInfoDto = new AsistenciaInfoDto(asistencia);
        return ResponseEntity.ok(asistenciaInfoDto);
    }

    @PostMapping("/clase/{claseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<AsistenciaInfoDto>> registrarAsistenciasPorClase(
            @PathVariable Long claseId,
            @RequestBody List<Long> usuariosPresentes) {
        List<Asistencia> asistencias = asistenciaService.registrarAsistenciasPorClase(claseId, usuariosPresentes);
        List<AsistenciaInfoDto> response = asistencias.stream()
                .map(AsistenciaInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAsistencia(@PathVariable Long id) {
        asistenciaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estadisticas/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Long> getEstadisticasAsistenciaUsuario(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Long cantidadAsistencias = asistenciaService.countAsistenciasByUsuarioIdAndFechaBetween(
                usuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(cantidadAsistencias);
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getRankingUsuariosPorAsistencia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Object[]> ranking = asistenciaService.findUsuariosConMayorAsistenciaEnPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(ranking);
    }
}
