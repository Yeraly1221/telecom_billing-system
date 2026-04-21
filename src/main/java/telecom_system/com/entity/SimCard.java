package telecom_system.com.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telecom_system.com.PaymentType;

@Entity
@Table(name = "sim_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimCard {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;


    @Column(name = "phone_number")
    private String phoneNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
