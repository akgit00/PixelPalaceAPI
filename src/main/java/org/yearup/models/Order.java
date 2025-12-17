package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {

    private Integer orderID;
    private Integer userID;
    private LocalDate date;
    private String address;
    private String city;
    private String state;
    private String zip;
    private BigDecimal amount;

    public Order() {}

    public Order(Integer orderID, Integer userID, LocalDate date, String address, String city, String state, String zip, BigDecimal amount) {
        this.orderID = orderID;
        this.userID = userID;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.amount = amount;
    }

    public Integer getOrderID() {
        return orderID;
    }