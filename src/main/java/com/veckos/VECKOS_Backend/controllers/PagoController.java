package com.veckos.VECKOS_Backend.controllers;

import com.veckos.VECKOS_Backend.dtos.pago.PagoCrearDto;
import com.veckos.VECKOS_Backend.dtos.pago.PagoInfoDto;
import com.veckos.VECKOS_Backend.entities.Cuenta;
import com.veckos.VECKOS_Backend.entities.Inscripcion;
import com.veckos.VECKOS_Backend.entities.Pago;
import com.veckos.VECKOS_Backend.services.CuentaService;
import com.veckos.VECKOS_Backend.services.InscripcionService;
import com.veckos.VECKOS_Backend.services.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<PagoInfoDto>> getAllPagos() {
        List<Pago> pagos = pagoService.findAll();
        List<PagoInfoDto> response = pagos.stream().map(PagoInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<PagoInfoDto> getPagoById(@PathVariable Long id) {
        Pago pago = pagoService.findById(id);
        PagoInfoDto response = new PagoInfoDto(pago);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/inscripcion/{inscripcionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<PagoInfoDto>> getPagosByInscripcionId(@PathVariable Long inscripcionId) {
        List<Pago> pagos = pagoService.findByInscripcionId(inscripcionId);
        List<PagoInfoDto> response = pagos.stream().map(PagoInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<PagoInfoDto>> getPagosByUsuarioId(@PathVariable Long usuarioId) {
        List<Pago> pagos = pagoService.findByUsuarioId(usuarioId);
        List<PagoInfoDto> response = pagos.stream().map(PagoInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<PagoInfoDto>> getPagosByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Pago> pagos = pagoService.findByFechaPagoBetween(fechaInicio, fechaFin);
        List<PagoInfoDto> response = pagos.stream().map(PagoInfoDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<PagoInfoDto> registrarPago(@Valid @RequestBody PagoCrearDto pagoDto) {
        // Validar que exista la inscripción
        inscripcionService.findById(pagoDto.getInscripcionId());

        // Convertir DTO a entidad
        Pago pago = new Pago();
        pago.setMonto(pagoDto.getMonto());
        pago.setFechaPago(pagoDto.getFechaPago() != null ? pagoDto.getFechaPago() : LocalDate.now());
        pago.setMetodoPago(pagoDto.getMetodoPago());
        if (Pago.MetodoPago.TRANSFERENCIA.equals(pago.getMetodoPago())) {
            Cuenta cuenta = cuentaService.obtenerCuentaPorId(pagoDto.getCuentaId());
            pago.setCuenta(cuenta);
        }
        pago.setDescripcion(pagoDto.getDescripcion());

        // Registrar el pago
        Pago registrado = pagoService.registrarPago(pagoDto.getInscripcionId(), pago);
        PagoInfoDto response = new PagoInfoDto(registrado);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<PagoInfoDto> updatePago(
            @PathVariable Long id,
            @Valid @RequestBody PagoCrearDto pagoDto) {

        // Convertir DTO a entidad
        Pago pago = new Pago();
        pago.setMonto(pagoDto.getMonto());
        pago.setFechaPago(pagoDto.getFechaPago());
        pago.setMetodoPago(pagoDto.getMetodoPago());
        pago.setDescripcion(pagoDto.getDescripcion());

        if (Pago.MetodoPago.TRANSFERENCIA.equals(pago.getMetodoPago())) {
            Cuenta cuenta = cuentaService.obtenerCuentaPorId(pagoDto.getCuentaId());
            pago.setCuenta(cuenta);
        }else{
            pago.setCuenta(null);
        }
        Pago updatedPago = pagoService.update(id, pago);
        PagoInfoDto response = new PagoInfoDto(updatedPago);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePago(@PathVariable Long id) {
        pagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estadisticas/periodo/suma")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getSumaPagosPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal suma = pagoService.sumMontoByFechaPagoBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(suma);
    }

    @GetMapping("/estadisticas/periodo/metodo-pago")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getEstadisticasPorMetodoPago(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Object[]> estadisticas = pagoService.countPagosByMetodoPagoAndFechaPagoBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas/periodo/mensual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getEstadisticasMensuales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Object[]> estadisticas = pagoService.sumMontoByMesAndAnio(fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }
}
