import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Created by jiangzeyin on 2018/8/21.
 */
public class utf8 {
    public static void main(String[] args) throws UnsupportedEncodingException {

        String test = "测试";

        test = "{\n" +
                "\t\"fahongbao\":{\n" +
                "\t\t\"activeTag\":5,\n" +
                "\t\t\"qrCode\":{\n" +
                "\t\t\t\"x\":238,\n" +
                "\t\t\t\"width\":0.4,\n" +
                "\t\t\t\"y\":260,\n" +
                "\t\t\t\"height\":0.0\n" +
                "\t\t},\n" +
                "\t\t\"share\":[],\n" +
                "\t\t\"bgImage\":\"http://images.ztoutiao.cn/1DDE5C27052D0DA4D023C6049E83736B\",\n" +
                "\t\t\"title\":\"给大家发个红包，祝大家天天好心情，扫码就能领奥????\\t\"\n" +
                "\t},\n" +
                "\t\"inviteCode\":{\n" +
                "\t\t\"inviteCodeUrl\":\"http://a.app.qq.com/o/simple.jsp?pkgname=cn.ztoutiao\",\n" +
                "\t\t\"inviteCodeTitle\":\"上热闻APP 看实时资讯、搞笑视频、热门段子就有丰厚奖励，空闲时间看看就赚钱，快来下载体验吧！下载安装登录后输入这个邀请码#code#更多奖励送给您。????????\"\n" +
                "\t},\n" +
                "\t\"inviteShare\":{\n" +
                "\t\t\"qqFriend\":{\n" +
                "\t\t\t\"img\":\"http://adimg1.yokead.com/81ECA62439B8F99EEE280B0AC880337C\",\n" +
                "\t\t\t\"activeTag\":2,\n" +
                "\t\t\t\"title\":\"发福利了！拆红包，1-88元现金大红包，先到先得，看新闻就能领钱，可立即提现！\",\n" +
                "\t\t\t\"url\":\"http://ssss.aixcun.com/to@.html\"\n" +
                "\t\t},\n" +
                "\t\t\"weibo\":{\n" +
                "\t\t\t\"img\":\"http://adimg1.yokead.com/81ECA62439B8F99EEE280B0AC880337C\",\n" +
                "\t\t\t\"activeTag\":2,\n" +
                "\t\t\t\"title\":\"发福利了！拆红包，1-88元现金大红包，先到先得，看新闻就能领钱，可立即提现！\",\n" +
                "\t\t\t\"url\":\"http://ssss.aixcun.com/to@.html\"\n" +
                "\t\t},\n" +
                "\t\t\"pyq\":{\n" +
                "\t\t\t\"img\":\"http://adimg1.yokead.com/81ECA62439B8F99EEE280B0AC880337C\",\n" +
                "\t\t\t\"activeTag\":2,\n" +
                "\t\t\t\"title\":\"发福利了！拆红包，1-88元现金大红包，先到先得，看新闻就能领钱，可立即提现！\\n\\n\\n\",\n" +
                "\t\t\t\"url\":\"http://ssss.aixcun.com/to@.html\"\n" +
                "\t\t},\n" +
                "\t\t\"qzone\":{\n" +
                "\t\t\t\"img\":\"http://adimg1.yokead.com/81ECA62439B8F99EEE280B0AC880337C\",\n" +
                "\t\t\t\"activeTag\":2,\n" +
                "\t\t\t\"title\":\"发福利了！拆红包，1-88元现金大红包，先到先得，看新闻就能领钱，可立即提现！\",\n" +
                "\t\t\t\"url\":\"http://ssss.aixcun.com/to@.html\"\n" +
                "\t\t},\n" +
                "\t\t\"wxFriend\":{\n" +
                "\t\t\t\"img\":\"http://adimg1.yokead.com/81ECA62439B8F99EEE280B0AC880337C\",\n" +
                "\t\t\t\"activeTag\":2,\n" +
                "\t\t\t\"title\":\"发福利了！拆红包，1-88元现金大红包，先到先得，看新闻就能领钱，可立即提现！\",\n" +
                "\t\t\t\"url\":\"http://ssss.aixcun.com/to@.html\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"mdm\":{\n" +
                "\t\t\"qqFriend\":{\n" +
                "\t\t\t\"image\":\"http://adimg1.yokead.com/0F16E24110144BB8C1C1DAF6D4A08EF5\",\n" +
                "\t\t\t\"qrCode\":{\n" +
                "\t\t\t\t\"x\":\"320\",\n" +
                "\t\t\t\t\"width\":\"0.4\",\n" +
                "\t\t\t\t\"y\":\"970\",\n" +
                "\t\t\t\t\"height\":\"2\"\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t\"weibo\":{\n" +
                "\t\t\t\"img\":[\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"image\":\"http://adimg1.yokead.com/0F16E24110144BB8C1C1DAF6D4A08EF5\",\n" +
                "\t\t\t\t\t\"qrCode\":{\n" +
                "\t\t\t\t\t\t\"x\":\"320\",\n" +
                "\t\t\t\t\t\t\"width\":\"0.4\",\n" +
                "\t\t\t\t\t\t\"y\":\"970\",\n" +
                "\t\t\t\t\t\t\"height\":\"5\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t],\n" +
                "\t\t\t\"title\":\"真实可靠！赚钱简单！只需要看看新闻，每天就有丰厚奖励，现在加入可参与抽奖活动，最高88元红包，绝对不要错过，赶快扫码加入吧！????????\"\n" +
                "\t\t},\n" +
                "\t\t\"pyq\":{\n" +
                "\t\t\t\"img\":[\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"image\":\"http://adimg1.yokead.com/0F16E24110144BB8C1C1DAF6D4A08EF5\",\n" +
                "\t\t\t\t\t\"qrCode\":{\n" +
                "\t\t\t\t\t\t\"x\":\"320\",\n" +
                "\t\t\t\t\t\t\"width\":\"0.4\",\n" +
                "\t\t\t\t\t\t\"y\":\"970\",\n" +
                "\t\t\t\t\t\t\"height\":\"3\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t],\n" +
                "\t\t\t\"title\":\"\uE112\uE112真实可靠！赚钱简单！[转圈]只需要看看新闻，每天就有丰厚奖励，现在加入可参与抽奖活动，最高88元红包[红包][红包]，绝对不要错过，赶快扫码加入吧！[机智][机智]\\n\"\n" +
                "\t\t},\n" +
                "\t\t\"qzone\":{\n" +
                "\t\t\t\"img\":[\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"image\":\"http://adimg1.yokead.com/0F16E24110144BB8C1C1DAF6D4A08EF5\",\n" +
                "\t\t\t\t\t\"qrCode\":{\n" +
                "\t\t\t\t\t\t\"x\":\"320\",\n" +
                "\t\t\t\t\t\t\"width\":\"0.4\",\n" +
                "\t\t\t\t\t\t\"y\":\"970\",\n" +
                "\t\t\t\t\t\t\"height\":\"4\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t],\n" +
                "\t\t\t\"title\":\"真实可靠！赚钱简单！只需要看看新闻，每天就有丰厚奖励，现在加入可参与抽奖活动，最高88元红包，绝对不要错过，赶快扫码加入吧！\"\n" +
                "\t\t},\n" +
                "\t\t\"poster\":\"扫码领取[1-88元]现金红包\",\n" +
                "\t\t\"wxFriend\":{\n" +
                "\t\t\t\"image\":\"http://adimg1.yokead.com/0F16E24110144BB8C1C1DAF6D4A08EF5\",\n" +
                "\t\t\t\"qrCode\":{\n" +
                "\t\t\t\t\"x\":\"320\",\n" +
                "\t\t\t\t\"width\":\"0.4\",\n" +
                "\t\t\t\t\"y\":\"970\",\n" +
                "\t\t\t\t\"height\":\"1\"\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"show\":{\n" +
                "\t\t\"img\":[\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"image\":\"http://adimg1.yokead.com/0F16E24110144BB8C1C1DAF6D4A08EF5\",\n" +
                "\t\t\t\t\"qrCode\":{\n" +
                "\t\t\t\t\t\"x\":\"320\",\n" +
                "\t\t\t\t\t\"width\":\"0.4\",\n" +
                "\t\t\t\t\t\"y\":\"970\",\n" +
                "\t\t\t\t\t\"height\":\"6\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"image\":\"http://adimg1.yokead.com/8E5B06FA7CB50577A7FA73A40F6815AC\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"title\":\"我在热闻[玫瑰]收益了#money#元[胜利][胜利]，免费加入得现金，万元现金红包大派送，还等什么，赶快加入吧！[勾引][勾引]，扫码就送最高88元大红包[红包]\\n\",\n" +
                "\t\t\"getJifen\":50\n" +
                "\t},\n" +
                "\t\"wholePointActivity\":{\n" +
                "\t\t\"activeTag\":2,\n" +
                "\t\t\"qrCode\":{\n" +
                "\t\t\t\"x\":185,\n" +
                "\t\t\t\"width\":0.5,\n" +
                "\t\t\t\"y\":815,\n" +
                "\t\t\t\"height\":0.9\n" +
                "\t\t},\n" +
                "\t\t\"share\":[\n" +
                "\t\t\t\"qqFriend\",\n" +
                "\t\t\t\"weixinPyq\",\n" +
                "\t\t\t\"weixinFriend\",\n" +
                "\t\t\t\"qqZone\"\n" +
                "\t\t],\n" +
                "\t\t\"bgImage\":\"http://images.ztoutiao.cn/9BD5E60F2518B4FC0C7BD96010C94B55\",\n" +
                "\t\t\"title\":\"[红包][红包]红包来了，热闻APP每天都有不定时整点红包大派送\uE112\uE112，现在加入还有最高88元拆现金红包活动，先到先得，赶快扫码抽奖吧！[勾引][勾引]\\n\\n\"\n" +
                "\t},\n" +
                "\t\"config\":{\n" +
                "\t\t\"activeTag\":\"3\",\n" +
                "\t\t\"inviteUrl\":\"http://ssss.aixcun.com/to@.html\",\n" +
                "\t\t\"poster\":\"每成功邀请一个新用户下载热闻完成任务，您将获得最低<8000>积分的奖励，快快行动起来吧\"\n" +
                "\t},\n" +
                "\t\"alwaysSingnIn\":{\n" +
                "\t\t\"activeTag\":2,\n" +
                "\t\t\"qrCode\":{\n" +
                "\t\t\t\"x\":200,\n" +
                "\t\t\t\"width\":0.5,\n" +
                "\t\t\t\"y\":830,\n" +
                "\t\t\t\"height\":0.5\n" +
                "\t\t},\n" +
                "\t\t\"share\":[\n" +
                "\t\t\t\"qqFriend\",\n" +
                "\t\t\t\"weixinPyq\",\n" +
                "\t\t\t\"weixinFriend\",\n" +
                "\t\t\t\"qqZone\"\n" +
                "\t\t],\n" +
                "\t\t\"bgImage\":\"http://images.ztoutiao.cn/9BD5E60F2518B4FC0C7BD96010C94B55\",\n" +
                "\t\t\"title\":\"真实可靠！赚钱简单！只需要看看新闻，每天就有丰厚奖励，现在加入可参与抽奖活动，最高88元红包，绝对不要错过，赶快扫码加入吧！\"\n" +
                "\t}\n" +
                "}";
        //  test = new String(test.getBytes(), CharsetUtil.ISO_8859_1);
//        System.out.println(test);
        System.out.println(autoToUtf8(test));

    }

    private static String autoToUtf8(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        String newStr = CharsetUtil.convert(str, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
        if (str.length() == newStr.length()) {
            System.out.println("=");
            return str;
        }
        return newStr;
    }

}
