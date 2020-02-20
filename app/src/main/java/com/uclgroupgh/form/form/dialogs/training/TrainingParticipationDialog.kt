package com.uclgroupgh.form.form.dialogs.training


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.uclgroupgh.form.R
import com.uclgroupgh.form.adapters.TrainingAttendanceAdapter
import com.uclgroupgh.form.databinding.DialogTrainingParticipantsBinding
import com.uclgroupgh.form.entities.TrainingAttendance

/**
 * A simple [Fragment] subclass.
 */
class TrainingParticipationDialog : DialogFragment(), View.OnClickListener {
    internal lateinit var binding: DialogTrainingParticipantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_training_participants,
            container,
            false
        )
        val view = binding.root
        val temp = "training participants (" + trainingAttendanceList.size + ")"
        binding.header.text = temp
        binding.close.setOnClickListener(this)
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.itemAnimator = DefaultItemAnimator()
        val adapter = TrainingAttendanceAdapter(trainingAttendanceList)
        binding.recycler.adapter = adapter
        runLayoutAnimation()
        return view
    }

    private fun runLayoutAnimation() {
        val context = context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        binding.recycler.layoutAnimation = controller
        binding.recycler.adapter!!.notifyDataSetChanged()
        binding.recycler.scheduleLayoutAnimation()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes.windowAnimations = R.style.traininganimate2
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.close -> dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    companion object {


        var TAG = "TrainingParticipationDialog"
        internal lateinit var trainingAttendanceList: List<TrainingAttendance>

        fun newInstance(list: List<TrainingAttendance>): TrainingParticipationDialog {
            trainingAttendanceList = list
            return TrainingParticipationDialog()
        }
    }
}
