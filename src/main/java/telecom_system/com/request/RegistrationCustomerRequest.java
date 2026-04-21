package telecom_system.com.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telecom_system.com.entity.Account;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationCustomerRequest {
    private String name;
    private String iin;
    private String phoneNumber;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerGetRequest {
        private Long id;
        private String name;
        private String iin;
        private Long tariff_id;
        private Account account;
        private List<Long> payment_ids;
        private List<String> simNumbers;
        private List<Long> subscription_ids;
    }
}
