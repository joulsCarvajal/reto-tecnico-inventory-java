package com.reto.tecnico.infrastructure.web.controller;

import com.reto.tecnico.application.dto.request.EnviarInventarioRequest;
import com.reto.tecnico.application.dto.request.InventarioRequest;
import com.reto.tecnico.application.dto.response.ApiResponse;
import com.reto.tecnico.application.dto.response.EmpresaResponse;
import com.reto.tecnico.application.dto.response.InventarioResponse;
import com.reto.tecnico.application.service.impl.EmpresaServiceImpl;
import com.reto.tecnico.application.service.impl.InventarioServiceImpl;
import com.reto.tecnico.infrastructure.email.SendGridEmailService;
import com.reto.tecnico.infrastructure.pdf.InventarioPdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private final InventarioServiceImpl inventarioService;
    private final EmpresaServiceImpl empresaService;
    private final InventarioPdfService pdfService;
    private final SendGridEmailService emailService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventarioResponse>>> findByEmpresa(
            @RequestParam String empresaNit) {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.findByEmpresa(empresaNit)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioResponse>> upsert(
            @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Inventario actualizado", inventarioService.upsert(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        inventarioService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registro eliminado", null));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(@RequestParam String empresaNit) {
        EmpresaResponse empresa = empresaService.findByNit(empresaNit);
        List<InventarioResponse> inventario = inventarioService.findByEmpresa(empresaNit);
        byte[] pdf = pdfService.generarPdfInventario(empresa.getNombre(), empresa.getNit(), inventario);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=inventario_" + empresaNit + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/enviar-correo")
    public ResponseEntity<ApiResponse<Void>> enviarPorCorreo(
            @Valid @RequestBody EnviarInventarioRequest request) {
        try {
            EmpresaResponse empresa = empresaService.findByNit(request.getEmpresaNit());
            List<InventarioResponse> inventario = inventarioService.findByEmpresa(request.getEmpresaNit());
            byte[] pdf = pdfService.generarPdfInventario(empresa.getNombre(), empresa.getNit(), inventario);
            emailService.enviarInventarioPdf(request.getDestinatario(),
                    empresa.getNombre(), empresa.getNit(), pdf);
            return ResponseEntity.ok(ApiResponse.ok("Correo enviado a " + request.getDestinatario(), null));
        } catch (Exception e) {
            log.error("Error enviando correo: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al enviar correo: " + e.getMessage()));
        }
    }
}
