package com.niesens.garagespring;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;

@RestController
public class GarageDoorController implements GpioPinListenerDigital {

    @Autowired
    private Garage garage;

    @Value("${gpio.simulation}")
    private boolean simulation;

    private GpioController gpio;

    private Logger log = Logger.getLogger(GarageDoorController.class);

    /**
     * Provision GPIO pins
     */
    @PostConstruct
    public void initGpio() {

        if (!simulation) {
            gpio = GpioFactory.getInstance();
        }

        for (GarageDoor garageDoor : getGarageDoors()) {
            int openerSwitchPin = garageDoor.getOpenerSwitchPin();
            int closedSensorPin = garageDoor.getClosedSensorPin();

            Pin gpioPin = getPin(openerSwitchPin);
            logGpio("Provisioning pin " + openerSwitchPin
                    + " (opener switch of garage " + garageDoor.getId() + ")");
            if (!simulation) {
                gpio.provisionDigitalOutputPin(gpioPin, Integer.toString(openerSwitchPin), PinState.HIGH);
            }

            gpioPin = getPin(closedSensorPin);
            logGpio("Provisioning pin " + closedSensorPin
                    + " (closed door sensor of garage " + garageDoor.getId() + ")");
            if (!simulation) {
                GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(gpioPin, Integer.toString(closedSensorPin), PinPullResistance.PULL_UP);
                sensor.addListener(this);
            }
        }
    }

    /**
     * Retrive GPIO pin by pin number
     *
     * @param pin number
     * @return Pin
     * @throws IllegalArgumentException for invalid pins
     */
    private Pin getPin(int pin) {
        switch (pin) {
            case 0:
                return RaspiPin.GPIO_00;
            case 1:
                return RaspiPin.GPIO_01;
            case 2:
                return RaspiPin.GPIO_02;
            case 3:
                return RaspiPin.GPIO_03;
            case 4:
                return RaspiPin.GPIO_04;
            case 5:
                return RaspiPin.GPIO_05;
            case 6:
                return RaspiPin.GPIO_06;
            case 7:
                return RaspiPin.GPIO_07;
            case 8:
                return RaspiPin.GPIO_08;
            case 9:
                return RaspiPin.GPIO_09;
            case 10:
                return RaspiPin.GPIO_10;
            case 11:
                return RaspiPin.GPIO_11;
            case 12:
                return RaspiPin.GPIO_12;
            case 13:
                return RaspiPin.GPIO_13;
            case 14:
                return RaspiPin.GPIO_14;
            case 15:
                return RaspiPin.GPIO_15;
            case 16:
                return RaspiPin.GPIO_16;
            default:
                throw new IllegalArgumentException("Invalid GPIO pin " + pin);
        }
    }

    /**
     * Stop all GPIO activity/thread by shutting down the GPIO controller
     * (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
     */
    @PreDestroy
    public void destroy() {
        logGpio("Shutdown GPIO");

        if (!simulation) {
            gpio.shutdown();
        }
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        boolean isClosed = event.getState().isLow() ? true : false;
        int garageDoorId = Integer.parseInt(event.getPin().getName());
        GarageDoor garageDoor = garage.getDoors().get(garageDoorId);
        garageDoor.setClosed(isClosed);
        logGpio("Received event: Pin " + event.getPin().getPin().getAddress() + " is " + event.getState()
                + " (closed door sensor of garage " + garageDoorId + ")");
    }


    @RequestMapping(value = "/garage", method = RequestMethod.GET)
    public Garage getGarage() {
        return garage;
    }

    @RequestMapping(value = "/garageDoors", method = RequestMethod.GET)
    public Collection<GarageDoor> getGarageDoors() {
        return garage.getDoors().values();
    }

    @RequestMapping(value = "/garageDoor/{id}", method = RequestMethod.GET)
    public ResponseEntity<GarageDoor> getGarageDoor(@PathVariable("id") int id) {
        GarageDoor garageDoor = garage.getDoors().get(id);
        if (garageDoor != null) {
            return new ResponseEntity(garageDoor, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    private GpioPinDigitalOutput getGpioPin(int pin) {
        for (GpioPin gpioPin : gpio.getProvisionedPins()) {
            if (gpioPin.getName().equals(Integer.toString(pin)) && gpioPin.isMode(PinMode.DIGITAL_OUTPUT))
                return (GpioPinDigitalOutput) gpioPin;
        }

        return null;
    }

    @RequestMapping(value = "garageDoor/{id}", method = RequestMethod.PUT, params = "action=ACTIVATE_OPENER")
    public ResponseEntity activateOpener(@PathVariable("id") int id) {
        GarageDoor garageDoor = garage.getDoors().get(id);
        if (garageDoor == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        int openerSwitchPin = garageDoor.getOpenerSwitchPin();

        logGpio("Trigger pin " + openerSwitchPin
                + " (opener switch of garage " + garageDoor.getId() + ")");

        if (!simulation) {
            GpioPinDigitalOutput gpioPin = getGpioPin(openerSwitchPin);
            gpioPin.pulse(100, PinState.LOW, false);
        }

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    private void logGpio(String message) {
        String logMessage = "GPIO";
        if (simulation) {
            logMessage += " simulated";
        }
        logMessage+= ": " + message;

        if (simulation) {
            log.warn(logMessage);
        } else {
            log.info(logMessage);
        }
    }

}
