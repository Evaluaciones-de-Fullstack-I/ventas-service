package cl.duoc.ventas.controller;

import cl.duoc.ventas.dto.VentaRequestDTO;
import cl.duoc.ventas.dto.VentaResponseDTO;
import cl.duoc.ventas.model.Venta;
import cl.duoc.ventas.repository.VentaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Controlador para el registro de transacciones comerciales, cálculo de comisiones y reportes globales")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    private static final double PORCENTAJE_COMISION = 0.15; // 15% de comisión para el Marketplace

    @PostMapping("/registrar")
    @Operation(
        summary = "Registrar una nueva venta",
        description = "Registra un comprobante de venta tras recibir la confirmación de pago exitosa. Calcula de forma automática el 15% de comisión de la plataforma y el saldo neto del vendedor.",
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "Venta registrada e ingresada con éxito"
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "El formato de los datos de entrada es inválido"
            )
        }
    )
    public ResponseEntity<VentaResponseDTO> registrarVenta(
            @Valid @RequestBody(
                description = "Estructura JSON con los parámetros del pedido para procesar el desglose de la venta",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VentaRequestDTO.class),
                    examples = @ExampleObject(
                        name = "Ejemplo de Registro de Venta",
                        value = "{\n  \"pedidoId\": 10024,\n  \"vendedorId\": 105,\n  \"montoTotal\": 49990\n}"
                    )
                )
            )
            @org.springframework.web.bind.annotation.RequestBody VentaRequestDTO dto) {
        
        Venta venta = new Venta();
        venta.setPedidoId(dto.getPedidoId());
        venta.setVendedorId(dto.getVendedorId());
        venta.setMontoTotal(dto.getMontoTotal());

        double comision = dto.getMontoTotal() * PORCENTAJE_COMISION;
        double ganancia = dto.getMontoTotal() - comision;

        venta.setComisionPlataforma(comision);
        venta.setGananciaVendedor(ganancia);

        Venta guardada = ventaRepository.save(venta);
        return new ResponseEntity<>(convertirADto(guardada), HttpStatus.CREATED);
    }

    @GetMapping("/vendedor/{vendedorId}")
    @Operation(
        summary = "Consultar ventas por Vendedor",
        description = "Permite a un vendedor revisar el listado histórico de todas sus ventas asociadas junto a los desgloses de ganancias obtenidos.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Historial de ventas del vendedor recuperado con éxito"
            )
        }
    )
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorVendedor(@PathVariable Long vendedorId) {
        List<Venta> ventas = ventaRepository.findByVendedorId(vendedorId);
        List<VentaResponseDTO> response = ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reporte")
    @Operation(
        summary = "Obtener reporte global de ventas",
        description = "Endpoint administrativo para consultar la totalidad de las ventas registradas en la plataforma del Marketplace.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Reporte general consolidado exitosamente"
            )
        }
    )
    public ResponseEntity<List<VentaResponseDTO>> obtenerReporteGlobal() {
        List<Venta> ventas = ventaRepository.findAll();
        List<VentaResponseDTO> response = ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

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