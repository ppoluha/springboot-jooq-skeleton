package se.hkr.java.db.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.hkr.java.db.generated.tables.records.EmployeeRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static se.hkr.java.db.generated.tables.Employee.EMPLOYEE;

@Repository
public class EmployeeRepository {
    @Autowired
    private DSLContext db;

    public List<EmployeeRecord> findAll() {
        // return all employees
        return new ArrayList<>();
    }

    public void save(EmployeeRecord employee) {
        // insert a new employee into the employee table
        db.insertInto(EMPLOYEE)
                .set(employee)
                .execute();
    }

    public Optional<EmployeeRecord> findById(Long id) {
        // optionally return one employee. Tip: use fetchOptional() instead of fetch()
        return Optional.empty();
    }
}
