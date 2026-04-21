package telecom_system.com.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telecom_system.com.TrafficType;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.UsageStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageRecord {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false,
            name = "amount")
    private Integer amount;


    @Enumerated(EnumType.STRING)
    @Column(name = "traffic_type")
    public TrafficType trafficType;


    @Column(name = "usage_status")
    private UsageStatus usageStatus;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void setCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;


    public void setUsageStatus(UsageStatus usageStatus) {
        if(this.usageStatus == UsageStatus.PROCESSED && usageStatus == UsageStatus.NEW){
            throw new BusinessException("SubscriptionStatus can not be changed from processed to new");
        }
        this.usageStatus = usageStatus;
    }

}
