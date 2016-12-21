package com.waynell.videolist.demo.util;

import com.waynell.videolist.demo.model.BaseItem;
import com.waynell.videolist.demo.model.PicItem;
import com.waynell.videolist.demo.model.TextItem;
import com.waynell.videolist.demo.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wayne
 */
public class ItemUtils {

    /*private static final String VIDEO_URL1 = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
    private static final String VIDEO_URL2 = "http://techslides.com/demos/sample-videos/small.mp4";
    private static final String VIDEO_URL3 = "http://download.wavetlan.com/SVV/Media/HTTP/H264/Other_Media/H264_test7_voiceclip_mp4_480x360.mp4";
    private static final String VIDEO_URL4 = "http://download.wavetlan.com/SVV/Media/HTTP/MP4/ConvertedFiles/Media-Convert/Unsupported/test7.mp4";*/

    private static final String VIDEO_URL1 = "http://wohuizhong.cn/iOS-4-3-1482209651-320x568-d-10.mov";
    private static final String VIDEO_URL2 = "http://fetchvideo.wohuizhong.cn/450881056-1482208481076-720x720-d-11.mp4";
    private static final String VIDEO_URL3 = "http://wohuizhong.cn/web-247-1-1482167497981-640x360-d-443.mp4";
    private static final String VIDEO_URL4 = "http://fetchvideo.wohuizhong.cn/6349-1482158470519-720x720-d-20.mp4";

    private static final String PIC_URL1 = "http://wohuizhong.cn/iOS-4-3-1482209651-320x568-d-10.mov?vframe/jpg/offset/5";
    private static final String PIC_URL2 = "http://fetchvideo.wohuizhong.cn/450881056-1482208481076-720x720-d-11.mp4?vframe/jpg/offset/5";
    private static final String PIC_URL3 = "http://wohuizhong.cn/web-247-1-1482167497981-640x360-d-443.mp4?vframe/jpg/offset/5";
    private static final String PIC_URL4 = "http://fetchvideo.wohuizhong.cn/6349-1482158470519-720x720-d-20.mp4?vframe/jpg/offset/5";

    public static List<BaseItem> generateMockData() {
        List<BaseItem> list = new ArrayList<>();

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL1));
        list.add(new VideoItem(VIDEO_URL4, PIC_URL4));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL2));
        list.add(new VideoItem(VIDEO_URL3, PIC_URL3));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL3));
        list.add(new VideoItem(VIDEO_URL2, PIC_URL2));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL4));
        list.add(new VideoItem(VIDEO_URL1, PIC_URL1));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL1));
        list.add(new VideoItem(VIDEO_URL4, PIC_URL4));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL2));
        list.add(new VideoItem(VIDEO_URL3, PIC_URL3));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL3));
        list.add(new VideoItem(VIDEO_URL2, PIC_URL2));

        list.add(new TextItem("TextItem"));
        list.add(new PicItem(PIC_URL4));
        list.add(new VideoItem(VIDEO_URL1, PIC_URL1));

        return list;
    }

}
