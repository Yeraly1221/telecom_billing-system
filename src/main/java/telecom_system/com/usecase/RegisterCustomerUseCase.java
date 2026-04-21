package telecom_system.com.usecase;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import telecom_system.com.entity.Account;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.SimCard;
import telecom_system.com.entity.Subscription;
import telecom_system.com.repository.AccountRepository;
import telecom_system.com.repository.CustomerRepository;
import telecom_system.com.repository.SimCardRepository;
import telecom_system.com.repository.SubscriptionRepository;
import telecom_system.com.service.*;


@Service
public class RegisterCustomerUseCase {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public RegisterCustomerUseCase(CustomerRepository customerRepository, CustomerService customerService, SimCardRepository simCardRepository, SubscriptionRepository subscriptionRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }
    @Transactional
    public void registerCustomer(String name, String iin, String phoneNumber){
        customerService.isCustomerExistByInn(iin);//if true will threw exception

        Customer customer = new Customer();
        customer.setName(name);
        customer.setIin(iin);

        Account account = new Account();
        account.setCustomer(customer);
        customer.setAccount(account);

        SimCard simCard = new SimCard();
        simCard.setCustomer(customer);
        simCard.setPhoneNumber(phoneNumber);

        customer.setSimCard(simCard);
        customer.setAccount(account);

        customerRepository.save(customer);

    }
}
