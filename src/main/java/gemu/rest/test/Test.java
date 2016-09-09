package gemu.rest.test;

import java.io.File;
import java.io.IOException;

public class Test {

	private static String pkg = "gemu.rest";

	public static void main(String[] args) throws IOException {

//		File file = new File("C:/Users/Gemu/Documents/test.json");
//		StringBuffer sb = new StringBuffer();
//		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
//		int ch ;
//		while((ch = reader.read()) != -1) {
//			sb.append((char) ch);
//		}
//		System.out.println(sb.toString());
//		JSONObject.fromObject(sb.toString());
//		System.out.println("--------------------");
//		String str = "@safa#@asdf";
//		for (String s: str.split("@")) {
//			System.out.println(s);
//		}

		File file1 = new File("/Users/gemu/Development/idea_workspace/MyRest/target/my-rest/WEB-INF/classes/gemu/rest");
		System.out.println(file1.isDirectory());
	}


	public static String getClassQualifiedName(String classFileName) {
		String substring = classFileName.substring(0, classFileName.length() - 6).replaceAll("/|\\\\", ".");
		return substring.substring(substring.indexOf(pkg));
	}


// 反射获取方法参数的注释内容
//	public void test(@RequestParam("abc") String t) {
//
//	}
//
//	public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
//		Class clazz = CourseServiceImpl.class;
//		Method test = clazz.getMethod("test", String.class);
//		System.out.println(test);
//		Annotation[][] parameterAnnotations = test.getParameterAnnotations();
//		for (Annotation[] annotations : parameterAnnotations) {
//			for (Annotation annotation : annotations) {
//				System.out.println(annotation);
//				Method m = annotation.getClass().getDeclaredMethod("value");
//				Object invoke = m.invoke(annotation);
//				System.out.println(invoke);
//			}
//		}
//	}

}
