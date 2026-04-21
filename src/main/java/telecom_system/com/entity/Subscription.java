package telecom_system.com.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telecom_system.com.SubscriptionStatus;
import telecom_system.com.TrafficType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "remaining_megabyte",
            nullable = false)
    private Integer megabyte;


    @Column(name = "remaining_sms",
            nullable = false)
    private Integer sms;


    @Column(name = "remaining_minutes",
            nullable = false)
    private Integer minutes;


    @Enumerated(EnumType.STRING)
    @Column(name = "subscriptionStatus")
    private SubscriptionStatus subscriptionStatus;


    @Column(name = "start_date")
    private LocalDateTime startDate;


    @Column(name = "end_date")
    private LocalDateTime endDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;


    @OneToMany(mappedBy = "subscription",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<UsageRecord> usageRecords;


    public void applyUsage(TrafficType trafficType, Integer remainingAfter){
        if(remainingAfter == null){
            throw new IllegalArgumentException("Remaining of tariff can not bw null ");
        }

        if(remainingAfter < 0){
            throw new IllegalArgumentException("Remaining of tariff can not be less then 0");
        }

        if(trafficType == TrafficType.MEGABYTE){
            this.megabyte = remainingAfter;
        }
        else if (trafficType == TrafficType.SMS) {
            this.sms = remainingAfter;
        }
        else if(trafficType == TrafficType.MINUTES){
            this.setMinutes(remainingAfter);
        }
    }

}
