package se.hkr.java.db.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.hkr.java.db.generated.tables.records.FurnitureRecord;

import java.util.ArrayList;
import java.util.List;

import static se.hkr.java.db.generated.tables.Furniture.FURNITURE;

@Repository
public class FurnitureRepository {
    @Autowired
    private DSLContext db;

    public List<FurnitureRecord> findAll() {
        // return all furniture
        return new ArrayList<>();
    }

    public void save(FurnitureRecord furniture) {
        // insert a furniture into the furniture table
    }
}
