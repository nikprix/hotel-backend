package com.mykolabs.hotel.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC Connection Manager Utility class.
 *
 * @author nikprixmar
 */
public class ConnectionHelper {
    
    /**
     * Returns Connection to the BOOKS DB
     * @return 
     */
    public static Connection getConnection() {
		Properties props = getProperties();
		Connection con = null;
		try {
			// load the Driver Class
			Class.forName(props.getProperty("DB_DRIVER_CLASS"));

			// create the connection now
			con = DriverManager.getConnection(props.getProperty("DB_URL"),
					props.getProperty("DB_USERNAME"),
					props.getProperty("DB_PASSWORD"));
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
    
    /**
     * Returns properties 
     * @return 
     */
    public static Properties getProperties(){
        Properties props = new Properties();
        InputStream in = ConnectionHelper.class.getClassLoader().getResourceAsStream("db.properties");
        
        try {
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }

}
