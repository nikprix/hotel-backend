/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beansLists;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mykolabs.hotel.persistence.ReservationDAO;
import com.mykolabs.hotel.util.CustomDateDeserializer;
import com.mykolabs.hotel.util.CustomDateSerializer;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds all reservations for today.
 *
 * @author nikprixmar
 */
@XmlRootElement
public class TodayReservation {

    private static final Logger log = Logger.getLogger(TodayReservation.class.getName());

    private int reservationId;
    private String firstName;
    private String lastName;
    private int roomNumber;
    
    @JsonSerialize(using = CustomDateSerializer.class)
    //@JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime checkinDate;

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDateTime getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDateTime checkinDate) {
        this.checkinDate = checkinDate;
    }

}
