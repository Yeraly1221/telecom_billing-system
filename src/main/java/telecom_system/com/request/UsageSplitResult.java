package telecom_system.com.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsageSplitResult {
    Integer trafficUsed;
    Integer userPaid;
    Integer trafficAfterUsing;
    BigDecimal CustomerMustPay ;
}
