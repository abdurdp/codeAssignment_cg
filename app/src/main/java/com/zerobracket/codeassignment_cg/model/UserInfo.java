package com.zerobracket.codeassignment_cg.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getDob() {
        return dob;
    }

    public void setDob(Long dob) {
        this.dob = dob;
    }

    private String imageUrl;
    private Long dob;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.dob);
    }

    public void readFromParcel(Parcel source) {
        this.userName = source.readString();
        this.imageUrl = source.readString();
        this.dob = (Long) source.readValue(Long.class.getClassLoader());
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.userName = in.readString();
        this.imageUrl = in.readString();
        this.dob = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
