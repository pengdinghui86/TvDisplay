package com.esc.screendisplay.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Role implements Parcelable {
    public String roleId;
    public String roleName;
    public String roleType;
    public String code;
    public String signature;

    public Role(){
    }

    public Role(Parcel in) {
        roleId = in.readString();
        roleName = in.readString();
        roleType = in.readString();
        code = in.readString();
        signature = in.readString();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public static final Creator<Role> CREATOR=new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel parcel) {
            return new Role(parcel);
        }

        @Override
        public Role[] newArray(int i) {
            return new Role[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.roleId);
        parcel.writeString(this.roleName);
        parcel.writeString(this.roleType);
        parcel.writeString(this.code);
        parcel.writeString(this.signature);
    }
}
