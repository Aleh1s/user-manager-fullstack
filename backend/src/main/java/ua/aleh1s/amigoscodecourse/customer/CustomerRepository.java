package ua.aleh1s.amigoscodecourse.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerById(Integer id);
    boolean existsCustomerByEmail(String email);
    Optional<Customer> findCustomerByEmail(String email);
    @Modifying(clearAutomatically = true)
    @Query("update Customer c set c.profileImageId = ?2 where c.id = ?1")
    void updateCustomerProfileImageIdByCustomerId(Integer id, String profileImageId);
}