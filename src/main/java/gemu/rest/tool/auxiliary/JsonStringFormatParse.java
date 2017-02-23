package gemu.rest.tool.auxiliary;

/**
 * Created by Gemu on 2016/9/1.
 */
public class JsonStringFormatParse {

    /**
     * 解析无格式的JSON字符串
     * @param unformatJsonString 带有格式的JSON字符串
     * @return
     */
    public static String formatParse(String unformatJsonString) {
        char[] stack = new char[1024]; // 存放括号，如 "{","}","[","]"
        int top = -1;

        StringBuffer sb = new StringBuffer();
        char[] charArray = unformatJsonString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if ('{' == c || '[' == c) {
                stack[++top] = c; // 将括号添加到数组中，这个可以简单理解为栈的入栈
                sb.append(charArray[i] + "\n");
                for (int j = 0; j <= top; j++) {
                    sb.append("\t");
                }
                continue;
            }
            if ((i + 1) <= (charArray.length - 1)) {
                char d = charArray[i+1];
                if ('}' == d || ']' == d) {
                    top--; // 将数组的最后一个有效内容位置下标减 1，可以简单的理解为将栈顶数据弹出
                    sb.append(charArray[i] + "\n");
                    for (int j = 0; j <= top; j++) {
                        sb.append("\t");
                    }
                    continue;
                }
            }
            if (',' == c) {
                sb.append(charArray[i] + "\n");
                for (int j = 0; j <= top; j++) {
                    sb.append("\t");
                }
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 解析无格式的JSON字符串（使用switch）
     * ref: https://my.oschina.net/jasonli0102/blog/517052?p={{page}} 看到了这个，感觉代码更易读些，拿来借鉴
     * @param unformatJsonString 带有格式的JSON字符串
     * @return
     */
    public static String formatParseUseSwitch(String unformatJsonString) {
        char[] stack = new char[1024]; // 存放括号，如 "{","}","[","]"
        int top = -1;

        StringBuffer sb = new StringBuffer();
        char[] charArray = unformatJsonString.toCharArray();
        int indentCount = 0;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (c) {
                case '{':
                case '[':
                    indentCount ++;
                    sb.append(c).append('\n');
                    for (int j = 0; j < indentCount; j++) {
                        sb.append('\t');
                    }
                    break;
                case ']':
                case '}':
                    indentCount --;
                    sb.append('\n');
                    for (int j = 0; j < indentCount; j++) {
                        sb.append('\t');
                    }
                    sb.append(c);
                    break;
                case ',':
                    sb.append(c).append('\n');
                    for (int j = 0; j < indentCount; j++) {
                        sb.append('\t');
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

}
