package telecom_system.com.usecase;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.PaymentType;
import telecom_system.com.TrafficType;
import telecom_system.com.request.UsageSplitResult;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.UsageRecord;
import telecom_system.com.service.*;

@AllArgsConstructor
@Service
public class ProcessUsageUseCase {
    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final UsageService usageService;
    private final BillingService billingService;

    @Transactional
    public void processUsage(Long customer_id, TrafficType trafficType, Integer amount, PaymentType paymentType){
        Subscription subscription = subscriptionService.getCustomerWithActiveSubscriptionStatus(customer_id);


        // 1. create + save usage immediately
        UsageRecord usageRecord =
                usageService.createUsageRecord(trafficType, amount, subscription);
        try {

            // 2. calculate billing
            UsageSplitResult result =
                    billingService.calculateSplit(usageRecord, subscription);

            // 3. update subscription
            subscriptionService.updateRemains(subscription, trafficType, result);

            // 4. charge account
            paymentService.withDrawBalance(subscription.getCustomer().getAccount(), result.getCustomerMustPay(), paymentType);

            // 5. mark success
            usageService.markProcessed(usageRecord);


        } catch (Exception e) {

            usageService.markFailed(usageRecord);

            throw e;
        }
    }
}
