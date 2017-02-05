package gemu.rest.resource;

import gemu.rest.annotation.MyUrl;
import gemu.rest.core.RequestMethod;
import gemu.rest.core.ReturnType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Random;

/**
 * 模拟数据
 * Created by gemu on 1/23/17.
 */
@MyUrl("/dataModel")
public class DataModelResource {

    private String randomWord(int num) {
        String word = "";
        String orginalWords = "手机阿里山fghijklmno的叫法就是对房pqr价可拉伸的积分拉萨abcde的叫法是孙杨说教法见识到了发生了stuvwxyz地方经验对方偶看了家里空间哦以";
        char[] words = orginalWords.toCharArray();
        for (int i = 0; i < num; i++) {
            word += words[new Random().nextInt(words.length)];
        }
        return word;
    }

    @MyUrl(value = "/list", type = ReturnType.JSON, method = RequestMethod.POST)
    public String list() {
        JSONArray list = new JSONArray();
        for (int i = 1; i < 9; i++) {
            JSONObject obj = new JSONObject();
            obj.put("name", randomWord(new Random().nextInt(2) + 2));
            obj.put("gender", Math.random() > 0.5 ? "男" : "女");
            obj.put("title", randomWord(new Random().nextInt(15) + 5));
            obj.put("content", randomWord(new Random().nextInt(400) + 30));
            list.add(obj);
        }
        return list.toString();
    }

}
