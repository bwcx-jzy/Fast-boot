package cn.jiangzeyin.util.database;

import java.util.List;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/16.
 */
public interface IUtilSelectPage {


    <T> List<T> selectPage(Class<T> cls, long pageNo, long pageSize, String orderBy, String where);
}
