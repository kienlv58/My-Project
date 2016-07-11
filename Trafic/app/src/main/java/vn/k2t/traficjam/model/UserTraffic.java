package vn.k2t.traficjam.model;

import java.util.ArrayList;

/**
 * Created by root on 7/7/16.
 */
public class UserTraffic {
    private String uid;
    private String name;
    private String avatar;
    private String email;
    private String uidProvider;
    private String rank;
    private String location;
    private String longitude;
    private ArrayList<String> list_friend = new ArrayList<>();

    public UserTraffic() {
    }

    public UserTraffic(String uid, String name, String avatar, String email, String uidProvider, String rank, String location, String longitude, ArrayList<String> list_friend) {

        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
        this.uidProvider = uidProvider;
        this.rank = rank;
        this.location = location;
        this.longitude = longitude;
        this.list_friend = list_friend;

    }

    public ArrayList<String> getList_friend() {
        return list_friend;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getLongitude() {
        return longitude;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUidProvider() {
        return uidProvider;
    }

    public void setUidProvider(String uidProvider) {
        this.uidProvider = uidProvider;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
