package com.esc.screendisplay.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserEntity implements Parcelable {
    public String success = "false";
    public String msg;
    public User user;

    public UserEntity(){

    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserEntity(Parcel in) {
        success = in.readString();
        msg = in.readString();
        user = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<UserEntity> CREATOR=new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel parcel) {
            return new UserEntity(parcel);
        }

        @Override
        public UserEntity[] newArray(int i) {
            return new UserEntity[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.success);
        parcel.writeString(this.msg);
        parcel.writeParcelable(this.user, 0);
    }
}
