package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import telecom_system.com.SubscriptionStatus;
import telecom_system.com.entity.Subscription;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByCustomer_IdAndSubscriptionStatus(
            Long customerId,
            SubscriptionStatus status
    );

    Boolean existsByCustomer_IdAndSubscriptionStatus(Long customerId,
                                                     SubscriptionStatus subscriptionStatus);
}
