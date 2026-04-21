package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telecom_system.com.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
