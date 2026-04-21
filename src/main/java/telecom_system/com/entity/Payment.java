package telecom_system.com.entity;

import jakarta.persistence.*;
import lombok.*;
import telecom_system.com.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false,
            name = "amount")
    private BigDecimal amount;


    @Column(name = "payment_type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
