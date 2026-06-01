package cl.duoc.ventas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VentaResponseDTO {
    private Long id;
    private Long pedidoId;
    private Long vendedorId;
    private Double montoTotal;
    private Double comisionPlataforma;
    private Double gananciaVendedor;
    private LocalDateTime fechaRegistro;
}