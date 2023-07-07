package ua.aleh1s.amigoscodecourse.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerById(Integer id);
    boolean existsCustomerByEmail(String email);
}