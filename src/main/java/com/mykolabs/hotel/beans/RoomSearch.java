/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beans;


import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author nikprixmar
 */

@XmlRootElement
public class RoomSearch {

    private BigDecimal roomPrice;
    private Date checkinDate;
    private Date checkoutDate;

    public BigDecimal getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(BigDecimal roomPrice) {
        this.roomPrice = roomPrice;
    }

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

        
}
