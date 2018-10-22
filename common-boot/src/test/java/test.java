import cn.jiangzeyin.common.PreLoadClass;
import cn.jiangzeyin.common.PreLoadMethod;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangzeyin on 2017/10/24.
 */
@PreLoadClass
public class test {
    @PreLoadMethod(1)
    public static void load1() {
        System.out.println("load1");
    }

    @PreLoadMethod(2)
    public static void load2() {
        System.out.println("load2");
    }

    private static final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 50000000; i++) {
            concurrentHashMap.put(i, i);
        }
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        concurrentHashMap.get(5000000);
        Thread.yield();
        System.out.println(System.currentTimeMillis() - time + "  " + time + "   " + System.currentTimeMillis());
        String[] s = new String[]{"1"};
        String[] t = new String[2];
        System.arraycopy(s, 0, t, 0, s.length);
        t[1] = "2";
        System.out.println(Arrays.toString(t));
    }
}
