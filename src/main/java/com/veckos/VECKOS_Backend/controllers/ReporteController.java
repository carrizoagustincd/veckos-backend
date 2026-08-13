package com.veckos.VECKOS_Backend.controllers;

import com.veckos.VECKOS_Backend.dtos.asistencia.AsistenciaInfoDto;
import com.veckos.VECKOS_Backend.dtos.reporte.ReporteAsistenciaGeneralDto;
import com.veckos.VECKOS_Backend.dtos.reporte.ReporteAsistenciaIndividualDto;
import com.veckos.VECKOS_Backend.dtos.reporte.ReporteAsistenciaRequestDto;
import com.veckos.VECKOS_Backend.dtos.reporte.ReporteFinancieroDto;
import com.veckos.VECKOS_Backend.entities.Asistencia;
import com.veckos.VECKOS_Backend.entities.Clase;
import com.veckos.VECKOS_Backend.entities.Inscripcion;
import com.veckos.VECKOS_Backend.entities.Pago;
import com.veckos.VECKOS_Backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private ClaseService claseService;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/asistencia")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<?> generarReporteAsistencia(@RequestBody ReporteAsistenciaRequestDto requestDto) {
        if(requestDto.getUsuarioId()!=null){
            ReporteAsistenciaIndividualDto reporte = reporteService.generarReporteAsistenciaIndividual(requestDto);
            return ResponseEntity.ok(reporte);
        }else{
            ReporteAsistenciaGeneralDto reporte = reporteService.generarReporteAsistenciaGeneral(requestDto);
            return ResponseEntity.ok(reporte);
        }
    }

    @GetMapping("/financiero")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteFinancieroDto> generarReporteFinanciero(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false, defaultValue = "false") Boolean agruparPorMes,
            @RequestParam(required = false, defaultValue = "false") Boolean agruparPorMetodoPago) {

        ReporteFinancieroDto reporte = reporteService.generarReporteFinanciero(fechaInicio, fechaFin, agruparPorMes,agruparPorMetodoPago);

        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/financiero/excel/periodo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarPagosPorPeriodoExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            ReporteFinancieroDto reporte = reporteService.generarReporteFinanciero(fechaInicio, fechaFin, false,false);
            byte[] excelBytes = excelExportService.exportPagosToExcel(reporte);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "pagos_periodo.xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inscripciones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<?> generarReporteInscripciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        Map<String, Object> reporte = new HashMap<>();

        // Datos básicos del reporte
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);

        // Obtener inscripciones en el período
        List<Inscripcion> inscripciones = inscripcionService.findByFechaFinBetween(fechaInicio, fechaFin);

        // Contar inscripciones
        reporte.put("totalInscripciones", inscripciones.size());

        // Agrupar por estado
        Map<Inscripcion.EstadoPago, Long> inscripcionesPorEstado = inscripciones.stream()
                .collect(Collectors.groupingBy(Inscripcion::getEstadoPago, Collectors.counting()));
        reporte.put("inscripcionesPorEstado", inscripcionesPorEstado);

        // Agrupar por frecuencia
        Map<Integer, Long> inscripcionesPorFrecuencia = inscripciones.stream()
                .collect(Collectors.groupingBy(Inscripcion::getFrecuencia, Collectors.counting()));
        reporte.put("inscripcionesPorFrecuencia", inscripcionesPorFrecuencia);

        // Incluir lista de inscripciones
        reporte.put("inscripciones", inscripciones);

        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<?> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        LocalDate inicioMesAnterior = hoy.minusMonths(1).withDayOfMonth(1);
        LocalDate finMesAnterior = inicioMes.minusDays(1);

        // Contar usuarios activos
        long usuariosActivos = usuarioService.cantidadUsuariosActivos();
        stats.put("usuariosActivos", usuariosActivos);

        // Contar usuarios próximos a vencer
        long usuariosProximosAVencer = inscripcionService.cantidadInscripcionesProximasAVencer();
        stats.put("usuariosProximosAVencer", usuariosProximosAVencer);

        // Calcular ingresos
        BigDecimal ingresosMesActual = pagoService.sumMontoByFechaPagoBetween(inicioMes, finMes);
        BigDecimal ingresosMesAnterior = pagoService.sumMontoByFechaPagoBetween(inicioMesAnterior, finMesAnterior);
        stats.put("ingresosMesActual", ingresosMesActual);
        stats.put("ingresosMesAnterior", ingresosMesAnterior);

        // Asistencias de hoy
        List<Clase> clasesHoy = claseService.findByFechaOrderByHora(hoy);
        long asistenciasHoy = clasesHoy.stream()
                .flatMap(c -> c.getAsistencias().stream())
                .filter(Asistencia::getPresente)
                .count();
        stats.put("asistenciasHoy", asistenciasHoy);

        return ResponseEntity.ok(stats);
    }
}
