package gemu.rest.resource;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import gemu.rest.annotation.MyUrl;
import gemu.rest.core.MyRestParams;
import gemu.rest.core.ReturnType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by gemu on 16/4/4.
 */
@MyUrl(value = "/itvop", method="POST")
public class ITVOPTestResource {

    @MyUrl(value="/mm/getApplyMonitors", type = ReturnType.JSON)
    private String getApplyMonitors() {
        System.out.println("invoke get apply monitors");
        return "[{a: 1},{b: 2}]";
    }

    @MyUrl(value="/getNav", type = ReturnType.JSON)
    private String getNavMenu() {
        System.out.println("invoke get nav menu");
        return "[{a: 1},{b: 2}]";
    }

    @MyUrl(value="/mm/querySeveritys", type = ReturnType.JSON)
    private String getSeverity() {
        JSONArray ret = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("severity", "0");
        obj.put("name", "严重");
        obj.put("color", "red");
        ret.add(obj);
        obj.clear();

        obj.put("severity", "1");
        obj.put("name", "蛀牙");
        obj.put("color", "orange");
        ret.add(obj);
        obj.clear();
        obj.put("severity", "2");
        obj.put("name", "次要");
        obj.put("color", "#E4E446");
        ret.add(obj);
        obj.clear();
        obj.put("severity", "3");
        obj.put("name", "警告");
        obj.put("color", "purple");
        ret.add(obj);
        obj.clear();
        obj.put("severity", "4");
        obj.put("name", "提示");
        obj.put("color", "green");
        ret.add(obj);
        return ret.toString();
    }

    @MyUrl(value="/mm/queryEventTabs", type = ReturnType.JSON)
    private String queryEventTabs() {
        Map<Integer, String> colorMap = new HashMap();
        colorMap.put(0, "red");
        colorMap.put(1, "orange");
        colorMap.put(2, "#E4E446");
        colorMap.put(3, "purple");
        colorMap.put(4, "green");
        JSONArray ret = new JSONArray();
//        {
//            eventNum : int //告警数
//            severity : MonSysSeverity, //告警级别
//                    {
//                            modifyTime : Long, //修改时间[MODIFY_TIME] yyyyMMddHHmmss
//                severity : Integer, //级别[SEVERITY] 代码维一标识
//                chineseName : String, //中文名称[CHINESE_NAME] 1=NUMBER 2=VARCHAR
//                id : Long, //ID[ID]
//                englishName : String, //英文名称[ENGLISH_NAME]
//                linecolor : String, //线的颜色[LINECOLOR]
//                createTime : Long, //创建时间[CREATE_TIME] yyyyMMddHHmmss
//                color : String //颜色[COLOR]
//            }
//        }
        for (int i = 0; i <= 4; i++) {
            JSONObject obj = new JSONObject();
            obj.put("eventNum", (int)(Math.random() * 100));
            JSONObject severityObj = new JSONObject();
            severityObj.put("modifyTime", "20160417172312");
            severityObj.put("severity", i);
            severityObj.put("chineseName", "级别" + i);
            severityObj.put("id", i + 1000);
            severityObj.put("englishName", "severity" + i);
            severityObj.put("linecolor", colorMap.get(i));
            severityObj.put("createTime", "20160417162312");
            severityObj.put("color", colorMap.get(i));
            obj.put("severity", severityObj);
            ret.add(obj);
        }
        return ret.toString();
    }

    @MyUrl(value="/mm/getAlarmTabsTitle", type = ReturnType.JSON)
    private String getAlarmTabsTitle() {
        JSONArray titles = new JSONArray();
        titles.add("告警来源");
        titles.add("CI");
        titles.add("KPI");
        titles.add("实例");
        titles.add("场景名称");
        titles.add("事件标识");
        titles.add("事件级别");
        titles.add("当前状态");
        titles.add("事件标题");
        titles.add("事件详细信息");
        titles.add("首次发生时间");
        titles.add("最后发生时间");
        titles.add("重复次数");
        return titles.toString();
    }

    @MyUrl(value="/mm/queryEventPage", type = ReturnType.JSON)
    private String getAlarmTabs() {
        JSONObject retObj = new JSONObject();
        JSONArray ret = new JSONArray();
        String[] alarmSource = new String[]{"场景级预警","第三方告警","全局级预警"};
        for (int i = 0; i < 10; i++) {
            JSONArray obj = new JSONArray();
            obj.add(alarmSource[(int)Math.floor(Math.random() * 3)]);
            obj.add("server_12.13.14." + i);
            obj.add(new String[]{"disk_usage", "cup_usage", "IQ_usage"}[(int)Math.floor(Math.random() * 3)] + i);
            obj.add("instance" + i);
            obj.add("SCENE场景" + i);
            obj.add("***");
            JSONArray serverityArr = JSONArray.fromObject(new ITVOPTestResource().getSeverity());
            String randomServerity = "" + (int)Math.floor(Math.random() * 5);
            for (int j = 0; j < serverityArr.size(); j++) {
                JSONObject serverity = serverityArr.getJSONObject(j);
                if (serverity.getString("severity").equals(randomServerity)) {
                    obj.add(serverity);
                    break;
                }
            }
            obj.add(new String[]{"OPEN", "CLOSED"}[(int)Math.floor(Math.random() * 2)] + i);
            obj.add("******");
            obj.add("*********");
            obj.add(new Date().getTime() - 100000);
            obj.add(new Date().getTime() - 90000);
            obj.add("***");
            ret.add(obj);
        }
        retObj.put("data", ret);
        retObj.put("totalRows", 98);
        return retObj.toString();
    }

    @MyUrl(value="/mm/getCiRelatedScene", type = ReturnType.JSON)
    private String getCiRelatedScene() {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < 10; i++) {
            JSONObject obj = new JSONObject();
            obj.put("场景名称", "scene" + i);
            obj.put("场景创建人", "author" + i);
            obj.put("场景创建日期", new Date().getTime() - 100000);
            obj.put("场景描述", "scene summary" + i);
            arr.add(obj);
        }
        return arr.toString();
    }

    @MyUrl(value="/mm/queryAllScenesPage", type= ReturnType.JSON, method = "POST")
    private String getScene(MyRestParams paramsObj) {
        System.out.println(paramsObj);
        JSONObject ret = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < 23; i++) {
            JSONObject obj = new JSONObject();
            obj.put("attentionKpiNum", (int)Math.floor(Math.random() * 100));
            obj.put("ciKpiNum", (int)Math.floor(Math.random() * 100));
            obj.put("eventNum", (int)Math.floor(Math.random() * 100));
            obj.put("severity", JSONArray.fromObject(getSeverity()).get((int)Math.floor(Math.random() * 5)));
            if (i % 2 == 0) {
                obj.put("isSubscription", 1);
                obj.put("isPublish", 0);
                obj.put("isOwner", 0);
            } else {
                obj.put("isSubscription", 0);
                obj.put("isPublish", 1);
                obj.put("isOwner", 1);
            }
            obj.put("sceneName", "scene" + i);
            obj.put("subscriptionNum", (int)Math.floor(Math.random() * 100));
            obj.put("imgPath", "");
            obj.put("sceneId", i);
            obj.put("isWrite", 1);
            arr.add(obj);
        }
        ret.put("data", arr);
        ret.put("totalRows", 98);
        ret.put("message", "获取shibai");
        ret.put("success", false);
        return ret.toString();
    }

    @MyUrl(value="/mm/queryOwnerScenesList", type= ReturnType.JSON, method = "POST")
    private String getOwnScene(MyRestParams paramsObj) {
        System.out.println(paramsObj);
        JSONObject ret = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < 23; i++) {
            JSONObject obj = new JSONObject();
            obj.put("attentionKpiNum", (int)Math.floor(Math.random() * 100));
            obj.put("ciKpiNum", (int)Math.floor(Math.random() * 100));
            obj.put("eventNum", (int)Math.floor(Math.random() * 100));
            obj.put("severity", JSONArray.fromObject(getSeverity()).get((int)Math.floor(Math.random() * 5)));
            if (i % 2 == 0) {
                obj.put("isSubscription", 1);
                obj.put("isPublish", 0);
                obj.put("isOwner", 0);
            } else {
                obj.put("isSubscription", 0);
                obj.put("isPublish", 1);
                obj.put("isOwner", 1);
            }
            obj.put("sceneName", "scene" + i);
            obj.put("subscriptionNum", (int)Math.floor(Math.random() * 100));
            obj.put("imgPath", "");
            obj.put("sceneId", i);
            arr.add(obj);
        }
        ret.put("data", arr);
        ret.put("totalRows", 98);
        ret.put("message", "获取shibai");
        ret.put("success", false);
        return ret.toString();
    }

    @MyUrl(value="/mm/querySubscriberScenesList", type= ReturnType.JSON, method = "POST")
    private String getSubscriberScene(MyRestParams paramsObj) {
        System.out.println(paramsObj);
        JSONObject ret = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < 23; i++) {
            JSONObject obj = new JSONObject();
            obj.put("attentionKpiNum", (int)Math.floor(Math.random() * 100));
            obj.put("ciKpiNum", (int)Math.floor(Math.random() * 100));
            obj.put("eventNum", (int)Math.floor(Math.random() * 100));
            obj.put("severity", JSONArray.fromObject(getSeverity()).get((int)Math.floor(Math.random() * 5)));
            if (i % 2 == 0) {
                obj.put("isSubscription", 1);
                obj.put("isPublish", 0);
                obj.put("isOwner", 0);
            } else {
                obj.put("isSubscription", 0);
                obj.put("isPublish", 1);
                obj.put("isOwner", 1);
            }
            obj.put("sceneName", "scene" + i);
            obj.put("subscriptionNum", (int)Math.floor(Math.random() * 100));
            obj.put("imgPath", "");
            obj.put("sceneId", i);
            arr.add(obj);
        }
        ret.put("data", arr);
        ret.put("totalRows", 98);
        ret.put("message", "获取shibai");
        ret.put("success", false);
        return ret.toString();
    }

}
