package com.mykolabs.hotel.persistence;

import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.Room;
import com.mykolabs.hotel.util.ConnectionHelper;
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
 * This class retrieves and persists Reservation related data into corresponding database.
 * @author nikprixmar
 */
public class ReservationDAO {

    private static final Logger log = Logger.getLogger(ReservationDAO.class.getName());

    public ReservationDAO() {
        super();
    }

    /**
     * Returns all reservations from the Reservation table.
     *
     * @param start
     * @param end
     * @param flag
     * @return
     * @throws java.sql.SQLException
     */
    public List<Reservation> getAllReservations(int start, int end, boolean useLimits) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        List<Reservation> rows = new ArrayList<>();

        String selectQuery = "SELECT RESERVATION_ID, CHECKIN_DATE, CHECKOUT_DATE, CUSTOMER_ID, ROOM_NUMBER, EMPLOYEE_ID "
                + "FROM RESERVATION "
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
                    reservationsData.setCheckinDate(resultSet.getDate("CHECKIN_DATE"));
                    reservationsData.setCheckoutDate(resultSet.getDate("CHECKOUT_DATE"));
                    
                    CustomerDAO customerDAO = new CustomerDAO();
                    Customer customer = customerDAO.getCustomer(resultSet.getInt("CUSTOMER_ID"));
                    RoomDAO roomDAO = new RoomDAO();
                    Room room = roomDAO.getRoom(resultSet.getInt("ROOM_NUMBER"));
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    Employee employee = employeeDAO.getEmployee(resultSet.getInt("EMPLOYEE_ID"));
                            
                    
                    reservationsData.setCustomerId(customer);
                    reservationsData.setRoomNumber(room);
                    reservationsData.setEmployeeId(employee);

                    rows.add(reservationsData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved reservations: {0}", rows.size());
        return rows;
    }

    /**
     * Returns single book from the INVENTORY table.
     *
     * @param isbn
     * @return
     */
    public Inventory getBook(String isbn) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        Inventory booksData = new Inventory();

        String selectQuery = "SELECT ISBN, DATEOFENTRY, TITLE, AUTHORS, PUBLISHER, DATEOFPUBLICATION, PAGES, GENRE, IMAGE, COST, LIST, REMOVALSTATUS, DESCRIPTION "
                + "FROM INVENTORY "
                + "WHERE ISBN = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setString(1, isbn);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    booksData.setIsbn(resultSet.getString("ISBN"));
                    booksData.setDateofentry(resultSet.getDate("DATEOFENTRY"));
                    booksData.setTitle(resultSet.getString("TITLE"));
                    booksData.setAuthors(resultSet.getString("AUTHORS"));
                    booksData.setPublisher(resultSet.getString("PUBLISHER"));
                    booksData.setDateofpublication(resultSet.getDate("DATEOFPUBLICATION"));
                    booksData.setPages(resultSet.getInt("PAGES"));
                    booksData.setGenre(resultSet.getString("GENRE"));

                    // Validating image URL and if it's partial, adding domain URL
                    if (isURL(resultSet.getString("IMAGE"))) {
                        // Image path contains full URL, not adding domain path
                        booksData.setImage(resultSet.getString("IMAGE"));
                    } else {
                        // adding full path to the image path, retrieved from the DB
                        booksData.setImage(props.getProperty("IMAGE_BASE_URL_DEV") + resultSet.getString("IMAGE"));
                    }

                    booksData.setCost(resultSet.getBigDecimal("COST"));
                    booksData.setList(resultSet.getBigDecimal("LIST"));
                    booksData.setRemovalstatus(resultSet.getBoolean("REMOVALSTATUS"));
                    booksData.setDescription(resultSet.getString("DESCRIPTION"));

                }
            }
        }
        log.log(Level.INFO, "Retrieved book with ISBN: {0}", booksData.getIsbn());
        return booksData;
    }

    /**
     * Updates single book in the INVENTORY table.
     *
     * @param book
     * @return
     * @throws java.sql.SQLException
     */
    public int updateBook(Inventory book) throws SQLException {
        int result = 0;

        String updateQuery = "UPDATE INVENTORY "
                + "SET DATEOFENTRY=?, TITLE=?, AUTHORS=?, PUBLISHER=?, "
                + "DATEOFPUBLICATION=?, PAGES=?, GENRE=?, IMAGE=?, COST=?, LIST=?, "
                + "REMOVALSTATUS=?, DESCRIPTION=? "
                + "WHERE ISBN=?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(updateQuery);) {

            pStatement.setDate(1, convertToSqlDate(book.getDateofentry()));
            pStatement.setString(2, book.getTitle());
            pStatement.setString(3, book.getAuthors());
            pStatement.setString(4, book.getPublisher());
            pStatement.setDate(5, convertToSqlDate(book.getDateofpublication()));
            pStatement.setInt(6, book.getPages());
            pStatement.setString(7, book.getGenre());
            pStatement.setString(8, book.getImage());
            pStatement.setBigDecimal(9, book.getCost());
            pStatement.setBigDecimal(10, book.getList());
            pStatement.setBoolean(11, book.getRemovalstatus());
            pStatement.setString(12, book.getDescription());

            pStatement.setString(13, book.getIsbn());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Updated book with ISBN: {0}", book.getIsbn());
        return result;
    }

    /**
     * Adds a single book into the INVENTORY table.
     *
     * @param book
     * @param isbn
     * @return
     */
    public int addBook(Inventory book) throws SQLException {
        int result = 0;

        String createQuery = "INSERT INTO INVENTORY "
                + "(ISBN, DATEOFENTRY, TITLE, AUTHORS, PUBLISHER, "
                + "DATEOFPUBLICATION, PAGES, GENRE, IMAGE, COST, LIST, "
                + "REMOVALSTATUS, DESCRIPTION) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(createQuery);) {

            pStatement.setString(1, book.getIsbn());
            pStatement.setDate(2, convertToSqlDate(book.getDateofentry()));
            pStatement.setString(3, book.getTitle());
            pStatement.setString(4, book.getAuthors());
            pStatement.setString(5, book.getPublisher());
            pStatement.setDate(6, convertToSqlDate(book.getDateofpublication()));
            pStatement.setInt(7, book.getPages());
            pStatement.setString(8, book.getGenre());
            pStatement.setString(9, book.getImage());
            pStatement.setBigDecimal(10, book.getCost());
            pStatement.setBigDecimal(11, book.getList());
            pStatement.setBoolean(12, book.getRemovalstatus());
            pStatement.setString(13, book.getDescription());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Updated book with ISBN: {0}", book.getIsbn());
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

    /**
     * URL validation helper method.
     *
     * @param path
     * @return
     */
    private boolean isURL(String path) {

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(path);

        return m.find();
    }

}
