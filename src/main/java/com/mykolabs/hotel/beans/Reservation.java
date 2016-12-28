/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mykolabs.hotel.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mykolabs.hotel.util.CustomDateDeserializer;
import com.mykolabs.hotel.util.CustomDateSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nikprixmar
 */
@Entity
@Table(name = "RESERVATION", catalog = "HOTEL", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r"),
    @NamedQuery(name = "Reservation.findByReservationId", query = "SELECT r FROM Reservation r WHERE r.reservationId = :reservationId"),
    @NamedQuery(name = "Reservation.findByCheckinDate", query = "SELECT r FROM Reservation r WHERE r.checkinDate = :checkinDate"),
    @NamedQuery(name = "Reservation.findByCheckoutDate", query = "SELECT r FROM Reservation r WHERE r.checkoutDate = :checkoutDate")})
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "RESERVATION_ID")
    private Integer reservationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHECKIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime checkinDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHECKOUT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime checkoutDate;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationId")
//    @JsonIgnore
//    private List<Payment> paymentList;
//    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID")
//    @ManyToOne(optional = false)
//    private Customer customerId;
//    @JoinColumn(name = "ROOM_NUMBER", referencedColumnName = "ROOM_NUMBER")
//    @ManyToOne(optional = false)
//    private Room roomNumber;
//    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
//    @ManyToOne(optional = false)
//    private Employee employeeId;
    @Basic(optional = false)
    @Column(name = "CUSTOMER_ID")
    private Integer customerId;

    @Basic(optional = false)
    @Column(name = "ROOM_NUMBER")
    private Integer roomNumberId;

    @Basic(optional = false)
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;

    public Reservation() {
    }

    public Reservation(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Reservation(Integer reservationId, LocalDateTime checkinDate, LocalDateTime checkoutDate) {
        this.reservationId = reservationId;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDateTime checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDateTime getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDateTime checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

//    @XmlTransient
//    public List<Payment> getPaymentList() {
//        return paymentList;
//    }
//
//    public void setPaymentList(List<Payment> paymentList) {
//        this.paymentList = paymentList;
//    }
//
//    public Customer getCustomerId() {
//        return customerId;
//    }
//
//    public void setCustomerId(Customer customerId) {
//        this.customerId = customerId;
//    }
//
//    public Room getRoomNumber() {
//        return roomNumber;
//    }
//
//    public void setRoomNumber(Room roomNumber) {
//        this.roomNumber = roomNumber;
//    }
//
//    public Employee getEmployeeId() {
//        return employeeId;
//    }
//
//    public void setEmployeeId(Employee employeeId) {
//        this.employeeId = employeeId;
//    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getRoomNumberId() {
        return roomNumberId;
    }

    public void setRoomNumberId(Integer roomNumberId) {
        this.roomNumberId = roomNumberId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mykolabs.hotel.beans.Reservation[ reservationId=" + reservationId + " ]";
    }

}
