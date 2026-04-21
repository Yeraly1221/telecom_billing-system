package telecom_system.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import telecom_system.com.entity.Account;
import telecom_system.com.request.BalanceRequest;
import telecom_system.com.request.TopUpBalanceRequest;
import telecom_system.com.service.CustomerService;
import telecom_system.com.service.PaymentService;

@Controller
@RestController
@RequestMapping("api/payments")
public class PaymentController {
    PaymentService paymentService;
    CustomerService customerService;

    PaymentController(PaymentService paymentService, CustomerService customerService){
        this.paymentService = paymentService;
        this.customerService = customerService;
    }

    @PutMapping("/top-up")
    public void TopUpYourBalance(@RequestBody TopUpBalanceRequest request){
        paymentService.topUpBalance(customerService.getCustomerById(request.getId()).getAccount(), request.getAmount(), request.getPaymentType());
    }

    @GetMapping("/information/{customerId}")
    public BalanceRequest getBalance(@PathVariable Long customerId){
        Account account = customerService.getCustomerById(customerId).getAccount();
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setBalance(account.getBalance());
        return balanceRequest;
    }



}
