package vn.k2t.traficjam.model;

/**
 * Created by chung on 7/12/16.
 */
public class Friends {
    private String friend_uid;
    private int type;
    private String status;

    public Friends(String friend_uid, int type, String status) {
        this.friend_uid = friend_uid;
        this.type = type;
        this.status = status;
    }

    public Friends() {
    }

    public String getFriend_uid() {
        return friend_uid;
    }

    public int getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
}
