package gemu.rest.core;

/**
 * Created by Gemu on 2016/8/26.
 */
public enum RequestMethod {

    GET("get"),POST("post"),DELETE("delete"),PUT("put");

    private String value;

    RequestMethod() {}

    RequestMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
