package telecom_system.com.entity;

import jakarta.persistence.*;
import lombok.*;
import telecom_system.com.exception.BusinessException;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer")
@EqualsAndHashCode(exclude = "customer")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Customer customer;


    @Column(name = "balance",
            nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;


    public void withDrawBalance(BigDecimal price) {
        if(price == null){
            throw new IllegalArgumentException("Price can not be null");
        }
        if(price.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Price must be greater then zero");
        }

        BigDecimal mirrorBalance = this.balance.subtract(price);
        if(mirrorBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Customer does not have enough money");
        }
        this.balance = mirrorBalance;
    }

    public void  setBalance(BigDecimal amount){
        if(amount == null){
            throw new IllegalArgumentException("Amount can not  be null");
        }
        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.balance = amount;
    }


    public void topUp(BigDecimal amount){
        if(amount == null){
            throw new IllegalArgumentException("Amount can not  be null");
        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater then 0");
        }
        this.balance = this.balance.add(amount);
    }
}
