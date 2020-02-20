package com.uclgroupgh.form.entities;

import com.orm.SugarRecord;

public class Districts extends SugarRecord {

    public String district;
    public String districtcode;
    public String targets;
    public String regioncode;
    public String region;

    public Districts() {
    }

    public Districts(String district, String districtcode, String targets, String regioncode, String region) {
        this.district = district;
        this.districtcode = districtcode;
        this.targets = targets;
        this.regioncode = regioncode;
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrictcode() {
        return districtcode;
    }

    public void setDistrictcode(String districtcode) {
        this.districtcode = districtcode;
    }

    public String getTargets() {
        return targets;
    }

    public void setTargets(String targets) {
        this.targets = targets;
    }

    public String getRegioncode() {
        return regioncode;
    }

    public void setRegioncode(String regioncode) {
        this.regioncode = regioncode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
