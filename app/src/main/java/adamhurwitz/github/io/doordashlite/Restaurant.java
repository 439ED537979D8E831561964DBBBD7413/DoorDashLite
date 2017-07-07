package adamhurwitz.github.io.doordashlite;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahurwitz on 7/7/17.
 */

public class Restaurant implements Parcelable {

    private int id;
    private String name;
    private String description;
    private String cover_img_url;
    private String status;
    private double delivery_fee;

    private Restaurant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        cover_img_url = in.readString();
        status = in.readString();
        delivery_fee = in.readDouble();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover_img_url() {
        return cover_img_url;
    }

    public void setCover_img_url(String cover_img_url) {
        this.cover_img_url = cover_img_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(double delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(cover_img_url);
        dest.writeString(status);
        dest.writeDouble(delivery_fee);
    }
}
//}
