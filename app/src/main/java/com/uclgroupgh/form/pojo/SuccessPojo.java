/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uclgroupgh.form.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Softmasters
 */
public class SuccessPojo {

    private String status;
    private String filename;
    private List<Feedback> feedbacklist;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the feedbacklist
     */
    public List<Feedback> getFeedbacklist() {
        if (feedbacklist == null)
            feedbacklist = new ArrayList<>();
        return feedbacklist;
    }

    /**
     * @param feedbacklist the feedbacklist to set
     */
    public void setFeedbacklist(List<Feedback> feedbacklist) {
        this.feedbacklist = feedbacklist;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

}
