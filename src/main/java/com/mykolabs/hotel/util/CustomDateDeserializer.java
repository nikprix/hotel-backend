/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.mykolabs.hotel.persistence.ReservationDAO;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nikprixmar
 */
public class CustomDateDeserializer extends StdDeserializer<LocalDateTime> {
    
    private static final Logger log = Logger.getLogger(CustomDateDeserializer.class.getName());
 
   //private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
   //private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
   // private DateFormat formatter = new ISO8601DateFormat();
   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CustomDateDeserializer() {
        this(null);
    }
 
    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }
 
    @Override
    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context)
      throws IOException, JsonProcessingException {
        String date = jsonparser.getText();
        
        log.log(Level.INFO, ">>>>>>>>>>> Date before decerialization: {0}", date);
        
        log.log(Level.INFO, ">>>>>>>>> Date after decerialization: {0}", LocalDateTime.parse(date, formatter).toString());
        
        return LocalDateTime.parse(date, formatter);
    }
}
