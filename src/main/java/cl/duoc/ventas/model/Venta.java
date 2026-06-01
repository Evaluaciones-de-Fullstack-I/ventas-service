package cl.duoc.ventas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long pedidoId;

    @Column(nullable = false)
    private Long vendedorId;

    @Column(nullable = false)
    private Double montoTotal;

    @Column(nullable = false)
    private Double comisionPlataforma; // Lo que gana Paris (ej. 15%)

    @Column(nullable = false)
    private Double gananciaVendedor; // Lo que se le paga al vendedor (85%)

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}