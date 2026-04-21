package telecom_system.com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import telecom_system.com.TrafficType;
import java.math.BigDecimal;

@Entity
@Table(name = "global_rate")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//we use this table weh we need to know price for one SMS, MINUTE CALL or MEGABYTE
public class GlobalRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "traffic_type")
    @Enumerated(EnumType.STRING)
    private TrafficType trafficType;


    @Column(name = "price")
    private BigDecimal price;
}
