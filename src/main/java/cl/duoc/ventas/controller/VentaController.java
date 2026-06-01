package cl.duoc.ventas.controller;

import cl.duoc.ventas.dto.VentaRequestDTO;
import cl.duoc.ventas.dto.VentaResponseDTO;
import cl.duoc.ventas.model.Venta;
import cl.duoc.ventas.repository.VentaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    private static final double PORCENTAJE_COMISION = 0.15; // 15% de comisión para el Marketplace

    // Registrar una nueva venta (Llamado luego de que el pago es exitoso)
    @PostMapping("/registrar")
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRequestDTO dto) {
        
        Venta venta = new Venta();
        venta.setPedidoId(dto.getPedidoId());
        venta.setVendedorId(dto.getVendedorId());
        venta.setMontoTotal(dto.getMontoTotal());

        // Cálculo de comisiones
        double comision = dto.getMontoTotal() * PORCENTAJE_COMISION;
        double ganancia = dto.getMontoTotal() - comision;

        venta.setComisionPlataforma(comision);
        venta.setGananciaVendedor(ganancia);

        Venta guardada = ventaRepository.save(venta);
        return new ResponseEntity<>(convertirADto(guardada), HttpStatus.CREATED);
    }

    // Vendedor consulta sus ventas
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorVendedor(@PathVariable Long vendedorId) {
        List<Venta> ventas = ventaRepository.findByVendedorId(vendedorId);
        List<VentaResponseDTO> response = ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Administrador obtiene el reporte global de ventas
    @GetMapping("/reporte")
    public ResponseEntity<List<VentaResponseDTO>> obtenerReporteGlobal() {
        List<Venta> ventas = ventaRepository.findAll();
        List<VentaResponseDTO> response = ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Método auxiliar
    private VentaResponseDTO convertirADto(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setPedidoId(venta.getPedidoId());
        dto.setVendedorId(venta.getVendedorId());
        dto.setMontoTotal(venta.getMontoTotal());
        dto.setComisionPlataforma(venta.getComisionPlataforma());
        dto.setGananciaVendedor(venta.getGananciaVendedor());
        dto.setFechaRegistro(venta.getFechaRegistro());
        return dto;
    }
}