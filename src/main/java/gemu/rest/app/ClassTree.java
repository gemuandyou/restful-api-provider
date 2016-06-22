package gemu.rest.app;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Gemu on 2/19/2016 0019.
 */
public class ClassTree {

    private List<String> clazzs;
    private List<Map<String, String>> clazzRlts;

    public List<String> getClazz() {
        return clazzs;
    }

    public void setClazz(List<String> clazz) {
        this.clazzs = clazz;
    }

    public List<Map<String, String>> getClazzRel() {
        return clazzRlts;
    }

    public void setClazzRel() {
    }

    public ClassTree(List<String> clazzs, List<Map<String, String>> clazzRlts) {
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
