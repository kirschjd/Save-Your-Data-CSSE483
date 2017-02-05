package edu.rose_hulman.bradylz.saveyourdata;

import com.google.firebase.database.Exclude;

import java.util.Map;

/**
 * Created by Matt Boutell on 9/4/2015.
 */
public class Owner implements Comparable<Owner> {

    public static final String USERNAME = "username";
    public static final String FILES = "files";

    private String key;

    private String username;
    private Map<String, Boolean> files;

    // Required by Firebase when deserializing json
    public Owner() {
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, Boolean> getFiles() {
        return files;
    }

    public void setFiles(Map<String, Boolean> courses) {
        this.files = courses;
    }

    @Override
    public String toString() {
        return username;
    }

    public boolean containsCourse(String fileKey) {
        return files != null && files.containsKey(fileKey);
    }

    @Override
    public int compareTo(Owner another) {
        return username.compareTo(another.username);
    }

}
