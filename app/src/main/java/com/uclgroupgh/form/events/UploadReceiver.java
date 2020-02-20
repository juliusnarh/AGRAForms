package com.uclgroupgh.form.events;

import android.content.Context;

import com.google.gson.Gson;
import com.uclgroupgh.form.entities.AggregationCenters;
import com.uclgroupgh.form.entities.CollectorInfo;
import com.uclgroupgh.form.entities.FertilizerBlending;
import com.uclgroupgh.form.entities.ParticipationInAgraEvent;
import com.uclgroupgh.form.entities.SeedProduction;
import com.uclgroupgh.form.entities.SupportedEnterprise;
import com.uclgroupgh.form.entities.TrainingAttendance;
import com.uclgroupgh.form.entities.TrainingInfo;
import com.uclgroupgh.form.pojo.Feedback;
import com.uclgroupgh.form.pojo.SuccessPojo;
import com.uclgroupgh.form.utils.AndroidUtils;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * This implementation is empty on purpose, just to show how it's possible to intercept
 * all the upload events app-wise with a global broadcast receiver registered in the manifest.
 *
 * @author Aleksandar Gotev
 */

public class UploadReceiver extends UploadServiceBroadcastReceiver {
    boolean delmsg;
    AggregationCenters aggregationCenters;
    CollectorInfo collectorInfo;
    FertilizerBlending fertilizerBlending;
    ParticipationInAgraEvent agraEvent;
    SeedProduction seedProduction;
    SupportedEnterprise supportedEnterprise;
    TrainingAttendance trainingAttendance;
    TrainingInfo trainingInfo;

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        super.onProgress(context, uploadInfo);
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        super.onError(context, uploadInfo, serverResponse, exception);
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        System.out.println("Resposonse: " + serverResponse.toString() + " Body: " + serverResponse.getBodyAsString());
        try {
            SuccessPojo servermsg = new Gson().fromJson(serverResponse.getBodyAsString(), SuccessPojo.class);

            if (servermsg.getStatus().trim().equalsIgnoreCase("success")) {
                if (updateTables(servermsg.getFeedbacklist())) {
                    delmsg = FileUtils.deleteQuietly(new File(AndroidUtils.INSTANCE.uploadDirectoryPath() + File.separator + servermsg.getFilename()));
                    if (delmsg == true) {
                        Toasty.success(context, "Server Message Success!!");
                    } else {
                        Toasty.error(context, "Server Message Error!!");
                    }
                }
            } else if (servermsg.getStatus().trim().equalsIgnoreCase("error")) {
                Toasty.error(context, "Error Saving Records On Server!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(context, "Server Didn't Return Proper Input!!!");
        }

    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        super.onCancelled(context, uploadInfo);
    }

    //method to update database using records from the server
    public boolean updateTables(List<Feedback> feedbackList) {
        try {
            for (Feedback feedback : feedbackList) {
                aggregationCenters = new AggregationCenters();
                collectorInfo = new CollectorInfo();
                fertilizerBlending = new FertilizerBlending();
                agraEvent = new ParticipationInAgraEvent();
                seedProduction = new SeedProduction();
                supportedEnterprise = new SupportedEnterprise();
                trainingAttendance = new TrainingAttendance();
                trainingInfo = new TrainingInfo();

                if (feedback.getTablename().trim().equalsIgnoreCase("aggregationcenters")) {
                    aggregationCenters = AggregationCenters.findById(AggregationCenters.class, feedback.getMobileid());
                    if (aggregationCenters != null) {
                        aggregationCenters.setServerid(feedback.getId());
                        aggregationCenters.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("collectorinfo")) {
                    collectorInfo = CollectorInfo.findById(CollectorInfo.class, feedback.getMobileid());
                    if (collectorInfo != null) {
                        collectorInfo.setServerid(feedback.getId());
                        collectorInfo.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("fertilizerblending")) {
                    fertilizerBlending = FertilizerBlending.findById(FertilizerBlending.class, feedback.getMobileid());
                    if (fertilizerBlending != null) {
                        fertilizerBlending.setServerid(feedback.getId());
                        fertilizerBlending.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("participationinagraevent")) {
                    agraEvent = ParticipationInAgraEvent.findById(ParticipationInAgraEvent.class, feedback.getMobileid());
                    if (agraEvent != null) {
                        agraEvent.setServerid(feedback.getId());
                        agraEvent.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("seedproduction")) {
                    seedProduction = SeedProduction.findById(SeedProduction.class, feedback.getMobileid());
                    if (seedProduction != null) {
                        seedProduction.setServerid(feedback.getId());
                        seedProduction.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("supportedenterprise")) {
                    supportedEnterprise = SupportedEnterprise.findById(SupportedEnterprise.class, feedback.getMobileid());
                    if (supportedEnterprise != null) {
                        supportedEnterprise.setServerid(feedback.getId());
                        supportedEnterprise.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("trainingattendance")) {
                    trainingAttendance = TrainingAttendance.findById(TrainingAttendance.class, feedback.getMobileid());
                    if (trainingAttendance != null) {
                        trainingAttendance.setServerid(feedback.getId());
                        trainingAttendance.save();
                    }
                } else if (feedback.getTablename().trim().equalsIgnoreCase("traininginfo")) {
                    trainingInfo = TrainingInfo.findById(TrainingInfo.class, feedback.getMobileid());
                    if (trainingInfo != null) {
                        trainingInfo.setServerid(feedback.getId());
                        trainingInfo.save();
                    }
                }

            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}


