package gemu.rest.app;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 项目类关系树
 * Created by Gemu on 2/19/2016 0019.
 */
public class ClassTree {

    private List<List<Map<String, String>>> clazzs;
    private List<Map<String, String>> clazzRlts;

    public List<Map<String, String>> getClazzRlts() {
        return clazzRlts;
    }

    public void setClazzRlts(List<Map<String, String>> clazzRlts) {
        this.clazzRlts = clazzRlts;
    }

    public List<List<Map<String, String>>> getClazzs() {
        return clazzs;
    }

    public void setClazzs(List<List<Map<String, String>>> clazzs) {
        this.clazzs = clazzs;
    }

    public ClassTree(List<List<Map<String, String>>> clazzs, List<Map<String, String>> clazzRlts) {
        this.clazzs = clazzs;
        this.clazzRlts = clazzRlts;
    }

    @Override
    public String toString() {

        JSONArray clazzs = JSONArray.fromObject(this.clazzs);
        JSONArray clazzRlts = JSONArray.fromObject(this.clazzRlts);
        JSONObject ret = new JSONObject();
        ret.put("nodes", clazzs);
        ret.put("rlts", clazzRlts);
        return ret.toString();
    }
}
