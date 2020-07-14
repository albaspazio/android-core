package org.albaspazio.core.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.progress_dialog_view.*
import kotlinx.android.synthetic.main.progress_dialog_view.view.*

import org.albaspazio.core.R

class CustomProgressAlertDialog(private val activity: Activity) {

    private lateinit var dialog: AlertDialog
    private lateinit var progressBar:ProgressBar

    fun show(title: CharSequence?, oklabel: CharSequence?, cancellabel: CharSequence?, okclk: DialogInterface.OnClickListener?, cancelclk: DialogInterface.OnClickListener?): Dialog {

        val inflater    = activity.layoutInflater
        val view        = inflater.inflate(R.layout.progress_dialog_alertdialog, null)
        progressBar     = view.cp_pbar

        val builder: AlertDialog.Builder = activity.let {
            AlertDialog.Builder(it)
        }
        builder.setTitle(title)
            .setView(view)
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
        dialog.labProgress.text = "$prg %"
    }

    fun onDownloadFinished(clklist: View.OnClickListener){
        dialog.setTitle(activity.resources.getString(R.string.download_complete_title))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).visibility    = View.VISIBLE // Install Manually
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(clklist)
    }
}