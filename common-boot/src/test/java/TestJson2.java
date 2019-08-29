import com.github.houbb.junitperf.core.annotation.JunitPerfConfig;
import com.github.houbb.junitperf.core.rule.JunitPerfRule;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

/**
 * @author bwcx_jzy
 * @date 2019/8/26
 */
public class TestJson2 {
    @Rule
    public JunitPerfRule junitPerfRule = new JunitPerfRule();

    /**
     * 单一线程，执行 1000ms，默认以 html 输出测试结果
     *
     * @throws InterruptedException if any
     */
    @Test
    @JunitPerfConfig(duration = 1000)
    public void helloWorldTest() throws InterruptedException {
        //This is what you want to test.
        System.out.println("hello world");
        Thread.sleep(20);
    }
}
