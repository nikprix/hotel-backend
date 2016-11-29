/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beansLists;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nikprixmar
 */
@XmlRootElement(name = "todayReservationsList")
@XmlAccessorType(XmlAccessType.FIELD)
public class TodayReservationList {

    @XmlElement(name = "todayReservation")
    private List<TodayReservation> todayReservationList;

    public List<TodayReservation> getTodayReservationList() {
        return todayReservationList;
    }

    public void setTodayReservationList(List<TodayReservation> todayReservationList) {
        this.todayReservationList = todayReservationList;
    }
}
