package com.uclgroupgh.form.pojo;

import androidx.databinding.ObservableField;

/**
 * Created by Junior on 12/22/2017.
 */

public class IPSettingsPojo {
    public final ObservableField<String> ipaddress = new ObservableField<>("webservice.uclgroupgh.com");
    public final ObservableField<String> port = new ObservableField<>("8080");
    public final ObservableField<String> context = new ObservableField<>("uclservice/uclservice");
    public final ObservableField<String> protocol = new ObservableField<>("http");
}
