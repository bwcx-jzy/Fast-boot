import java.io.IOException;

/**
 * Created by jiangzeyin on 2018/12/27.
 */
public class TestE {
    public static void main(String[] args) {
        Exception exception = new Exception();
        exception.addSuppressed(new RuntimeException());
        System.out.println(isFromOrSuppressedThrowable(exception, IOException.class));
        RuntimeException runtimeException = convertFromOrSuppressedThrowable(exception, RuntimeException.class);
        System.out.println(runtimeException);
    }

    /**
     * 判断指定异常是否来自或者包含指定异常
     *
     * @param throwable      异常
     * @param exceptionClass 定义的引起异常的类
     * @return true 来自或者包含
     */
    public static boolean isFromOrSuppressedThrowable(Throwable throwable, Class<? extends Throwable> exceptionClass) {
        return convertFromOrSuppressedThrowable(throwable, exceptionClass) != null;
    }

    /**
     * 转化指定异常为来自或者包含指定异常
     *
     * @param throwable      异常
     * @param exceptionClass 定义的引起异常的类
     * @return 结果为null 不是来自或者包含
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable throwable, Class<T> exceptionClass) {
        if (throwable == null || exceptionClass == null) {
            return null;
        }
        if (exceptionClass.isAssignableFrom(throwable.getClass())) {
            return (T) throwable;
        }
        Throwable[] throwables = throwable.getSuppressed();
        if (throwables != null && throwables.length >= 1) {
            for (Throwable throwable1 : throwables) {
                if (exceptionClass.isAssignableFrom(throwable1.getClass())) {
                    return (T) throwable1;
                }
            }
        }
        return null;
    }
}
