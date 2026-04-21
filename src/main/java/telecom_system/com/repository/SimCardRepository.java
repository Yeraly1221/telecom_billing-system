package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import telecom_system.com.entity.SimCard;

public interface SimCardRepository extends JpaRepository<SimCard, Long> {
}
