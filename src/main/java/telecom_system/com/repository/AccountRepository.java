package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telecom_system.com.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
