package com.wohuizhong.client.app.util;


public final class Consts {
    /**
     * shared preference key
     */
    public static final String SP_FILENAME = "shared_prefs";
    public static final String SP_KEY_PHONE = "phone";
    public static final String SP_KEY_USERNAME = "username";
    public static final String SP_KEY_UID = "uid";
    public static final String SP_KEY_ACCESS_TOKEN = "accessToken";
    public static final String SP_KEY_ME_DETAIL = "meDetail";
    public static final String SP_KEY_FLOWMATE = "flowmate";
    public static final String SP_KEY_GEO_LOCATION = "address";

    public static final String SP_KEY_NOTIFY_UNREAD_COUNT_GENERAL = "unread_count_general";
    public static final String SP_KEY_NOTIFY_UNREAD_COUNT_UP      = "unread_count_up";
    public static final String SP_KEY_NOTIFY_UNREAD_COUNT_INVITE  = "unread_count_invite";
    public static final String SP_KEY_NOTIFY_UNREAD_COUNT_FOCUS   = "unread_count_focus";

    public static final String SP_KEY_TIMELINE_BACKGROUND = "backgroundImgUrl";
    public static final String SP_KEY_OAUTH_INFOS = "openIds";
    public static final String SP_KEY_APP_CONFIG = "app_config";

    public static final String SP_KEY_AT_USER_HISTORY = "at_user_history";

    //安卓版本微信分享图标须方角，圆角会有黑边
    public static final String SHARE_COVER_URL_APP_ICON = "http://resource.wohuizhong.cn/share-cover-wx-android.png";
    public static final String SHARE_COVER_URL_RED_ENVELOP = "http://resource.wohuizhong.cn/android_share_cover_red_envelop.png";

    public static final String SP_KEY_PM_MESSAGE_UNREAD_COUNT = "unread_count_pm_message";

    public static final String SP_KEY_SETTING_SWITCH_LOCATE = "setting_switch_locate";
    public static final String SP_KEY_SETTING_SWITCH_NOTIFY = "setting_switch_notify";
    public static final String SP_KEY_SETTING_SWITCH_SOUND  = "setting_switch_sound";
    public static final String SP_KEY_SETTING_SWITCH_EDIT_PWD = "setting_setting_edit_pwd";

    public static final String SP_KEY_PAY_TYPE_PREV_SELECT = "pay_type_prev_select";
    public static final String SP_KEY_MY_WALLET_HAS_NEW = "my_wallet_has_new";
    public static final String SP_KEY_HAS_NEW_APPRECIATE = "has_new_appreciate";

    public static final String SP_KEY_WITHDRAW_ALIPAY_ACCOUNT = "withdraw_alipay_account";
    public static final String SP_KEY_WITHDRAW_ALIPAY_NAME = "withdraw_alipay_name";

    public static final String SP_KEY_PLUS_CLICK_COUNTER = "new_content_badge_counter";
    public static final String SP_KEY_USER_IS_ADMIN = "user_is_admin";
    public static final String SP_KEY_USER_IS_VIP = "user_is_vip";

    public static final String SP_KEY_INTRO_STICKY_SHOWN = "intro_sticky_shown";
    public static final String SP_KEY_INTRO_INVITE_SHOWN = "intro_invite_shown";

    public static final String SP_KEY_IS_CONCERNS_SYNCED = "topic_concerned";
    /**
     * 第三方开放平台授权
     */
    public static  final String WX_APPID = "wx26368ec02636ad57";
    public static  final String WX_SECRET= "91157c658a334037c5b481d333e26b20";
    public static  final String QQ_APPID = "1104724491";
    public static  final String QQ_SECRET= "099Zwl0mTeTTzQ6M";
    public static  final String SINA_APPID = "3105597143";
    public static  final String SINA_SECRET= "2ece2ea73722f7cba30cab2cb34c0f1b";

    public static final String LEANCLOUND_APPID  = "FSNOga0apw9s5G0VGJW4C4ut";
    public static final String LEANCLOUND_APPKEY = "cwkEU3GISMmuSk89Qz5NhlJF";

    /**
     * url base
     */
    public static final String API_URL_BASE = "http://wohuizhong.com";
    //public static final String API_URL_BASE = "http://192.168.0.124";

    public static final String URL_BASE_IMG_AVATAR = API_URL_BASE + "/pictures/avatar/";
    public static final String URL_BASE_IMG_TOPIC  = API_URL_BASE + "/pictures/topic/";
    public static final String HTML_LINEBREAK = "<br/>"; //注意：</br>不行
    public static final String SUFFIX_WANT_ANSWER = "想知道答案";
    public static final int MS_WAIT_KEYBOARD_SHOWN = 400;

    private Consts() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public final static String[] VIOLATE_REASONS = {
            "广告及垃圾信息",
            "内容与话题不匹配",
            "违反法律法规",
            "其他",
    };

    public final static String[] NEW_CONTENT_TYPES = {
            "发表问题",
            "撰写帖子",
            "展示农场",
            "转发文章",
            "发红包",
    };

    public final static String[] DynamicTypes = {
            "all",
            "question",
            "answer",
            "article",
            "farm",
            "comment"
    };

    public static final String SHARE_SUFFIX_NEW_QUESTION = " 提出了问题";
    public static final String SHARE_SUFFIX_NEW_ARTICLE  = " 发表了帖子";
    public static final String SHARE_SUFFIX_NEW_FARM     = " 发表了农场";
    public static final String SHARE_SUFFIX_NEW_ANSWER   = " 回答了问题";

    public static final String VOTE_SIGN_NAME_SUFFIX = "已赞";

    public static final String APP_EXTERNAL_DIR = "我会种";
    public static final String DEFAULT_SIGNATURE = "这家伙太勤奋了，连签名都没空写";
    public static final String DEFAULT_INFO = "未填写";
    public static final String PLEASE_WAIT = "请稍候";
    public static final String PLEASE_CONNECT_NET = "请连接网络";
    public static final String TIME_OUT = "网络超时，请稍候再试";
    public static final String RESPONSE_INVALID = "网络返回无效";
    public static final String SHARE_ANSWER_PREFIX = "分享了答案 ：";

    public static final String TEXT_REFRESH_DOING = "正在获取内容, 请稍候...";
    public static final String TEXT_REFRESH_RETRY = "请下拉刷新重试";
    public static final String TEXT_WANT_ANSWER = "想知道答案";
    public static final String TEXT_COMMENT_INPUT_TIP = "说点什么吧";

    public static final String BAIDU_GEO_AK = "wjio8A0QfkQsSDHNGGwn4fLj";

    //sub cache dir under: /data/data/com.wohuizhong.client/cache
    public static final String CACHE_DIR_VIDEO = "video_cache";    //视频播放数据缓存目录
    public static final String CACHE_DIR_CRAWLER = "crawler_temp";

    public static final String URL_PREFIX_LOCAL = "file://";

    public static final String SHARED_ELEMENT_NAME = "sharedElementName";

    public static final int HTTP_STATUS_CODE_CACHE_IS_EMPTY = 504;


}
