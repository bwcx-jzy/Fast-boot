package cn.jiangzeyin;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 自定义压缩
 *
 * @author jiangzeyin
 * @date 2018/8/15.
 */
public class VelocityHtmlCompressor extends HtmlCompressor {


    public VelocityHtmlCompressor() {
        List<Pattern> patterns = new ArrayList<>();
        // text/html
        Pattern pattern = Pattern.compile("<script[^>]*type\\s*=\\s*([\"']*)text/html\\1[^>]*>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        patterns.add(pattern);
        // text/plan
        pattern = Pattern.compile("<script[^>]*type\\s*=\\s*([\"']*)text/plan\\1[^>]*>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        patterns.add(pattern);
        //
        pattern = Pattern.compile("<script[^>]*type\\s*=\\s*([\"']*)text/*\\1[^>]*>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        patterns.add(pattern);
        this.setPreservePatterns(patterns);
    }
}
