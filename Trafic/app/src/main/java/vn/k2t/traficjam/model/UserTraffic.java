package vn.k2t.traficjam.model;

import java.io.Serializable;

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

    public UserTraffic() {
    }

    public UserTraffic(String name, String avatar, String email) {
        this.name = name;
        this.avatar = avatar;
        this.email = email;
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

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getUidProvider() {
        return uidProvider;
    }

    public String getRank() {
        return rank;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getStatus() {
        return status;
    }

    public String getPhone() {
        return phone;
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
