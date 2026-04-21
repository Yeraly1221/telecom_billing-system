package telecom_system.com.controller;

import org.springframework.web.bind.annotation.*;
import telecom_system.com.request.CreateTariffRequest;
import telecom_system.com.request.RemainingOfTariff;
import telecom_system.com.service.SubscriptionService;
import telecom_system.com.usecase.ActivateTariffUseCase;

@RestController
@RequestMapping("/api/customer/subscription")
public class SubscriptionController {
    private final ActivateTariffUseCase activateTariffUseCase;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(ActivateTariffUseCase activateTariffUseCase, SubscriptionService subscriptionService){
        this.activateTariffUseCase = activateTariffUseCase;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{customerId}")
    public RemainingOfTariff getInformationAboutCustomerTariff(@PathVariable long customerId){
        return subscriptionService.currentRemainFromTariff(customerId);

    }

    @PostMapping("/{customerId}/{tariffId}")
    public void activateTariff(@PathVariable long customerId, @PathVariable long tariffId){
        activateTariffUseCase.activateTariff(customerId, tariffId);
    }

    @PostMapping("/adminLevel")
    public void addTariff(@RequestBody CreateTariffRequest request){
        activateTariffUseCase.addTariff(
                request.getName(),
                request.getPrice(),
                request.getMegabyte(),
                request.getMinutes(),
                request.getSms()
        );
    }

}
