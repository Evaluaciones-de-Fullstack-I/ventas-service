package cl.duoc.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaRequestDTO {
    
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El ID del vendedor es obligatorio")
    private Long vendedorId;

    @NotNull(message = "El monto total es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Double montoTotal;
}
