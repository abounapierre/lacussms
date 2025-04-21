package com.abouna.lacussms.dto;

public class ClientCSVModel {
    private final String col1;
    private final String col2;
    private final String col3;
    private final String col4;
    private final String col5;
    private final String col6;
    private final String col7;

    public ClientCSVModel(String col1, String col2, String col3, String col4, String col5, String col6, String col7) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
        this.col6 = col6;
        this.col7 = col7;
    }

    public String getCol1() {
        return col1;
    }
    public String getCol2() {
        return col2;
    }
    public String getCol3() {
        return col3;
    }
    public String getCol4() {
        return col4;
    }
    public String getCol5() {
        return col5;
    }
    public String getCol6() {
        return col6;
    }
    public String getCol7() {
        return col7;
    }
}
