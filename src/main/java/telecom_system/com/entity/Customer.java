package telecom_system.com.entity;

import jakarta.persistence.*;
import lombok.*;
import telecom_system.com.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    @Column(unique = true,
            nullable = false
    )
    private String iin;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    // When object will creating createdAt will set with local time
    protected void setCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_id")
    private Account account;


    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<SimCard> simCards = new ArrayList<>();


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private  List<Payment> payments;


    public void addSubscription(Subscription subscription){
        if(subscription == null){
            throw new BusinessException("Subscription can not be null");
        }
        this.subscriptions.add(subscription);
    }


    public void setIin(String iin){
        if(iin.length() != 8){
            throw new BusinessException("Customer iin must have 8 digits");
        }else if(iin.length() == 8){
            this.iin = iin;
        }
    }

    public void  setPayment(Payment payment){
        if(payment == null){
            throw new BusinessException("Payment can not be null");
        }
        this.payments.add(payment);
    }


    public void setSimCard(SimCard simCard){
        if(simCard == null){
            throw new BusinessException("Simcard can not be null");
        }
        this.simCards.add(simCard);
        simCard.setCustomer(this);
    }
}
