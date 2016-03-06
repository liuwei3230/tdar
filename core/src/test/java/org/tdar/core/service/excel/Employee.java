package org.tdar.core.service.excel;

import org.fluttercode.datafactory.impl.DataFactory;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by jimdevos on 3/5/16.
 */
public class Employee {
    private String name;
    private Date birthDate;
    private BigDecimal payment;
    private BigDecimal bonus;

    private static DataFactory dataFactory = new DataFactory();

    public static Employee randomEmployee() {
        return new Employee(
                dataFactory.getName(),
                dataFactory.getBirthDate(),
                new BigDecimal(dataFactory.getNumberBetween(500, 5_000)),
                new BigDecimal(dataFactory.getNumberBetween(1_000, 10_000)));
    }

    public Employee() {}

    public Employee(String name, Date birthDate, BigDecimal payment, BigDecimal bonus) {
        this.name = name;
        this.birthDate = birthDate;
        this.payment = payment;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
}