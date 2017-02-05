package gemu.rest.app;

import gemu.rest.log.JavaLog;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gemu on 2/16/2016 0016.
 */
public class GetAllClassForProject {


    private static Logger logger = Logger.getLogger(GetAllClassForProject.class);

    /**
     * 扫描的包路径
     */
    private static String pkg = "gemu";

    /**
     * 源码路径
     */
    public static String srcPath = "/Users/gemu/Development/idea_workspace/MyRest";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 项目中所有类名和方法名集合
     */
    private static List<List<Map<String, String>>> clazzsEntity = new ArrayList<List<Map<String, String>>>();

    /**
     * 所有类之间的关联关系
     */
    private static List<Map<String, String>> clazzRlts = new ArrayList<Map<String, String>>();

    /**
     * 类名和其内容的映射
     */
    private static Map<String, String> codeClazzMap = new HashMap<String, String>();


    /**
     * 获取当前项目下指定包下（pkg变量）的所有类
     * @return
     * @throws UnsupportedEncodingException
     * @throws ClassNotFoundException
     */
    //TODO 无用方法
    @Deprecated
    public List<Class> getAllClassForProject() throws UnsupportedEncodingException, ClassNotFoundException {
        List<String> clazzNames = new ArrayList<String>();
        String path = Thread.currentThread().getContextClassLoader()
                .getResource("").getPath();
        path = path.substring(1) + pkg.replace(".", File.separator);
        path = URLDecoder.decode(path, "utf-8"); // 作用举例：将路径中的 %20 形式的空格转为空格 （=。=）
        File file = new File(path);
        if (file.isDirectory()) {
            File[] fileObjs = file.listFiles();
            List<File> fileList = new ArrayList<File>();
            for (File f : fileObjs) {
                fileList.add(f);
            }
            // 广度遍历文件目录
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
        List<Class> clazzs = new ArrayList<Class>();
        for (String clazzName : clazzNames) {
            JavaLog.info(clazzName);
            Class clazz = Class.forName(clazzName);
            clazzs.add(clazz);
        }
        return clazzs;
    }

    /**
     * 获取项目中的所有Java文件
     * @return
     */
    public List<File> getAllJavaForProject() {
        List<File> files = new ArrayList<File>();
        File file = new File(srcPath);
        if (file.isDirectory()) {
            File[] fileObjs = file.listFiles();
            List<File> fileList = new ArrayList<File>();
            for (File f : fileObjs) {
                fileList.add(f);
            }
            // 广度遍历文件目录
            while (fileList != null && !fileList.isEmpty()) {
                List<File> tmp = new ArrayList<File>();
                for (File currFile : fileList) {
                    if (currFile.isDirectory()) {
                        for (File subFile : currFile.listFiles()) {
                            tmp.add(subFile);
                        }
                    } else {
                        String fileName = currFile.getAbsolutePath();
                        if (fileName.endsWith(".java")) {
                            files.add(currFile);
                        }
                    }
                }
                // 为fileList赋新值，执行下一层的遍历
                fileList = tmp;
            }
        } else {
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith(".java")) {
                files.add(file);
            }
        }
        return files;
    }

    public Method[] getMethodsForClass(Class clazz) {
//        Method[] methods = clazz.getDeclaredMethods();
        return clazz.getDeclaredMethods();
    }

    public Field[] getFieldsForClass(Class clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * 通过类文件的文件路径得到类的全类名
     *
     * @param classFileName
     *            类文件路径
     * @return 类的全类名
     */
    //TODO 无用方法
    public String getClassQualifiedName(String classFileName) {
        String substring = classFileName.substring(0,
                classFileName.length() - 6).replaceAll("/|\\\\", ".");
        return substring.substring(substring.indexOf(pkg));
    }

    /**
     * 通过方法获取其参数
     * @param method
     * @return
     */
    //TODO 无用方法
    @Deprecated
    public String getMethodParams(Method method) {
        String paramsStr = "";
        Class<?>[] types = method.getParameterTypes();
        for (Class<?> class1 : types) {
            paramsStr += class1.getSimpleName() + ", ";
        }
        if (!"".equals(paramsStr)) {
            paramsStr = paramsStr.substring(0, paramsStr.length() - 2);
        }
        return paramsStr;
    }

    // TODO DELETE
    public static void main(String[] args) {
        String string = " */";
        String reg = "^\\/\\*\\*.*\\*\\/ *";
        String reg2 = "\\*\\/";
//        Pattern p = Pattern.compile("(.*[a-zA-Z]+( +)[a-zA-Z]+( *)\\((.* [a-zA-Z]*|)\\).*\\{)|([a-zA-Z\\[\\]<>]+( +)[a-zA-Z]+( *)\\((.* [a-zA-Z]*|)\\).*;$)");
        Pattern p = Pattern.compile(reg2);
        Matcher matcher = p.matcher(string);
        boolean isMatch = matcher.find();
        System.out.println(isMatch);

        Integer i = 140000000;
        String s = Integer.toBinaryString(i);
        System.out.println(s);
    }

    /**
     * 从字符串中解析方法（单个匹配）。 //TODO 正则匹配需要完善（不把握是否能匹配正确）
     * 不能匹配事例：
     *  <ul>
     *      <li>client.setUserInteraction(new UserInteraction() {
     *  </ul>
     * @param string 要解析的字符串
     * @return
     */
    private String parseMethodFromString(String string) {
        string = string.trim();
        Pattern p = Pattern.compile("(.*[a-zA-Z]+( +)[a-zA-Z]+( *)\\((.* [a-zA-Z]*|)\\).*\\{)|(^ *[a-zA-Z]+( *((\\[|<)[a-zA-Z]*(\\]|>)){0,1})( +)[a-zA-Z]+( *)\\((.* [a-zA-Z]*|)\\).*;$)");
        Matcher matcher = p.matcher(string);
        boolean isMatch = matcher.find();
        if (isMatch) {
            String beforeMName = string.substring(0, string.indexOf("("));
            beforeMName = beforeMName.trim();
            if (beforeMName.lastIndexOf(" ") == -1)
                return null;
            beforeMName = beforeMName.substring(beforeMName.lastIndexOf(" "));
            beforeMName = beforeMName.trim();
            return beforeMName;
        }
        return null;
    }

    /**
     * 获取文件的内容和该Java类的方法
     * @param file java文件
     * @return {"javaCode": java文件内容, "methods": 方法集合}
     */
    private Map<String, Object> getHandledContentByFile(File file) {
        Map<String, Object> codeAndMethods = new HashMap<String, Object>();
        List<Map<String, String>> methods = new ArrayList<Map<String, String>>(); // {方法名：注释内容}
        StringBuffer annoSb = new StringBuffer(); // 用于存放注释内容
        boolean loadingAnno = false;

        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(file));
            String buffer = "";
            while((buffer = br.readLine()) != null) {

                // 分析注释内容
                if (buffer.trim().matches("^\\/\\*\\*")) {
                    if (annoSb.length() != 0 && !loadingAnno) {
                        annoSb = new StringBuffer();
                    }
                    loadingAnno = true;
                }
                if (buffer.trim().matches(".*\\*\\/$")) {
                    loadingAnno = false;
                    annoSb.append(buffer);
                    annoSb.append("\n");
                }
                if (loadingAnno) {
                    annoSb.append(buffer);
                    annoSb.append("\n");
                }


                // 检测是否匹配方法
                String mName = parseMethodFromString(buffer);
                if (mName != null) {
                    Map<String, String> methodAndAnno = new HashMap<String, String>();
                    methodAndAnno.put("mName", mName);
                    methodAndAnno.put("anno", annoSb.toString());
                    methods.add(methodAndAnno);

                    if (!loadingAnno) {
                        annoSb = new StringBuffer();
                    }
                }

                sb.append(buffer);
                sb.append("\n");

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println(sb);
        codeAndMethods.put("javaCode", sb.toString());
        codeAndMethods.put("methods", methods);
        return codeAndMethods;
    }

    //TODO 报废的方法（未完成）
    @Deprecated
    public static Map<Integer, StringBuffer> parseJavaCodeLayer(String javaCode) {
        Map<Integer, StringBuffer> ret = new HashMap<Integer, StringBuffer>();

        ret.put(1, new StringBuffer());
        ret.put(2, new StringBuffer());

        int layerNum = 0; // 层数

        int uselessBracketNum = 0;

        for (int i = 0; i < javaCode.length(); i++) {
            char c = javaCode.charAt(i);
            switch(c) {
                case '{' :
                    if (layerNum <= 2) {
                        layerNum ++;
                        if (layerNum == 1) {

                        }
                        if (layerNum == 2) {

                        }
                    } else {
                        uselessBracketNum ++;
                    }
                    break;
                case '}' :
                    if (uselessBracketNum > 0) {
                        uselessBracketNum --;
                    }
                    break;
            }
        }

        System.out.println(ret);
        return ret;
    }

    /**
     * 获取关联某个类的所有类
     * @param clazzName 要关联的类名
     * @param clazzsEntity 所有类名
     * @return 关联的类名集合
     */
    private static List<String> getRltClazz(String clazzName, List<List<Map<String, String>>> clazzsEntity) {
        List<String> ret = new ArrayList<String>();
        List<String> importClazzs = new ArrayList<String>();

        String clazzCode = codeClazzMap.get(clazzName); // 获取到类的代码内容

        String[] partCode = splitJavaCode(clazzCode);

        String importPart = partCode[0];
        String logicPart = partCode[1];

        if (importPart.indexOf("import ") != -1) {

            // 通过import判断引用的类(全类名)
            while (!"".equals(importPart.trim())) {
                if (importPart.indexOf("import ") == -1) break;
                importPart = importPart.substring(importPart.indexOf("import "));
                String clazzDefPart = importPart.substring(importPart.indexOf("import "), importPart.indexOf(";")); // eg. import ReturnType

                importClazzs.add(clazzDefPart.substring(7));

                importPart = importPart.substring(clazzDefPart.length() + 1);
            }

            // 通过代码内容判断引用的类（普通类名）
            for (List<Map<String, String>> entity : clazzsEntity) {
                if (logicPart.indexOf(entity.get(0).get("name").substring(entity.get(0).get("name").lastIndexOf(".") + 1)) != -1 && importClazzs.indexOf(entity.get(0).get("name")) != -1) {
                    ret.add(entity.get(0).get("name"));
                }
            }
        }

        System.out.println(ret);
        return ret;
    }

    /**
     * 将Java代码分割为两部分。1：import部分（头几行） 2：剩下的部分
     * @param clazzCode
     * @return
     */
    private static String[] splitJavaCode(String clazzCode) {
        String[] parts = new String[2];

        String part1 = "";
        String part2 = "";
        if (clazzCode.indexOf("public ") == -1) {
            if (clazzCode.indexOf("class ") != -1) {
                part1 = clazzCode.substring(0, clazzCode.indexOf("class "));
                part2 = clazzCode.substring(clazzCode.indexOf("class "));
            }
            if (clazzCode.indexOf("interface ") != -1) {
                part1 = clazzCode.substring(0, clazzCode.indexOf("interface "));
                part2 = clazzCode.substring(clazzCode.indexOf("interface "));
            }
            if (clazzCode.indexOf("enum ") != -1) {
                part1 = clazzCode.substring(0, clazzCode.indexOf("enum "));
                part2 = clazzCode.substring(clazzCode.indexOf("enum "));
            }
        } else {
            part1 = clazzCode.substring(0, clazzCode.indexOf("public "));
            part2 = clazzCode.substring(clazzCode.indexOf("public "));
        }

        parts[0] = part1;
        parts[1] = part2;
        return parts;
    }

    public static ClassTree getClassTree() throws FileNotFoundException {
        GetAllClassForProject currObj = new GetAllClassForProject();
        clazzRlts.clear();
        clazzsEntity.clear();
        // 获取项目文件
        List<File> files = currObj.getAllJavaForProject();
        for (File file : files) {
            Map<String, Object> javaCodeAndMethods = currObj.getHandledContentByFile(file);
            String javaCode = (String) javaCodeAndMethods.get("javaCode");

            if (javaCode.indexOf("package ") == -1)
                continue;
            javaCode = javaCode.substring(javaCode.indexOf("package "));

            String qualifiedName = javaCode.substring(javaCode.indexOf("package ") + 8, javaCode.indexOf(";"))
                    + "." + file.getName().replace(".java", "");

            codeClazzMap.put(qualifiedName, javaCode);
            List<Map<String, String>> methods = (List<Map<String, String>>) javaCodeAndMethods.get("methods");
            List<Map<String, String>> qualifiedNameAndMethods = new ArrayList<Map<String, String>>();

            // 添加类名和注释
            Map<String, String> qualifiedNameAndAnno = new HashMap<String, String>();
            qualifiedNameAndAnno.put("name", qualifiedName);
            qualifiedNameAndAnno.put("anno", "");
            qualifiedNameAndMethods.add(qualifiedNameAndAnno);

            // 添加方法和注释
            for (int i = 0; i < methods.size(); i++) {
                Map<String, String> methodsAndAnno = new HashMap<String, String>();
                methodsAndAnno.put("name", methods.get(i).get("mName"));
                methodsAndAnno.put("anno", methods.get(i).get("anno"));
                qualifiedNameAndMethods.add(methodsAndAnno);
            }
            clazzsEntity.add(qualifiedNameAndMethods);
        }

        for (int i = 0; i < clazzsEntity.size(); i++) {
            String clazzName = clazzsEntity.get(i).get(0).get("name");

            if (codeClazzMap.get(clazzName) != null) {

                List<String> rltClazzList = getRltClazz(clazzName, clazzsEntity);

                for (String rlt : rltClazzList) {
                    Map<String, String> rltObj = new HashMap<String, String>();
                    rltObj.put("to", clazzName);
                    rltObj.put("from", rlt);
                    clazzRlts.add(rltObj);
                }

            }
        }

        ClassTree treeData = new ClassTree(clazzsEntity, clazzRlts);
        JavaLog.info(treeData.toString());

        return treeData;
    }


}
