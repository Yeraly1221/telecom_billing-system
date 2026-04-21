package telecom_system.com;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telecom_system.com.entity.Customer;
import telecom_system.com.repository.CustomerRepository;
import telecom_system.com.service.BillingService;
import telecom_system.com.entity.GlobalRate;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.UsageRecord;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.GlobalRateRepository;
import telecom_system.com.request.UsageSplitResult;
import telecom_system.com.service.CustomerService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp(){
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Yeraly");
        customer.setIin("01010102");
        //customer have created at by default

    }

    @Test
    void createCustomer_Success() {
        //Create and save
        customerService.createCustomer(customer);

        //Check
        verify(customerRepository).save(customer);

    }

    @Test
    void VerifyCorrectDataSaved(){
        //Create

        Long id = 2L;
        String name = "Yerasyl";
        String iin =  "12345678";
        Customer newCustomer = new Customer();
        newCustomer.setId(id);
        newCustomer.setName(name);
        newCustomer.setIin(iin);

        //Save
        customerRepository.save(newCustomer);

        //Check

        verify(customerRepository).save(newCustomer);

        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void getCustomerById() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("Yeraly", result.getName());
        assertEquals("01010102", result.getIin());
    }

    @Test
    void getCustomerNotFound(){
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            customerService.getCustomerById(999L);
        });
    }

    @Test
    void isCustomerExistByInnException() {
        when(customerRepository.existsByIin("12345678"))
                .thenThrow( new BusinessException("Customer iin does not exist"));

        assertThrows(BusinessException.class, () ->{
                customerService.isCustomerExistByInn("12345678");});

    }

}