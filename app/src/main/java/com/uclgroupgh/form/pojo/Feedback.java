/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.uclgroupgh.form.pojo;

import java.io.Serializable;

/**
 * @author Junior
 */
public class Feedback implements Serializable {

    private String tablename;
    private Integer id;
    private String uniqueuid;
    private String farmerid;
    private String syncstatus;
    private Integer mobileid;

    public Feedback() {
    }

    public Feedback(String tablename, Integer id, String farmerid, String syncstatus) {
        this.tablename = tablename;
        this.id = id;
        this.farmerid = farmerid;
        this.syncstatus = syncstatus;
    }

    public Integer getMobileid() {
        return mobileid;
    }

    public void setMobileid(Integer mobileid) {
        this.mobileid = mobileid;
    }

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }

    /**
     * @param tablename the tablename to set
     */
    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }


    /**
     * @return the syncstatus
     */
    public String getSyncstatus() {
        return syncstatus;
    }

    /**
     * @param syncstatus the syncstatus to set
     */
    public void setSyncstatus(String syncstatus) {
        this.syncstatus = syncstatus;
    }

    /**
     * @return the farmerid
     */
    public String getFarmerid() {
        return farmerid;
    }

    /**
     * @param farmerid the farmerid to set
     */
    public void setFarmerid(String farmerid) {
        this.farmerid = farmerid;
    }

    /**
     * @return the uniqueuid
     */
    public String getUniqueuid() {
        return uniqueuid;
    }

    /**
     * @param uniqueuid the uniqueuid to set
     */
    public void setUniqueuid(String uniqueuid) {
        this.uniqueuid = uniqueuid;
    }


}
