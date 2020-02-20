package com.uclgroupgh.form.entities;

import com.orm.SugarRecord;

import java.util.Date;

public class FilledForms extends SugarRecord {
    public String category;
    public String subcategory;
    public String uniqueuid;
    public Date datecreated;
    public int serverid;
    public String macaddress;
    public String imei;

    public FilledForms() {
    }

    public FilledForms(String category, String subcategory, String uniqueuid, Date datecreated, int serverid, String macaddress, String imei) {
        this.category = category;
        this.subcategory = subcategory;
        this.uniqueuid = uniqueuid;
        this.datecreated = datecreated;
        this.serverid = serverid;
        this.macaddress = macaddress;
        this.imei = imei;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getUniqueuid() {
        return uniqueuid;
    }

    public void setUniqueuid(String uniqueuid) {
        this.uniqueuid = uniqueuid;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public int getServerid() {
        return serverid;
    }

    public void setServerid(int serverid) {
        this.serverid = serverid;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
