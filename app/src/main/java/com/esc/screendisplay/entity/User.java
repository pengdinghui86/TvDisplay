package com.esc.screendisplay.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    public String id;
    public String username;
    public String loginName;
    public String password;
    public String roleType; //"1"=领导；"2"=一二线工程师
    public String post;
    public String postName;//服务端返回的所有岗位名称
    public String orgId;
    public String orgName;
    public String telephone;
    public String sex;
    public String email;
    public List<Role> roleList;

    public User(String loginName, String password){
        this.loginName = loginName;
        this.password = password;
    }

    public User(Parcel in) {
        id = in.readString();
        username = in.readString();
        loginName = in.readString();
        password = in.readString();
        roleType = in.readString();
        post = in.readString();
        postName = in.readString();
        orgId = in.readString();
        orgName = in.readString();
        telephone = in.readString();
        sex = in.readString();
        email = in.readString();
        roleList = new ArrayList<Role>();
        in.readList(roleList, getClass().getClassLoader());
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public static final Creator<User> CREATOR=new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.username);
        parcel.writeString(this.loginName);
        parcel.writeString(this.password);
        parcel.writeString(this.roleType);
        parcel.writeString(this.post);
        parcel.writeString(this.postName);
        parcel.writeString(this.orgId);
        parcel.writeString(this.orgName);
        parcel.writeString(this.telephone);
        parcel.writeString(this.sex);
        parcel.writeString(this.email);
        parcel.writeList(this.roleList);
    }
}
