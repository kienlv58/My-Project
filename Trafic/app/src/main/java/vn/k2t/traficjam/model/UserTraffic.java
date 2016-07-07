package vn.k2t.traficjam.model;

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

    public UserTraffic(String uid, String name, String avatar, String email, String uidProvider,String rank) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
        this.uidProvider = uidProvider;
        this.rank = rank;
    }

    public UserTraffic() {
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
}
