package com.uclgroupgh.form.entities;

import com.orm.SugarRecord;

public class Regions extends SugarRecord {
    public String region;
    public String regioncode;

    public Regions() {
    }

    public Regions(String region, String regioncode) {
        this.region = region;
        this.regioncode = regioncode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegioncode() {
        return regioncode;
    }

    public void setRegioncode(String regioncode) {
        this.regioncode = regioncode;
    }
}
