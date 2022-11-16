package com.mzamodev.conferenceapp;

public class Users {

    private String FName;
    private String StudentNo;
    private String Cellphone;
    private String Type;
    private String Password;

    public Users(){}

    public Users(String FName, String studentNo, String cellphone, String type, String password) {
        this.FName = FName;
        StudentNo = studentNo;
        Cellphone = cellphone;
        Type = type;
        Password = password;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getStudentNo() {
        return StudentNo;
    }

    public void setStudentNo(String studentNo) {
        StudentNo = studentNo;
    }

    public String getCellphone() {
        return Cellphone;
    }

    public void setCellphone(String cellphone) {
        Cellphone = cellphone;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


}
