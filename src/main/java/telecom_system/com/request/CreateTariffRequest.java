package telecom_system.com.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTariffRequest {
    String name;
    BigDecimal price;
    Integer megabyte;
    Integer minutes;
    Integer sms;
}
