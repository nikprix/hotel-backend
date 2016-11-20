package com.mykolabs.hotel.exceptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Used for exception reporting.
 *
 * @author nikprixmar
 */
public class GenericErrorMessage implements Serializable {

    private static final long serialVersionUID = 6103739170311479902L;
    private String message;
    private int code;
    private Collection<Object> challenges = new ArrayList<>();

    // zero arg constructor required for Jersey
    public GenericErrorMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Collection<Object> getChallenges() {
        return challenges;
    }

    public void setChallenges(Collection<Object> challenges) {
        this.challenges = challenges;
    }
}
