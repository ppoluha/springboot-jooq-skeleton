package se.hkr.java.db.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.hkr.java.db.generated.tables.records.OrderHeadRecord;
import se.hkr.java.db.generated.tables.records.OrderLineRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import static se.hkr.java.db.generated.tables.Customer.CUSTOMER;
import static se.hkr.java.db.generated.tables.Furniture.FURNITURE;
import static se.hkr.java.db.generated.tables.OrderHead.ORDER_HEAD;
import static se.hkr.java.db.generated.tables.OrderLine.ORDER_LINE;

@Repository
public class OrderRepository {
    @Autowired
    private DSLContext db;

    /**
     * Fetches orders using multiset, where each order row contains a collection of order lines.
     * @param employeeId id of employee
     * @return a list of orders
     */
    public List<OrderHead> findByEmployeeIdOrderByOrderDateDesc(Long employeeId) {
        return db.select(
                ORDER_HEAD.ORDER_DATE,
                ORDER_HEAD.customer().LAST_NAME,
                ORDER_HEAD.customer().FIRST_NAME,
                multiset(
                    select(
                        ORDER_LINE.furniture().NAME,
                        ORDER_LINE.QUANTITY)
                    .from(ORDER_LINE)
                    .where(ORDER_LINE.ORDER_ID.eq(ORDER_HEAD.ID)))
                        .convertFrom(r -> r.map(mapping(OrderLine::new))))
                .from(ORDER_HEAD)
                .where(ORDER_HEAD.EMPLOYEE_ID.eq(employeeId))
                .orderBy(ORDER_HEAD.ORDER_DATE.desc())
                .fetch(mapping(OrderHead::new));
    }

    /**
     * Fetches orders using a join and then aggregating the result into a
     * list of order heads, each containing a list of order lines.
     * The ordering gets lost during the aggregation and needs to be done (again) after the fetch.
     * @param employeeId id of employee
     * @return a list of orders
     */
    public List<OrderHead> findByEmployeeIdOrderByOrderDateDesc2(Long employeeId) {
        var result = db
                .select(ORDER_HEAD.ORDER_DATE, CUSTOMER.LAST_NAME, CUSTOMER.FIRST_NAME,
                        FURNITURE.NAME, ORDER_LINE.QUANTITY)
                .from(ORDER_HEAD)
                .join(CUSTOMER).on(ORDER_HEAD.CUSTOMER_ID.eq(CUSTOMER.ID))
                .join(ORDER_LINE).on(ORDER_HEAD.ID.eq(ORDER_LINE.ORDER_ID))
                .join(FURNITURE).on(ORDER_LINE.FURNITURE_ID.eq(FURNITURE.ID))
                .where(ORDER_HEAD.EMPLOYEE_ID.eq(employeeId))
                .orderBy(ORDER_HEAD.ORDER_DATE)
                .fetchGroups(
                        r -> new OrderHead(
                                r.get(ORDER_HEAD.ORDER_DATE),
                                r.get(CUSTOMER.LAST_NAME),
                                r.get(CUSTOMER.FIRST_NAME),
                                null
                        ),
                        r -> new OrderLine(
                                r.get(FURNITURE.NAME),
                                r.get(ORDER_LINE.QUANTITY)
                        )
                );
        return result.entrySet().stream()
                .map(entry -> entry.getKey().withOrderLines(entry.getValue()))
                .sorted((o1, o2) -> o2.orderDate.compareTo(o1.orderDate))
                .collect(Collectors.toList());
    }

    /**
     * Fetches orders using two queries and then grouping the orders with the associated order lines.
     * @param employeeId id of employee
     * @return a list of orders
     */
    public List<OrderHead> findByEmployeeIdOrderByOrderDateDesc3(Long employeeId) {
        // Select orders and their customers for a given employee
        var ordersWithCustomers = db
                .select(ORDER_HEAD.ID, ORDER_HEAD.ORDER_DATE, CUSTOMER.FIRST_NAME, CUSTOMER.LAST_NAME)
                .from(ORDER_HEAD)
                .join(CUSTOMER).on(ORDER_HEAD.CUSTOMER_ID.eq(CUSTOMER.ID))
                .where(ORDER_HEAD.EMPLOYEE_ID.eq(employeeId))
                .orderBy(ORDER_HEAD.ORDER_DATE.desc())
                .fetch();

        // Collect order IDs to select their order lines
        List<Long> orderIds = ordersWithCustomers.stream()
                .map(r -> r.get(ORDER_HEAD.ID))
                .collect(Collectors.toList());

        // Select order lines and their furniture for the collected order IDs
        var orderLinesWithFurniture = db
                .select(ORDER_LINE.ORDER_ID, FURNITURE.NAME, ORDER_LINE.QUANTITY, FURNITURE.COLOR)
                .from(ORDER_LINE)
                .join(FURNITURE).on(ORDER_LINE.FURNITURE_ID.eq(FURNITURE.ID))
                .where(ORDER_LINE.ORDER_ID.in(orderIds))
                .fetch();

        // Map order lines and furniture to orders
        return ordersWithCustomers.stream().map(o -> {
                    List<OrderLine> orderLines = orderLinesWithFurniture.stream()
                            .filter(r -> r.get(ORDER_LINE.ORDER_ID).equals(o.get(ORDER_HEAD.ID)))
                            .map(r -> new OrderLine(r.get(FURNITURE.NAME), r.get(ORDER_LINE.QUANTITY)))
                            .collect(Collectors.toList());
                    return new OrderHead(
                            o.get(ORDER_HEAD.ORDER_DATE),
                            o.get(CUSTOMER.LAST_NAME),
                            o.get(CUSTOMER.FIRST_NAME),
                            orderLines);
                }).toList();
    }

    public void save(OrderHeadRecord orderHead, List<OrderLineRecord> orderLines) {
        // insert order and order lines into their respective tables
        // Tip: To get hold of the order's id, make the insertInto statement return the id.
        // Do this by calling returning(ORDER_HEAD.ID).fetchOne() after the set operation.
    }

    record OrderLine (String furnitureName, int quantity) {}
    record OrderHead (LocalDate orderDate, String customerLastName, String customerFirstName, List<OrderLine> orderLines) {
        OrderHead withOrderLines(List<OrderLine> orderLines) {
            return new OrderHead(this.orderDate, this.customerLastName, this.customerFirstName, orderLines);
        }
    }
}

