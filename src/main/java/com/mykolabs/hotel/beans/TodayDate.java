/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beans;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nikprixmar
 */
@XmlRootElement
public class TodayDate {
    
    private Date currentDate;

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
   
}
