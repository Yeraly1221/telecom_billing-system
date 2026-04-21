package telecom_system.com.request;

import lombok.Getter;
import lombok.Setter;
import telecom_system.com.PaymentType;

import java.math.BigDecimal;

@Getter
@Setter
public class TopUpBalanceRequest {
    Long id;
    BigDecimal amount;
    PaymentType paymentType;
}
