package telecom_system.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import telecom_system.com.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // save() - to persist entities into the database
    //findById() - to find database record by its id
    //findAll() - to get all entities
    //findById() - to get an entity by its id


    //@Query("SELECT p FROM Publishers p WHERE p.journals > :minJournals AND p.location = :location")
    //List<Publisher> findPublishersWithMinJournalsInLocation(Integer minJournals,String location);

    boolean existsByIin(String iin);





}
