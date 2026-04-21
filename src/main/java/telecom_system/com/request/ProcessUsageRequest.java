package telecom_system.com.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telecom_system.com.PaymentType;
import telecom_system.com.TrafficType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ProcessUsageRequest {

    private Long customerId;
    private TrafficType trafficType;
    private Integer amount;
    private PaymentType paymentType;

    // getters & setters
}
