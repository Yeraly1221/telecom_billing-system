package telecom_system.com.entity;

import jakarta.persistence.*;
//связывает java и Database типо класс == таблица
import lombok.*;
//он автоматом генерирует getters, setters и конструкторы
import java.math.BigDecimal;
// мы использум bigdecimal для более точности потомучто будем работать с деньгами
import java.util.List;

@Entity
@Table(name = "tariff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name",
            nullable = false)
    private String name;


    @Column(name = "price",
            nullable = false)
    private BigDecimal price;


    @Column(name = "internet_limit",
            nullable = false)
    private Integer megabyteLimit;


    @Column(name = "minutes_limit",
            nullable = false)
    private Integer minutesLimit;


    @Column(name = "sms_limit",
            nullable = false)
    private Integer smsLimit;


    @OneToMany(mappedBy = "tariff", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;
}