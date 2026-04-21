package telecom_system.com.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.*;
import telecom_system.com.entity.GlobalRate;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.UsageRecord;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.GlobalRateRepository;
import telecom_system.com.request.UsageSplitResult;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class BillingService {
    public final GlobalRateRepository globalRateRepository;

    //when Customer use traffic(mb, sms, minutes) function calculate split
    //if customer have traffic function use traffic
    //if customer has no traffic left function use customer money
    public UsageSplitResult calculateSplit(UsageRecord usageRecord, Subscription subscription ){
        TrafficType trafficType = usageRecord.getTrafficType();
        Integer amount = usageRecord.getAmount();

        if(amount <=  0){
            throw new BusinessException("Usage amount must be greater than 0");
        }

        GlobalRate globalRate = globalRateRepository.findByTrafficType(trafficType)
                .orElseThrow(() -> new BusinessException("Global rate with thi trafficType not found "));

        Integer remainingOfTariff;

        if(getRemainingTraffic(trafficType, subscription) < 0){
            throw new BusinessException("Customer remaining can not be less then 0");
        }else {
            remainingOfTariff = getRemainingTraffic(trafficType, subscription);
        }




        Integer trafficUsed = 0;
        Integer userPaid = 0;
        Integer trafficAfterUsing = 0;

        //get price for traffic(mb, sms, calls(minutes))
        if(trafficType == TrafficType.MEGABYTE){
            remainingOfTariff = subscription.getMegabyte();
        }else if(trafficType == TrafficType.MINUTES){
            remainingOfTariff = subscription.getMinutes();
        }else if(trafficType == TrafficType.SMS){
            remainingOfTariff = subscription.getSms();
        }

        if(amount > remainingOfTariff){
            trafficUsed = remainingOfTariff;
            trafficAfterUsing = 0;
            userPaid = amount - remainingOfTariff;
        }else{
            trafficUsed = amount;
            trafficAfterUsing = remainingOfTariff - amount;
            userPaid = 0;
        }


        BigDecimal customerMustPay = BigDecimal.valueOf(userPaid);
        customerMustPay = customerMustPay.multiply(globalRate.getPrice());

        return  new UsageSplitResult(trafficUsed, userPaid, trafficAfterUsing, customerMustPay);

    }

    public Integer getRemainingTraffic(TrafficType trafficType, Subscription subscription) {
        return switch(trafficType) {
            case MEGABYTE -> subscription.getMegabyte();
            case MINUTES -> subscription.getMinutes();
            case SMS -> subscription.getSms();
            default -> throw new BusinessException("Unknown traffic type: " + trafficType);
        };
    }
}
