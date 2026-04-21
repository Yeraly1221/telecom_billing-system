package telecom_system.com.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telecom_system.com.SubscriptionStatus;
import telecom_system.com.TrafficType;
import telecom_system.com.entity.Customer;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.Tariff;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.SubscriptionRepository;
import telecom_system.com.request.RemainingOfTariff;
import telecom_system.com.request.UsageSplitResult;
import telecom_system.com.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Customer customer;
    private Tariff tariff;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        // Создаём Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Yeraly");
        customer.setIin("12345678");

        // Создаём Tariff
        tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Premium Plan");
        tariff.setPrice(java.math.BigDecimal.valueOf(99.99));
        tariff.setMegabyteLimit(10000);
        tariff.setMinutesLimit(500);
        tariff.setSmsLimit(200);

        // Создаём Subscription
        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCustomer(customer);
        subscription.setTariff(tariff);
        subscription.setMegabyte(10000);
        subscription.setMinutes(500);
        subscription.setSms(200);
        subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
    }

    // ========== ТЕСТЫ activateSubscription ==========

    @Test
    void testActivateSubscription_Success() {
        // Arrange
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.activateSubscription(customer, tariff);

        // Assert
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());

        Subscription savedSubscription = captor.getValue();
        assertEquals(customer, savedSubscription.getCustomer());
        assertEquals(tariff, savedSubscription.getTariff());
        assertEquals(SubscriptionStatus.ACTIVE, savedSubscription.getSubscriptionStatus());
        assertEquals(10000, savedSubscription.getMegabyte());
        assertEquals(500, savedSubscription.getMinutes());
        assertEquals(200, savedSubscription.getSms());
    }

    @Test
    void testActivateSubscription_VerifyDateRange() {
        // Arrange
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.activateSubscription(customer, tariff);

        // Assert
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());

        Subscription saved = captor.getValue();
        assertNotNull(saved.getStartDate());
        assertNotNull(saved.getEndDate());
        // EndDate должен быть на 1 месяц больше StartDate
        assertTrue(saved.getEndDate().isAfter(saved.getStartDate()));
    }

    @Test
    void testActivateSubscription_WithDifferentTariff() {
        // Arrange
        Tariff standardTariff = new Tariff();
        standardTariff.setId(2L);
        standardTariff.setName("Standard Plan");
        standardTariff.setMegabyteLimit(5000);
        standardTariff.setMinutesLimit(250);
        standardTariff.setSmsLimit(100);

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.activateSubscription(customer, standardTariff);

        // Assert
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());

        Subscription saved = captor.getValue();
        assertEquals(5000, saved.getMegabyte());
        assertEquals(250, saved.getMinutes());
        assertEquals(100, saved.getSms());
    }

    // ========== ТЕСТЫ getCustomerWithActiveSubscriptionStatus ==========

    @Test
    void testGetCustomerWithActiveSubscription_Found() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        // Act
        Subscription result = subscriptionService.getCustomerWithActiveSubscriptionStatus(1L);

        // Assert
        assertNotNull(result);
        assertEquals(subscription.getId(), result.getId());
        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
    }

    @Test
    void testGetCustomerWithActiveSubscription_NotFound() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(999L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            subscriptionService.getCustomerWithActiveSubscriptionStatus(999L);
        });
    }

    @Test
    void testGetCustomerWithActiveSubscription_ExceptionMessage() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(999L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.getCustomerWithActiveSubscriptionStatus(999L);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testGetCustomerWithActiveSubscription_VerifyRepositoryCall() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        // Act
        subscriptionService.getCustomerWithActiveSubscriptionStatus(1L);

        // Assert
        verify(subscriptionRepository).findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE);
    }

    // ========== ТЕСТЫ changeTariff ==========

    @Test
    void testChangeTariff_Success() {
        // Arrange
        Subscription activeSubscription = new Subscription();
        activeSubscription.setId(1L);
        activeSubscription.setCustomer(customer);
        activeSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        Tariff newTariff = new Tariff();
        newTariff.setId(2L);
        newTariff.setName("New Plan");
        newTariff.setMegabyteLimit(20000);
        newTariff.setMinutesLimit(1000);
        newTariff.setSmsLimit(400);

        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.changeTariff(customer, newTariff);

        // ПРАВИЛЬНО - используем ArgumentCaptor ОДИН раз с times(2)
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository, times(2)).save(captor.capture());

// Проверяем ОБА вызова
        Subscription closedSubscription = captor.getAllValues().get(0);
        assertEquals(SubscriptionStatus.CLOSE, closedSubscription.getSubscriptionStatus());

        Subscription newSubscription = captor.getAllValues().get(1);
        assertEquals(SubscriptionStatus.ACTIVE, newSubscription.getSubscriptionStatus());
    }

    @Test
    void testChangeTariff_OldSubscriptionClosed() {
        // Arrange
        Subscription activeSubscription = new Subscription();
        activeSubscription.setId(1L);
        activeSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.changeTariff(customer, tariff);

        // Assert - проверяем что старая подписка закрыта
        assertEquals(SubscriptionStatus.CLOSE, activeSubscription.getSubscriptionStatus());
    }

    @Test
    void testChangeTariff_NewSubscriptionActivated() {
        // Arrange
        Subscription oldSubscription = new Subscription();
        oldSubscription.setId(1L);
        oldSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        Tariff newTariff = new Tariff();
        newTariff.setMegabyteLimit(15000);
        newTariff.setMinutesLimit(750);
        newTariff.setSmsLimit(300);

        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(oldSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.changeTariff(customer, newTariff);

        // Assert - второй save должен быть с новой подпиской
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository, times(2)).save(captor.capture());

        Subscription newSubscription = captor.getAllValues().get(1);
        assertEquals(15000, newSubscription.getMegabyte());
        assertEquals(750, newSubscription.getMinutes());
        assertEquals(300, newSubscription.getSms());
    }

    // ========== ТЕСТЫ currentRemainFromTariff ==========

    @Test
    void testCurrentRemainFromTariff_Success() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        // Act
        RemainingOfTariff result = subscriptionService.currentRemainFromTariff(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Premium Plan", result.getName());
        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
        assertEquals(10000, result.getMegabyte());
        assertEquals(500, result.getMinutes());
        assertEquals(200, result.getSms());
    }

    @Test
    void testCurrentRemainFromTariff_VerifyAllFields() {
        // Arrange
        subscription.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        subscription.setEndDate(LocalDateTime.of(2024, 2, 1, 0, 0));

        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        // Act
        RemainingOfTariff result = subscriptionService.currentRemainFromTariff(1L);

        // Assert
        assertEquals("Premium Plan", result.getName());
        assertEquals(SubscriptionStatus.ACTIVE, result.getSubscriptionStatus());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0), result.getStartDate());
        assertEquals(LocalDateTime.of(2024, 2, 1, 0, 0), result.getEndDate());
        assertEquals(10000, result.getMegabyte());
        assertEquals(500, result.getMinutes());
        assertEquals(200, result.getSms());
    }

    @Test
    void testCurrentRemainFromTariff_SubscriptionNotFound() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(999L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            subscriptionService.currentRemainFromTariff(999L);
        });
    }

    // ========== ТЕСТЫ isActiveSubscriptionByCustomerId ==========

    @Test
    void testIsActiveSubscriptionByCustomerId_True() {
        // Arrange
        when(subscriptionRepository.existsByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(true);

        // Act
        boolean result = subscriptionService.isActiveSubscriptionByCustomerId(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsActiveSubscriptionByCustomerId_False() {
        // Arrange
        when(subscriptionRepository.existsByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(false);

        // Act
        boolean result = subscriptionService.isActiveSubscriptionByCustomerId(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsActiveSubscriptionByCustomerId_VerifyRepositoryCall() {
        // Arrange
        when(subscriptionRepository.existsByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(true);

        // Act
        subscriptionService.isActiveSubscriptionByCustomerId(1L);

        // Assert
        verify(subscriptionRepository).existsByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE);
    }

    // ========== ТЕСТЫ updateRemains ==========

    @Test
    void testUpdateRemains_MegabyteUsage() {
        // Arrange
        Subscription sub = new Subscription();
        sub.setMegabyte(10000);
        sub.setMinutes(500);
        sub.setSms(200);

        UsageSplitResult result = new UsageSplitResult();
        result.setTrafficAfterUsing(9000); // Использовано 1000 МБ

        // Act
        subscriptionService.updateRemains(sub, TrafficType.MEGABYTE, result);

        // Assert
        verify(subscriptionRepository, never()).save(any()); // updateRemains не сохраняет
        // Но subscription.applyUsage должен быть вызван (если у Subscription есть этот метод)
    }

    @Test
    void testUpdateRemains_MinutesUsage() {
        // Arrange
        Subscription sub = new Subscription();
        sub.setMegabyte(10000);
        sub.setMinutes(500);
        sub.setSms(200);

        UsageSplitResult result = new UsageSplitResult();
        result.setTrafficAfterUsing(450); // Использовано 50 минут

        // Act
        subscriptionService.updateRemains(sub, TrafficType.MINUTES, result);

        // Assert
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void testUpdateRemains_SmsUsage() {
        // Arrange
        Subscription sub = new Subscription();
        sub.setMegabyte(10000);
        sub.setMinutes(500);
        sub.setSms(200);

        UsageSplitResult result = new UsageSplitResult();
        result.setTrafficAfterUsing(180); // Использовано 20 SMS

        // Act
        subscriptionService.updateRemains(sub, TrafficType.SMS, result);

        // Assert
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void testUpdateRemains_ZeroRemaining() {
        // Arrange
        Subscription sub = new Subscription();
        sub.setMegabyte(10000);

        UsageSplitResult result = new UsageSplitResult();
        result.setTrafficAfterUsing(0); // Всё использовано

        // Act
        subscriptionService.updateRemains(sub, TrafficType.MEGABYTE, result);

        // Assert
        verify(subscriptionRepository, never()).save(any());
    }

    // ========== ГРАНИЧНЫЕ СЛУЧАИ ==========

    @Test
    void testActivateSubscription_WithNullTariff() {
        // Arrange - tariff = null

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            subscriptionService.activateSubscription(customer, null);
        });
    }

    @Test
    void testActivateSubscription_WithNullCustomer() {
        // Arrange - customer = null

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            subscriptionService.activateSubscription(null, tariff);
        });
    }

    @Test
    void testGetCustomerWithActiveSubscription_WithNegativeId() {
        // Arrange
        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(-1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            subscriptionService.getCustomerWithActiveSubscriptionStatus(-1L);
        });
    }

    @Test
    void testIsActiveSubscriptionByCustomerId_WithZeroId() {
        // Arrange
        when(subscriptionRepository.existsByCustomer_IdAndSubscriptionStatus(0L, SubscriptionStatus.ACTIVE))
                .thenReturn(false);

        // Act
        boolean result = subscriptionService.isActiveSubscriptionByCustomerId(0L);

        // Assert
        assertFalse(result);
    }

    // ========== ИНТЕГРАЦИОННЫЕ СЦЕНАРИИ ==========


    @Test
    void testMultipleSubscriptionChanges() {
        // Arrange
        Subscription oldSub = new Subscription();
        oldSub.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        Tariff tariff1 = new Tariff();
        tariff1.setMegabyteLimit(5000);

        Tariff tariff2 = new Tariff();
        tariff2.setMegabyteLimit(10000);

        when(subscriptionRepository.findByCustomer_IdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(oldSub));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        // Act
        subscriptionService.changeTariff(customer, tariff1);
        subscriptionService.changeTariff(customer, tariff2);

        // Assert
        verify(subscriptionRepository, times(4)).save(any()); // 2 раза по 2 save
    }
}