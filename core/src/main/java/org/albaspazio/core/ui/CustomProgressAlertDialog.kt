package org.albaspazio.core.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog


import org.albaspazio.core.R
import org.albaspazio.core.databinding.ProgressDialogAlertdialogBinding

class CustomProgressAlertDialog(private val activity: Activity) {

    private lateinit var binding: ProgressDialogAlertdialogBinding

    private lateinit var dialog: AlertDialog
    private lateinit var progressBar:ProgressBar

    fun show(title: CharSequence?, oklabel: CharSequence?, cancellabel: CharSequence?, okclk: DialogInterface.OnClickListener?, cancelclk: DialogInterface.OnClickListener?): Dialog {

        val inflater    = activity.layoutInflater
        binding = ProgressDialogAlertdialogBinding.inflate(inflater)
        progressBar     = binding.cpPbar

        val builder: AlertDialog.Builder = activity.let {
            AlertDialog.Builder(it)
        }
        builder.setTitle(title)
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton(oklabel, okclk)
            .setNegativeButton(cancellabel, cancelclk)

        dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).visibility    = View.GONE

        return dialog
    }

    fun dismiss(){
        dialog.dismiss()
    }

    fun updateProgress(prg:Int){
        progressBar.progress = prg
        binding.labProgress.text = "$prg %"
    }

    fun onDownloadFinished(clklist: View.OnClickListener){
        dialog.setTitle(activity.resources.getString(R.string.download_complete_title))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).visibility    = View.VISIBLE // Install Manually
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(clklist)
    }
}