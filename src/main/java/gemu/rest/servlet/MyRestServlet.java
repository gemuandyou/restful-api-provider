package gemu.rest.servlet;

import gemu.rest.annotation.MyUrl;
import gemu.rest.core.MyRestParams;
import gemu.rest.core.ReturnType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * 用来描述参数（这个应该就是领域模型吧？）
 */
class Params {
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

public class MyRestServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(MyRestServlet.class);

	/**
	 * 扫描的包路径
	 */
	private static String pkg = "gemu.rest";

	/**
	 * MyUrl注解和类的映射
	 */
	private Map<MyUrl, Class> url_class_mapping = new HashMap<MyUrl, Class>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		List<String> clazzNames = new ArrayList<String>();
		String path = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		path = path.substring(1) + pkg.replace(".", File.separator);
		try {
			path = URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//** Mac 系统需要在路径前加上 /
		path = "/" + path;

		File file = new File(path);
		if (file.isDirectory()) {
			File[] fileObjs = file.listFiles();
			List<File> fileList = new ArrayList<File>();
			for (File f : fileObjs) {
				fileList.add(f);
			}
			// 广度遍历文件目录（减少内存开销，参见：http://blog.csdn.net/gemuandyou/article/details/44082001）
			while (fileList != null && !fileList.isEmpty()) {
				List<File> tmp = new ArrayList<File>();
				for (File currFile : fileList) {
					if (currFile.isDirectory()) {
						for (File subFile : currFile.listFiles()) {
							tmp.add(subFile);
						}
					} else {
						String fileName = currFile.getAbsolutePath();
						if (fileName.endsWith(".class")) {
							clazzNames.add(getClassQualifiedName(fileName));
						}
					}
				}
				// 为fileList赋新值，执行下一层的遍历
				fileList = tmp;
			}
		} else {
			clazzNames.add(file.getName());
		}
		for (String clazzName : clazzNames) {
			try {
				Class<?> clazz = Class.forName(clazzName);
				MyUrl myAnno = clazz.getAnnotation(MyUrl.class);
				if (myAnno != null) // 过滤获取MyUrl注释文件
					url_class_mapping.put(myAnno, clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("invoking service");

		String reqMethod = request.getMethod();

		Map<String, String[]> reqParams = request.getParameterMap();

		// 解析URL参数（不是rest url的参数）
		Map<String, String[]> parameterMap = request.getParameterMap();
		Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            System.out.println(paramName + " = " + request.getParameter(paramName));
        }

		String reqPath = request.getPathInfo();

		// 遍历所有带有MyUrl注释的类（说明它是个资源）
		for (Entry<MyUrl, Class> entity : url_class_mapping.entrySet()) {
			MyUrl anno = entity.getKey();
			Class clazz = entity.getValue();
//			if (reqMethod.equals(anno.method())) {// TODO BUG,还要判断方法的注释
				try {
					if (reqPath != null && reqPath.equals(anno.value())) {
						executeMatchingMethod("", clazz, response, reqParams);
					}
					if (reqPath != null
							&& reqPath.startsWith(anno.value() + "/")) {
						executeMatchingMethod(
								reqPath.substring(reqPath.indexOf(anno.value())
										+ anno.value().length()), clazz, response, reqParams);
					}
				} catch (ReflectiveOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//			}
		}

		 System.out.println(reqMethod);
	}

	/**
	 * 通过类文件的文件路径得到类的全类名
	 * 
	 * @param classFileName
	 *            类文件路径
	 * @return 类的全类名
	 */
	private String getClassQualifiedName(String classFileName) {
		String substring = classFileName.substring(0,
				classFileName.length() - 6).replaceAll("/|\\\\", ".");
		return substring.substring(substring.indexOf(pkg));
	}

	/**
	 * 解析url获取匹配的方法
	 * @param subUrl
	 * @param clazz
	 * @param response
	 * @param postParams
	 * @return
	 * @throws ReflectiveOperationException
	 * @throws IOException 
	 */
	private Object executeMatchingMethod(String subUrl, Class clazz, HttpServletResponse response, Map<String, String[]> postParams)
			throws ReflectiveOperationException, IOException {
		System.out.println("解析的URL：" + subUrl); //TODO log
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			MyUrl myUrlAnno = method.getAnnotation(MyUrl.class);
			System.out.println("方法名：" + method.getName()); //TODO log
			if (myUrlAnno != null) {
				String annoValue = myUrlAnno.value();
				Map<String, Object> params = null;
				byte flag = 0;
				if (annoValue.indexOf("@") != -1) {
					params = parseParamUrl(annoValue, subUrl);
					flag = 1;
				}
				if (subUrl.equals(annoValue)) {
					flag = 2;
				}
				if (flag != 0) { // flag -> 0: 匹配url失败； 1：带参数的url； 2：不带参数的url
					// 反射获取url对应的方法
					method.setAccessible(true);
					Object obj = clazz.newInstance();
					// 响应
					if (params != null || !postParams.isEmpty()) {
						MyRestParams restParams = new MyRestParams(params, postParams);
						if (method.getParameterTypes().length != 0) {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj, restParams));
						} else {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj));
						}
					} else if (flag == 2){
						if (method.getParameterTypes().length != 0) {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj, new MyRestParams(null, null))); //TODO 需要调整
						} else {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj));
						}
					}
				}
//				System.out.println(annoValue); //TODO log
//				String annoMethod = myUrlAnno.method();
//				System.out.println(annoMethod); //TODO log
			}
		}
		return null;
	}

	/**
	 * 解析资源中带有参数（@）的URL
	 * @param annoValue
	 * @param subUrl
	 * @return 参数键值对 参数名：参数值
	 */
	private Map<String, Object> parseParamUrl(String annoValue, String subUrl) {
		Map<String, Object> params = null; // 用到的时候在初始化。可优化代码 *

		if (annoValue.charAt(annoValue.length() - 1) == '/') {
			annoValue.substring(0, annoValue.length() - 1);
		}
		if (subUrl.charAt(subUrl.length() - 1) == '/') {
			subUrl.substring(0, subUrl.length() - 1);
		}

		String[] annoValues = annoValue.split("/");
		String[] subUrls = subUrl.split("/");

		if (annoValues.length > subUrls.length) {
			return null;
		}

		for (int i = 0; i < annoValues.length; i++) {
			String part1 = annoValues[i];
			String part2 = subUrls[i];

			List<Params> paramsList = getParamsFromPart(part1);

			if (paramsList != null) {
				int listLen = paramsList.size();
				for(Params paramObj : paramsList) {
					if (part2.indexOf(paramObj.getPrefix()) != 0 || (listLen == 1 && !part2.endsWith(paramObj.getSuffix()))) {
						return null;
					}
					if (params == null) {
						params = new HashMap<String, Object>();
					}
					String paramVal = part2.substring(paramObj.getPrefix().length(),
							("".equals(paramObj.getSuffix())) ? part2.length() : part2.indexOf(paramObj.getSuffix()));
					params.put(paramObj.getParam(), paramVal);
					part2 = part2.substring(paramObj.getPrefix().length() + paramVal.length());
					listLen --;
				}
			} else if (!part1.equals(part2)) {
				return null;
			}
		}

		return params;
	}

	/**
	 * 解析URL片段，获取参数对象
	 * @param part1 资源URL的带参数的部分（以"/"分割开来的某个层级）如：id@param1@-hash!@param2@test
	 * @return Params对象集合
	 */
	private List<Params> getParamsFromPart(String part1) {
		List<Params> paramsList = null;
		Params paramObj = null;
		String[] part1s = part1.split("@");
		for (int i = 1; i < part1s.length; i = i + 2) { // TODO 处理资源URL书写不规范的问题
			if (paramsList == null)
				paramsList = new ArrayList<Params>();
			paramObj = new Params();
			paramObj.setPrefix(part1s[i - 1]);
			paramObj.setParam(part1s[i]);
			paramObj.setSuffix((i + 2) > part1s.length ? "" : part1s[(i + 1)]);
			paramsList.add(paramObj);
		}
		return paramsList;
	}

	/**
	 * 根据不同返回类型，做出不同响应方式
	 * @param rt 返回值类型
	 * @param response servlet响应对象
	 * @param responseBody 响应体，响应内容
	 * @throws IOException 
	 */
	private void executeResponse(ReturnType rt, HttpServletResponse response, Object responseBody) throws IOException {
		
		switch(rt) {
			case STRING:
				System.out.println(responseBody.toString());
				response.getWriter().write(responseBody.toString());
				break;
			case JSON:
				System.out.println(responseBody.toString());
				response.setContentType(rt.getValue());
				response.getWriter().write(responseBody.toString());
				break;
			case TEXT:
				byte[] buff = new byte[1024];
				if (responseBody instanceof File) {
					File file = (File) responseBody;
					FileInputStream in = null;
					try {
						in = new FileInputStream(file);
						response.setContentType(rt.getValue());
						response.setHeader("Location", file.getName());
						response.setHeader("Content-Disposition",
								"attachment; filename=" + file.getName());
						int len = 0;
						while ((len = in.read(buff)) != -1) {
							response.getOutputStream().write(buff, 0, len);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					throw new RuntimeException("返回值类型ReturnType与实际不符, 应返回File格式对象");
				}
				break;
			default:
				break;
		}
		
	}
}
