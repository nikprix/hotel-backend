package com.mykolabs.hotel.beansLists;

import com.mykolabs.hotel.beans.Room;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nikprixmar
 */
@XmlRootElement(name = "roomList")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomList {

    @XmlElement(name = "room")
    private List<Room> roomList;

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

}
