package vn.k2t.traficjam.model;

/**
 * Created by root on 12/07/2016.
 */
public class Posts {
    private String user_id;
    private String name;
    private String avatar;
    private String title;

    private String type;
    private String rank;
    private String latitude;
    private String longitude;
    private String location;
    private String image;
    private String created_at;
    private String like;
    private String share;
    private String report;
    public Posts() {

    }

    public Posts(String user_id, String name, String avatar, String title, String type, String rank, String latitude, String longitude, String location, String image, String created_at, String like, String share, String report) {
        this.user_id = user_id;
        this.name = name;
        this.avatar = avatar;
        this.title = title;
        this.type = type;
        this.rank = rank;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.image = image;
        this.created_at = created_at;
        this.like = like;
        this.share = share;
        this.report = report;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
