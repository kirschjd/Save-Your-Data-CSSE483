package edu.rose_hulman.bradylz.saveyourdata;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.firebase.database.Exclude;

/**
 * Created by bradylz on 1/20/2017.
 */

public class File implements Parcelable{
    private String title;
    private String description;
    private Integer imageID;
    private String filePath;
    private String key;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public String getKey() {
        return key;
    }


    public File(String title, String description, Integer imageID) {
        this.title = title;
        this.description = description;
        this.imageID = imageID;
    }

    protected File(Parcel in) {
        title = in.readString();
        description = in.readString();
        imageID = in.readInt();
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImageID() {
        return imageID;
    }

    public void setImageID(Integer imageID) {
        this.imageID = imageID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(imageID);
    }
}
