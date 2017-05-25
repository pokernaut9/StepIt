package net.devcats.stepit.Model;

import com.google.gson.annotations.SerializedName;

import net.devcats.stepit.Utils.DateUtils;
import net.devcats.stepit.Utils.StringUtils;

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public int getParticipants() {
        return users.size();
    }

    public String getDateRange() {
        return DateUtils.formatDate(getStartDate()) + " - " + DateUtils.formatDate(getEndDate());
    }
}
