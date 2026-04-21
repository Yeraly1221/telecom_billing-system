package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telecom_system.com.TrafficType;
import telecom_system.com.entity.GlobalRate;

import java.util.Optional;

public interface GlobalRateRepository extends JpaRepository<GlobalRate, Long>{
    Optional<GlobalRate> findByTrafficType(TrafficType trafficType);

}
