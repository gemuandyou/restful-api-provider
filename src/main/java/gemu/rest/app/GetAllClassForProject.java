package gemu.rest.app;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static String srcPath = "/Users/gemu/Development/idea_workspace/MyRest";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 项目中所有类名集合
     */
    private static List<String> clazzsEntity = new ArrayList<String>();

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
            System.out.println(clazzName);
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

    /**
     * 获取文件的内容
     * @param file java文件
     * @return
     */
    private String getHandledContentByFile(File file) {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(file));
            String buffer = "";
            while((buffer = br.readLine()) != null) {
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
        return sb.toString();
    }

    //TODO 报废的方法（未完成）
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
    private static List<String> getRltClazz(String clazzName, List<String> clazzsEntity) {
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
            for (String entity : clazzsEntity) {
                if (logicPart.indexOf(entity.substring(entity.lastIndexOf(".") + 1)) != -1 && importClazzs.indexOf(entity) != -1) {
                    ret.add(entity);
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
            String javaCode = currObj.getHandledContentByFile(file);
            System.setOut(new PrintStream(new File("C:\\Users\\Gemu\\Documents\\javaConsole.txt")));

            if (javaCode.indexOf("package ") == -1)
                continue;
            javaCode = javaCode.substring(javaCode.indexOf("package "));

            String qualifiedName = javaCode.substring(javaCode.indexOf("package ") + 8, javaCode.indexOf(";"))
                    + "." + file.getName().replace(".java", "");

            codeClazzMap.put(qualifiedName, javaCode);
            clazzsEntity.add(qualifiedName);
        }

        for (int i = 0; i < clazzsEntity.size(); i++) {
            String clazzName = clazzsEntity.get(i);

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
        System.out.println(treeData);

        return treeData;
    }

    public static void main(String[] args) throws Exception {
        GetAllClassForProject currObj = new GetAllClassForProject();
        // 获取项目文件
        List<File> files = currObj.getAllJavaForProject();
        for (File file : files) {
            String javaCode = currObj.getHandledContentByFile(file);
            System.setOut(new PrintStream(new File("/Users/gemu/Development/idea_workspace/MyRest2/src/main/resources/javaConsole.txt")));

            if (javaCode.indexOf("package ") == -1)
                continue;
            javaCode = javaCode.substring(javaCode.indexOf("package "));

            String qualifiedName = javaCode.substring(javaCode.indexOf("package ") + 8, javaCode.indexOf(";"))
                    + "." + file.getName().replace(".java", "");

            codeClazzMap.put(qualifiedName, javaCode);
            clazzsEntity.add(qualifiedName);
        }

        for (int i = 0; i < clazzsEntity.size(); i++) {
            String clazzName = clazzsEntity.get(i);

            if (codeClazzMap.get(clazzName) != null) {

                List<String> rltClazzList = getRltClazz(clazzName, clazzsEntity);

                for (String rlt : rltClazzList) {
                    Map<String, String> rltObj = new HashMap<String, String>();
                    rltObj.put("from", clazzName);
                    rltObj.put("to", rlt);
                    clazzRlts.add(rltObj);
                }

            }
        }

        ClassTree treeData = new ClassTree(clazzsEntity, clazzRlts);
        System.out.println(treeData);

    }

}
