package telecom_system.com.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telecom_system.com.SubscriptionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemainingOfTariff {
    String name;
    SubscriptionStatus subscriptionStatus;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer megabyte;
    Integer minutes;
    Integer sms;
}
