package com.mykolabs.hotel.persistence;

import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.beans.Payment;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.Room;
import com.mykolabs.hotel.util.ConnectionHelper;
import com.mysql.jdbc.Statement;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class retrieves and persists Customer related data into corresponding
 * database.
 *
 * @author nikprixmar
 */
public class CustomerDAO {

    private static final Logger log = Logger.getLogger(CustomerDAO.class.getName());

    public CustomerDAO() {
        super();
    }

    /**
     * Returns all customers from the CUSTOMER table.
     *
     * @param start
     * @param end
     * @param useLimits
     * @return
     * @throws java.sql.SQLException
     */
    public List<Customer> getAllCustomers(int start, int end, boolean useLimits) throws SQLException {

        List<Customer> rows = new ArrayList<>();

        String selectQuery = "SELECT CUSTOMER_ID, FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, PHONE "
                + "FROM CUSTOMER "
                + "LIMIT ?, ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            if (useLimits) {
                pStatement.setInt(1, start);
                pStatement.setInt(2, end);
            } else {
                // TODO
            }

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    Customer customerData = new Customer();

                    customerData.setCustomerId(resultSet.getInt("CUSTOMER_ID"));
                    customerData.setFirstName(resultSet.getString("FIRST_NAME"));
                    customerData.setLastName(resultSet.getString("LAST_NAME"));
                    customerData.setAddress(resultSet.getString("ADDRESS"));
                    customerData.setCity(resultSet.getString("CITY"));
                    customerData.setState(resultSet.getString("STATE"));
                    customerData.setPhone(resultSet.getString("PHONE"));

                    rows.add(customerData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved customers: {0}", rows.size());
        return rows;
    }

    /**
     * Returns single customer from the CUSTOMERS table.
     *
     * @param customerID
     * @return
     * @throws java.sql.SQLException
     */
    public Customer getCustomer(int customerID) throws SQLException {

        Customer customerData = new Customer();

        String selectQuery = "SELECT CUSTOMER_ID, FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, PHONE "
                + "FROM CUSTOMER "
                + "WHERE CUSTOMER_ID = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setInt(1, customerID);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    customerData.setCustomerId(resultSet.getInt("CUSTOMER_ID"));
                    customerData.setFirstName(resultSet.getString("FIRST_NAME"));
                    customerData.setLastName(resultSet.getString("LAST_NAME"));
                    customerData.setAddress(resultSet.getString("ADDRESS"));
                    customerData.setCity(resultSet.getString("CITY"));
                    customerData.setState(resultSet.getString("STATE"));
                    customerData.setPhone(resultSet.getString("PHONE"));

                }
            }
        }
        log.log(Level.INFO, "Retrieved customer with customerID: {0}", customerData.getCustomerId());
        return customerData;
    }

    /**
     * Updates single customer in the CUSTOMER table.
     *
     * @param customer
     * @return
     * @throws java.sql.SQLException
     */
    public int updateCustomer(Customer customer) throws SQLException {
        int result;

        String updateQuery = "UPDATE CUSTOMER "
                + "SET CUSTOMER_ID=?, FIRST_NAME=?, LAST_NAME=?, "
                + "ADDRESS=?, CITY=?, STATE=? "
                + "PHONE=? "
                + "WHERE CUSTOMER_ID=?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(updateQuery);) {

            pStatement.setInt(1, customer.getCustomerId());
            pStatement.setString(2, customer.getFirstName());
            pStatement.setString(3, customer.getLastName());
            pStatement.setString(4, customer.getAddress());
            pStatement.setString(5, customer.getCity());
            pStatement.setString(6, customer.getState());
            pStatement.setString(7, customer.getPhone());

            pStatement.setInt(8, customer.getCustomerId());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Updated customer with customerID: {0}", customer.getCustomerId());
        return result;
    }

    /**
     * Adds a single customer into the CUSTOMER table.
     *
     * @param customer
     * @return
     * @throws java.sql.SQLException
     */
    public int addCustomer(Customer customer) throws SQLException {
        int result;

        String createQuery = "INSERT INTO CUSTOMER "
                + "(FIRST_NAME, LAST_NAME, ADDRESS, CITY, STATE, PHONE) "
                + "VALUES (?,?,?,?,?,?)";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);) {

            pStatement.setString(1, customer.getFirstName());
            pStatement.setString(2, customer.getLastName());
            pStatement.setString(3, customer.getAddress());
            pStatement.setString(4, customer.getCity());
            pStatement.setString(5, customer.getState());
            pStatement.setString(6, customer.getPhone());

            result = pStatement.executeUpdate();

            try (ResultSet generatedKeys = pStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating CUSTOMER failed, no ID obtained.");
                }
            }

        }
        log.log(Level.INFO, "Created customer with customerID: {0}", customer.getCustomerId());
        return customer.getCustomerId();
    }

    /**
     * util DATE to sql DATE converter.
     *
     * @return
     * @param date
     */
    public java.sql.Date convertToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * Converts string to LocalDateTime object
     *
     * @param dateTime
     * @return
     */
    public LocalDateTime convertToLocalDateTime(String dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);

        return localDateTime;
    }
}
