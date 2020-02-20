package com.uclgroupgh.form.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.ActivityLoginBinding
import com.uclgroupgh.form.fragments.IPSettingsFragment
import com.uclgroupgh.form.interfaces.LoginClickListener
import com.uclgroupgh.form.login.fragment.LoginFragment
import com.uclgroupgh.form.login.fragment.LogoFragment
import com.uclgroupgh.form.pojo.LoginSuccessPojo
import com.uclgroupgh.form.pojo.Message
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.AsyncRequest
import com.uclgroupgh.form.utils.Constants
import es.dmoral.toasty.Toasty
import org.apache.commons.lang3.StringUtils
import java.util.*

class LoginActivity : AppCompatActivity(), LoginClickListener, AsyncRequest.OnAsyncRequestComplete {
    private val MOVE_DEFAULT_TIME: Long = 1200
    private val FADE_DEFAULT_TIME: Long = 500
    internal var loginSuccessPojo: LoginSuccessPojo? = null
    internal lateinit var binding: ActivityLoginBinding
    internal lateinit var params: MutableMap<String, String>
    private val mDelayedTransactionHandler = Handler()
    private val mloginHandler = Handler()
    private var mFragmentManager: FragmentManager? = null
    private val mRunnable = Runnable { this.performTransition() }
    private var username: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        AndroidUtils.checkPermissions(this)

        mFragmentManager = supportFragmentManager

        loadInitialFragment()
        mDelayedTransactionHandler.postDelayed(mRunnable, 1500)
    }

    private fun loadInitialFragment() {
        val initialFragment = LogoFragment.newInstance()
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, initialFragment)
        fragmentTransaction.commit()
    }

    private fun performTransition() {
        // more on this later
        if (this.isDestroyed) {
            return
        }
        val previousFragment = mFragmentManager!!.findFragmentById(R.id.fragment_container)
        val nextFragment = LoginFragment.newInstance(this)

        val fragmentTransaction = mFragmentManager!!.beginTransaction()

        // 1. Exit for Previous Fragment
        val exitFade = Fade()
        exitFade.duration = FADE_DEFAULT_TIME
        previousFragment!!.exitTransition = exitFade

        // 2. Shared Elements Transition
        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move))

        enterTransitionSet.duration = MOVE_DEFAULT_TIME
        enterTransitionSet.startDelay = FADE_DEFAULT_TIME
        nextFragment.sharedElementEnterTransition = enterTransitionSet

        // 3. Enter Transition for New Fragment
        val enterFade = Fade()
        enterFade.startDelay = MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME
        enterFade.duration = FADE_DEFAULT_TIME
        nextFragment.enterTransition = enterFade

        val logo = findViewById<View>(R.id.frag1logo)
        fragmentTransaction.addSharedElement(logo, logo.transitionName)
        fragmentTransaction.replace(R.id.fragment_container, nextFragment)
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelayedTransactionHandler.removeCallbacks(mRunnable)
    }

    override fun onLoginClickListener(username: String, password: String) {
        if (AndroidUtils.getPreferenceData(
                this@LoginActivity,
                Constants.IPCOMPLETEPREF,
                ""
            )!!.isEmpty()
        ) {

            AndroidUtils.savePreferenceData(this@LoginActivity, Constants.FIRSTTIME, "y")
            val fm = supportFragmentManager
            val ipSettingsFragment = IPSettingsFragment()
            ipSettingsFragment.show(fm, "Dialog Fragment")
        } else {
            this.username = username
            this.password = password
            AndroidUtils.hideKeyboard(this@LoginActivity)
            binding.loginProgress.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
            UserLoginTask().execute()
            AndroidUtils.hideKeyboard(this@LoginActivity)
        }
    }

    fun loginRequest() {
        params = HashMap()
        params["username"] = username!!
        params["password"] = password!!
        val getPosts = AsyncRequest(this@LoginActivity, "GET", params)
        getPosts.execute(Constants.SERVER_URL + "/login")
    }


    override fun asyncResponse(response: String) {
        if (StringUtils.isNotBlank(response) && !StringUtils.endsWithIgnoreCase(
                response,
                "empty"
            )
        ) {
            try {
                loginSuccessPojo = Gson().fromJson(response, LoginSuccessPojo::class.java)
            } catch (ex: Exception) {
                try {
                    val msg = Gson().fromJson(response, Message::class.java)
                    Toasty.error(this, msg.msg).show()
                    ex.printStackTrace()
                } catch (e: Exception) {

                }

            }

            if (loginSuccessPojo != null) {
                if (loginSuccessPojo!!.status != null) {
                    if (loginSuccessPojo!!.status.equals("success", ignoreCase = true)) {
                        AndroidUtils.savePreferenceData(
                            this@LoginActivity,
                            Constants.AGENTID,
                            loginSuccessPojo!!.staffid
                        )
                        AndroidUtils.savePreferenceData(
                            this@LoginActivity,
                            Constants.AGENTNAME,
                            loginSuccessPojo!!.loginname
                        )
                        AndroidUtils.savePreferenceData(
                            this@LoginActivity,
                            Constants.PASSWORD,
                            password
                        )

                        val intent = Intent(baseContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.loginProgress.visibility = View.GONE
                        binding.fragmentContainer.visibility = View.VISIBLE
                        Toasty.error(this, "Login Error!!").show()
                    }
                } else {
                    binding.loginProgress.visibility = View.GONE
                    binding.fragmentContainer.visibility = View.VISIBLE
                    Toasty.error(this, "Login Error!!").show()
                }

            }
        }
    }

    inner class UserLoginTask : AsyncTask<Void, Void, Boolean> {

        private var mEmail: String = ""
        private var mPassword: String = ""

        internal constructor() {}

        internal constructor(email: String, password: String) {
            mEmail = email
            mPassword = password
        }


        override fun onPreExecute() {
            binding.loginProgress.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {

                val agentid =
                    AndroidUtils.getPreferenceData(this@LoginActivity, Constants.AGENTID, "")
                val agentname =
                    AndroidUtils.getPreferenceData(this@LoginActivity, Constants.AGENTNAME, "")
                val password =
                    AndroidUtils.getPreferenceData(this@LoginActivity, Constants.PASSWORD, "")
                if (StringUtils.isNotBlank(agentid) && StringUtils.isNotBlank(agentname) && StringUtils.isNotBlank(
                        password
                    )
                ) {
                    if (username == agentname && this@LoginActivity.password == password) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        loginRequest()
                    }
                } else {
                    loginRequest()
                }
            } catch (e: Exception) {
                return false
            }

            // TODO: register the new account here.
            return true
        }

        override fun onPostExecute(success: Boolean?) {

        }

        override fun onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }


}
