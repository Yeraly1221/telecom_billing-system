package telecom_system.com.usecase;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.Tariff;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.SubscriptionRepository;
import telecom_system.com.repository.TariffRepository;
import telecom_system.com.service.CustomerService;
import telecom_system.com.service.SubscriptionService;
import telecom_system.com.service.TariffService;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class ActivateTariffUseCase {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final CustomerService customerService;
    private final TariffRepository tariffRepository;
    private final TariffService tariffService;

    @Transactional
    public void activateTariff(long customerId, long tariffId){

        Customer customer = customerService.getCustomerById(customerId);
        Tariff tariff = tariffService.getTariffById(tariffId);

        if (subscriptionService.isActiveSubscriptionByCustomerId(customerId)) {
            throw new BusinessException("Customer already has active subscription");
        }

        subscriptionService.activateSubscription(customer, tariff);
    }

    @Transactional
    public void addTariff(String name, BigDecimal price, Integer megabyte, Integer minutes, Integer sms){
        Tariff tariff = new Tariff();

        tariff.setName(name);
        tariff.setPrice(price);
        tariff.setMegabyteLimit(megabyte);
        tariff.setMinutesLimit(minutes);
        tariff.setSmsLimit(sms);

        tariffRepository.save(tariff);
    }
}
