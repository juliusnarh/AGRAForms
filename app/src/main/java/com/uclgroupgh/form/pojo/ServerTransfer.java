package com.uclgroupgh.form.pojo;


import com.uclgroupgh.form.entities.AggregationCenters;
import com.uclgroupgh.form.entities.CollectorInfo;
import com.uclgroupgh.form.entities.FertilizerBlending;
import com.uclgroupgh.form.entities.ParticipationInAgraEvent;
import com.uclgroupgh.form.entities.SeedProduction;
import com.uclgroupgh.form.entities.SupportedEnterprise;
import com.uclgroupgh.form.entities.TrainingAttendance;
import com.uclgroupgh.form.entities.TrainingInfo;

/**
 * Created by Junior on 12/15/2017.
 */

public class ServerTransfer {
    //    private FarmAssessment assessment;
    private AggregationCenters aggregationCenters;
    private CollectorInfo collectorInfo;
    private FertilizerBlending fertilizerBlending;
    private ParticipationInAgraEvent participationInAgraEvent;
    private SeedProduction seedProduction;
    private SupportedEnterprise supportedEnterprise;
    private TrainingAttendance trainingAttendance;
    private TrainingInfo trainingInfo;


    public ServerTransfer() {
    }

    public AggregationCenters getAggregationCenters() {
        return aggregationCenters;
    }

    public void setAggregationCenters(AggregationCenters aggregationCenters) {
        this.aggregationCenters = aggregationCenters;
    }

    public CollectorInfo getCollectorInfo() {
        return collectorInfo;
    }

    public void setCollectorInfo(CollectorInfo collectorInfo) {
        this.collectorInfo = collectorInfo;
    }

    public FertilizerBlending getFertilizerBlending() {
        return fertilizerBlending;
    }

    public void setFertilizerBlending(FertilizerBlending fertilizerBlending) {
        this.fertilizerBlending = fertilizerBlending;
    }

    public ParticipationInAgraEvent getParticipationInAgraEvent() {
        return participationInAgraEvent;
    }

    public void setParticipationInAgraEvent(ParticipationInAgraEvent participationInAgraEvent) {
        this.participationInAgraEvent = participationInAgraEvent;
    }

    public SeedProduction getSeedProduction() {
        return seedProduction;
    }

    public void setSeedProduction(SeedProduction seedProduction) {
        this.seedProduction = seedProduction;
    }

    public SupportedEnterprise getSupportedEnterprise() {
        return supportedEnterprise;
    }

    public void setSupportedEnterprise(SupportedEnterprise supportedEnterprise) {
        this.supportedEnterprise = supportedEnterprise;
    }

    public TrainingAttendance getTrainingAttendance() {
        return trainingAttendance;
    }

    public void setTrainingAttendance(TrainingAttendance trainingAttendance) {
        this.trainingAttendance = trainingAttendance;
    }

    public TrainingInfo getTrainingInfo() {
        return trainingInfo;
    }

    public void setTrainingInfo(TrainingInfo trainingInfo) {
        this.trainingInfo = trainingInfo;
    }
}





