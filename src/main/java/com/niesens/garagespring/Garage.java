package com.niesens.garagespring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConfigurationProperties(prefix="garage")
public class Garage {
    Map<Integer, GarageDoor> doors = new HashMap<Integer, GarageDoor>();

    public Map<Integer, GarageDoor> getDoors() {
        return doors;
    }

    public void setDoors(Map<Integer, GarageDoor> doors) {
        this.doors = doors;
    }
}
