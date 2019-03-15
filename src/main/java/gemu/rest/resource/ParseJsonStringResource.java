package gemu.rest.resource;

import gemu.rest.annotation.MyUrl;
import gemu.rest.core.MyRestParams;
import gemu.rest.core.RequestMethod;
import gemu.rest.tool.auxiliary.JsonStringFormatParse;

/**
 * 解析JSON数据
 * Created by Gemu on 2016/9/19.
 */
@MyUrl("/parseJson")
public class ParseJsonStringResource {

    /**
     * 解析无格式的JSON字符串
     * param paramsObj
     * @return
     */
    @MyUrl(value = "/", method = RequestMethod.POST)
    public String parseJsonString(MyRestParams paramsObj) {
//        String jsonString = JsonStringFormatParse.formatParse(paramsObj.getFormParams().get("jsonString")[0].toString());
        String jsonString = JsonStringFormatParse.formatParseUseSwitch(paramsObj.getFormParams().get("jsonString")[0].toString());
        return jsonString;
    }

}
