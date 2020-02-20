package com.uclgroupgh.form.form.dialogs


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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.ilhasoft.support.validation.Validator
import com.afollestad.materialdialogs.MaterialDialog
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.DialogCollectorBinding
import com.uclgroupgh.form.entities.CollectorInfo
import com.uclgroupgh.form.entities.Districts
import com.uclgroupgh.form.entities.Regions
import com.uclgroupgh.form.form.dialogs.previews.FormPreviews
import com.uclgroupgh.form.interfaces.PreviewClickListener
import com.uclgroupgh.form.pojo.ServerTransfer
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants
import com.uclgroupgh.form.utils.ListDialogFragment
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CollectorDialog : DialogFragment(), View.OnClickListener, PreviewClickListener,
    ListDialogFragment.OnListDialogItemSelect, SignaturePad.OnSignedListener {
    internal lateinit var binding: DialogCollectorBinding
    internal lateinit var validator: Validator
    internal lateinit var dialogFragment: ListDialogFragment
    internal lateinit var fm: FragmentManager
    internal lateinit var listitems: Array<String>
    internal var popupcomponent = arrayOf("country", "region", "district", "year", "quarter")
    internal var popuptitle = arrayOf(
        "Select Country",
        "Select Region",
        "Select District",
        "Select Year",
        "Select Quarter"
    )
    internal lateinit var collectorInfo: CollectorInfo
    internal lateinit var array: Array<String>
    internal lateinit var regions: List<Regions>
    internal lateinit var districtArray: Array<String>
    internal lateinit var contentUri: Uri
    private var mParam1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                closeDialog()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_collector, container, false)
        val view = binding.root
        validator = Validator(binding)
        binding.clearpad.setOnClickListener(this)
        binding.country.setOnClickListener(this)
        binding.region.setOnClickListener(this)
        binding.district.setOnClickListener(this)
        binding.year.setOnClickListener(this)
        binding.quarter.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.save.setOnClickListener(this)
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
            R.id.close -> closeDialog()
            R.id.clearpad -> binding.signaturePad.clear()
            R.id.country -> {
                listitems = resources.getStringArray(R.array.countries_array)
                showOptionDialog(listitems, popuptitle[0], popupcomponent[0])
            }
            R.id.region -> {
                listitems = loadRegions()
                showOptionDialog(listitems, popuptitle[1], popupcomponent[1])
            }
            R.id.district -> {
                listitems =
                    loadDistricts(getRegionCode(binding.region.text.toString().trim { it <= ' ' }))
                showOptionDialog(listitems, popuptitle[2], popupcomponent[2])
            }
            R.id.year -> {
                listitems = resources.getStringArray(R.array.years)
                showOptionDialog(listitems, popuptitle[3], popupcomponent[3])
            }
            R.id.quarter -> {
                listitems = resources.getStringArray(R.array.quarter)
                showOptionDialog(listitems, popuptitle[4], popupcomponent[4])
            }
            R.id.save -> savecollecrtorform()
        }
    }

    //    REMEMBER TO GENERATE COLLECTOR ID
    private fun savecollecrtorform() {
        if (validator.validate()) {
            if (!binding.signaturePad.isEmpty) {
                try {


                    collectorInfo = CollectorInfo()
                    collectorInfo.collectorname =
                        binding.surname.text.toString().trim { it <= ' ' } + " " + binding.othernames.text.toString().trim { it <= ' ' }
                    collectorInfo.country = binding.country.text.toString().trim { it <= ' ' }
                    collectorInfo.region = binding.region.text.toString().trim { it <= ' ' }
                    collectorInfo.project = binding.project.text.toString().trim { it <= ' ' }
                    collectorInfo.district = binding.district.text.toString().trim { it <= ' ' }
                    collectorInfo.grantee = binding.grantee.text.toString().trim { it <= ' ' }
                    collectorInfo.year = binding.year.text.toString().trim { it <= ' ' }
                    collectorInfo.quarter = binding.quarter.text.toString().trim { it <= ' ' }
                    collectorInfo.entrydate = Date()
                    collectorInfo.imei = AndroidUtils.getIMEI(context)
                    collectorInfo.macaddress = AndroidUtils.getMacAddress(context)
                    collectorInfo.agentname =
                        AndroidUtils.getPreferenceData(context!!, Constants.AGENTNAME, "")
                    collectorInfo.uniqueuid = AndroidUtils.uuid
                    collectorInfo.eventType = mParam1
                    collectorInfo.collectorid = getcollectorid(
                        collectorInfo.region!!,
                        collectorInfo.district!!,
                        collectorInfo.collectorname!!
                    )

                    val signatureBitmap = binding.signaturePad.transparentSignatureBitmap
                    if (addJpgSignatureToGallery(signatureBitmap)) {

                        collectorInfo.signature = AndroidUtils.uriToByteArray(contentUri, context!!)

                        //                        Toasty.success(getContext(), "Signature saved into the Gallery").show();
                    } else {
                        Toasty.error(context!!, "Unable to store the signature").show()
                    }

                    collectorInfo.save()
                    fragmentManager?.let {
                        FormPreviews.newInstance(1, collectorInfo.uniqueuid!!, this)
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

    private fun getcollectorid(region: String, district: String, collectorname: String): String {
        var rc = "XX"
        var dc = "XXX"
        if (rc.isNotEmpty()) {
            rc = getRegionCode(region).toUpperCase()
            dc = getDistrictCode(district, rc).toUpperCase()
        }

        val timestamp = System.currentTimeMillis().toString()
        return "COL-" + collectorname.toUpperCase()[0] +
                collectorname.toUpperCase()[5] + "-" +
                timestamp.substring(0, 3) + "-" +
                timestamp.substring(3, 7) + "-" +
                timestamp.substring(7, 11) + "-" +
                timestamp.substring(11) + "-" +
                rc + "-" +
                dc
    }

    override fun onListItemSelected(selection: String) {
        val componentname = dialogFragment.arguments!!.get("componentname") as String
        when {
            componentname.equals(popupcomponent[0], ignoreCase = true) -> binding.country.setText(
                selection
            )
            componentname.equals(popupcomponent[1], ignoreCase = true) -> {
                binding.region.setText(selection)
                binding.district.setText("")

            }
            componentname.equals(popupcomponent[2], ignoreCase = true) -> binding.district.setText(
                selection
            )
            componentname.equals(popupcomponent[3], ignoreCase = true) -> binding.year.setText(
                selection
            )
            componentname.equals(popupcomponent[4], ignoreCase = true) -> binding.quarter.setText(
                selection
            )
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

    fun getDistrictCode(district: String, regioncode: String): String {
        val districts: List<Districts>
        try {
            districts = Districts.find(
                Districts::class.java,
                "district=? and regioncode=?",
                district,
                regioncode
            )
            return if (districts.isNotEmpty()) {
                districts[0].getDistrictcode()
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun uploadToServer(info: CollectorInfo) {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                var outcome = false

                override fun onPreExecute() {
                    //some logic logic logic
                }

                override fun doInBackground(vararg params: Void): Boolean? {
                    try {
                        val transfer = ServerTransfer()
                        transfer.collectorInfo = info
                        val jsonstring = Gson().toJson(transfer)
                        val uploadId = AndroidUtils.uuid
                        val uploadfilepath = AndroidUtils.writeToFile(jsonstring, "$uploadId.txt")
                        AndroidUtils.uploadFileToServer(context)

                        outcome = true
                    } catch (e: Exception) {
                        println("Exception" + e.message)
                        e.printStackTrace()
                    }

                    return outcome
                }

                override fun onPostExecute(outcome: Boolean) {
                    //some logic logic logic
                    if (outcome) {
                        dismiss()
                    }
                }


            }
        asyncTask.execute()
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

    private fun getAlbumStorageDir(albumName: String): File {
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
            uploadToServer(collectorInfo)
            Toasty.success(context!!, "Form saved").show()
            mParam1?.let {
                fragmentManager?.let { it1 ->
                    AllCollectorsDialog.newInstance(it).show(
                        it1, AllCollectorsDialog.TAG
                    )
                }
            }
        } else
            collectorInfo.delete()
        dialog.dismiss()
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        var TAG = "FullScreenDialog"

        fun newInstance(eventtype: String): CollectorDialog {
            val dialog = CollectorDialog()
            val args = Bundle()
            args.putString(ARG_PARAM1, eventtype)
            dialog.arguments = args
            return dialog
        }
    }
}
