package com.mzamodev.conferenceapp;

public class Chat {
    String name;
    String cellphone;
    String text;

    public Chat(){

    }

    public Chat(String name, String cellphone, String text) {
        this.name = name;
        this.cellphone = cellphone;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
