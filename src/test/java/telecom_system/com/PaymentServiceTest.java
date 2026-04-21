package telecom_system.com;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telecom_system.com.entity.Account;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.Payment;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.AccountRepository;
import telecom_system.com.repository.PaymentRepository;
import telecom_system.com.service.PaymentService;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentService paymentService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setBalance(new BigDecimal(100));
        Customer customer = new Customer();
        customer.setName("Karly");
        customer.setIin("12345678");
        customer.setAccount(account);
        account.setCustomer(customer);
    }


    @Test
    void createAccount() {
        paymentService.createAccount(account);
        verify(accountRepository).save(account);
    }

    @Test
    void withDrawBalanceSuccess() {
        paymentService.withDrawBalance(account, new BigDecimal(90), PaymentType.WITHDRAW);

        assertEquals(new BigDecimal(10), account.getBalance());
    }

    @Test
    void withDrawBalanceFail(){
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.withDrawBalance(account, new BigDecimal(120), PaymentType.WITHDRAW);
        });
    }

    @Test
    void withDrawBalanceWithMinusNumber(){
        assertThrows(BusinessException.class, () -> {
            paymentService.withDrawBalance(account, new BigDecimal(-120), PaymentType.WITHDRAW);
        });
    }

    @Test
    void topUpBalance() {
        paymentService.topUpBalance(account, new BigDecimal(120), PaymentType.WITHDRAW);

        assertEquals(new BigDecimal(220), account.getBalance());
    }

    @Test void topUpBalanceWithMinusNumber(){
        assertThrows(BusinessException.class, () -> {
            paymentService.topUpBalance(account, new BigDecimal(-120), PaymentType.WITHDRAW);
        });
    }

    @Test
    void getCustomerBalance() {
        assertEquals(new BigDecimal(10), account.getBalance());
    }

}