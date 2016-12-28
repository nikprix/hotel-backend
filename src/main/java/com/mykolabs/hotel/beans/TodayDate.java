/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beans;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mykolabs.hotel.util.CustomDateDeserializer;
import com.mykolabs.hotel.util.CustomDateSerializer;
import java.time.LocalDateTime;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nikprixmar
 */
@XmlRootElement
public class TodayDate {
    
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime currentDate;

    public LocalDateTime getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDateTime currentDate) {
        this.currentDate = currentDate;
    }
   
}
