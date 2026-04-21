package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import telecom_system.com.TrafficType;
import telecom_system.com.entity.UsageRecord;
import java.math.BigDecimal;
import java.util.Optional;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
}
