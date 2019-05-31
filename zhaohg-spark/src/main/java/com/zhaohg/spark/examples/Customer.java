package com.zhaohg.spark.examples;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaohg on 2017/3/6.
 */
public class Customer {

    private static final Logger  logger            = Logger.getLogger("Customer");
    private static final String  LOG_ENTRY_PATTERN =
            //1:IP address 2:customer ID  3:firs tname 3:last name 4:time elapsed  5:ordermessage date 6:time elapsed 7:total price
            "^(\\S+),(\\d+),(\\S+),(\\S+),\\[([\\w:/]+\\s[+\\-]\\d{4})\\],(\\d+),(\\d+)";
    private static final Pattern PATTERN           = Pattern.compile(LOG_ENTRY_PATTERN);
    private              String  ipAddress;
    private              String  id;
    private              String  firstName;
    private              String  lastName;
    private              int     timeElapsed;
    private              String  dateString;
    private              long    amount;

    public Customer(String ipAddress, String id, String firstName, String lastName,
                    String dateString, String timeElapsed, String totalPrice) {
        super();
        this.ipAddress = ipAddress;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.timeElapsed = Integer.parseInt(timeElapsed);
        this.dateString = dateString;
        this.amount = Long.parseLong(totalPrice);
    }

    public static Customer parseFromCustomerLine(String customerLine) {
        System.out.println("line: " + customerLine);
        Matcher m = PATTERN.matcher(customerLine);
        if (!m.find()) {
            logger.log(Level.ALL, "Cannot parse customer file" + customerLine);
            throw new RuntimeException("Error parsing customer file");
        }

        return new Customer(m.group(1), m.group(2), m.group(3), m.group(4),
                m.group(5), m.group(6), m.group(7));
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public long getAmount() {
        return amount;
    }

    public void setTotalPrice(long totalPrice) {
        this.amount = totalPrice;
    }

}