import cn.jiangzeyin.common.JsonMessage;

/**
 * Created by jiangzeyin on 2018/6/21.
 */
public class json {
    public static void main(String[] args) {
        JsonMessage jsonMessage = new JsonMessage<>(1, "ss", 18523345054L);
        System.out.println(jsonMessage.toFormatJson());
    }
}
