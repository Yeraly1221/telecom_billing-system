package telecom_system.com.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.*;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.Tariff;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.SubscriptionRepository;
import telecom_system.com.request.RemainingOfTariff;
import telecom_system.com.request.UsageSplitResult;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private static final int SUBSCRIPTION_PERIOD_MONTHS = 1;


    private Subscription createSubscription(Customer customer, Tariff tariff){
        if(customer == null){
            throw new BusinessException("Customer con not be null");
        }
        if(tariff == null){
            throw new BusinessException("Tariff can not be a null");
        }
        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setMegabyte(tariff.getMegabyteLimit());
        subscription.setSms(tariff.getSmsLimit());
        subscription.setMinutes(tariff.getMinutesLimit());
        subscription.setTariff(tariff);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(SUBSCRIPTION_PERIOD_MONTHS));
        subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        return subscription;
    }


    public Subscription getCustomerWithActiveSubscriptionStatus(long customerId){
        return subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(customerId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("Subscription not found"));
    }


    @Transactional
    public void activateSubscription(Customer customer, Tariff tariff){
        Subscription subscription = createSubscription(customer, tariff);
        subscriptionRepository.save(subscription);
    }


    @Transactional
    public void changeTariff(Customer customer, Tariff tariff){
        Subscription subscription = getCustomerWithActiveSubscriptionStatus(customer.getId());
        subscription.setSubscriptionStatus(SubscriptionStatus.CLOSE);
        subscriptionRepository.save(subscription);


        subscription = createSubscription(customer, tariff);
        subscriptionRepository.save(subscription);
    }


    @Transactional
    public RemainingOfTariff currentRemainFromTariff(long customerId){
        Subscription subscription = getCustomerWithActiveSubscriptionStatus(customerId);
        RemainingOfTariff remainingOfTariff = new RemainingOfTariff();

        remainingOfTariff.setName(subscription.getTariff().getName());
        remainingOfTariff.setSubscriptionStatus(subscription.getSubscriptionStatus());
        remainingOfTariff.setStartDate(subscription.getStartDate());
        remainingOfTariff.setEndDate(subscription.getEndDate());
        remainingOfTariff.setMegabyte(subscription.getMegabyte());
        remainingOfTariff.setMinutes(subscription.getMinutes());
        remainingOfTariff.setSms(subscription.getSms());

        return remainingOfTariff;
    }


    public boolean  isActiveSubscriptionByCustomerId(long customerId){
            return subscriptionRepository
                    .existsByCustomer_IdAndSubscriptionStatus(customerId, SubscriptionStatus.ACTIVE);

    }


    @Transactional
    public void updateRemains(Subscription subscription, TrafficType trafficType, UsageSplitResult usageSplitResult){
        Integer result = usageSplitResult.getTrafficAfterUsing();
        subscription.applyUsage(trafficType, result);
    }

}
