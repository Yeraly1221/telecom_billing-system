package telecom_system.com.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.entity.Customer;
import telecom_system.com.repository.CustomerRepository;

import java.util.List;

// каждый сервис делает только свою часть и незнает про остальные сервисы
//Domain services НЕ должны управлять бизнес-процессом
@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;


    public void createCustomer(Customer customer){
        customerRepository.save(customer);
    }


    public Customer getCustomerById(long customer_id){
        return customerRepository.findById(customer_id)
                .orElseThrow(() -> new BusinessException("Customer not found"));
    }


    public void isCustomerExistByInn(String iin){
        if(customerRepository.existsByIin(iin)){
            throw new BusinessException("Customer with this iin already exist");
        }
    }


    public List<Customer> getAllCustomers(){
       return customerRepository.findAll();
    }


}


