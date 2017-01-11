package net.devcats.stepit.Model;

/**
 * Created by Ken Juarez on 12/17/16.
 * Holds information about currently logged in StepIt user
 */

public class UserModel {

    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private Device device;

//    private String fitbitToken;

    private int stepCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

//    public String getFitbitToken() {
//        return fitbitToken;
//    }

//    public void setFitbitToken(String fitbitToken) {
//        this.fitbitToken = fitbitToken;
//    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
}
