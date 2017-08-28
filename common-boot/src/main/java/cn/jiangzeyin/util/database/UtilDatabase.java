package cn.jiangzeyin.util.database;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/16.
 */
public class UtilDatabase implements IUtilUpdate, IUtilSelectPage {
    private static UtilDatabase utilDatabase;
    private static IUtilUpdate iUtilUpdate;
    private static IUtilSelectPage iUtilSelectPage;

    public static void init(IUtilUpdate iUtilUpdate, IUtilSelectPage iUtilSelectPage) {
        UtilDatabase.iUtilUpdate = iUtilUpdate;
        UtilDatabase.iUtilSelectPage = iUtilSelectPage;
    }

    public static UtilDatabase getInstance() {
        if (utilDatabase == null) {
            synchronized (UtilDatabase.class) {
                if (utilDatabase == null) {
                    utilDatabase = new UtilDatabase();
                }
            }
        }
        return utilDatabase;
    }

    @Override
    public long update(Class cls, HashMap<String, Object> map, String keyColumn, Object keyValue) {
        Assert.notNull(iUtilUpdate);
        return iUtilUpdate.update(cls, map, keyColumn, keyValue);
    }

    @Override
    public <T> List<T> selectPage(Class<T> cls, long pageNo, long pageSize, String orderBy, String where) {
        Assert.notNull(iUtilSelectPage);
        return iUtilSelectPage.selectPage(cls, pageNo, pageSize, orderBy, where);
    }
}
