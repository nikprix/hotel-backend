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
@XmlRootElement(name = "allReservationsList")
@XmlAccessorType(XmlAccessType.FIELD)
public class AllReservationListWithCustomer {

    @XmlElement(name = "todayReservation")
    private List<TodayReservation> allReservationList;

    public List<TodayReservation> getAllReservationList() {
        return allReservationList;
    }

    public void setAllReservationList(List<TodayReservation> allReservationList) {
        this.allReservationList = allReservationList;
    }
}
