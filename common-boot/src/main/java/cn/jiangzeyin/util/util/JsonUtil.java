package cn.jiangzeyin.util.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * json工具类
 *
 * @author jiangzeyin
 */
public final class JsonUtil {

    public static JSONArray sortJsonArrayByDate(JSONArray mJSONArray, String dateName) {
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObj = null;
        for (int i = 0; i < mJSONArray.size(); i++) {
            jsonObj = mJSONArray.getJSONObject(i);
            list.add(jsonObj);
        }
        // 排序操作
        JsonComparator pComparator = new JsonComparator(dateName);
        Collections.sort(list, pComparator);
        // 把数据放回去
        JSONArray mJSONArray_ = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonObj = list.get(i);
            mJSONArray_.add(jsonObj);
        }
        return mJSONArray_;
    }

    /**
     * 对 json 进行排序
     *
     * @param mJSONArray array
     * @param dateName   date
     * @param way        way
     * @return array
     * @author jiangzeyin
     */
    public static JSONArray sortJsonArrayByDate(JSONArray mJSONArray, String dateName, String way) {
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObj = null;
        for (int i = 0; i < mJSONArray.size(); i++) {
            jsonObj = mJSONArray.getJSONObject(i);
            list.add(jsonObj);
        }
        // 排序操作
        JsonComparator pComparator = new JsonComparator(dateName, way);
        Collections.sort(list, pComparator);
        // 把数据放回去
        JSONArray mJSONArray_ = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonObj = list.get(i);
            mJSONArray_.add(jsonObj);
        }
        return mJSONArray_;
    }

    /**
     * 删除 json 指定值
     *
     * @param array  a
     * @param remove r
     * @return array
     * @author jiangzeyin
     */
    public static JSONArray filterJson(JSONArray array, String remove) {
        JSONArray array_ = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            obj = filterJson(obj, remove);
            array_.add(obj);
        }
        return array_;
    }

    /**
     * @param obj    obj
     * @param remove r
     * @return json
     * @author jiangzeyin
     */
    public static JSONObject filterJson(JSONObject obj, String remove) {
        String[] rs = StringUtil.StringToArray(remove, ",");
        for (String string : rs) {
            obj.remove(string);
        }
        return obj;
    }

    /**
     * 格式化
     *
     * @param jsonStr json
     * @return r
     * @author lizhgb
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }
        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb     sb
     * @param indent i
     * @author lizhgb
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }

    public static JSONObject toJSONObject(JSONArray jsonArray, String key) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject temp = jsonArray.getJSONObject(i);
            String[] keyValues = StringUtil.StringToArray(key);
            if (keyValues != null) {
                for (String item : keyValues) {
                    String[] k = item.split(":");
                    String[] keys = k[0].split("_");
                    String keyName = k[0];
                    if (keys.length == 2) {
                        keyName = keys[0];
                    }
                    String jsonKeyName = temp.getString(keyName);
                    if (keys.length == 2) {
                        jsonKeyName += "_" + keys[1];
                    }
                    jsonObject.put(jsonKeyName, temp.getString(k[1]));
                }
            }
        }
        return jsonObject;
    }


    /**
     * jsonn 排序 类
     *
     * @author jiangzeyin
     */
    public static class JsonComparator implements Comparator<JSONObject> {

        String dateName = "";
        String way = "desc";

        JsonComparator(String dateName) {
            this.dateName = dateName;
        }

        JsonComparator(String dateName, String way) {
            this.dateName = dateName;
        }

        @Override
        public int compare(JSONObject json1, JSONObject json2) {
            String date1 = json1.getString(dateName);
            String date2 = json2.getString(dateName);
            System.out.println(date1 + "  " + date2);
            if (date1.compareTo(date2) < 0) {
                return "desc".equalsIgnoreCase(way) ? -1 : 1;
            } else if (date1.compareTo(date2) > 0) {
                return "desc".equalsIgnoreCase(way) ? 1 : -1;
            }
            return 0;
        }
    }
}
