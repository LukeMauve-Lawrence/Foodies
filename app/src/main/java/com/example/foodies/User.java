package com.example.foodies;

import java.time.LocalDate;

public class User {

    //variables
    public String userID, userName, userSurname, userGender, userMeasurementSystem, userEmail, userBirthday;
    public double userHeight, userStartingWeight, userWeightGoal, userCalorieGoal, userStepGoal;

    //blank constructor
    public User() {
    }

    //constructor
    public User(String userID, String userName, String userSurname, String userGender, String userMeasurementSystem, String userEmail, String userBirthday, double userHeight, double userStartingWeight, double userWeightGoal, double userCalorieGoal, double userStepGoal) {
        this.userID = userID;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userGender = userGender;
        this.userMeasurementSystem = userMeasurementSystem;
        this.userEmail = userEmail;
        this.userBirthday = userBirthday;
        this.userHeight = userHeight;
        this.userStartingWeight = userStartingWeight;
        this.userWeightGoal = userWeightGoal;
        this.userCalorieGoal = userCalorieGoal;
        this.userStepGoal = userStepGoal;
    }

    //getters and setters
    public double getUserStartingWeight() {
        return userStartingWeight;
    }

    public void setUserStartingWeight(double userStartingWeight) {
        this.userStartingWeight = userStartingWeight;
    }

    public double getUserWeightGoal() {
        return userWeightGoal;
    }

    public void setUserWeightGoal(double userWeightGoal) {
        this.userWeightGoal = userWeightGoal;
    }

    public double getUserCalorieGoal() {
        return userCalorieGoal;
    }

    public void setUserCalorieGoal(double userCalorieGoal) {
        this.userCalorieGoal = userCalorieGoal;
    }

    public double getUserStepGoal() {
        return userStepGoal;
    }

    public void setUserStepGoal(double userStepGoal) {
        this.userStepGoal = userStepGoal;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserMeasurementSystem() {
        return userMeasurementSystem;
    }

    public void setUserMeasurementSystem(String userMeasurementSystem) {
        this.userMeasurementSystem = userMeasurementSystem;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public double getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(double userHeight) {
        this.userHeight = userHeight;
    }
}
