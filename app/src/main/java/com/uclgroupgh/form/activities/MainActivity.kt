package com.uclgroupgh.form.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.ActivityMainBinding
import com.uclgroupgh.form.entities.Districts
import com.uclgroupgh.form.entities.Regions
import com.uclgroupgh.form.fragments.FormsFragment
import com.uclgroupgh.form.fragments.HistoryFragment
import com.uclgroupgh.form.service.FormService
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants
import com.uclgroupgh.form.utils.Converter

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    internal var datasaved = false
    internal var synccount = 0
    internal var mHandler = Handler()
    internal lateinit var binding: ActivityMainBinding
    internal lateinit var loading: MaterialDialog
    internal lateinit var formservice: Intent
    internal var historyFragment: HistoryFragment? = null
    internal lateinit var adapter: ViewPagerAdapter
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateUI(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setupviewpager()
        binding.content.forms.requestFocus()


        val firstTimer = AndroidUtils.getPreferenceData(applicationContext, Constants.FIRSTTIME, "")
        if (firstTimer == "y") {
            startloadingdialog()
            saveregionsanddistricts()
            AndroidUtils.savePreferenceData(this@MainActivity, Constants.FIRSTTIME, "n")
            val runnable = object : Runnable {
                override fun run() {
                    if (datasaved) {

                        datasaved = false

                        if (!datasaved) {
                            AndroidUtils.savePreferenceData(
                                this@MainActivity,
                                Constants.FIRSTTIME,
                                "n"
                            )
                            loading.dismiss()
                            mHandler.removeCallbacks(this)
                        }

                    }
                    mHandler.postDelayed(this, 2000)
                }
            }

            mHandler.postDelayed(runnable, 2000)
        }

        binding.content.forms.setOnClickListener {
            if (binding.content.pager.currentItem != 0) {
                binding.content.pager.currentItem = 0
                binding.content.forms.requestFocus()
                binding.content.forms.setTextColor(resources.getColor(R.color.grey_800))
                binding.content.history.setTextColor(resources.getColor(R.color.grey_400))
            }
        }
        binding.content.history.setOnClickListener {
            if (binding.content.pager.currentItem != 1) {
                binding.content.pager.currentItem = 1
                binding.content.history.requestFocus()
                binding.content.history.setTextColor(resources.getColor(R.color.grey_800))
                binding.content.forms.setTextColor(resources.getColor(R.color.grey_400))
            }
        }

        binding.content.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {

            }

            override fun onPageSelected(i: Int) {
                if (i == 0) {
                    binding.content.forms.setBackgroundDrawable(getDrawable(R.drawable.mainform_background_selected))
                    binding.content.history.setBackgroundDrawable(getDrawable(R.drawable.mainform_background_normal))
                    binding.content.forms.setTextColor(resources.getColor(R.color.grey_800))
                    binding.content.history.setTextColor(resources.getColor(R.color.grey_400))
                } else {
                    historyFragment = adapter.currentFragment
                    if (historyFragment != null) {
                        historyFragment!!.reloaddata(historyFragment!!)
                    }
                    binding.content.forms.setBackgroundDrawable(getDrawable(R.drawable.mainform_background_normal))
                    binding.content.history.setBackgroundDrawable(getDrawable(R.drawable.mainform_background_selected))
                    binding.content.history.setTextColor(resources.getColor(R.color.grey_800))
                    binding.content.forms.setTextColor(resources.getColor(R.color.grey_400))
                }
            }

            override fun onPageScrollStateChanged(i: Int) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainactivitymenu, menu)
        val menuItem = menu.findItem(R.id.sync)
        menuItem.icon =
            Converter.convertLayoutToImage(this@MainActivity, synccount, R.drawable.round_sync_24)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.sync -> {
                if (synccount == 0) {
                    Toasty.error(baseContext, "No sync files").show()
                } else {
                    AndroidUtils.uploadFileToServer(baseContext)
                    Toasty.success(baseContext, "Syncing").show()
                }
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        formservice = Intent(this, FormService::class.java)
        startService(formservice)
        registerReceiver(broadcastReceiver, IntentFilter(FormService.BROADCAST_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
        stopService(formservice)
    }

    private fun loadRegions() {
        try {
            val array = JSONArray(AndroidUtils.loadJSONFromAsset(resources, "region.json"))

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val region = item.getString("region")
                val regioncode = item.getString("regioncode")

                val regions = Regions()
                regions.region = region
                regions.regioncode = regioncode
                regions.save()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun loadDistricts() {
        try {
            val array = JSONArray(AndroidUtils.loadJSONFromAsset(resources, "districts.json"))

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val district = item.getString("district")
                val districtcode = item.getString("districtcode")
                val targets = item.getInt("targets")
                val regioncode = item.getString("regioncode")
                val region = item.getString("region")

                val districts = Districts()
                districts.district = district
                districts.districtcode = districtcode
                districts.targets = targets.toString()
                districts.region = region
                districts.regioncode = regioncode
                districts.save()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun saveregionsanddistricts() {
        @SuppressLint("StaticFieldLeak") val asyncTask =
            object : AsyncTask<Void, String, Boolean>() {
                var outcome = false

                override fun doInBackground(vararg params: Void): Boolean? {
                    loadDistricts()
                    loadRegions()
                    outcome = true
                    return outcome
                }

                override fun onPostExecute(outcome: Boolean) {
                    if (outcome) {
                        datasaved = true
                    }
                }
            }
        asyncTask.execute()
    }

    private fun startloadingdialog() {
        loading = MaterialDialog(this@MainActivity).cornerRadius(16f).cancelable(false).show {
            customView(R.layout.item_progressbar, scrollable = true)
        }

        loading.show()
    }

    private fun updateUI(intent: Intent) {
        if (intent.getStringExtra("synccount") == null) {
            synccount = 0
        } else {
            synccount = Integer.parseInt(intent.getStringExtra("synccount"))
        }
        invalidateOptionsMenu()
    }

    private fun setupviewpager() {

        val formsFragment = FormsFragment.newInstance("a", "b")
        val historyFragment = HistoryFragment.newInstance("a", "b")

        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(formsFragment, "Forms")
        adapter.addFragment(historyFragment, "History")
        binding.content.pager.adapter = adapter
        //        binding.content.pager.setCurrentItem(0);
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
        var currentFragment: HistoryFragment? = null

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {

            if (`object` is HistoryFragment) {
                currentFragment = `object`
            }

            super.setPrimaryItem(container, position, `object`)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}
