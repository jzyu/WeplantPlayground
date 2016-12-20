package com.wohuizhong.client.app.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final char COMBIN_CHAR = ',';

    /**
     * 判断字符串是否为null或空串
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.toString().equals("");
    }

    public static boolean isEmptyOrAllWhiteSpace(String str) {
        return str == null || str.equals("") || str.matches("^\\s*$");
    }

    //fixme
    public static boolean isValidPhone(String phone) {
        boolean flag;
        try{
            Pattern p = Pattern.compile("^((1[0-9])|(1[0,5-9])|(1[^4,\\D])|(1[0,5-9])|(1[0,5-9]))\\d{9}$");
            Matcher m = p.matcher(phone);
            flag = m.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    public static boolean isValidUsername(String str) {
        if (!StringUtil.isEmpty(str) && str.length() >= 2) {
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return true;
        } else {
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            return matcher.matches();
        }
    }

    public static boolean isValidPassword(String str) {
        return str.length() >= 6;
    }

    public static String extractTextFromHtml(String htmlStr) {
        if (isEmpty(htmlStr))
            return "";
        else
            return htmlStr.replaceAll("(<[^>]+>)|&nbsp;", ""); //消除<>和空格
    }

    public static String extractTextFromHtmlForShare(String htmlStr) {
        if (isEmpty(htmlStr))
            return "";
        else
            //比上一个函数多消除空白字符：空格和换行符
            return htmlStr.replaceAll("(<[^>]+>)|&nbsp;|\\s", "");
    }

    //如果字段不存在或者值为null,则返回空串""
    public static String jsonGetString(JSONObject jso, String key) {
        String value = "";
        try {
            value = jso.isNull(key) ? "" : jso.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    //如果字段不存在或者值为null,则返回空串""
    public static String jsonArrayGetString(JSONArray jsoArray, int i) {
        String value = "";
        try {
            value = jsoArray.isNull(i) ? "" : jsoArray.getString(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int jsonGetInt(JSONObject jso, String key) {
        int value = 0;
        try {
            value = jso.isNull(key) ? 0 : jso.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static long jsonGetLong(JSONObject jso, String key) {
        long value = 0L;
        try {
            value = jso.isNull(key) ? 0L : jso.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean jsonGetBoolean(JSONObject jso, String key) {
        if (jso.isNull(key)) return false;

        boolean value = false;
        try {
            value = jso.getBoolean(key);
        } catch (JSONException e) {
            try {
                int intValue = jso.getInt(key);
                value = (intValue > 0);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 合并 json string list
     * 比如 JSONArray是
     *    votes: ["can","ls"]
     * 返回值是
     *    "can,ls"
     */
    public static String jsonStrListCombin(JSONObject jso, String key) {
        if (jso.isNull(key))
            return "";

        StringBuilder combinStr = new StringBuilder("");
        try {
            JSONArray jsoArray = jso.getJSONArray(key);
            for (int i = 0; i < jsoArray.length(); i++) {
                String str = jsoArray.getString(i);
                combinStr.append(str);

                if (i < jsoArray.length() - 1)
                    combinStr.append(COMBIN_CHAR);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return combinStr.toString();
    }

    public static String convertStrToUtf8(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }

    public static JSONObject makeJso(String jsonString) {
        JSONObject jso;
        try {
            jso = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            jso = new JSONObject();
        }
        return jso;
    }

    /**
     * @param timestamp
     * @return
     */
    public static String formatDate(long timestamp) {
        Date time = new Date(timestamp);
        long now = new Date().getTime();
        long distance = (int) ((time.getTime() - now) / 1000);
        String suffix = distance < 0 ? "前" : "后";
        distance = Math.abs(distance);
        if (distance < 60) { //秒
            return "刚刚";
        } else if ((distance /= 60) < 60) {   //分
            return String.format("%d分钟%s", (int) distance, suffix);
        } else if ((distance /= 60) < 24) {   //时
            return String.format("%d小时%s", (int) distance, suffix);
        } else if ((distance /= 24) < 31) {
            return String.format("%d天%s", (int) distance, suffix);
        } else if ((distance /= 31) < 12) {
            return String.format("%d个月%s", (int) distance, suffix);
        } else {
            distance /= 12;
            return String.format("%d年%s", (int) distance, suffix);
        }
    }

    public static String urlNormalize(String url) {
        String ascUrl;
        try {
            ascUrl = (new URI(url)).toASCIIString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            ascUrl = url;
        }

        ascUrl = ascUrl.replace(" ", "%20");
        return ascUrl;
    }

    public static String join(String[] strArray, String split) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArray.length; i++) {
            String str = strArray[i];

            if (StringUtil.isEmpty(str)){
                continue;
            }

            if (i == strArray.length - 1)
                sb.append(str);
            else
                sb.append(str).append(split);
        }
        return sb.toString();
    }

    public static String join(List<String> strings, String split) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);

            if (StringUtil.isEmpty(str)){
                continue;
            }

            if (i == strings.size() - 1)
                sb.append(str);
            else
                sb.append(str).append(split);
        }
        return sb.toString();
    }

    private static final int SECONDS_IN_DAY = 60 * 60 * 24;
    private static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;

    public static String tsToHuman(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        //long now = getCurrentTime(ctx);
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "";
        }

        // localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "刚刚"; //"just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1分钟前"; //"a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return  diff / MINUTE_MILLIS + "分钟之前"; //" minutes ago";
        } else {
            if (isSameDayOfMillis(now, time)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date date = new Date(time);
                String times = sdf.format(date);
                return "今天" + times;
            } else if (isSameYearOfMillis(now, time)) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                Date date = new Date(time);
                return sdf.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date date = new Date(time);
                return sdf.format(date);
            }
        }
    }

    private static boolean isSameYearOfMillis(long now, final long time) {
        Date date1 = new Date(now);
        Date date2 = new Date(time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);

        calendar.setTime(date2);
        int year2 = calendar.get(Calendar.YEAR);

        int result = year2 - year1;
        return result == 0 ? true : false;
    }

    private static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }


    public static String getStringFromAssets(Context context, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ids2str(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<ids.size();  i++) {
            sb.append(String.valueOf(ids.get(i)));
            if (i < ids.size() - 1)
                sb.append(COMBIN_CHAR);
        }
        return sb.toString();
    }

    public static String LongIds2str(long[] ids) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<ids.length;  i++) {
            sb.append(String.valueOf(ids[i]));
            if (i < ids.length - 1)
                sb.append(COMBIN_CHAR);
        }
        return sb.toString();
    }

    public static String LongIds2str(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<ids.size();  i++) {
            sb.append(String.valueOf(ids.get(i)));
            if (i < ids.size() - 1)
                sb.append(COMBIN_CHAR);
        }
        return sb.toString();
    }

    public static List<String> str2List(String str) {
        if (isEmpty(str))
            return null;
        else
            return Arrays.asList(str.split(","));
    }

    public static long[] stringToLong(String stringArray[]) {
        if (stringArray == null || stringArray.length < 1) {
            return null;
        }
        long longArray[] = new long[stringArray.length];
        for (int i = 0; i < longArray.length; i++) {
            try {
                longArray[i] = Long.valueOf(stringArray[i]);
            } catch (NumberFormatException e) {
                longArray[i] = 0;
                continue;
            }
        }
        return longArray;
    }

    /*// ? addSpanTag
    public static String replaceAtUserNameToHtml(String content,
                                                 Map<String, UserLite> atUsers,
                                                 JSONArray outputUids,
                                                 boolean addSpanTag) {
        for (String key : atUsers.keySet()) {
            UserLite user = atUsers.get(key);

            if (outputUids != null) {
                outputUids.put(user.uid);
            }

            String html = "<a href=\"/user/" + user.uid
                    + "\" class=\"name\" mention-uid=\"" + user.uid
                    + "\">@" + user.name
                    + "</a>";
            content = content.replace(key, html);
        }

        if (addSpanTag) {
            content = "<span>" + content + "</span>";
        }

        return content;
    }*/

    public static String niceFormat(float value) {
        if (value == (int) value) {
            return String.format("%d.00", (int) value);
        } else {
            return String.format("%.2f", value);
        }
    }

    public static String niceFormat(double value) {
        if (value == (int) value) {
            return String.format("%d.00", (int) value);
        } else {
            return String.format("%.2f", value);
        }
    }

    public static String stringMD5(String input) {
        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest =MessageDigest.getInstance("MD5");

            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes();

            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);

            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();

            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f' };

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray =new char[byteArray.length * 2];

        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    public static boolean isEndWithPunctuation(String content) {
        boolean flag;
        try{
            Pattern p = Pattern.compile("[，,.。?？、~！@#￥%&*（）——+!$%^()_+;；'\"“”‘’]$");
            Matcher m = p.matcher(content);
            flag = m.find();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    public static String getSuffix(String str, char preChar) {
        int index = str.lastIndexOf(preChar);
        if (index == -1) return null;

        return str.substring(index + 1);
    }

    public static long getLmidFromH5Url(String url, String keyStr) {
        if (! url.contains(keyStr)) {
            return 0L;
        }

        String suffix = getSuffix(url, '\\');
        if (StringUtil.isEmpty(suffix)) {
            suffix = getSuffix(url, '/');
        }

        if (StringUtil.isEmpty(suffix)) {
            return 0L;
        } else {
            try {
                return Long.parseLong(suffix);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
    }

    //显示为时:分:秒，不足时简化显示
    public static String tsToHuman2(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        if (hours > 0) {
            return String.format("%d小时%d分%d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    public static String tsToHuman3(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("0:%02d", seconds);
        }
    }

    public static String getCoverUrlFromContent(String content) {
        String coverUrl = "";
        try{
            Pattern p1 = Pattern.compile("<img[^>]*src=\"([^\"]*)\"");
            Matcher m = p1.matcher(content);
            if (m.find()) {
                String url = m.group(1);
                coverUrl = url.replaceFirst("\\?imageView2.*$","?imageView2/5/w/100");
            }
        }catch(Exception e){
            return "";
        }

        return coverUrl;
    }

    public static String stripUrlQueryParams(String url) {
        Uri uri = Uri.parse(url);
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.encodedPath(uri.getPath());

        return builder.build().toString();
    }

    public static String urlRemoveHost(String url) {
        Uri uri = Uri.parse(url);
        int hostEnd = url.indexOf(uri.getHost())
                + uri.getHost().length();

        return url.substring(hostEnd);
    }

    public static String extractStringInBracket(final String source, char bracketLeft, char bracketRight) {
        String re = String.format(Locale.getDefault(),
                "\\%c(.*?)\\%c", bracketLeft, bracketRight);
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(source);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * 提取标题
     */
    @NonNull
    public static String extractTitle(String source, int extractLength) {
        final int TITLE_LENGTH_MIN = 4;

        //尝试从括号中提取
        String title = extractStringInBracket(source, '【', '】');
        if (isEmpty(title)) {
            title = extractStringInBracket(source, '[', ']');
        }

        if (title != null && title.length() > TITLE_LENGTH_MIN) {
            return title;
        }

        //无括号则截取前面一段
        title = source.substring(0, Math.min(extractLength, source.length()));

        //找句号或问号再截断一次
        if (title.contains("。")) {
            title = title.substring(0, title.indexOf("。") + 1);
        } else if (title.contains("？")) {
            title = title.substring(0, title.indexOf("？") + 1);
        }

        return title;
    }

    public static int getMatchCountByChar(String source, String pattern) {
        int count = 0;
        for (int i=0; i < pattern.length(); i++) {
            CharSequence cs = pattern.subSequence(i, i + 1);
            if (! cs.equals(" ") && source.contains(cs)) {
                count++;
            }
        }
        return count;
    }
}
