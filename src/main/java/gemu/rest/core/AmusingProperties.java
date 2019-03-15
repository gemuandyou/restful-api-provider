package gemu.rest.core;

/**
 * Created by Gemu on 2016/8/26.
 */
public class AmusingProperties {

    /**
     * URL传递参数的标志符号
     */
    public final static String URL_PARAM_SYMBOL = "@@";

    /**
     * 日志文件位置
     */
    public final static String INFO_LOG_CATALOG = "logInfo.jlog"; // 信息日志
    public final static String DEBUG_LOG_CATALOG = "logDebug.jlog"; // 调试日志
    public final static String ISSUE_LOG_CATALOG = "logIssue.jlog"; // 问题日志

    /**
     * 解析项目类引用关系应用中关系数据的存放文件位置
     */
    public final static String TREE_DATE_FILE = AmusingProperties.class.getClassLoader().getResource("").getPath() + "tree.json";

}
