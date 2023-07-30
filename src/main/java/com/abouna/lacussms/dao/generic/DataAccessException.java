package com.abouna.lacussms.dao.generic;

public class DataAccessException extends Exception {
    public DataAccessException() {
    }

    public DataAccessException(String string) {
        super(string);
    }

    public DataAccessException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public DataAccessException(Throwable thrwbl) {
        super(thrwbl);
    }
}
