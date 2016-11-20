package com.mykolabs.hotel.util;

import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.beans.Payment;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.Room;

/**
 * Validation helper for JSON objects / Parameters in requests.
 *
 * @author nikprixmar
 */
public class Validator {

    /**
     * Validates passed to the POST / PUT methods Customer instances are not
     * null
     *
     * @param customer
     * @return
     */
    public static boolean isCustomerExists(Customer customer) {
        return customer != null;
    }

    /**
     * Validates if Customer instances, passed to the POST / PUT methods, have
     * valid data
     *
     * @param customer
     * @return
     */
    public static boolean isCustomerValid(Customer customer) {

        return !(customer.getCustomerId() == null
                || customer.getFirstName() == null
                || customer.getLastName() == null
                || customer.getAddress() == null
                || customer.getCity() == null
                || customer.getState() == null
                || customer.getPhone() == null);
    }

    /**
     * Validates passed to the POST / PUT methods Reservation instances are not
     * null
     *
     * @param reservation
     * @return
     */
    public static boolean isReservationExists(Reservation reservation) {
        return reservation != null;
    }

    /**
     * Validates if Reservation instances, passed to the POST / PUT methods,
     * have valid data
     *
     * @param reservation
     * @return
     */
    public static boolean isReservationValid(Reservation reservation) {

        return !(reservation.getReservationId() == null
                || reservation.getCheckinDate() == null
                || reservation.getCheckoutDate() == null
                || reservation.getCustomerId() == null
                || reservation.getRoomNumber() == null
                || reservation.getEmployeeId() == null);
    }

    /**
     * Validates passed to the POST / PUT methods Payment instances are not null
     *
     * @param payment
     * @return
     */
    public static boolean isPaymentExists(Payment payment) {
        return payment != null;
    }

    /**
     * Validates if Payment instances, passed to the POST / PUT methods, have
     * valid data
     *
     * @param payment
     * @return
     */
    public static boolean isPaymentValid(Payment payment) {

        return !(payment.getPaymentId() == null
                || payment.getCardType() == null
                || payment.getCardNumber() == null
                || payment.getCardExpiration() == null
                || payment.getDescription() == null
                || payment.getCustomerId() == null
                || payment.getReservationId() == null);
    }
    
     /**
     * Validates passed to the POST / PUT methods Room instances are not null
     *
     * @param room
     * @return
     */
    public static boolean isRoomExists(Room room) {
        return room != null;
    }

    /**
     * Validates if Room instances, passed to the POST / PUT methods, have
     * valid data
     *
     * @param room
     * @return
     */
    public static boolean isRoomValid(Room room) {

        return !(room.getRoomNumber() == null
                || room.getRoomPrice() == null
                || room.getRoomType() == null
                || room.getDescription() == null);
    }

}
