package com.uclgroupgh.form.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.orm.SugarRecord
import com.uclgroupgh.form.R
import com.uclgroupgh.form.entities.CollectorInfo
import com.uclgroupgh.form.entities.Userbiometrics

import net.gotev.uploadservice.MultipartUploadRequest
import net.gotev.uploadservice.UploadNotificationConfig

import org.apache.commons.io.FilenameUtils

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.Collections
import java.util.Currency
import java.util.Date
import java.util.GregorianCalendar
import java.util.LinkedList
import java.util.Locale
import java.util.TreeMap
import java.util.UUID

import okhttp3.OkHttpClient
import java.nio.charset.Charset

/**
 * Created by Junior on 11/24/2017.
 */

object AndroidUtils {
    internal var progressDialog: ProgressDialog? = null
    internal lateinit var ss2: SpannableString
    internal var client = OkHttpClient()
    internal lateinit var fileList: List<File>
    internal var uploadid: String = ""

    /**
     * Generate uniqueuid random number
     *
     * @return
     */
    val uuid: String
        get() = UUID.randomUUID().toString()

    val filename: String
        get() {
            val file = File(Environment.getExternalStorageDirectory().path, "MyFolder/Images")
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"

        }


    val currencies: List<String>
        get() {
            val list = ArrayList<String>()
            val currencies = availableCurrencies
            for (country in currencies.keys) {
                list.add(currencies[country] ?: error(""))
            }

            return list
        }

    private// We use TreeMap so that the order of the data in the map sorted
    // based on the country name.
    // when the locale is not supported
    val availableCurrencies: Map<String, String>
        get() {
            val locales = Locale.getAvailableLocales()
            val currencies = TreeMap<String, String>()
            for (locale in locales) {
                try {
                    currencies[locale.displayCountry] = Currency.getInstance(locale).currencyCode
                } catch (e: Exception) {
                }

            }
            return currencies
        }

    fun savePreferenceData(activity: Activity, key: String, value: String?) {
        val prefs = activity.getSharedPreferences(Constants.mypreference, Context.MODE_PRIVATE)
        prefs.edit().putString(key, value).apply()
    }

    fun getPreferenceData(con: Context, variable: String, defaultValue: String): String? {
        val prefs = con.getSharedPreferences(Constants.mypreference, Context.MODE_PRIVATE)
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
        return prefs.getString(variable, defaultValue)
    }

    fun removePreferenceValue(activity: Activity, key: String) {
        val prefs = activity.getSharedPreferences(Constants.mypreference, Context.MODE_PRIVATE)
        prefs.edit().remove(key).apply()
    }

    fun clearPreference(con: Context): Boolean {
        return con.getSharedPreferences(Constants.mypreference, Context.MODE_PRIVATE).edit()
            .clear().commit()
    }


    fun uploadDirectoryPath(): String {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "uploadfolder"
    }

    fun csvDirectoryPath(): String {
        //return Environment.getExternalStorageDirectory() + File.separator + "csvdatafolder";
        val folder =
            File(Environment.getExternalStorageDirectory().toString() + File.separator + "csvdatafolder")
        if (!folder.exists()) {
            folder.mkdirs()
            return folder.absolutePath
        } else {
            return folder.absolutePath
        }
    }

    fun cropDirectoryPath(): String {
        //return Environment.getExternalStorageDirectory() + File.separator + "csvdatafolder";
        val folder =
            File(Environment.getExternalStorageDirectory().toString() + File.separator + "cropped")
        if (!folder.exists()) {
            folder.mkdirs()
            return folder.absolutePath
        } else {
            return folder.absolutePath
        }
    }

    /**
     * method to upload files to the server
     */
    fun uploadFileToServer(context: Context?) {

        val folder = File(uploadDirectoryPath())

        if (!folder.exists()) {
            folder.mkdirs()
            fileList = getListOfFiles(folder)
        } else {
            fileList = getListOfFiles(folder)
        }
        if (fileList.isNotEmpty()) {
            for (file in fileList) {
                try {
                    uploadid = FilenameUtils.getBaseName(file.absolutePath)
                    //Creating a multi part request
                    MultipartUploadRequest(
                        context, uploadid, getPreferenceData(
                            context!!, Constants.IPCOMPLETEPREF, Constants.SERVER_URL
                        )!!
                    )
                        .addFileToUpload(file.absolutePath, "file") //Adding file
                        //.addParameter("name", "filename") //Adding text parameter to the
                        // request
                        .setNotificationConfig(
                            UploadNotificationConfig()
                                .setTitleForAllStatuses("CMS File Upload")
                                .setIconForAllStatuses(R.mipmap.ic_launcher)
                                //                            .setIconColorForAllStatuses(R.drawable.cocoa)
                                .setIconColorForAllStatuses(R.color.text_color)
                            /*.setLargeIconForAllStatuses(BitmapFactory.decodeResource(context
                            .getResources(), R.drawable.cocoa))*/
                        )
                        //.setMaxRetries(4)
                        .setAutoDeleteFilesAfterSuccessfulUpload(false)
                        .startUpload() //Starting the upload

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    /**
     * Convert Bitmap image to byte array
     *
     * @param bitmap
     * @return
     */
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }


    fun byteArrayToBase64(barray: ByteArray): String {
        return Base64.encodeToString(barray, Base64.DEFAULT)
    }

    fun base64ToByteArray(base64string: String): ByteArray {
        return Base64.decode(base64string.toByteArray(), Base64.DEFAULT)
    }

    /**
     * method to set image in imageview
     *
     * @param imageView
     * @param imagebyte
     */
    fun setImageViewResource(imageView: ImageView, imagebyte: ByteArray) {

        Glide.with(imageView.context)
            .asBitmap()
            .load(imagebyte)
            .into(imageView)
    }

    /**
     * method to convert drawable to bitmap
     */
    fun drawableToBitmap(drawable: Int, context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, drawable)
    }

    /**
     * method to convert drawable to bitmap
     */
    fun arraytodrawable(imgbyte: ByteArray, context: Context): Drawable {
        return BitmapDrawable(
            context.resources,
            BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.size)
        )
    }

    /**
     * @param imageView
     * @param imagename
     */
    fun setImageViewResourceDrawable(imageView: ImageView, imagename: String) {
        Glide.with(imageView.context)
            .load(
                imageView.context.resources.getDrawable(
                    getGlideImage(
                        imagename,
                        imageView.context
                    )
                )
            ) // Uri of the picture
            .into(imageView)
    }

    fun getGlideImage(imagename: String, context: Context): Int {
        return context.resources.getIdentifier(imagename, "drawable", context.packageName)
    }

    /**
     * Convert Image URI to byte[]
     *
     * @param uri
     */
    fun uriToByteArray(uri: Uri, context: Context): ByteArray {
        // this is storage overwritten on each iteration with bytes
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var byteBuffer: ByteArrayOutputStream? = null
        // we need to know how may bytes were read to write them to the byteBuffer
        var len: Int
        try {
            // this dynamically extends to take the bytes you read
            val inputStream = context.contentResolver.openInputStream(uri)
            byteBuffer = ByteArrayOutputStream()

            len = inputStream!!.read(buffer)
            while ( len != -1) {
                byteBuffer.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // and then we can return your byte array.
        return byteBuffer!!.toByteArray()
    }


    fun dateStringToDate(datestring: String, pattern: String): Date? {
        var date: Date? = null
        try {
            date = SimpleDateFormat(pattern).parse(datestring)
        } catch (e: Exception) {
            println(e.message)
            //e.printStackTrace();
        }

        return date
    }

    fun dateToFormattedString(date: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern)
        var datestring = ""
        try {
            datestring = formatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return datestring
    }


    fun datetoint(date: Date, pattern: String): Int {
        val formatter = SimpleDateFormat(pattern)
        var datestring = 0
        try {
            datestring = Integer.parseInt(formatter.format(date))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return datestring
    }

    fun datetoage(date: Date): Int {
        val cal =
            GregorianCalendar(datetoint(date, "yyyy"), datetoint(date, "MM"), datetoint(date, "dd"))
        val now = GregorianCalendar()
        var res = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR)
        if (cal.get(Calendar.MONTH) > now.get(Calendar.MONTH) || cal.get(Calendar.MONTH) == now.get(
                Calendar.MONTH
            ) && cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH)
        ) {
            res--
        }
        return res
    }

    fun dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.cancel()
            progressDialog!!.dismiss()
        }
    }

    fun showProgressDialog(c: Context, msg: String) {
        progressDialog = ProgressDialog(c)
        progressDialog!!.isIndeterminate
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFF")))
        progressDialog!!.isIndeterminate = true
        ss2 = SpannableString(msg)
        ss2.setSpan(RelativeSizeSpan(1.5f), 0, ss2.length, 0)
        ss2.setSpan(ForegroundColorSpan(Color.parseColor("#43A047")), 0, ss2.length, 0)
        progressDialog!!.setMessage(ss2)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setTitle("")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
    }

    fun age(y: Int, m: Int, d: Int): Int {
        val cal = GregorianCalendar(y, m, d)
        val now = GregorianCalendar()
        var res = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR)
        if (cal.get(Calendar.MONTH) > now.get(Calendar.MONTH) || cal.get(Calendar.MONTH) == now.get(
                Calendar.MONTH
            ) && cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH)
        ) {
            res--
        }
        return res
    }


    /**
     * method to get list of files
     *
     * @param parentDir
     * @return
     */
    fun getListOfFiles(parentDir: File): List<File> {
        val inFiles = ArrayList<File>()
        val files = LinkedList<File>()
        files.addAll(Arrays.asList(*parentDir.listFiles()))
        while (!files.isEmpty()) {
            val file = files.remove()
            if (file.isDirectory) {
                files.addAll(Arrays.asList(*file.listFiles()))
            } else if (file.name.endsWith(".txt")) {
                inFiles.add(file)
            }
        }
        return inFiles
    }

    fun getListOfCSVFiles(parentDir: File): List<File> {
        val inFiles = ArrayList<File>()
        val files = LinkedList<File>()
        files.addAll(Arrays.asList(*parentDir.listFiles()))
        while (!files.isEmpty()) {
            val file = files.remove()
            if (file.isDirectory) {
                files.addAll(Arrays.asList(*file.listFiles()))
            } else if (file.name.endsWith(".csv")) {
                inFiles.add(file)
            }
        }
        return inFiles
    }

    fun getListOfJsonFiles(parentDir: File): List<File> {
        val inFiles = ArrayList<File>()
        val files = LinkedList<File>()
        files.addAll(Arrays.asList(*parentDir.listFiles()))
        while (!files.isEmpty()) {
            val file = files.remove()
            if (file.isDirectory) {
                files.addAll(Arrays.asList(*file.listFiles()))
            } else if (file.name.endsWith(".json")) {
                inFiles.add(file)
            }
        }
        return inFiles
    }

    fun writeToFile(data: String, filename: String): String {
        val dirpath = Environment.getExternalStorageDirectory().toString() + File.separator +
                "uploadfolder"
        val dir = File(dirpath)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, filename)

        try {
            file.createNewFile()
            val fOut = FileOutputStream(file)
            val osw = OutputStreamWriter(fOut)
            osw.append(data)

            osw.close()
            fOut.flush()
            fOut.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }

        return file.absolutePath
    }

    @SuppressLint("MissingPermission")
    fun getIMEI(context: Context?): String {
        try {
            return (context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    fun getMacAddress(context: Context?): String? {

        val `in`: BufferedReader? = null
        var macAddress: String? = null
        var wifiManager: WifiManager? = null
        var wifiInfo: WifiInfo? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val all = Collections.list(NetworkInterface.getNetworkInterfaces())
                    for (nif in all) {
                        if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                        val macBytes = nif.hardwareAddress ?: return ""

                        val res1 = StringBuilder()
                        for (b in macBytes) {
                            res1.append(String.format("%02X:", b))
                        }

                        if (res1.isNotEmpty()) {
                            res1.deleteCharAt(res1.length - 1)
                        }
                        return res1.toString()
                    }
                } catch (ex: Exception) {
                }

                return "02:00:00:00:00:00"

            } else {
                wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiInfo = wifiManager.connectionInfo
                macAddress = wifiInfo!!.macAddress
                return macAddress
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error"
        }

    }

    /**
     * method to request for permissions
     *
     * @param activity
     */
    fun checkPermissions(activity: Activity) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS

            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {/* ... */
                }
            }).check()
    }

    fun hideKeyboard(ctx: Activity) {
        try {
            val inputManager =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(ctx.currentFocus!!.windowToken, 0)
        } catch (ex: Exception) {
        }

    }

    fun sendMessage(mContext: Context, contact: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val sentPI: PendingIntent
            val deliveredPI: PendingIntent
            val SENT = "SMS_SENT"
            val DELIVERED = "SMS_DELIVERED"

            sentPI = PendingIntent.getBroadcast(mContext, 0, Intent(SENT), 0)
            deliveredPI = PendingIntent.getBroadcast(mContext, 0, Intent(DELIVERED), 0)

            val parts = smsManager.divideMessage(message)

            val sendList = ArrayList<PendingIntent>()
            sendList.add(sentPI)

            val deliverList = ArrayList<PendingIntent>()
            deliverList.add(deliveredPI)


            smsManager.sendMultipartTextMessage(contact, null, parts, sendList, deliverList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun drawableToArray(drawable: Int, resources: Resources): ByteArray {
        val bitmap = BitmapFactory.decodeResource(resources, drawable)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        return outputStream.toByteArray()
    }

    fun tolower(str: String): String {
        return str.toLowerCase().trim { it <= ' ' }
    }

    fun loadFarmerImg(farmer_id: String, resources: Resources): ByteArray {
        val farmer: Userbiometrics?
        try {
            val farmersList = SugarRecord.find(Userbiometrics::class.java, "farmerid = ?", farmer_id)
            if (farmersList.isNotEmpty()) {
                farmer = farmersList[0]

                return if (farmer != null) {
                    if (farmer.picture!!.isNotEmpty())
                        farmer.picture!!
                    else
                        drawableToArray(R.drawable.placeholder, resources)
                } else
                    drawableToArray(R.drawable.placeholder, resources)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return drawableToArray(R.drawable.placeholder, resources)
    }

    fun loadFarmerSignature(farmer_id: String, resources: Resources): ByteArray {
        val farmer: Userbiometrics?
        try {
            val farmersList = SugarRecord.find(Userbiometrics::class.java, "farmerid = ?", farmer_id)
            if (farmersList.isNotEmpty()) {
                farmer = farmersList[0]

                return if (farmer != null) {
                    if (farmer.signature!!.isNotEmpty())
                        farmer.signature!!
                    else
                        drawableToArray(R.drawable.placeholder, resources)
                } else
                    drawableToArray(R.drawable.placeholder, resources)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return drawableToArray(R.drawable.placeholder, resources)
    }


    fun loadJSONFromAsset(resources: Resources, filename: String): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val `is` = resources.assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    fun getCollectorname(collectorid: String?): String {
        try {
            val infos =
                SugarRecord.find(CollectorInfo::class.java, "collectorid = ?", collectorid)
            return infos[0].collectorname!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

}
