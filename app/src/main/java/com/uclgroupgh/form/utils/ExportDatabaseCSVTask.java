package com.uclgroupgh.form.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.orm.SugarRecord;
import com.uclgroupgh.form.R;
import com.uclgroupgh.form.entities.AggregationCenters;
import com.uclgroupgh.form.entities.FertilizerBlending;
import com.uclgroupgh.form.entities.ParticipationInAgraEvent;
import com.uclgroupgh.form.entities.SeedProduction;
import com.uclgroupgh.form.entities.SupportedEnterprise;
import com.uclgroupgh.form.entities.TrainingAttendance;
import com.uclgroupgh.form.entities.TrainingInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import es.dmoral.toasty.Toasty;

public class ExportDatabaseCSVTask extends AsyncTask<String, String, String> {
    private Context context;
    private SeedProduction seedProduction;
    private List<SeedProduction> seedProductionList;
    private AggregationCenters aggregationCenters;
    private List<AggregationCenters> aggregationCentersList;
    private SupportedEnterprise supportedEnterprise;
    private List<SupportedEnterprise> supportedEnterpriseList;
    private ParticipationInAgraEvent participationInAgraEvent;
    private List<ParticipationInAgraEvent> participationInAgraEventList;
    private TrainingAttendance trainingAttendance;
    private List<TrainingInfo> trainingInfoList;
    private FertilizerBlending fertilizerBlending;
    private List<FertilizerBlending> fertilizerBlendingList;
    private String[] selection = {"Seed Production", "Fertilizer Blends", "Extension Events", "Supported Enterprises", "Aggregation Centres", "Training"};
    private String exportname = "export";

    public ExportDatabaseCSVTask(Activity a, SugarRecord orm) {
        context = a;

        if (orm instanceof SeedProduction) {
            try {
                seedProduction = (SeedProduction) orm;
                seedProductionList = SeedProduction.listAll(SeedProduction.class);
                exportname = context.getString(R.string.form1);
            } catch (Exception e) {
                seedProduction = null;
                e.printStackTrace();
            }
        } else if (orm instanceof AggregationCenters) {
            try {
                aggregationCenters = (AggregationCenters) orm;
                aggregationCentersList = AggregationCenters.listAll(AggregationCenters.class);
                exportname = context.getString(R.string.form5);
            } catch (ClassCastException e) {
                aggregationCenters = null;
                e.printStackTrace();
            }
        } else if (orm instanceof SupportedEnterprise) {

            try {
                supportedEnterprise = (SupportedEnterprise) orm;
                supportedEnterpriseList = SupportedEnterprise.listAll(SupportedEnterprise.class);
                exportname = context.getString(R.string.form4);
            } catch (ClassCastException e) {
                supportedEnterprise = null;
                e.printStackTrace();
            }
        } else if (orm instanceof ParticipationInAgraEvent) {
            try {
                participationInAgraEvent = (ParticipationInAgraEvent) orm;
                participationInAgraEventList = ParticipationInAgraEvent.listAll(ParticipationInAgraEvent.class);
                exportname = context.getString(R.string.form3);
            } catch (ClassCastException e) {
                participationInAgraEvent = null;
                e.printStackTrace();
            }
        } else if (orm instanceof TrainingAttendance) {
            try {
                trainingAttendance = (TrainingAttendance) orm;
                trainingInfoList = TrainingInfo.listAll(TrainingInfo.class);
                exportname = context.getString(R.string.form6);
            } catch (ClassCastException e) {
                trainingAttendance = null;
                e.printStackTrace();
            }
        } else if (orm instanceof FertilizerBlending) {
            try {
                fertilizerBlending = (FertilizerBlending) orm;
                fertilizerBlendingList = FertilizerBlending.listAll(FertilizerBlending.class);
                exportname = context.getString(R.string.form2);
            } catch (ClassCastException e) {
                fertilizerBlending = null;
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPreExecute() {
        AndroidUtils.INSTANCE.showProgressDialog(context, "Exporting " + exportname + "...");
    }

    protected String doInBackground(final String... args) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "AgraForms/exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, exportname + ".csv");
        try {

            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            if (exportname.equalsIgnoreCase(this.selection[0])) {

                //Headers
                String arrStr1[] = {"item", "variety", "firsttimeinproduction", "plotsize", "quantityproduced", "quantitycertified", "quantitysold",
                        "unitprice", "currency", "entrydate", "collectorid", "collectorname"};
                csvWrite.writeNext(arrStr1);

                //data
                if (seedProductionList.size() > 0) {
                    for (SeedProduction seedProduction : seedProductionList) {
                        String arrStr[] = {seedProduction.getItem(), seedProduction.getVariety(), seedProduction.getFirsttimeinproduction()
                                , seedProduction.getPlotsize(), String.valueOf(seedProduction.getQuantityproduced()), String.valueOf(seedProduction.getQuantitycertified()), String.valueOf(seedProduction.getQuantitysold())
                                , String.valueOf(seedProduction.getUnitprice()), seedProduction.getCurrency(), AndroidUtils.INSTANCE.dateToFormattedString(seedProduction.getDatecreated(), "dd MMM, yyyy")
                                , seedProduction.getCollectorid(), seedProduction.getCollectorname()};
                        csvWrite.writeNext(arrStr);
                    }
                }

            } else if (exportname.equalsIgnoreCase(this.selection[1])) {

                //Headers
                String arrStr1[] = {"soillabname", "soillabtest", "numberofblends", "typeofagrasupport", "crops", "fertilizercompanies", "quantityproduced(MT)", "quantitysold(MT)", "collectorid", "collectorname", "entrydate"};
                csvWrite.writeNext(arrStr1);

                //data
                if (fertilizerBlendingList.size() > 0) {
                    for (FertilizerBlending blending : fertilizerBlendingList) {
                        String arrStr[] = {blending.getSoillabname(), blending.getSoillabtest(), String.valueOf(blending.getNumberofblends()),
                                blending.getTypeofagrasupport(), blending.getCrops(), blending.getFertilizercompanies(), String.valueOf(blending.getQuantityproduced()), String.valueOf(blending.getQuantitysold()), blending.getCollectorid(), blending.getCollectorname(), AndroidUtils.INSTANCE.dateToFormattedString(blending.getEntrydate(), "dd MMM, yyyy")};
                        csvWrite.writeNext(arrStr);
                    }
                }
            } else if (exportname.equalsIgnoreCase(this.selection[2])) {

                //Headers
                String arrStr1[] = {"extentionevent", "nocompleted", "farmergender", "chainactorgender", "collectorid", "collectorname", "entrydate"};
                csvWrite.writeNext(arrStr1);

                //data
                if (participationInAgraEventList.size() > 0) {
                    for (ParticipationInAgraEvent participation : participationInAgraEventList) {
                        String arrStr[] = {participation.getExtentionevent(), String.valueOf(participation.getNocompleted()), participation.getFarmergender(), participation.getChainactorgender(), participation.getCollectorid(), participation.getCollectorname(), AndroidUtils.INSTANCE.dateToFormattedString(participation.getEntrydate(), "dd MMM, yyyy")};
                        csvWrite.writeNext(arrStr);
                    }
                }
            } else if (exportname.equalsIgnoreCase(this.selection[3])) {

                //Headers
                String arrStr1[] = {"enterpriseName", "contacts", "agroDealerType", "seedProducer", "femaleOwnerStatus", "youthOwnerStatus", "fullTimeEmploymentGender", "casualEmploymentGender", "partTimeEmploymentGender", "ageGroup", "collectorid", "collectorName", "entrydate"};
                csvWrite.writeNext(arrStr1);

                //data
                if (supportedEnterpriseList.size() > 0) {
                    for (SupportedEnterprise enterprise : supportedEnterpriseList) {
                        String arrStr[] = {enterprise.getEnterpriseName(), enterprise.getContacts(), enterprise.getAgroDealerType(), enterprise.getSeedProducer(), enterprise.getFemaleOwnerStatus(), enterprise.getYouthOwnerStatus(), enterprise.getFullTimeEmploymentGender(), enterprise.getCasualEmploymentGender(), enterprise.getPartTimeEmploymentGender(), enterprise.getAgeGroup(), enterprise.getCollectorid(), enterprise.getCollectorName(), AndroidUtils.INSTANCE.dateToFormattedString(enterprise.getEntrydate(), "dd MMM, yyyy")};
                        csvWrite.writeNext(arrStr);
                    }
                }
            } else if (exportname.equalsIgnoreCase(this.selection[4])) {

                //Headers
                String arrStr1[] = {"storageFacilityName", "location", "newConstructionType", "refurbishedType", "warehouseType", "silosType", "picsbagsType", "storesType", "volume", "crop", "quantityStored", "handlingCost", "currency", "servedFarmers", "quantitySold", "averagePrice", "collectorid", "collectorName", "entrydate"};
                csvWrite.writeNext(arrStr1);

                //data
                if (aggregationCentersList.size() > 0) {
                    for (AggregationCenters centers : aggregationCentersList) {
                        String arrStr[] = {centers.getStorageFacilityName(), centers.getLocation(), centers.getNewConstructionType(), centers.getRefurbishedType(), centers.getWarehouseType(), centers.getSilosType(), centers.getPicsbagsType(), centers.getStoresType(), centers.getVolume(), centers.getCrop(), centers.getQuantityStored(), centers.getHandlingCost(), centers.getCurrency(), centers.getServedFarmers(), centers.getQuantitySold(), centers.getAveragePrice(), centers.getCollectorid(), centers.getCollectorName(), AndroidUtils.INSTANCE.dateToFormattedString(centers.getEntrydate(), "dd MMM, yyyy")};
                        csvWrite.writeNext(arrStr);
                    }
                }
            } else if (exportname.equalsIgnoreCase(this.selection[5])) {
                List<TrainingAttendance> trainingAttendanceList = new ArrayList<>();
                // TRAINING INFO Header
                String infoheader[] = {"theme", "disaggregationLevels", "period", "venue", "trainerName", "trainerContact", "event", "collectorid", "collectorname", "entrydate"};
                csvWrite.writeNext(infoheader);

                for (TrainingInfo info : trainingInfoList) {

                    //TRAINING INFO DATA
                    String infodata[] = {info.getTheme(), info.getDisaggregationLevels(), info.getPeriod(), info.getPeriod(), info.getVenue(), info.getTrainerName(), info.getTrainerContact(), info.getEvent(), info.getCollectorid(), info.getCollectorname(), AndroidUtils.INSTANCE.dateToFormattedString(info.getEntrydate(), "dd MMM, yyyy")};
                    csvWrite.writeNext(infodata);

                    trainingAttendanceList = TrainingAttendance.find(TrainingAttendance.class, "traineeid = ?", info.getTraineeid());

                    if (trainingAttendanceList.size() > 0) {
                        // TRAINING INFO Header
                        String attendanceheader[] = {"title", "firstname", "lastname", "gender", "participantType", "function", "institution", "region", "districts", "telephone", "email", "collectorid", "collectorname", "entrydate"};
                        csvWrite.writeNext(attendanceheader);

                        for (TrainingAttendance attendance : trainingAttendanceList) {
                            String attendancedata[] = {attendance.getTitle(), attendance.getFirstname(), attendance.getLastname(), attendance.getGender(), attendance.getParticipantType(), attendance.getFunction(), attendance.getInstitution(), attendance.getRegion(), attendance.getDistricts(), attendance.getTelephone(), attendance.getEmail(), attendance.getCollectorid(), attendance.getCollectorname(), AndroidUtils.INSTANCE.dateToFormattedString(attendance.getEntrydate(), "dd MMM, yyyy")};
                            csvWrite.writeNext(attendancedata);
                        }
                    }
                }


            }


            csvWrite.close();
            return "";
        } catch (IOException e) {
            Log.e("MainActivity", e.getMessage(), e);
            return "";
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPostExecute(final String success) {
        AndroidUtils.INSTANCE.dismissProgressDialog();

        if (success.isEmpty()) {
            Toasty.success(context, "Export successful!").show();
        } else {
            Toasty.error(context, "Export failed!").show();
        }
    }
}