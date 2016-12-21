package com.example.jzyu.weplantplayground.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weplant on 16/4/26.
 */
public class Size implements Parcelable {
    public int width;
    public int height;

    public Size() {}
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // -- Parcelable auto gen --
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected Size(Parcel in) {
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<Size> CREATOR = new Creator<Size>() {
        @Override
        public Size createFromParcel(Parcel source) {
            return new Size(source);
        }

        @Override
        public Size[] newArray(int size) {
            return new Size[size];
        }
    };
}
