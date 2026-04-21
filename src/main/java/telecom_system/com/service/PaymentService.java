package telecom_system.com.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import telecom_system.com.PaymentType;
import telecom_system.com.entity.Account;
import telecom_system.com.entity.Payment;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.AccountRepository;
import telecom_system.com.repository.PaymentRepository;
import java.math.BigDecimal;

@Getter
@Service
@AllArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;


    public void createAccount(Account account){
        account.setBalance(new BigDecimal("0.0"));
        accountRepository.save(account);
    }


    @Transactional
    public void withDrawBalance(Account account, BigDecimal price, PaymentType paymentType){
        if(price == null){
            throw new BusinessException("Price can not be null");
        }else if(price.compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException("Price can not be less then 0");
        }
        account.withDrawBalance(price);
        Payment payment = new Payment();
        payment.setPaymentType(paymentType);
        payment.setId(account.getCustomer().getId());
        payment.setAmount(price);
        paymentRepository.save(payment);
    }


    @Transactional
    public void topUpBalance(Account account, BigDecimal amount, PaymentType paymentType){
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Customer can not topUp Balance to 0 or less amount");
        }
        account.topUp(amount);
        Payment payment = new Payment();
        payment.setPaymentType(paymentType);
        payment.setId(account.getCustomer().getId());
        payment.setAmount(amount);
        paymentRepository.save(payment);
    }


    public BigDecimal getCustomerBalance(Account account){

        return account.getBalance();
    }

}
