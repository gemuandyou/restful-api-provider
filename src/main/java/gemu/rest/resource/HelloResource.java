package gemu.rest.resource;

import java.io.File;

import gemu.rest.annotation.MyUrl;
import gemu.rest.core.MyRestParams;
import gemu.rest.core.ReturnType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@MyUrl("/hello")
public class HelloResource {

	// 还是给你个实例URL吧！ http://localhost:8080/MyRest2/rest/hello/say/idabc1-hash!abc2test/!abc3$abc4/hello
	@MyUrl(value="/say/id@param1@-hash!@param2@test/!@param3@$@param4@/hello", type=ReturnType.STRING) //TODO 设置GET, PUT, POST, DELETE访问资源
	public String sayHello(MyRestParams paramsObj) {
		System.out.println(paramsObj.getParams());
		return "hello : " + paramsObj;
	}

	@MyUrl(value="/download", type=ReturnType.TEXT)
	private File downLoad() {
		return new File("F:/1.json");
	}

	@MyUrl(value="/getJson/@op@", type=ReturnType.JSON)
	private String getJSON(MyRestParams paramsObj) {
		System.out.println(paramsObj.getParams().get("op"));
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		obj.put("key", "demo1");
		for(int i = 0; i < 10; i ++) {
			arr.add("value" + i);
		}
		obj.put("value", arr);
		return obj.toString();
	}
	
}
