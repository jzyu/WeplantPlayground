package com.example.jzyu.weplantplayground.util;

import com.example.jzyu.weplantplayground.bean.Size;
import com.wohuizhong.client.app.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: jzyu
 * Date  : 2016/12/17
 */

public class UrlUtil {

    // http://wohuizhong.cn/web-4-1-1480901012038-320x240-d-6.mp4
    // http://wohuizhong.cn/upload/weplant-android-7461-1480783913896-800x1422.jpeg?imageView2/2/w/800
    public static Size getSize(String url) {
        if (StringUtil.isEmpty(url))
            return new Size();

        final Size size = new Size();
        final String re = "http\\S+wohuizhong\\S+-(\\d+)x(\\d+)[\\.|-]";

        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(url);
        if (! matcher.find() || matcher.groupCount() < 2)
            return size;

        size.width = StringUtil.isEmpty(matcher.group(1)) ? 0 : Integer.valueOf(matcher.group(1));
        size.height = StringUtil.isEmpty(matcher.group(2)) ? 0 : Integer.valueOf(matcher.group(2));
        return size;
    }

    public static final int getVideoSeconds(String url) {
        final String re = "http\\S+wohuizhong\\S+-(\\d+)x(\\d+)-d-(\\d+)\\.";

        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(url);
        if (! matcher.find() || matcher.groupCount() < 3)
            return 0;

        return StringUtil.isEmpty(matcher.group(3)) ? 0 : Integer.valueOf(matcher.group(3));
    }

    public static boolean isVideo(String url) {
        return (! StringUtil.isEmpty(url)) &&
                (url.contains(".mp4") || url.contains(".mov")  || url.contains(".avi"));
    }
}
