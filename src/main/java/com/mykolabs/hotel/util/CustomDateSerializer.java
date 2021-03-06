/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author nikprixmar
 */
public class CustomDateSerializer extends StdSerializer<LocalDateTime> {
    // private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a z");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
 
    public CustomDateSerializer() {
        this(null);
    }
 
    public CustomDateSerializer(Class t) {
        super(t);
    }
     
    @Override
    public void serialize (LocalDateTime value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}
