/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nikprixmar
 */
@Entity
@Table(name = "ROOM", catalog = "HOTEL", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r"),
    @NamedQuery(name = "Room.findByRoomNumber", query = "SELECT r FROM Room r WHERE r.roomNumber = :roomNumber"),
    @NamedQuery(name = "Room.findByRoomPrice", query = "SELECT r FROM Room r WHERE r.roomPrice = :roomPrice"),
    @NamedQuery(name = "Room.findByRoomType", query = "SELECT r FROM Room r WHERE r.roomType = :roomType")})
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ROOM_NUMBER")
    private Integer roomNumber;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ROOM_PRICE")
    private BigDecimal roomPrice;
    @Size(max = 45)
    @Column(name = "ROOM_TYPE")
    private String roomType;
    @Size(max = 225)
    @Column(name = "IMAGE")
    private String image;
    @Lob
    @Size(max = 65535)
    @Column(name = "DESCRIPTION")
    private String description;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomNumber")
//    @JsonIgnore
//    private List<Reservation> reservationList;
    public Room() {
    }

    public Room(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(BigDecimal roomPrice) {
        this.roomPrice = roomPrice;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    @XmlTransient
//    public List<Reservation> getReservationList() {
//        return reservationList;
//    }
//
//    public void setReservationList(List<Reservation> reservationList) {
//        this.reservationList = reservationList;
//    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomNumber != null ? roomNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomNumber == null && other.roomNumber != null) || (this.roomNumber != null && !this.roomNumber.equals(other.roomNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mykolabs.hotel.beans.Room[ roomNumber=" + roomNumber + " ]";
    }

}
