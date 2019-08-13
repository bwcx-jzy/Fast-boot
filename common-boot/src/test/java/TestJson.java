import cn.jiangzeyin.common.JsonMessage;
import com.alibaba.fastjson.JSONObject;

/**
 * @author bwcx_jzy
 * @date 2019/8/13
 */
public class TestJson {

    public static class ts {
        private String s;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "sss");

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("s", "sssssss11111111");
        jsonObject.put("data", jsonObject1);

        JsonMessage<ts> tsJsonMessage = JsonMessage.toJsonMessage(jsonObject.toString(), ts.class);

        System.out.println(tsJsonMessage.getData().getS());
    }
}
