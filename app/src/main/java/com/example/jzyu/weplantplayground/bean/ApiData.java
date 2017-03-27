package com.example.jzyu.weplantplayground.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Author: jzyu
 * Date  : 2017/3/27
 */

public class ApiData {
    public static class Explore {
        @JSONField(name="post")
        public ArrayList<Post> posts = new ArrayList<>(0);

        public static class Post {
            public long qid;
            public long time;
            public int type;
            public String title = "";
            public String detail = "";
            public String thumbnail = "";
            public String info = "";

            @Override
            public boolean equals(Object o) {
                return o instanceof Post
                        && ((Post) o).time == time;
            }
        }
    }
}
