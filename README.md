GarageSpring
============ 
A Spring Boot application to monitor and control garages via a
Raspberry Pi.

GPIO Configuration
------------------
Update config/application.yml for your specific Rasberry Pi setup. Reference the pin numbering information at http://www.pi4j.com and use the GPIO number in this configuration file. 

First of all change the security.user.password value to something else. Currently this configuration allows the user "user" to authenticate with "garage as the password.  Don't forget to run use SSL otherwise any password and communication is in clear text and very insecure.

The gpio.simulation property can be set to true in order to run GarageSpring on a device other than Rasberry Pi.  Obviously no GPIO signaling is performed. Yes, you don't actually monitor and control the garage in this case.

The garage door number and id must match.  Here's an example of the file for three garage doors.  Note that two "n" on the third door are there for clarification and they needs to be a number (i.e. 3) in order to be valid.

        security.user.password: garage
        gpio.simulation: true

        garage:
            doors:
                1:
                    id: 1
                    description: Double Garage Door
                    openerSwitchPin: 0
                    closedSensorPin: 1
                2:
                    id: 2
                    description: Single Garage Door
                    openerSwitchPin: 2
                    closedSensorPin: 3
                n:
                    id: n
                    description: Single Garage Door
                    openerSwitchPin: 4
                    closedSensorPin: 5
