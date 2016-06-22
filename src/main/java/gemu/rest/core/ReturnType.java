package gemu.rest.core;

/**
 * 返回值类型
 * @author Gemu
 * @version	Nov 17, 2015 10:10:59 AM
 */
public enum ReturnType {
	
	STRING,TEXT("text/plain"),JSON("application/json; charset=utf-8"); //TODO 添加其他格式
	
	private String value;

	
	private ReturnType() {
	}

	private ReturnType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
