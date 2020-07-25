package com.schoolvote.schoolvote;

public class pubMethods {

    public boolean isNumeric(String s) {
        if(s != "" && s != null) return s.matches("-?\\d+(\\.\\d+)?");
        else return false;
    }
}
