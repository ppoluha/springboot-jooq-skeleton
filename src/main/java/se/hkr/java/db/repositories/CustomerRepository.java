package se.hkr.java.db.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.hkr.java.db.generated.tables.records.CustomerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static se.hkr.java.db.generated.tables.Customer.CUSTOMER;

@Repository
public class CustomerRepository {

    @Autowired
    private DSLContext db;
    public void save(CustomerRecord customer) {
        // insert a new customer into the customer table
    }

    public List<CustomerRecord> findAll() {
        // return all customers
        return new ArrayList<>();
    }

    public Optional<CustomerRecord> findById(Long customerId) {
        // optionally return one customer. Tip: use fetchOptional() instead of fetch()
        return Optional.empty();
    }
}
