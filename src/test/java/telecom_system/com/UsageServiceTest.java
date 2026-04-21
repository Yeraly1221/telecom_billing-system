package telecom_system.com;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.GlobalRate;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.UsageRecord;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.GlobalRateRepository;
import telecom_system.com.repository.UsageRecordRepository;
import telecom_system.com.service.UsageService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageServiceTest {

    @Mock
    private GlobalRateRepository globalRateRepository;

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @InjectMocks
    private UsageService usageService;

    private Subscription subscription;
    private Customer customer;
    private GlobalRate globalRate;
    private UsageRecord usageRecord;

    @BeforeEach
    void setUp() {
        // Создаём Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Yeraly");
        customer.setIin("12345678");

        // Создаём Subscription
        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCustomer(customer);
        subscription.setMegabyte(10000);
        subscription.setMinutes(500);
        subscription.setSms(200);

        // Создаём GlobalRate
        globalRate = new GlobalRate();
        globalRate.setId(1L);
        globalRate.setTrafficType(TrafficType.MEGABYTE);
        globalRate.setPrice(BigDecimal.valueOf(0.5));

        // Создаём UsageRecord
        usageRecord = new UsageRecord();
        usageRecord.setId(1L);
        usageRecord.setTrafficType(TrafficType.MEGABYTE);
        usageRecord.setAmount(500);
        usageRecord.setSubscription(subscription);
        usageRecord.setUsageStatus(UsageStatus.NEW);
    }

    // ========== ТЕСТЫ createUsageRecord ==========

    @Test
    void testCreateUsageRecord_Success() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.MEGABYTE,
                500,
                subscription
        );

        // Assert
        assertNotNull(result);
        assertEquals(UsageStatus.NEW, result.getUsageStatus());
        assertEquals(TrafficType.MEGABYTE, result.getTrafficType());
        assertEquals(500, result.getAmount());
        assertEquals(subscription, result.getSubscription());

        verify(usageRecordRepository).save(any(UsageRecord.class));
    }

    @Test
    void testCreateUsageRecord_VerifySaveCalledWithCorrectData() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        usageService.createUsageRecord(TrafficType.MINUTES, 100, subscription);

        // Assert - проверяем, что save был вызван с правильными данными
        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageRecordRepository).save(captor.capture());

        UsageRecord saved = captor.getValue();
        assertEquals(UsageStatus.NEW, saved.getUsageStatus());
        assertEquals(TrafficType.MINUTES, saved.getTrafficType());
        assertEquals(100, saved.getAmount());
        assertEquals(subscription, saved.getSubscription());
    }

    @Test
    void testCreateUsageRecord_WithMegabyte() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.MEGABYTE,
                1000,
                subscription
        );

        // Assert
        assertEquals(TrafficType.MEGABYTE, result.getTrafficType());
        assertEquals(1000, result.getAmount());
    }

    @Test
    void testCreateUsageRecord_WithMinutes() {
        // Arrange
        UsageRecord minutesRecord = new UsageRecord();
        minutesRecord.setTrafficType(TrafficType.MINUTES);
        minutesRecord.setAmount(50);
        minutesRecord.setUsageStatus(UsageStatus.NEW);
        minutesRecord.setSubscription(subscription);

        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(minutesRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.MINUTES,
                50,
                subscription
        );

        // Assert
        assertEquals(TrafficType.MINUTES, result.getTrafficType());
        assertEquals(50, result.getAmount());
    }

    @Test
    void testCreateUsageRecord_WithSms() {
        // Arrange
        UsageRecord smsRecord = new UsageRecord();
        smsRecord.setTrafficType(TrafficType.SMS);
        smsRecord.setAmount(25);
        smsRecord.setUsageStatus(UsageStatus.NEW);
        smsRecord.setSubscription(subscription);

        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(smsRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.SMS,
                25,
                subscription
        );

        // Assert
        assertEquals(TrafficType.SMS, result.getTrafficType());
        assertEquals(25, result.getAmount());
    }

    @Test
    void testCreateUsageRecord_AlwaysNewStatus() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.MEGABYTE,
                500,
                subscription
        );

        // Assert - статус ВСЕГДА должен быть NEW при создании
        assertEquals(UsageStatus.NEW, result.getUsageStatus());
    }

    // ========== ТЕСТЫ getGlobalRateByType ==========

    @Test
    void testGetGlobalRateByType_Megabyte() {
        // Arrange
        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        // Act
        BigDecimal result = usageService.getGlobalRateByType(TrafficType.MEGABYTE);

        // Assert
        assertEquals(BigDecimal.valueOf(0.5), result);
        verify(globalRateRepository).findByTrafficType(TrafficType.MEGABYTE);
    }

    @Test
    void testGetGlobalRateByType_Minutes() {
        // Arrange
        GlobalRate minutesRate = new GlobalRate();
        minutesRate.setTrafficType(TrafficType.MINUTES);
        minutesRate.setPrice(BigDecimal.valueOf(1.0));

        when(globalRateRepository.findByTrafficType(TrafficType.MINUTES))
                .thenReturn(Optional.of(minutesRate));

        // Act
        BigDecimal result = usageService.getGlobalRateByType(TrafficType.MINUTES);

        // Assert
        assertEquals(BigDecimal.valueOf(1.0), result);
    }

    @Test
    void testGetGlobalRateByType_Sms() {
        // Arrange
        GlobalRate smsRate = new GlobalRate();
        smsRate.setTrafficType(TrafficType.SMS);
        smsRate.setPrice(BigDecimal.valueOf(2.0));

        when(globalRateRepository.findByTrafficType(TrafficType.SMS))
                .thenReturn(Optional.of(smsRate));

        // Act
        BigDecimal result = usageService.getGlobalRateByType(TrafficType.SMS);

        // Assert
        assertEquals(BigDecimal.valueOf(2.0), result);
    }

    @Test
    void testGetGlobalRateByType_NotFound() {
        // Arrange
        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            usageService.getGlobalRateByType(TrafficType.MEGABYTE);
        });
    }

    @Test
    void testGetGlobalRateByType_ExceptionMessage() {
        // Arrange
        when(globalRateRepository.findByTrafficType(TrafficType.MINUTES))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            usageService.getGlobalRateByType(TrafficType.MINUTES);
        });

        assertTrue(exception.getMessage().contains("Global rate"));
        assertTrue(exception.getMessage().contains("trafficType"));
    }

    @Test
    void testGetGlobalRateByType_VerifyRepositoryCall() {
        // Arrange
        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        // Act
        usageService.getGlobalRateByType(TrafficType.MEGABYTE);

        // Assert
        verify(globalRateRepository).findByTrafficType(TrafficType.MEGABYTE);
        verify(globalRateRepository, times(1)).findByTrafficType(any());
    }

    // ========== ТЕСТЫ saveUsageRecord ==========

    @Test
    void testSaveUsageRecord_Success() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        usageService.saveUsageRecord(usageRecord);

        // Assert
        verify(usageRecordRepository).save(usageRecord);
    }

    @Test
    void testSaveUsageRecord_WithProcessedStatus() {
        // Arrange
        usageRecord.setUsageStatus(UsageStatus.PROCESSED);
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        usageService.saveUsageRecord(usageRecord);

        // Assert
        verify(usageRecordRepository).save(usageRecord);
    }

    @Test
    void testSaveUsageRecord_WithFailedStatus() {
        // Arrange
        usageRecord.setUsageStatus(UsageStatus.FAILED);
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        usageService.saveUsageRecord(usageRecord);

        // Assert
        verify(usageRecordRepository).save(usageRecord);
    }

    @Test
    void testSaveUsageRecord_VerifyCorrectObject() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        usageService.saveUsageRecord(usageRecord);

        // Assert
        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageRecordRepository).save(captor.capture());

        assertEquals(usageRecord.getId(), captor.getValue().getId());
        assertEquals(usageRecord.getAmount(), captor.getValue().getAmount());
    }

    // ========== ТЕСТЫ markProcessed ==========

    @Test
    void testMarkProcessed_Success() {
        // Arrange
        usageRecord.setUsageStatus(UsageStatus.NEW);

        // Act
        usageService.markProcessed(usageRecord);

        // Assert
        assertEquals(UsageStatus.PROCESSED, usageRecord.getUsageStatus());
    }

    @Test
    void testMarkProcessed_ChangesStatus() {
        // Arrange
        assertEquals(UsageStatus.NEW, usageRecord.getUsageStatus());

        // Act
        usageService.markProcessed(usageRecord);

        // Assert
        assertNotEquals(UsageStatus.NEW, usageRecord.getUsageStatus());
        assertEquals(UsageStatus.PROCESSED, usageRecord.getUsageStatus());
    }

    @Test
    void testMarkProcessed_DoesNotSave() {
        // Arrange
        // Act
        usageService.markProcessed(usageRecord);

        // Assert - markProcessed НЕ вызывает save() (только меняет статус)
        verify(usageRecordRepository, never()).save(any());
    }

    @Test
    void testMarkProcessed_MultipleTimesOverwrites() {
        // Arrange
        usageRecord.setUsageStatus(UsageStatus.NEW);

        // Act
        usageService.markProcessed(usageRecord);
        usageService.markProcessed(usageRecord); // Второй раз

        // Assert
        assertEquals(UsageStatus.PROCESSED, usageRecord.getUsageStatus());
    }

    // ========== ТЕСТЫ markFailed ==========

    @Test
    void testMarkFailed_Success() {
        // Arrange
        usageRecord.setUsageStatus(UsageStatus.NEW);

        // Act
        usageService.markFailed(usageRecord);

        // Assert
        assertEquals(UsageStatus.FAILED, usageRecord.getUsageStatus());
    }

    @Test
    void testMarkFailed_ChangesStatus() {
        // Arrange
        assertEquals(UsageStatus.NEW, usageRecord.getUsageStatus());

        // Act
        usageService.markFailed(usageRecord);

        // Assert
        assertNotEquals(UsageStatus.NEW, usageRecord.getUsageStatus());
        assertEquals(UsageStatus.FAILED, usageRecord.getUsageStatus());
    }

    @Test
    void testMarkFailed_DoesNotSave() {
        // Arrange
        // Act
        usageService.markFailed(usageRecord);

        // Assert - markFailed НЕ вызывает save() (только меняет статус)
        verify(usageRecordRepository, never()).save(any());
    }

    @Test
    void testMarkFailed_CanChangeFromProcessed() {
        // Arrange
        usageRecord.setUsageStatus(UsageStatus.PROCESSED);

        // Act
        usageService.markFailed(usageRecord);

        // Assert
        assertEquals(UsageStatus.FAILED, usageRecord.getUsageStatus());
    }

    // ========== ГРАНИЧНЫЕ СЛУЧАИ ==========

    @Test
    void testCreateUsageRecord_ZeroAmount() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.MEGABYTE,
                0,
                subscription
        );

        // Assert
        assertEquals(0, result.getAmount());
    }

    @Test
    void testCreateUsageRecord_LargeAmount() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act
        UsageRecord result = usageService.createUsageRecord(
                TrafficType.MEGABYTE,
                999999,
                subscription
        );

        // Assert
        assertEquals(999999, result.getAmount());
    }

    @Test
    void testCreateUsageRecord_NullSubscription() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);

        // Act & Assert
        assertDoesNotThrow(() -> {
            usageService.createUsageRecord(TrafficType.MEGABYTE, 500, null);
        });
    }

    @Test
    void testGetGlobalRateByType_VerySmallPrice() {
        // Arrange
        globalRate.setPrice(BigDecimal.valueOf(0.01));
        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        // Act
        BigDecimal result = usageService.getGlobalRateByType(TrafficType.MEGABYTE);

        // Assert
        assertEquals(0, result.compareTo(BigDecimal.valueOf(0.01)));
    }

    @Test
    void testGetGlobalRateByType_VeryLargePrice() {
        // Arrange
        globalRate.setPrice(BigDecimal.valueOf(9999.99));
        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        // Act
        BigDecimal result = usageService.getGlobalRateByType(TrafficType.MEGABYTE);

        // Assert
        assertEquals(0, result.compareTo(BigDecimal.valueOf(9999.99)));
    }

    // ========== ИНТЕГРАЦИОННЫЕ СЦЕНАРИИ ==========

    @Test
    void testFullUsageLifecycle() {
        // Arrange
        when(usageRecordRepository.save(any(UsageRecord.class)))
                .thenReturn(usageRecord);
        when(globalRateRepository.findByTrafficType(TrafficType.MEGABYTE))
                .thenReturn(Optional.of(globalRate));

        // Act
        // 1. Создаём запись об использовании
        UsageRecord created = usageService.createUsageRecord(
                TrafficType.MEGABYTE,
                500,
                subscription
        );
        assertEquals(UsageStatus.NEW, created.getUsageStatus());

        // 2. Получаем глобальную цену
        BigDecimal rate = usageService.getGlobalRateByType(TrafficType.MEGABYTE);
        assertNotNull(rate);

        // 3. Отмечаем успешной
        usageService.markProcessed(created);
        assertEquals(UsageStatus.PROCESSED, created.getUsageStatus());

        // Assert
        verify(usageRecordRepository, times(1)).save(any());
        verify(globalRateRepository, times(1)).findByTrafficType(TrafficType.MEGABYTE);
    }

}