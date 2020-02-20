package com.uclgroupgh.form.form.dialogs.training


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.ilhasoft.support.validation.Validator
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.files.folderChooser
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.orm.SugarRecord
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogTrainingAttendanceBinding
import com.uclgroupgh.form.entities.*
import com.uclgroupgh.form.entities.server.*
import com.uclgroupgh.form.form.dialogs.previews.FormPreviews
import com.uclgroupgh.form.fragments.ImportParticipantsBottomsheet
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants
import com.uclgroupgh.form.utils.ListDialogFragment
import es.dmoral.toasty.Toasty
import org.apache.commons.io.FilenameUtils
import java.io.*
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TrainingAttendanceDialog : DialogFragment(), View.OnClickListener, PreviewClickListener,
    SignaturePad.OnSignedListener, ListDialogFragment.OnListDialogItemSelect, ImportParticipantsBottomsheet.OnImportParticipantsListener {
    override fun onFinishImport(attendanceList: MutableList<TrainingAttendance>) {
        trainingAttendanceList = attendanceList
        fragmentManager?.let {
            FormPreviews.newInstance(7, trainingAttendanceList[0].traineeid!!, this)
                .show(it, FormPreviews.TAG)
        }
    }

    internal lateinit var trainingAttendanceList: MutableList<TrainingAttendance>
    internal lateinit var binding: DialogTrainingAttendanceBinding
    internal lateinit var validator: Validator
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var listitems: Array<String>
    internal lateinit var array: Array<String>
    internal lateinit var regions: List<Regions>
    internal lateinit var districtArray: Array<String>
    internal var biolist: Array<Farmerbiometrics>? = null
    internal var aggregatorInfolist: Array<AggregatorInfo>? = null
    internal var inputDealerInfolist: Array<InputDealerInfo>? = null
    internal var offtakerInfoList: Array<OfftakerInfo>? = null
    internal var basiclist: Array<Basicfarmerinfo>? = null
    internal lateinit var contentUri: Uri
    internal var cancelled = false
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var farmergender: String? = null
    private var participanttype: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_training_attendance, container, false)
        val view = binding.root
        validator = Validator(binding)
        trainingAttendanceList = ArrayList()
        binding.add.text =
            resources.getString(R.string.addbtn, trainingAttendanceList.size.toString())
        binding.male.isClickable = false
        binding.female.isClickable = false
        binding.fbo.isClickable = false
        binding.sme.isClickable = false
        binding.genderLayout.setOnClickListener(this)
        binding.genderLayout2.setOnClickListener(this)
        binding.fboLayout.setOnClickListener(this)
        binding.smeLayout.setOnClickListener(this)
        binding.region.setOnClickListener(this)
        binding.district.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.add.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.title.setOnClickListener(this)
        binding.clearpad.setOnClickListener(this)

        binding.importParticipants.setOnClickListener {
            context?.let {
                MaterialDialog(it).show {
                    folderChooser { dialog, folder ->
                        Log.e("FOLDER PATH", folder.absolutePath)
                        LoadFileFromFolder().execute(folder.name, "", "")
                    }
                }
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog!!.setCancelable(false)
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.attributes.windowAnimations = R.style.traininganimate
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.clearpad -> binding.signaturePad.clear()
            R.id.gender_layout2 -> if (binding.female.isChecked) {
                binding.female.isChecked = false
                binding.male.isChecked = true
                farmergender = "male"
            } else {
                binding.female.isChecked = true
                binding.male.isChecked = false
                farmergender = "female"
            }
            R.id.gender_layout -> if (binding.male.isChecked) {
                binding.male.isChecked = false
                binding.female.isChecked = true
                farmergender = "female"
            } else {
                binding.male.isChecked = true
                binding.female.isChecked = false
                farmergender = "male"
            }
            R.id.sme_layout -> if (binding.sme.isChecked) {
                binding.sme.isChecked = false
                binding.fbo.isChecked = true
                participanttype = "Farmer/FBO"
            } else {
                binding.sme.isChecked = true
                binding.fbo.isChecked = false
                participanttype = "SME's"
            }
            R.id.fbo_layout -> if (binding.fbo.isChecked) {
                binding.fbo.isChecked = false
                binding.sme.isChecked = true
                participanttype = "SME's"
            } else {
                binding.fbo.isChecked = true
                binding.sme.isChecked = false
                participanttype = "Farmer/FBO"
            }
            R.id.title -> {
                listitems = resources.getStringArray(R.array.title)
                showOptionDialog(listitems, "Select Title", "title")
            }
            R.id.close -> closeDialog()
            R.id.add -> addform()
            R.id.save -> saveform()
            R.id.region -> {
                listitems = loadRegions()
                showOptionDialog(listitems, "Select Region", "region")
            }
            R.id.district -> {
                listitems =
                    loadDistricts(getRegionCode(binding.region.text.toString().trim { it <= ' ' }))
                showOptionDialog(listitems, "Select District", "district")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                closeDialog()
            }
        }
    }

    private fun closeDialog() {
        context?.let {
            MaterialDialog(it).cornerRadius(16f).show {
                title(R.string.close_dialog_title)
                message(R.string.close_dialog)
                positiveButton(R.string.dialog_positive) { dialog?.dismiss() }
                negativeButton(R.string.dialog_negative)
                cancelable(false)
            }
        }
    }

    private fun addform() {
        if (validator.validate()) {
            if (!binding.signaturePad.isEmpty) {
                try {
                    val attendance = TrainingAttendance()
                    attendance.traineeid = mParam1
                    attendance.title = binding.title.text.toString().trim { it <= ' ' }
                    attendance.firstname = binding.othername.text.toString().trim { it <= ' ' }
                    attendance.lastname = binding.surname.text.toString().trim { it <= ' ' }
                    attendance.gender = farmergender
                    attendance.participantType = participanttype
                    attendance.function = binding.function.text.toString().trim { it <= ' ' }
                    attendance.institution = binding.institution.text.toString().trim { it <= ' ' }
                    attendance.region = binding.region.text.toString().trim { it <= ' ' }
                    attendance.districts = binding.district.text.toString().trim { it <= ' ' }
                    attendance.telephone = binding.contact.text.toString().trim { it <= ' ' }
                    attendance.email = binding.email.text.toString().trim { it <= ' ' }
                    attendance.collectorid = mParam2
                    attendance.collectorname = AndroidUtils.getCollectorname(mParam2)
                    attendance.agentname =
                        AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                    attendance.imei = AndroidUtils.getIMEI(context)
                    attendance.macaddress = AndroidUtils.getMacAddress(context)
                    attendance.uniqueuid = AndroidUtils.uuid
                    attendance.entrydate = Date()
                    val signatureBitmap = binding.signaturePad.transparentSignatureBitmap
                    if (addJpgSignatureToGallery(signatureBitmap)) {

                        attendance.signature = AndroidUtils.uriToByteArray(contentUri, context!!)

                        Toasty.success(context!!, "Signature saved into the Gallery").show()
                    } else {
                        Toasty.error(context!!, "Unable to store the signature").show()
                    }

                    attendance.save()
                    trainingAttendanceList.add(attendance)
                    binding.add.text =
                        resources.getString(R.string.addbtn, trainingAttendanceList.size.toString())
                    resetform()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else
                Toasty.error(context!!, "Please Sign").show()
        } else
            Toasty.error(context!!, "An error occurred").show()
    }

    fun uploadToServer() {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                internal var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {

                        prepareuploadfiles()
                        AndroidUtils.uploadFileToServer(context)
                        trainingAttendanceList.clear()

                        outcome = true
                    } catch (e: Exception) {
                        println("Exception" + e.message)
                        e.printStackTrace()
                    }

                    return outcome
                }

                override fun onPostExecute(outcome: Boolean?) {

                }


            }
        asyncTask.execute()
    }

    fun prepareuploadfiles() {
        if (trainingAttendanceList.size > 0) {
            val list = TrainingInfo.find(
                TrainingInfo::class.java,
                "traineeid = ?",
                trainingAttendanceList[0].traineeid
            )
            var transfer = ServerTransfer()
            transfer.trainingInfo = list[0]
            var jsonstring = Gson().toJson(transfer)
            var uploadId = AndroidUtils.uuid
            var uploadfilepath = AndroidUtils.writeToFile(jsonstring, uploadId + ".txt")
            for (trainingAttendance in trainingAttendanceList) {
                try {
                    val forms = FilledForms()
                    forms.category = mParam1
                    forms.subcategory = mParam2
                    forms.datecreated = Date()
                    forms.uniqueuid = AndroidUtils.uuid
                    forms.imei = AndroidUtils.getIMEI(context)
                    forms.macaddress = AndroidUtils.getMacAddress(context)
                    forms.save()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                transfer = ServerTransfer()
                transfer.trainingAttendance = trainingAttendance
                jsonstring = Gson().toJson(transfer)
                uploadId = AndroidUtils.uuid
                uploadfilepath = AndroidUtils.writeToFile(jsonstring, uploadId + ".txt")
            }
        }
    }

    fun resetform() {
        binding.surname.setText("")
        binding.othername.setText("")
        binding.title.setText("")
        binding.contact.setText("")
        binding.region.setText("")
        binding.district.setText("")
        binding.function.setText("")
        binding.institution.setText("")
        binding.email.setText("")
        binding.signaturePad.clear()
        binding.male.isChecked = false
        binding.female.isChecked = false
        binding.sme.isChecked = false
        binding.fbo.isChecked = false
    }

    //method to save farmers information
    fun saveFarmers(
        farmerid: String?,
        surname: String?,
        othername: String?,
        placeofbirth: String?,
        dateofbirth: Date?,
        age: Int?,
        gender: String?,
        hometown: String?,
        address: String?,
        district: String?,
        region: String?,
        country: String?,
        idcardtype: String?,
        idcardno: String?,
        levelofeducation: String?,
        religion: String?,
        maritalstatus: String?,
        nextofkin: String?,
        relation: String?,
        qfarmergroup: String?,
        farmergroup: String?,
        qcooperative: String?,
        cooperative: String?,
        qincomesource: String?,
        incomesource: String?,
        qspecialty: String?,
        specialty: String?,
        certification: String?,
        funding: String?,
        imagelink: String?,
        community: String?,
        tel: String?,
        email: String?,
        uniqueuid: String?,
        postbox: String?,
        synstatus: String?,
        datecreated: Date?,
        serverid: Int?,
        macaddress: String?,
        imei: String?,
        agentname: String?,
        agentid: String?
    ) {
        try {
            var farmers: Farmers? = null
            val list = SugarRecord.find(
                Farmers::class.java,
                "uniqueuid=?",
                arrayOf(uniqueuid),
                null,
                null,
                "1"
            )

            farmers = if (list != null && list.size > 0)
                list[0]
            else
                Farmers()

            if (farmers != null) {
                farmers.macaddress = (macaddress)
                farmers.imei = (imei)
                farmers.agentname = (agentname)
                farmers.agentid = (agentid)
                farmers.serverid = (serverid)
                farmers.datecreated = (datecreated)
                farmers.synstatus = ("")
                farmers.age = (age)
                farmers.postbox = (postbox)
                farmers.email = (email)
                farmers.uniqueuid = (uniqueuid)
                farmers.farmerid = (farmerid)
                farmers.surname = (surname)
                farmers.othername = (othername)
                farmers.placeofbirth = (placeofbirth)
                farmers.dateofbirth = (dateofbirth)
                farmers.gender = (gender)
                farmers.hometown = (hometown)
                farmers.address = (address)
                farmers.district = (district)
                farmers.region = (region)
                farmers.country = (country)
                farmers.idcardtype = (idcardtype)
                farmers.idcardno = (idcardno)
                farmers.levelofeducation = (levelofeducation)
                farmers.religion = (religion)
                farmers.maritalstatus = (maritalstatus)
                farmers.nextofkin = (nextofkin)
                farmers.relation = (relation)
                farmers.qfarmergroup = (qfarmergroup)
                farmers.farmergroup = (farmergroup)
                farmers.qcooperative = (qcooperative)
                farmers.cooperative = (cooperative)
                farmers.qincomesource = (qincomesource)
                farmers.incomesource = (incomesource)
                farmers.qspecialty = (qspecialty)
                farmers.specialty = (specialty)
                farmers.certification = (certification)
                farmers.funding = (funding)
                farmers.imagelink = (imagelink)
                farmers.community = (community)
                farmers.tel = (tel)
                farmers.save()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //method to save userbiometrics
    fun saveUserbiometrics(
        farmerid: String?,
        picture: ByteArray?,
        signature: ByteArray?,
        height: String?,
        eyecolor: String?,
        createdDate: Date?,
        placeOfRegistered: String?,
        datecreatedserver: Date?,
        uniqueuid: String?,
        synstatus: String?,
        datecreated: Date?,
        serverid: Int?,
        macaddress: String?,
        imei: String?,
        agentname: String?,
        agentid: String?
    ) {
        try {
            if (uniqueuid != null) {
                var bio: Userbiometrics? = null

                val list = SugarRecord.find(
                    Userbiometrics::class.java,
                    "uniqueuid=?",
                    arrayOf(uniqueuid),
                    null,
                    null,
                    "1"
                )

                bio = if (list != null && list.size > 0)
                    list[0]
                else
                    Userbiometrics()

                if (bio != null) {
                    bio.macaddress = (macaddress)

                    bio.imei = (imei)
                    bio.agentname = (agentname)
                    bio.agentid = (agentid)
                    bio.serverid = (serverid)
                    bio.datecreated = (datecreated)
                    bio.synstatus = ("")
                    bio.datecreatedserver = (datecreatedserver)
                    bio.uniqueuid = (uniqueuid)
                    bio.farmerid = (farmerid)
                    bio.picture = (picture)
                    bio.signature = (signature)
                    bio.height = (height)
                    bio.eyecolor = (eyecolor)
                    bio.createdDate = (createdDate)
                    bio.placeOfRegistered = (placeOfRegistered)
                    bio.save()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun saveOffTakers() {}

    fun saveAggregators() {}

    fun saveInputDealers() {}

    inner class LoadFileFromFolder : AsyncTask<String?, String?, String?>() {
        lateinit var progressDialog: MaterialDialog
        override fun onPreExecute() {
            super.onPreExecute()

            progressDialog = MaterialDialog(infodialog!!.context)
                .cornerRadius(16f)
                .cancelable(false)
                .customView(R.layout.item_progressbar, scrollable = true)

            val customView = progressDialog.getCustomView()
            val tv: TextView = customView.findViewById(R.id.progress_text)
            tv.text = infodialog!!.context.resources.getString(R.string.please_wait)
            progressDialog.show()
        }

        override fun doInBackground(vararg f_url: String?): String? {

            try {
                val files: List<File> = AndroidUtils.getListOfJsonFiles(File(Environment.getExternalStorageDirectory().toString() + File.separator + f_url[0]))
                for (filename in files) {
                    readJSONFromDirectory(filename)
                }

            } catch (e: Exception) {
                Log.e("Error: ", e.message)
            }

            return null
        }

        override fun onPostExecute(file_url: String?) {
            // dismiss the dialog after the file was downloaded
            if (progressDialog != null) {
                progressDialog.dismiss()
                val sheet = mParam1?.let { mParam2?.let { it1 -> ImportParticipantsBottomsheet.newInstance(param1 = it, param2 = it1) } }
                fragmentManager?.let { sheet!!.show(it, sheet.tag) }
                sheet!!.setOnImportParticipantsListener(this@TrainingAttendanceDialog)
            }
        }
    }

    fun readJSONFromDirectory(filename: File) {
        println("File name: $filename")
        val basefilename = FilenameUtils.getBaseName(filename.absolutePath)

        when {

            basefilename.startsWith("farmers") -> try {
                JsonReader(InputStreamReader(FileInputStream(filename), StandardCharsets.UTF_8)).use { jsonReader ->
                    val gson =
                        GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer())
                            .create()
                    try {
                        basiclist = gson!!.fromJson<Any>(jsonReader, Array<Basicfarmerinfo>::class.java) as Array<Basicfarmerinfo>?
                        for (basic in basiclist!!) {
                            //System.out.println("Farmer: "+basic.toString());
                            if (basic.othername == null) {
                                saveFarmers(
                                    basic.farmerid,
                                    basic.surname,
                                    basic.othernames,
                                    basic.placeofbirth,
                                    basic.dateofbirth,
                                    0,
                                    basic.gender,
                                    basic.hometown,
                                    basic.address,
                                    basic.district,
                                    basic.region,
                                    basic.country,
                                    null,
                                    null,
                                    basic.levelofeducation,
                                    basic.religion,
                                    basic.maritalstatus,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    basic.community,
                                    basic.tel,
                                    basic.email,
                                    basic.uniqueuid,
                                    basic.postbox,
                                    "0",
                                    basic.datecreated,
                                    basic.id,
                                    basic.macaddress,
                                    basic.imei,
                                    basic.agentname,
                                    basic.agentid
                                )
                            } else {
                                saveFarmers(
                                    basic.farmerid,
                                    basic.surname,
                                    basic.othername,
                                    basic.placeofbirth,
                                    basic.dateofbirth,
                                    0,
                                    basic.gender,
                                    basic.hometown,
                                    basic.address,
                                    basic.district,
                                    basic.region,
                                    basic.country,
                                    null,
                                    null,
                                    basic.levelofeducation,
                                    basic.religion,
                                    basic.maritalstatus,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    basic.community,
                                    basic.tel,
                                    basic.email,
                                    basic.uniqueuid,
                                    basic.postbox,
                                    "0",
                                    basic.datecreated,
                                    basic.serverid,
                                    basic.macaddress,
                                    basic.imei,
                                    basic.agentname,
                                    basic.agentid
                                )
                            }
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }


                    //                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            basefilename.startsWith("userbiometrics") -> try {
                JsonReader(
                    InputStreamReader(
                        FileInputStream(filename),
                        StandardCharsets.UTF_8
                    )
                ).use { jsonReader ->
                    val gson = Gson()

                    try {
                        biolist = gson.fromJson<Any>(
                            jsonReader,
                            Array<Farmerbiometrics>::class.java
                        ) as Array<Farmerbiometrics>?
                        for (bio in biolist!!) {
                            if (bio.picture!!.isNotEmpty())
                                saveUserbiometrics(
                                    bio.farmerid,
                                    bio.picture,
                                    bio.signature,
                                    null,
                                    null,
                                    bio.datecreated,
                                    bio.placeofregistered,
                                    null,
                                    bio.uniqueuid,
                                    "0",
                                    bio.datecreated,
                                    bio.id,
                                    bio.macaddress,
                                    bio.imei,
                                    bio.agentname,
                                    bio.agentid
                                )
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    //                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            basefilename.startsWith("aggregator") -> try {
                JsonReader(
                    InputStreamReader(
                        FileInputStream(filename),
                        StandardCharsets.UTF_8
                    )
                ).use { jsonReader ->
                    val gson = Gson()

                    try {
                        aggregatorInfolist = gson.fromJson<Any>(jsonReader, Array<AggregatorInfo>::class.java) as Array<AggregatorInfo>?
                        for (bio in aggregatorInfolist!!) {
                            saveAggregators()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    //                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            basefilename.startsWith("inputdealer") -> try {
                JsonReader(
                    InputStreamReader(
                        FileInputStream(filename),
                        StandardCharsets.UTF_8
                    )
                ).use { jsonReader ->
                    val gson = Gson()

                    try {
                        inputDealerInfolist = gson.fromJson<Any>(
                            jsonReader,
                            Array<InputDealerInfo>::class.java
                        ) as Array<InputDealerInfo>?
                        for (bio in inputDealerInfolist!!) {
                            saveInputDealers()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    //                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            basefilename.startsWith("offtaker") -> try {
                JsonReader(
                    InputStreamReader(
                        FileInputStream(filename),
                        StandardCharsets.UTF_8
                    )
                ).use { jsonReader ->
                    val gson = Gson()

                    try {
                        offtakerInfoList = gson.fromJson<Any>(
                            jsonReader,
                            Array<OfftakerInfo>::class.java
                        ) as Array<OfftakerInfo>?
                        for (bio in offtakerInfoList!!) {
                            saveOffTakers()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    //                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private inner class DateDeserializer : JsonDeserializer<Date> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            jsonElement: JsonElement,
            typeOF: Type,
            context: JsonDeserializationContext
        ): Date {
            for (format in DATE_FORMATS) {
                try {
                    return SimpleDateFormat(format, Locale.US).parse(jsonElement.asString)
                } catch (e: ParseException) {
                }

            }
            throw JsonParseException(
                "Unparseable date: \"" + jsonElement.asString
                        + "\". Supported formats: " + DATE_FORMATS.contentToString()
            )
        }
    }

    private fun saveform() {
        if (trainingAttendanceList.size > 0) {
            if (cancelled) {
                for (attendance in trainingAttendanceList) {
                    attendance.save()
                }
            }
            fragmentManager?.let {
                FormPreviews.newInstance(7, trainingAttendanceList[0].traineeid!!, this)
                    .show(it, FormPreviews.TAG)
            }
        } else {
            if (validator.validate()) {
                if (!binding.signaturePad.isEmpty) {
                    try {
                        val attendance = TrainingAttendance()
                        attendance.traineeid = mParam1
                        attendance.title = binding.title.text.toString().trim { it <= ' ' }
                        attendance.firstname = binding.othername.text.toString().trim { it <= ' ' }
                        attendance.lastname = binding.surname.text.toString().trim { it <= ' ' }
                        attendance.gender = farmergender
                        attendance.participantType = participanttype
                        attendance.function = binding.function.text.toString().trim { it <= ' ' }
                        attendance.institution =
                            binding.institution.text.toString().trim { it <= ' ' }
                        attendance.region = binding.region.text.toString().trim { it <= ' ' }
                        attendance.districts = binding.district.text.toString().trim { it <= ' ' }
                        attendance.telephone = binding.contact.text.toString().trim { it <= ' ' }
                        attendance.email = binding.email.text.toString().trim { it <= ' ' }
                        attendance.collectorid = mParam2
                        attendance.collectorname = AndroidUtils.getCollectorname(mParam2)
                        attendance.agentname =
                            AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                        attendance.imei = AndroidUtils.getIMEI(context)
                        attendance.macaddress = AndroidUtils.getMacAddress(context)
                        attendance.uniqueuid = AndroidUtils.uuid
                        attendance.entrydate = Date()
                        val signatureBitmap = binding.signaturePad.transparentSignatureBitmap
                        if (addJpgSignatureToGallery(signatureBitmap)) {

                            attendance.signature =
                                AndroidUtils.uriToByteArray(contentUri, context!!)

                            Toasty.success(context!!, "Signature saved into the Gallery").show()
                        } else {
                            Toasty.error(context!!, "Unable to store the signature").show()
                        }


                        trainingAttendanceList.add(attendance)
                        attendance.save()
                        fragmentManager?.let {
                            FormPreviews.newInstance(7, attendance.traineeid!!, this)
                                .show(it, FormPreviews.TAG)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else
                    Toasty.error(context!!, "Please Sign").show()
            } else
                Toasty.error(context!!, "An error occurred").show()
        }
    }

    override fun onListItemSelected(selection: String) {
        val componentname = dialogFragment.arguments!!.get("componentname") as String
        if (componentname.equals("region", ignoreCase = true)) {
            binding.region.setText(selection)
            binding.district.setText("")

        } else if (componentname.equals("district", ignoreCase = true)) {
            binding.district.setText(selection)
        } else if (componentname.equals("title", ignoreCase = true)) {
            binding.title.setText(selection)
        }
    }

    private fun showOptionDialog(array: Array<String>, title: String, componentname: String) {
        fm = childFragmentManager
        dialogFragment = ListDialogFragment(this, array, title, componentname)
        dialogFragment.show(fm, componentname)
    }

    //method to retrieve region from the database
    fun loadRegions(): Array<String> {
        try {
            regions = Regions.listAll(Regions::class.java)
            array = Array(regions.size) { "" }
            for (i in regions.indices) {
                array[i] = regions[i].getRegion()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return array
    }

    //method to select district based on region's selection
    fun loadDistricts(regioncode: String): Array<String> {
        try {
            val districts = Districts.find(Districts::class.java, "regioncode=?", regioncode)
            districtArray = Array(districts.size) { "" }
            for (i in districts.indices) {
                districtArray[i] = districts[i].getDistrict()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return districtArray
    }

    fun getRegionCode(region: String): String {
        try {
            regions = Regions.find(Regions::class.java, "region=?", region)
            return if (regions.isNotEmpty()) {
                regions[0].getRegioncode()
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getAlbumStorageDir(albumName: String): File {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        activity!!.sendBroadcast(mediaScanIntent)
    }

    override fun onStartSigning() {

    }

    override fun onSigned() {

    }

    override fun onClear() {
        Toasty.success(context!!, "Pad Cleared").show()
    }

    override fun onPreviewClickListener(save: Boolean, dialog: Dialog) {
        if (save) {
            Toasty.success(context!!, "Form saved").show()
            dismiss()
            if (infodialog != null) {
                infodialog!!.dismiss()
            }
            uploadToServer()
        } else {
            for (attendance in trainingAttendanceList) {
                cancelled = true
                attendance.delete()
            }
        }
        dialog.dismiss()
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        var TAG = "FullScreenDialog"
        private val DATE_FORMATS = arrayOf(
            "MMM dd, yyyy HH:mm:ss a",
            "MMM dd, yyyy HH:mm:ss",
            "MMM dd, yyyy",
            "MMM d, yyyy",
            "HH:mm:ss a"
        )
        internal var infodialog: Dialog? = null
        fun newInstance(
            traineeid: String,
            collectorid: String,
            indialog: Dialog
        ): TrainingAttendanceDialog {
            val dialog = TrainingAttendanceDialog()
            infodialog = indialog
            val args = Bundle()
            args.putString(ARG_PARAM1, traineeid)
            args.putString(ARG_PARAM2, collectorid)
            dialog.arguments = args
            return dialog
        }
    }
}
