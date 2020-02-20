package com.uclgroupgh.form.login.fragment


import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.uclgroupgh.form.R
import com.uclgroupgh.form.databinding.FragmentLoginBinding
import com.uclgroupgh.form.interfaces.LoginClickListener

import br.com.ilhasoft.support.validation.Validator


class LoginFragment : Fragment() {
    internal lateinit var binding: FragmentLoginBinding
    internal lateinit var validator: Validator


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val view = binding.root
        validator = Validator(binding)
        binding.btnlogin.setOnClickListener { v ->
            if (validator.validate()) {
                clickListener.onLoginClickListener(
                    binding.username.text.toString().trim { it <= ' ' },
                    binding.password.text.toString().trim { it <= ' ' })
            }
        }
        return view
    }

    companion object {
        internal lateinit var clickListener: LoginClickListener

        // TODO: Rename and change types and number of parameters
        fun newInstance(listener: LoginClickListener): LoginFragment {
            clickListener = listener
            return LoginFragment()
        }
    }

}// Required empty public constructor
