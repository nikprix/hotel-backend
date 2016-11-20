package com.mykolabs.hotel.persistence;

import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.util.ConnectionHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class retrieves and persists EMployee related data into corresponding
 * database.
 *
 * @author nikprixmar
 */
public class EmployeeDAO {

    private static final Logger log = Logger.getLogger(EmployeeDAO.class.getName());

    public EmployeeDAO() {
        super();
    }

    /**
     * Retrieves Employee's details from the DB by username and saves into the Employee object.
     *
     * @param username
     * @return
     * @throws SQLException
     */
    public Employee getEmployee(String username) throws SQLException {

        Employee employee = new Employee();

        String selectQuery = "SELECT EMPLOYEE_ID, USERNAME, PASSWORD, TOKEN, ROLES "
                + "FROM EMPLOYEE "
                + "WHERE USERNAME = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setString(1, username);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    employee.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employee.setUsername(resultSet.getString("USERNAME"));
                    employee.setPassword(resultSet.getString("PASSWORD"));
                    employee.setToken(resultSet.getString("TOKEN"));
                    employee.setRoles(resultSet.getString("ROLES"));
                }
            }
        }
        log.log(Level.INFO, "Retrieved username: {0}", employee.getUsername());
        log.log(Level.INFO, "Retrieved password: {0}", employee.getPassword());
        return employee;
    }
    
        /**
     * Retrieves Employee's details from the DB by ID and saves into the Employee object.
     *
     * @param employeeId
     * @return
     * @throws SQLException
     */
    public Employee getEmployee(int employeeId) throws SQLException {

        Employee employee = new Employee();

        String selectQuery = "SELECT EMPLOYEE_ID, USERNAME, PASSWORD, TOKEN, ROLES "
                + "FROM EMPLOYEE "
                + "WHERE EMPLOYEE_ID = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {

            pStatement.setInt(1, employeeId);

            try (ResultSet resultSet = pStatement.executeQuery();) {
                while (resultSet.next()) {

                    employee.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employee.setUsername(resultSet.getString("USERNAME"));
                    employee.setPassword(resultSet.getString("PASSWORD"));
                    employee.setToken(resultSet.getString("TOKEN"));
                    employee.setRoles(resultSet.getString("ROLES"));
                }
            }
        }
        log.log(Level.INFO, "Retrieved username: {0}", employee.getUsername());
        log.log(Level.INFO, "Retrieved password: {0}", employee.getPassword());
        return employee;
    }

    /**
     * Records generated token into the DB under current employee's record.
     *
     * @param username
     * @param token
     * @return
     * @throws SQLException
     */
    public int setToken(String username, String token) throws SQLException {
        int result = 0;

        String updateQuery = "UPDATE EMPLOYEE "
                + "SET TOKEN = ?"
                + "WHERE USERNAME = ?";

        // Using Java 1.7 try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = ConnectionHelper.getConnection();
                // Using PreparedStatements to guard against SQL Injection
                PreparedStatement pStatement = connection.prepareStatement(updateQuery);) {

            pStatement.setString(1, token);
            pStatement.setString(2, username);

            result = pStatement.executeUpdate();

        }
        log.log(Level.INFO, "Employee token set query result: {0}", result);
        return result;
    }
}
