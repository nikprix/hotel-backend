package com.mykolabs.hotel.persistence;

import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.beans.Payment;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.Room;
import com.mykolabs.hotel.beans.RoomSearch;
import com.mykolabs.hotel.util.ConnectionHelper;
import com.mysql.jdbc.Statement;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
 * This class retrieves and persists Room related data into corresponding
 * database.
 *
 * @author nikprixmar
 */
public class RoomDAO {

    private static final Logger log = Logger.getLogger(RoomDAO.class.getName());
    // very basic URL validation regex
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.){1}";

    public RoomDAO() {
        super();
    }

    /**
     * Returns all rooms from the ROOM table.
     *
     * @param start
     * @param end
     * @param useLimits
     * @return
     * @throws java.sql.SQLException
     */
    public List<Room> getAllRooms(int start, int end, boolean useLimits) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        List<Room> rows = new ArrayList<>();

        String selectQuery = "SELECT ROOM_NUMBER, ROOM_PRICE, ROOM_TYPE, IMAGE, DESCRIPTION "
                + "FROM ROOM "
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

                    Room roomData = new Room();

                    roomData.setRoomNumber(resultSet.getInt("ROOM_NUMBER"));
                    roomData.setRoomPrice(resultSet.getBigDecimal("ROOM_PRICE"));
                    roomData.setRoomType(resultSet.getString("ROOM_TYPE"));

                    // Validating image URL and if it's partial, adding domain URL
                    if (isURL(resultSet.getString("IMAGE"))) {
                        // Image path contains full URL, not adding domain path
                        roomData.setImage(resultSet.getString("IMAGE"));
                    } else {
                        // adding full path to the image path, retrieved from the DB
                        roomData.setImage(props.getProperty(props.getProperty("IMG_ENVIRONMENT")) + resultSet.getString("IMAGE"));
                    }

                    roomData.setDescription(resultSet.getString("DESCRIPTION"));

                    rows.add(roomData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved rooms: {0}", rows.size());
        return rows;
    }

    /**
     * Returns all rooms which are available between two dates and price is
     * lower and x.
     *
     * @param roomSearch
     * @return
     * @throws java.sql.SQLException
     */
    public List<Room> getAllAvailableRooms(RoomSearch roomSearch) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        List<Room> rows = new ArrayList<>();

        String selectQuery = "SELECT ROOM_NUMBER, ROOM_PRICE, ROOM_TYPE, IMAGE, DESCRIPTION "
                + "FROM ROOM "
                + "WHERE ROOM_NUMBER NOT IN "
                + "("
                + "SELECT rm.ROOM_NUMBER "
                + "FROM RESERVATION rs "
                + "JOIN ROOM rm ON rs.ROOM_NUMBER = rm.ROOM_NUMBER "
                + "WHERE CHECKIN_DATE BETWEEN ? AND ?"
                + ")"
                + "AND ROOM_PRICE <= ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

                pStatement.setDate(1, convertToSqlDate(roomSearch.getCheckinDate()));
                pStatement.setDate(2, convertToSqlDate(roomSearch.getCheckoutDate()));
                pStatement.setBigDecimal(3, roomSearch.getRoomPrice());

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    Room roomData = new Room();

                    roomData.setRoomNumber(resultSet.getInt("ROOM_NUMBER"));
                    roomData.setRoomPrice(resultSet.getBigDecimal("ROOM_PRICE"));
                    roomData.setRoomType(resultSet.getString("ROOM_TYPE"));

                    // Validating image URL and if it's partial, adding domain URL
                    if (isURL(resultSet.getString("IMAGE"))) {
                        // Image path contains full URL, not adding domain path
                        roomData.setImage(resultSet.getString("IMAGE"));
                    } else {
                        // adding full path to the image path, retrieved from the DB
                        roomData.setImage(props.getProperty(props.getProperty("IMG_ENVIRONMENT")) + resultSet.getString("IMAGE"));
                    }

                    roomData.setDescription(resultSet.getString("DESCRIPTION"));

                    rows.add(roomData);
                }
            }
        }
        log.log(Level.INFO, "Amount of retrieved rooms matching search criteria: {0}", rows.size());
        return rows;
    }

    /**
     * Returns single room from the ROOM table.
     *
     * @param roomNumber
     * @return
     * @throws java.sql.SQLException
     */
    public Room getRoom(int roomNumber) throws SQLException {

        Properties props = ConnectionHelper.getProperties();
        Room roomData = new Room();

        String selectQuery = "SELECT ROOM_NUMBER, ROOM_PRICE, ROOM_TYPE, IMAGE, DESCRIPTION "
                + "FROM ROOM "
                + "WHERE ROOM_NUMBER = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setInt(1, roomNumber);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    roomData.setRoomNumber(resultSet.getInt("ROOM_NUMBER"));
                    roomData.setRoomPrice(resultSet.getBigDecimal("ROOM_PRICE"));
                    roomData.setRoomType(resultSet.getString("ROOM_TYPE"));

                    // Validating image URL and if it's partial, adding domain URL
                    if (isURL(resultSet.getString("IMAGE"))) {
                        // Image path contains full URL, not adding domain path
                        roomData.setImage(resultSet.getString("IMAGE"));
                    } else {
                        // adding full path to the image path, retrieved from the DB
                        roomData.setImage(props.getProperty(props.getProperty("IMG_ENVIRONMENT")) + resultSet.getString("IMAGE"));
                    }

                    roomData.setDescription(resultSet.getString("DESCRIPTION"));

                }
            }
        }
        log.log(Level.INFO, "Retrieved room with roomID: {0}", roomData.getRoomNumber());
        return roomData;
    }

    /**
     * Updates single room in the ROOM table.
     *
     * @param room
     * @return
     * @throws java.sql.SQLException
     */
    public int updateRoom(Room room) throws SQLException {
        int result = 0;

        String updateQuery = "UPDATE ROOM "
                + "SET ROOM_NUMBER=?, ROOM_PRICE=?, ROOM_TYPE=?, "
                + "IMAGE=?, DESCRIPTION=? "
                + "WHERE ROOM_NUMBER=?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(updateQuery);) {

            pStatement.setInt(1, room.getRoomNumber());
            pStatement.setBigDecimal(2, room.getRoomPrice());
            pStatement.setString(3, room.getRoomType());
            pStatement.setString(4, room.getImage());
            pStatement.setString(5, room.getDescription());

            pStatement.setInt(6, room.getRoomNumber());

            result = pStatement.executeUpdate();
        }
        log.log(Level.INFO, "Updated room with roomID: {0}", room.getRoomNumber());
        return result;
    }

    /**
     * Adds a single room into the ROOM table.
     *
     * @param room
     * @return
     * @throws java.sql.SQLException
     */
    public int addRoom(Room room) throws SQLException {
        int result = 0;

        String createQuery = "INSERT INTO ROOM "
                + "(ROOM_PRICE, ROOM_TYPE, IMAGE, DESCRIPTION) "
                + "VALUES (?,?,?,?)";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);) {

            pStatement.setBigDecimal(1, room.getRoomPrice());
            pStatement.setString(2, room.getRoomType());
            pStatement.setString(3, room.getImage());
            pStatement.setString(4, room.getDescription());

            result = pStatement.executeUpdate();

            try (ResultSet generatedKeys = pStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setRoomNumber(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating room failed, no ID obtained.");
                }
            }

        }
        log.log(Level.INFO, "Created room with roomID: {0}", result);
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
