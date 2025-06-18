package com.koralix.oneforall.base.parser;

public class ParseException extends Exception {
    private int loc;
    private String code;

    public ParseException(int loc, String code) {
        super("An exception happened while parsing");
        this.loc = loc;
        this.code = code;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
