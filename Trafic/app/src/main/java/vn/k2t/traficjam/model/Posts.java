package vn.k2t.traficjam.model;

/**
 * Created by root on 12/07/2016.
 */
public class Posts {
    private String user_id;
    private String title;
    private String name;
    private String type;
    private String rank;
    private String latitude;
    private String longitude;
    private String image;
    private String created_at;

    public Posts() {

    }

    public Posts(String user_id, String title, String name, String type, String rank, String latitude, String longitude, String image, String created_at) {
        this.user_id = user_id;
        this.title = title;
        this.name = name;
        this.type = type;
        this.rank = rank;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.created_at = created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTitle() {
        return title;
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


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }




}
