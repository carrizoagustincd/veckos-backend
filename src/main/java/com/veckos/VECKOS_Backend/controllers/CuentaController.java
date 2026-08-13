package com.veckos.VECKOS_Backend.controllers;

import com.veckos.VECKOS_Backend.dtos.cuenta.CuentaDto;
import com.veckos.VECKOS_Backend.dtos.cuenta.CuentaRequestDto;
import com.veckos.VECKOS_Backend.services.CuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<CuentaDto>> obtenerTodasLasCuentas(){
        return ResponseEntity.ok(cuentaService.obtenerTodasLasCuentas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<CuentaDto> getCuentaPorId(@PathVariable Long id){
        return ResponseEntity.ok(cuentaService.obtenerCuentaDtoPorId(id));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuentaDto> postNuevaCuenta(@Valid @RequestBody CuentaRequestDto cuentaRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cuentaService.guardarCuenta(cuentaRequestDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuentaDto> postNuevaCuenta(@Valid @RequestBody CuentaRequestDto cuentaRequestDto, @PathVariable Long id){
        return ResponseEntity.ok(cuentaService.editarCuenta(id,cuentaRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCuentaPorId(@PathVariable Long id){
        cuentaService.eliminarCuentaPorId(id);
        return ResponseEntity.noContent().build();
    }
}
