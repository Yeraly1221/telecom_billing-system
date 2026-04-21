package telecom_system.com.controller;

import org.springframework.web.bind.annotation.*;
import telecom_system.com.entity.Payment;
import telecom_system.com.entity.Subscription;
import telecom_system.com.request.RegistrationCustomerRequest;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.SimCard;
import telecom_system.com.service.CustomerService;
import telecom_system.com.usecase.RegisterCustomerUseCase;


@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final CustomerService customerService;
    public CustomerController(RegisterCustomerUseCase registerCustomerUseCase , CustomerService customerService){
        this.registerCustomerUseCase = registerCustomerUseCase;
        this.customerService = customerService;
    }

    @PostMapping("/registration")
    public void RegisterCustomer(@RequestBody RegistrationCustomerRequest request){
        System.out.println(request.getPhoneNumber());
        registerCustomerUseCase.registerCustomer(
                request.getName(),
                request.getIin(),
                request.getPhoneNumber()
        );
    }

    @GetMapping("/information/{id}")
    public RegistrationCustomerRequest.CustomerGetRequest GetInformationAboutCustomerById(@PathVariable Long id){
        Customer customer = customerService.getCustomerById(id);

        RegistrationCustomerRequest.CustomerGetRequest request = new RegistrationCustomerRequest.CustomerGetRequest();

        request.setId(customer.getId());
        request.setName(customer.getName());
        request.setIin(customer.getIin());
        request.setSimNumbers(
                customer.getSimCards()
                        .stream()
                        .map(SimCard::getPhoneNumber)
                        .toList()

        );
        request.setPayment_ids(
                customer.getPayments()
                        .stream()
                        .map(Payment::getId)
                        .toList()
        );
         request.setSubscription_ids(
                customer.getSubscriptions()
                        .stream()
                        .map(Subscription::getId)
                        .toList()
        );


        return request;

    }











}
