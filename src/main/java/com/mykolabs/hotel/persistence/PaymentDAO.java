package com.mykolabs.hotel.persistence;

import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.beans.Payment;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.Room;
import com.mykolabs.hotel.util.ConnectionHelper;
import java.math.BigDecimal;
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
 * This class retrieves and persists Payment related data into corresponding
 * database.
 *
 * @author nikprixmar
 */
public class PaymentDAO {

    private static final Logger log = Logger.getLogger(PaymentDAO.class.getName());

    public PaymentDAO() {
        super();
    }

    /**
     * Returns all payments from the PAYMENT table.
     *
     * @param start
     * @param end
     * @param useLimits
     * @return
     * @throws java.sql.SQLException
     */
    public List<Payment> getAllPayments(int start, int end, boolean useLimits) throws SQLException {

        List<Payment> rows = new ArrayList<>();

        String selectQuery = "SELECT PAYMENT_ID, CARD_TYPE, CARD_NUMBER, CARD_EXPIRATION, PAYMENT_AMOUNT, DESCRIPTION, "
                + "CUSTOMER_ID, RESERVATION_ID "
                + "FROM PAYMENT "
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

                    Payment paymentsData = new Payment();

                    paymentsData.setPaymentId(resultSet.getInt("PAYMENT_ID"));
                    paymentsData.setCardType(resultSet.getString("CARD_TYPE"));
                    paymentsData.setCardNumber(resultSet.getString("CARD_NUMBER"));
                    paymentsData.setCardExpiration(resultSet.getString("CARD_EXPIRATION"));
                    paymentsData.setPaymentAmount(resultSet.getBigDecimal("PAYMENT_AMOUNT"));
                    paymentsData.setDescription(resultSet.getString("DESCRIPTION"));

                    CustomerDAO customerDAO = new CustomerDAO();
                    Customer customer = customerDAO.getCustomer(resultSet.getInt("CUSTOMER_ID"));

                    ReservationDAO reservationDAO = new ReservationDAO();
                    Reservation reservation = reservationDAO.getReservation(resultSet.getInt("RESERVATION_ID"));

                    paymentsData.setCustomerId(customer);
                    paymentsData.setReservationId(reservation);

                    rows.add(paymentsData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved payments: {0}", rows.size());
        return rows;
    }

    /**
     * Returns single payment from the PAYMENTS table.
     *
     * @param paymentID
     * @return
     * @throws java.sql.SQLException
     */
    public Payment getPayment(int paymentID) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        Payment paymentData = new Payment();

        String selectQuery = "SELECT PAYMENT_ID, CARD_TYPE, CARD_NUMBER, CARD_EXPIRATION, PAYMENT_AMOUNT, DESCRIPTION, "
                + "CUSTOMER_ID, RESERVATION_ID "
                + "FROM PAYMENT "
                + "WHERE PAYMENT_ID = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setInt(1, paymentID);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    paymentData.setPaymentId(resultSet.getInt("PAYMENT_ID"));
                    paymentData.setCardType(resultSet.getString("CARD_TYPE"));
                    paymentData.setCardNumber(resultSet.getString("CARD_NUMBER"));
                    paymentData.setCardExpiration(resultSet.getString("CARD_EXPIRATION"));
                    paymentData.setPaymentAmount(resultSet.getBigDecimal("PAYMENT_AMOUNT"));
                    paymentData.setDescription(resultSet.getString("DESCRIPTION"));

                    CustomerDAO customerDAO = new CustomerDAO();
                    Customer customer = customerDAO.getCustomer(resultSet.getInt("CUSTOMER_ID"));

                    ReservationDAO reservationDAO = new ReservationDAO();
                    Reservation reservation = reservationDAO.getReservation(resultSet.getInt("RESERVATION_ID"));

                    paymentData.setCustomerId(customer);
                    paymentData.setReservationId(reservation);

                }
            }
        }
        log.log(Level.INFO, "Retrieved payment with paymentID: {0}", paymentData.getPaymentId());
        return paymentData;
    }

    /**
     * Updates single payment in the PAYMENT table.
     *
     * @param payment
     * @return
     * @throws java.sql.SQLException
     */
    public int updatePayment(Payment payment) throws SQLException {
        int result = 0;

        String updateQuery = "UPDATE PAYMENT "
                + "SET PAYMENT_ID=?, CARD_TYPE=?, CARD_NUMBER=?, "
                + "CARD_EXPIRATION=?, PAYMENT_AMOUNT=?, DESCRIPTION=?, CUSTOMER_ID=? "
                + "RESERVATION_ID=? "
                + "WHERE PAYMENT_ID=?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(updateQuery);) {

            pStatement.setInt(1, payment.getPaymentId());
            pStatement.setString(2, payment.getCardType());
            pStatement.setString(3, payment.getCardNumber());
            pStatement.setString(4, payment.getCardExpiration());
            pStatement.setBigDecimal(5, payment.getPaymentAmount());
            pStatement.setString(6, payment.getDescription());
            pStatement.setInt(7, payment.getCustomerId().getCustomerId());
            pStatement.setInt(8, payment.getReservationId().getReservationId());
            
            pStatement.setInt(9, payment.getPaymentId());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Updated payment with paymentID: {0}", payment.getPaymentId());
        return result;
    }

    /**
     * Adds a single payment into the PAYMENT table.
     *
     * @param payment
     * @return
     * @throws java.sql.SQLException
     */
    public int addPayment(Payment payment) throws SQLException {
        int result = 0;

        String createQuery = "INSERT INTO PAYMENT "
                + "(CARD_TYPE, CARD_NUMBER, "
                + "CARD_EXPIRATION, PAYMENT_AMOUNT, DESCRIPTION, CUSTOMER_ID, RESERVATION_ID) "
                + "VALUES (?,?,?,?,?,?,?)";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(createQuery);) {
            
            pStatement.setString(1, payment.getCardType());
            pStatement.setString(2, payment.getCardNumber());
            pStatement.setString(3, payment.getCardExpiration());
            pStatement.setBigDecimal(4, payment.getPaymentAmount());
            pStatement.setString(5, payment.getDescription());
            pStatement.setInt(6, payment.getCustomerId().getCustomerId());
            pStatement.setInt(7, payment.getReservationId().getReservationId());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Created payment with paymentID: {0}", payment.getPaymentId());
        return result;
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
