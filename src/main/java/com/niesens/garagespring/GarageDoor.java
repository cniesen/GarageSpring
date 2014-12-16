package com.niesens.garagespring;

public class GarageDoor {

    private int id;
    private String description;
    private int openerSwitchPin;
    private int closedSensorPin;
    private boolean closed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOpenerSwitchPin() {
        return openerSwitchPin;
    }

    public void setOpenerSwitchPin(int openerSwitchPin) {
        this.openerSwitchPin = openerSwitchPin;
    }

    public int getClosedSensorPin() {
        return closedSensorPin;
    }

    public void setClosedSensorPin(int closedSensorPin) {
        this.closedSensorPin = closedSensorPin;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
