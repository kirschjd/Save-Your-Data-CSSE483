package edu.rose_hulman.bradylz.saveyourdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradylz on 1/20/2017.
 */

public class File implements Parcelable{
    private String name;
    private String description;
    private String filePath;
    private String key;
    private Map<String, Boolean> owners;

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

    public File() {

    }

    public File(String title, String description, String uid) {
        this.name = title;
        this.description = description;
        owners = new HashMap<>();
        owners.put(uid, true);
    }

    protected File(Parcel in) {
        name = in.readString();
        description = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
    }
}
