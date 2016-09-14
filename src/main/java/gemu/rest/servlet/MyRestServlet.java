package gemu.rest.servlet;

import gemu.rest.annotation.MyUrl;
import gemu.rest.core.AmusingProperties;
import gemu.rest.core.MyRestParams;
import gemu.rest.core.Params;
import gemu.rest.core.ReturnType;
import gemu.rest.log.JavaLog;

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

public class MyRestServlet extends HttpServlet {

	private JavaLog jLog = new JavaLog("info");

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

	private String urlParamSymbol = "@@";

	@Override
	public void init() throws ServletException {
		// URL 参数的标志符号
		String urlParamSymbolConfig = AmusingProperties.URL_PARAM_SYMBOL;
		if (urlParamSymbolConfig == null || "".equals(urlParamSymbolConfig)) {} else { urlParamSymbol = urlParamSymbolConfig; };

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

		String reqMethod = request.getMethod();

		Map<String, String[]> reqParams = request.getParameterMap();

		Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            System.out.println(paramName + " = " + request.getParameter(paramName));
        }

		String reqPath = request.getPathInfo();
		JavaLog.info("请求URL：" + reqMethod + " " + reqPath);

		byte isMatch = 0;

		// 遍历所有带有MyUrl注释的类（带有MyUrl注释说明它是个资源）
		for (Entry<MyUrl, Class> entity : url_class_mapping.entrySet()) {
			MyUrl anno = entity.getKey();
			Class clazz = entity.getValue();
			try {
				if (reqPath != null && reqPath.equals(anno.value())) {
					isMatch = executeMatchingMethod("", clazz, response, reqParams, reqMethod);
				}
				if (reqPath != null
						&& reqPath.startsWith(anno.value() + "/")) {
					isMatch = executeMatchingMethod(
							reqPath.substring(reqPath.indexOf(anno.value())
									+ anno.value().length()), clazz, response, reqParams, reqMethod);
				}
			} catch (ReflectiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (isMatch == 0) {
			JavaLog.info("未找到匹配路径：" + reqMethod + " " + reqPath);
		}
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
	 * @param reqMethod 请求方式
	 * @return 是否匹配
	 * @throws ReflectiveOperationException
	 * @throws IOException 
	 */
	private byte executeMatchingMethod(String subUrl, Class clazz, HttpServletResponse response, Map<String, String[]> postParams, String reqMethod)
			throws ReflectiveOperationException, IOException {
		Method[] methods = clazz.getDeclaredMethods();

		byte isMatch = 0; // 是否有匹配的URL

		for (Method method : methods) {
			MyUrl myUrlAnno = method.getAnnotation(MyUrl.class);
			if (myUrlAnno != null) {
				String annoValue = myUrlAnno.value();
				Map<String, Object> params = null;

				// 判断请求方法是否匹配
				if (!reqMethod.toLowerCase().equals(myUrlAnno.method().getValue())) {
					continue;
				}

				JavaLog.info("资源调用的方法名：" + clazz.getName() + "." + method.getName());

                // 去除URL后面多余的"/"
                if (annoValue.charAt(annoValue.length() - 1) == '/') {
                    annoValue = annoValue.substring(0, annoValue.length() - 1);
                }
                if (subUrl.charAt(subUrl.length() - 1) == '/') {
                    subUrl = subUrl.substring(0, subUrl.length() - 1);
                }

				// 带参数的URL
				if (annoValue.indexOf(urlParamSymbol) != -1) {
					params = parseParamUrl(annoValue, subUrl);
                    if (params == null)
                        isMatch = 0;
                    else
                        isMatch = 1;
				}

				// 判断不带参数的URL是否匹配
				if (subUrl.equals(annoValue)) {
					isMatch = 2;
				}

				if (isMatch != 0) { // flag -> 0: 匹配url失败； 1：带参数的url； 2：不带参数的url
					// 反射获取url对应的方法
					method.setAccessible(true);
					Object obj = clazz.newInstance();
					// 响应
					if (params != null || !postParams.isEmpty()) {
						MyRestParams restParams = new MyRestParams(params, postParams);
                        int methodParamsLen = method.getParameterTypes().length;
						if (methodParamsLen != 0) {
							Object[] customParams = new Object[methodParamsLen]; // 请求的目标方法参数
							for (int i = 0; i < methodParamsLen; i++) {
								Class methodParamType = method.getParameterTypes()[i];
                                if (MyRestParams.class.getName().equals(methodParamType.getName())) {
                                    customParams[i] = restParams;
                                    continue;
                                }
                                if (HttpServletResponse.class.getName().equals(methodParamType.getName())) {
                                    customParams[i] = response;
                                }
							}
							executeResponse(myUrlAnno.type(), response, method.invoke(obj, customParams));
						} else {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj));
						}
					} else if (isMatch == 2){
						if (method.getParameterTypes().length != 0) {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj, new MyRestParams(null, null)));
						} else {
							executeResponse(myUrlAnno.type(), response, method.invoke(obj));
						}
					}
					return 3;
				}
			}
		}
		return isMatch;
	}

	/**
	 * 解析资源中带有参数（@@）的URL
	 * @param annoValue
	 * @param subUrl
	 * @return 参数键值对 参数名：参数值
	 */
	private Map<String, Object> parseParamUrl(String annoValue, String subUrl) {
		Map<String, Object> params = null; // 用到的时候在初始化。可优化代码 *

		String[] annoValues = annoValue.split("/");
		String[] subUrls = subUrl.split("/");

		if (annoValues.length != subUrls.length) {
			return null;
		}

		for (int i = 0; i < annoValues.length; i++) {
			String part1 = annoValues[i];
			String part2 = subUrls[i];

			List<Params> paramsList = getParamsFromPart(part1, part2);

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
	 * @param part1 资源URL的带参数的部分（以"/"分割开来的某个层级）如：id@@param1@@-hash!@@param2@@test
     * @param part2 客户端请求的部分URL
	 * @return Params对象集合
	 */
	private List<Params> getParamsFromPart(String part1, String part2) {
		List<Params> paramsList = null;
		Params paramObj = null;
        String[] part1s = part1.split(urlParamSymbol);

        if (checkParamUrlMatch(part2, part1s) == 0) {
            return null;
        }

		for (int i = 1; i < part1s.length; i = i + 2) { // TODO 处理资源URL书写不规范的问题，URL分隔符不是偶数
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
     * 判断带参数的URL是否匹配
     * @param matchUrl 客户端请求的部分URL 如：id@@param1@@-hash!@@param2@@test
     * @param exclusiveReqUrl 去除参数部分的URL数组
     * @return 0:不匹配；1:匹配
     */
    private byte checkParamUrlMatch(String matchUrl, String... exclusiveReqUrl) {
        for (int i = 0; i < exclusiveReqUrl.length; i = i + 2) {
            if (matchUrl.indexOf(exclusiveReqUrl[i]) == -1)
                return 0;
            else
                matchUrl = matchUrl.substring(matchUrl.indexOf(exclusiveReqUrl[i]) + exclusiveReqUrl[i].length());
        }
        return 1;
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
				response.getWriter().write(responseBody.toString());
				break;
			case JSON:
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
