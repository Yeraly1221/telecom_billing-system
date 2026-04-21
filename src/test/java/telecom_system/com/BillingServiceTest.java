package telecom_system.com;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telecom_system.com.service.BillingService;
import telecom_system.com.entity.GlobalRate;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.UsageRecord;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.GlobalRateRepository;
import telecom_system.com.request.UsageSplitResult;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private GlobalRateRepository globalRateRepository;

    @InjectMocks
    private BillingService billingService;

    private Subscription subscription;
    private GlobalRate globalRate;

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setMegabyte(1000);
        subscription.setMinutes(500);
        subscription.setSms(200);

        globalRate = new GlobalRate();
        globalRate.setId(1L);
        globalRate.setTrafficType(TrafficType.MEGABYTE);
        globalRate.setPrice(BigDecimal.valueOf(0.5));
    }

    @Test
    void testCalculateSplit_UserHasEnoughTraffic() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(500);

        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        UsageSplitResult result = billingService.calculateSplit(usageRecord, subscription);

        assertEquals(500, result.getTrafficUsed());
        assertEquals(0, result.getUserPaid());
        assertEquals(500, result.getTrafficAfterUsing());
        assertTrue(result.getCustomerMustPay().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void testCalculateSplit_UserExceedsTraffic() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(1500);

        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        UsageSplitResult result = billingService.calculateSplit(usageRecord, subscription);

        assertEquals(1000, result.getTrafficUsed());
        assertEquals(500, result.getUserPaid());
        assertEquals(0, result.getTrafficAfterUsing());
        assertEquals(0, result.getCustomerMustPay().compareTo(BigDecimal.valueOf(250)));
    }

    @Test
    void testCalculateSplit_WithMinutesTraffic() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MINUTES);
        usageRecord.setAmount(100);

        GlobalRate minutesRate = new GlobalRate();
        minutesRate.setTrafficType(TrafficType.MINUTES);
        minutesRate.setPrice(BigDecimal.valueOf(1.0));

        when(globalRateRepository.findByTrafficType(TrafficType.MINUTES))
                .thenReturn(Optional.of(minutesRate));

        UsageSplitResult result = billingService.calculateSplit(usageRecord, subscription);

        assertEquals(100, result.getTrafficUsed());
        assertEquals(0, result.getUserPaid());
        assertEquals(400, result.getTrafficAfterUsing());
    }

    @Test
    void testCalculateSplit_WithSmsTraffic() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.SMS);
        usageRecord.setAmount(50);

        GlobalRate smsRate = new GlobalRate();
        smsRate.setTrafficType(TrafficType.SMS);
        smsRate.setPrice(BigDecimal.valueOf(2.0));

        when(globalRateRepository.findByTrafficType(TrafficType.SMS))
                .thenReturn(Optional.of(smsRate));

        UsageSplitResult result = billingService.calculateSplit(usageRecord, subscription);

        assertEquals(50, result.getTrafficUsed());
        assertEquals(0, result.getUserPaid());
        assertEquals(150, result.getTrafficAfterUsing());
    }

    @Test
    void testCalculateSplit_InvalidAmountZero() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(0);

        assertThrows(BusinessException.class, () ->
                billingService.calculateSplit(usageRecord, subscription)
        );
    }

    @Test
    void testCalculateSplit_InvalidAmountNegative() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(-100);

        assertThrows(BusinessException.class, () ->
                billingService.calculateSplit(usageRecord, subscription)
        );
    }

    @Test
    void testCalculateSplit_GlobalRateNotFound() {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(100);

        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                billingService.calculateSplit(usageRecord, subscription)
        );
    }

    @Test
    void testCalculateSplit_ZeroTrafficLeftButStillBuyMore() {
        subscription.setMegabyte(0);
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(100);

        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        UsageSplitResult result = billingService.calculateSplit(usageRecord, subscription);

        assertEquals(0, result.getTrafficUsed());
        assertEquals(100, result.getUserPaid());
        assertEquals(0, result.getTrafficAfterUsing());
        assertEquals(0, result.getCustomerMustPay().compareTo(BigDecimal.valueOf(50)));
    }
}