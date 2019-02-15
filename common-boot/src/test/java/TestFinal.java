import cn.hutool.core.lang.Validator;

/**
 * Created by jiangzeyin on 2019/2/15.
 */
public class TestFinal {
    public static void main(String[] args) {
        String test = "123";
        test1(test);
//        test1(test);
        test2(test);
        String test2 = "1234";
        test2(test2);
        test3(test2);
    }

    private static void test1(CharSequence str) {
        System.out.println("test1:" + str);
    }

    private static void test2(final String str) {
//        System.out.println("test2:" + str);
//        test1(str);
        System.out.println(Validator.isEmail(str));
        test3(str);
        test3(str);
    }

    private static void test3(final String str) {
//        System.out.println("test2:" + str);
//        test1(str);
        System.out.println(Validator.isEmail(str));
//        test2(str);
    }
}
