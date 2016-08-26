package gemu.rest.core;

/**
 * 用来描述参数（这个应该就是领域模型吧？）
 */
public class Params {
    private String prefix;
    private String suffix;
    private String param;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Params() {
    }

    public Params(String prefix, String suffix, String param) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.param = param;
    }
}
