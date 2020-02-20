/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uclgroupgh.form.pojo;

/**
 * @author Softmasters
 */
public class LoginSuccessPojo {

    private String status;
    private String staffid;
    private String loginname;

    public LoginSuccessPojo() {
    }

    public LoginSuccessPojo(String status, String staffid, String loginname) {
        this.status = status;
        this.staffid = staffid;
        this.loginname = loginname;
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the staffid
     */
    public String getStaffid() {
        return staffid;
    }

    /**
     * @param staffid the staffid to set
     */
    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    /**
     * @return the loginname
     */
    public String getLoginname() {
        return loginname;
    }

    /**
     * @param loginname the loginname to set
     */
    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }


}
