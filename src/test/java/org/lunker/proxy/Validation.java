package org.lunker.proxy;

/**
 * Created by dongqlee on 2018. 5. 16..
 */
public class Validation {
    private boolean isValidate=false;
    private int responseCode=0;
    private String cause="";

    public Validation() {
    }

    public Validation(boolean isValidate, int responseCode, String cause) {
        this.isValidate = isValidate;
        this.responseCode = responseCode;
        this.cause = cause;
    }

    public boolean isValidate() {
        return isValidate;
    }

    public void setValidate(boolean validate) {
        isValidate = validate;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "Validation{" +
                "isValidate=" + isValidate +
                ", responseCode=" + responseCode +
                ", cause='" + cause + '\'' +
                '}';
    }
}
