package net.devcats.stepit.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Competition {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("size")
    private int size;
    @SerializedName("createdBy")
    private int createdBy;
    @SerializedName("createdOn")
    private Date createdOn;
    @SerializedName("startDate")
    private Date startDate;
    @SerializedName("endDate")
    private Date endDate;
    @SerializedName("users")
    private List<User> users;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<User> getUsers() {
        return users;
    }
}
