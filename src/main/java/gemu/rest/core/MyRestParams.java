package gemu.rest.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取请求url中的参数
 * @author Gemu
 * @version	Nov 17, 2015 2:10:53 PM
 */
public class MyRestParams {

	/**
	 * url中的参数
	 */
	private Map<String, Object> params = new HashMap<String, Object>();
	/**
	 * post请求参数
	 */
	private Map<String, String[]> formParams = new HashMap<String, String[]>();

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Map<String, String[]> getFormParams() {
		return formParams;
	}

	public void setFormParams(Map<String, String[]> formParams) {
		this.formParams = formParams;
	}

	public MyRestParams() {
	}

	public MyRestParams(Map<String, Object> params, Map<String, String[]> formParams) {
		this.params = params;
		this.formParams = formParams;
	}

	@Override
	public String toString() {
		return "MyRestParams{" +
				"params=" + params +
				" , postParms=" + formParams +
				'}';
	}
}
