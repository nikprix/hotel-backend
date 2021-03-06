package com.mykolabs.hotel.persistence;

import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.Room;
import com.mykolabs.hotel.beans.ReservationSearch;
import com.mykolabs.hotel.beans.TodayDate;
import com.mykolabs.hotel.beansLists.TodayReservation;
import com.mykolabs.hotel.util.ConnectionHelper;
import com.mysql.jdbc.Statement;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class retrieves and persists Reservation related data into corresponding
 * database.
 *
 * @author nikprixmar
 */
public class ReservationDAO {

    private static final Logger log = Logger.getLogger(ReservationDAO.class.getName());

    public ReservationDAO() {
        super();
    }

    /**
     * Returns all reservations from the RESERVATION table.
     *
     * @param start
     * @param end
     * @param useLimits
     * @return
     * @throws java.sql.SQLException
     */
    public List<Reservation> getAllReservations(int start, int end, boolean useLimits) throws SQLException {

        List<Reservation> rows = new ArrayList<>();

        String selectQuery = "SELECT RESERVATION_ID, CHECKIN_DATE, CHECKOUT_DATE, CUSTOMER_ID, ROOM_NUMBER, EMPLOYEE_ID "
                + "FROM RESERVATION "
                + "ORDER BY CHECKIN_DATE DESC "
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

                    Reservation reservationsData = new Reservation();

                    reservationsData.setReservationId(resultSet.getInt("RESERVATION_ID"));
                    reservationsData.setCheckinDate(resultSet.getTimestamp("CHECKIN_DATE").toLocalDateTime());
                    reservationsData.setCheckoutDate(resultSet.getTimestamp("CHECKOUT_DATE").toLocalDateTime());

                    //CustomerDAO customerDAO = new CustomerDAO();
                    //Customer customer = customerDAO.getCustomer(resultSet.getInt("CUSTOMER_ID"));
                    //RoomDAO roomDAO = new RoomDAO();
                    //Room room = roomDAO.getRoom(resultSet.getInt("ROOM_NUMBER"));
                    // This logic needs to be revised. Jersey returns the whole Employee obj, which
                    // exposes Password/Token
                    //EmployeeDAO employeeDAO = new EmployeeDAO();
                    //Employee employee = employeeDAO.getEmployee(resultSet.getInt("EMPLOYEE_ID"));
                    //reservationsData.setCustomerId(customer);
                    //reservationsData.setRoomNumber(room);
                    //reservationsData.setEmployeeId(employee);
                    reservationsData.setCustomerId(resultSet.getInt("CUSTOMER_ID"));
                    reservationsData.setRoomNumberId(resultSet.getInt("ROOM_NUMBER"));
                    reservationsData.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));

                    rows.add(reservationsData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved reservations: {0}", rows.size());
        return rows;
    }

    /**
     * Returns all TODAYs reservations from the RESERVATION table Joined on
     * Customer table.
     *
     * @param currentDate
     * @return
     * @throws java.sql.SQLException
     */
    public List<TodayReservation> getAllTodayReservations(TodayDate currentDate) throws SQLException {

        List<TodayReservation> rows = new ArrayList<>();

        log.log(Level.INFO, "=========Provided by Client Date========: {0}", currentDate.getCurrentDate().toString());

        String selectQuery = "SELECT rs.RESERVATION_ID, cst.FIRST_NAME, cst.LAST_NAME, rs.ROOM_NUMBER, rs.CHECKIN_DATE "
                + "FROM RESERVATION rs "
                + "JOIN CUSTOMER cst ON rs.CUSTOMER_ID = cst.CUSTOMER_ID "
                + "WHERE rs.CHECKIN_DATE BETWEEN ? AND DATE_ADD(?, INTERVAL 24 HOUR)";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setDate(1, convertLocalDateTimeToSqlDate(currentDate.getCurrentDate()));
            pStatement.setDate(2, convertLocalDateTimeToSqlDate(currentDate.getCurrentDate()));

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    TodayReservation todayReservationData = new TodayReservation();

                    todayReservationData.setReservationId(resultSet.getInt("RESERVATION_ID"));
                    todayReservationData.setFirstName(resultSet.getString("FIRST_NAME"));
                    todayReservationData.setLastName(resultSet.getString("LAST_NAME"));
                    todayReservationData.setRoomNumber(resultSet.getInt("ROOM_NUMBER"));
                    todayReservationData.setCheckinDate(resultSet.getTimestamp("CHECKIN_DATE").toLocalDateTime());

                    // printing converted date and time to the server logs:
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    log.log(Level.INFO, "=====Retrieved Checking Date/Time====: {0}", dateFormat.format(resultSet.getDate("CHECKIN_DATE")));

                    rows.add(todayReservationData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved reservations: {0}", rows.size());
        return rows;
    }

    /**
     * Returns all reservations JOINED with ROOM/CUSTOMER data from the
     * RESERVATION/ROOM/CUSTOMER tables, Using 'TodayReservation' class
     *
     * @return
     * @throws java.sql.SQLException
     */
    public List<TodayReservation> getAllReservationsWithCustomerData() throws SQLException {
        
        log.log(Level.INFO, "=====Entering getAllReservationsWithCustomerData() ====");

        List<TodayReservation> rows = new ArrayList<>();

        String selectQuery = "SELECT rs.RESERVATION_ID, cst.FIRST_NAME, cst.LAST_NAME, rs.ROOM_NUMBER, rs.CHECKIN_DATE "
                + "FROM RESERVATION rs "
                + "JOIN CUSTOMER cst ON rs.CUSTOMER_ID = cst.CUSTOMER_ID "
                + "ORDER BY rs.CHECKIN_DATE DESC";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    TodayReservation todayReservationData = new TodayReservation();

                    todayReservationData.setReservationId(resultSet.getInt("RESERVATION_ID"));
                    todayReservationData.setFirstName(resultSet.getString("FIRST_NAME"));
                    todayReservationData.setLastName(resultSet.getString("LAST_NAME"));
                    todayReservationData.setRoomNumber(resultSet.getInt("ROOM_NUMBER"));
                    todayReservationData.setCheckinDate(resultSet.getTimestamp("CHECKIN_DATE").toLocalDateTime());

                    // printing converted date and time to the server logs:
//                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                    log.log(Level.INFO, "=====Retrieved Checking Date/Time====: {0}", dateFormat.format(resultSet.getDate("CHECKIN_DATE")));
                    
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    log.log(Level.INFO, "=====Retrieved Checking Date/Time====: {0}", todayReservationData.getCheckinDate().toString());

                    rows.add(todayReservationData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved reservations: {0}", rows.size());
        return rows;
    }

    /**
     * Returns all reservations for checkin date using 'TodayReservation' class
     *
     * @param reservationSearch
     * @return
     * @throws java.sql.SQLException
     */
    public List<TodayReservation> getReservationsForCheckin(ReservationSearch reservationSearch) throws SQLException {

        List<TodayReservation> rows = new ArrayList<>();

        String selectQuery = "SELECT rs.RESERVATION_ID, cst.FIRST_NAME, cst.LAST_NAME, rs.ROOM_NUMBER, rs.CHECKIN_DATE "
                + "FROM RESERVATION rs "
                + "JOIN CUSTOMER cst ON rs.CUSTOMER_ID = cst.CUSTOMER_ID "
                + "WHERE rs.CHECKIN_DATE BETWEEN ? AND DATE_ADD(?, INTERVAL 24 HOUR) "
                + "ORDER BY rs.CHECKIN_DATE ASC";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setDate(1, convertToSqlDate(reservationSearch.getCheckinDate()));
            pStatement.setDate(2, convertToSqlDate(reservationSearch.getCheckinDate()));

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    TodayReservation todayReservationData = new TodayReservation();

                    todayReservationData.setReservationId(resultSet.getInt("RESERVATION_ID"));
                    todayReservationData.setFirstName(resultSet.getString("FIRST_NAME"));
                    todayReservationData.setLastName(resultSet.getString("LAST_NAME"));
                    todayReservationData.setRoomNumber(resultSet.getInt("ROOM_NUMBER"));
                    todayReservationData.setCheckinDate(resultSet.getTimestamp("CHECKIN_DATE").toLocalDateTime());

                    // printing converted date and time to the server logs:
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    log.log(Level.INFO, "=====Retrieved Checking Date/Time====: {0}", dateFormat.format(resultSet.getDate("CHECKIN_DATE")));

                    rows.add(todayReservationData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved reservations: {0}", rows.size());
        return rows;
    }

    /**
     * Returns single reservation from the RESERVATION table.
     *
     * @param reservationID
     * @return
     */
    public Reservation getReservation(int reservationID) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        Reservation reservationData = new Reservation();

        String selectQuery = "SELECT RESERVATION_ID, CHECKIN_DATE, CHECKOUT_DATE, CUSTOMER_ID, ROOM_NUMBER, EMPLOYEE_ID " + "FROM RESERVATION "
                + "WHERE RESERVATION_ID = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setInt(1, reservationID);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    reservationData.setReservationId(resultSet.getInt("RESERVATION_ID"));
                    reservationData.setCheckinDate(resultSet.getTimestamp("CHECKIN_DATE").toLocalDateTime());
                    reservationData.setCheckoutDate(resultSet.getTimestamp("CHECKOUT_DATE").toLocalDateTime());

                    //CustomerDAO customerDAO = new CustomerDAO();
                    //Customer customer = customerDAO.getCustomer(resultSet.getInt("CUSTOMER_ID"));
                    //RoomDAO roomDAO = new RoomDAO();
                    //Room room = roomDAO.getRoom(resultSet.getInt("ROOM_NUMBER"));
                    //EmployeeDAO employeeDAO = new EmployeeDAO();
                    //Employee employee = employeeDAO.getEmployee(resultSet.getInt("EMPLOYEE_ID"));
                    //reservationData.setCustomerId(customer);
                    //reservationData.setRoomNumber(room);
                    //reservationData.setEmployeeId(employee);
                    reservationData.setCustomerId(resultSet.getInt("CUSTOMER_ID"));
                    reservationData.setRoomNumberId(resultSet.getInt("ROOM_NUMBER"));
                    reservationData.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));

                }
            }
        }
        log.log(Level.INFO, "Retrieved reservation with reservationID: {0}", reservationData.getReservationId());
        return reservationData;
    }

    /**
     * Updates single reservation in the RESERVATION table.
     *
     * @param reservation
     * @return
     * @throws java.sql.SQLException
     */
    public int updateReservation(Reservation reservation) throws SQLException {
        int result;

        String updateQuery = "UPDATE RESERVATION "
                + "SET RESERVATION_ID=?, CHECKIN_DATE=?, CHECKOUT_DATE=?, "
                + "CUSTOMER_ID=?, ROOM_NUMBER=?, EMPLOYEE_ID=? "
                + "WHERE RESERVATION_ID=?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(updateQuery);) {

            pStatement.setInt(1, reservation.getReservationId());
            pStatement.setTimestamp(2, Timestamp.valueOf(reservation.getCheckinDate()));
            pStatement.setTimestamp(3, Timestamp.valueOf(reservation.getCheckoutDate()));
            //pStatement.setInt(4, reservation.getCustomerId().getCustomerId());
            //pStatement.setInt(5, reservation.getRoomNumber().getRoomNumber());
            //pStatement.setInt(6, reservation.getEmployeeId().getEmployeeId());

            pStatement.setInt(4, reservation.getCustomerId());
            pStatement.setInt(5, reservation.getRoomNumberId());
            pStatement.setInt(6, reservation.getEmployeeId());

            pStatement.setInt(7, reservation.getReservationId());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Update status: {0}", result);
        log.log(Level.INFO, "Updated reservation with reservationID: {0}", reservation.getReservationId());
        return result;
    }

    /**
     * Adds a single reservation into the RESERVATION table.
     *
     * @param reservation
     * @return
     * @throws java.sql.SQLException
     */
    public int addReservation(Reservation reservation) throws SQLException {
        int result;

        String createQuery = "INSERT INTO RESERVATION "
                + "(CHECKIN_DATE, CHECKOUT_DATE, "
                + "CUSTOMER_ID, ROOM_NUMBER, EMPLOYEE_ID) "
                + "VALUES (?,?,?,?,?)";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);) {

            log.log(Level.INFO, "Printing DATE - UTIL obj: {0}", reservation.getCheckinDate());
            log.log(Level.INFO, "Printing DATE - SQL obj: {0}", convertLocalDateTimeToSqlDate(reservation.getCheckinDate()));
            pStatement.setTimestamp(1, Timestamp.valueOf(reservation.getCheckinDate()));
            pStatement.setTimestamp(2, Timestamp.valueOf(reservation.getCheckoutDate()));
            //pStatement.setInt(3, reservation.getCustomerId().getCustomerId());
            //pStatement.setInt(4, reservation.getRoomNumber().getRoomNumber());
            //pStatement.setInt(5, reservation.getEmployeeId().getEmployeeId());

            pStatement.setInt(3, reservation.getCustomerId());
            pStatement.setInt(4, reservation.getRoomNumberId());
            pStatement.setInt(5, reservation.getEmployeeId());

            result = pStatement.executeUpdate();

            try (ResultSet generatedKeys = pStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setReservationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }

        }
        log.log(Level.INFO, "Create status: {0}", result);
        log.log(Level.INFO, "Created reservation with reservationID: {0}", reservation.getReservationId());

        return reservation.getReservationId();
    }

    /**
     * This method deletes a single Reservation record based on the criteria of
     * the primary key field ID value.
     *
     * @param reservationId
     * @return The number of records deleted, should be 0 or 1
     * @throws SQLException
     */
    public int deleteReservation(int reservationId) throws SQLException {
        int result = 0;

        String deleteQuery = "DELETE FROM RESERVATION WHERE RESERVATION_ID = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(deleteQuery);) {

            pStatement.setInt(1, reservationId);

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Create status: {0}", result);

        if (result == 1) {
            log.log(Level.INFO, "Removed reservation with reservationID: {0}", reservationId);
        }

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
     * Converts LocalDateTime to SQL Date
     * @param dateTime
     * @return 
     */
    public java.sql.Date convertLocalDateTimeToSqlDate(LocalDateTime dateTime){    
        return new java.sql.Date(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
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
