package vn.k2t.traficjam.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by root on 7/7/16.
 */
public class UserTraffic implements Serializable {
    private String uid;
    private String name;
    private String avatar;
    private String email;
    private String uidProvider;
    private String rank;
    private String latitude;
    private String longitude;
    private int status;
    private String phone;
    private ArrayList<String> list_friend;

    public UserTraffic() {
    }

    public UserTraffic(String name, String avatar, String email) {
        this.name = name;
        this.avatar = avatar;
        this.email = email;
    }

    public UserTraffic(String name, String avatar, int status) {
        this.name = name;
        this.avatar = avatar;
        this.status = status;
    }

    public UserTraffic(String uid, String name, String avatar, String email, String uidProvider, int status) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
        this.uidProvider = uidProvider;
        this.status = status;
    }

    public UserTraffic(String uid, String name, String avatar, String email, String uidProvider, String rank, String latitude, String longitude, int status, String phone) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
        this.uidProvider = uidProvider;
        this.rank = rank;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.phone = phone;
    }

    public UserTraffic(String uid, String name, String avatar, String email, String uidProvider, String rank, String latitude, String longitude) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
        this.uidProvider = uidProvider;
        this.rank = rank;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUidProvider() {
        return uidProvider;
    }

    public void setUidProvider(String uidProvider) {
        this.uidProvider = uidProvider;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getList_friend() {
        return list_friend;
    }

    public void setList_friend(ArrayList<String> list_friend) {
        this.list_friend = list_friend;
    }

    @Override
    public String toString() {
        return "UserTraffic{" +
                "phone='" + phone + '\'' +
                ", status=" + status +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", rank='" + rank + '\'' +
                ", uidProvider='" + uidProvider + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
