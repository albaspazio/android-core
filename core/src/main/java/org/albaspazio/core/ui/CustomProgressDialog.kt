package org.albaspazio.core.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.progress_dialog_view.*
import kotlinx.android.synthetic.main.progress_dialog_view.view.*
import org.albaspazio.core.R

class CustomProgressDialog(private val context: Context) {

    lateinit var dialog: CustomDialog

    private lateinit var progressBar:ProgressBar

    fun show(): Dialog {
        return show(null, null, null, null)
    }

    fun show(title: CharSequence?, oklabel: CharSequence?, cancellabel: CharSequence?, cancelclk: View.OnClickListener?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog_view, null)
        if (title != null)          view.cp_title.text = title


        // Card Color
        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000"))

        // Progress Bar Color
        setColorFilter(view.cp_pbar.indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null))
        progressBar = view.cp_pbar
        // Text Color
        view.cp_title.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        dialog.setContentView(view)

        dialog.btLeft.visibility = View.GONE
//        dialog.btRight.visibility = View.GONE

        if (oklabel != null)        dialog.btLeft.text  = oklabel
        if (cancellabel != null)    dialog.btRight.text = cancellabel

        dialog.btRight.setOnClickListener(cancelclk)

        dialog.show()
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

        dialog.btLeft.visibility    = View.VISIBLE //Install Manually
        dialog.btRight.visibility   = View.VISIBLE //Download Again
        dialog.cp_title.text        = context.resources.getString(R.string.download_complete_title)

        dialog.btLeft.setOnClickListener(clklist)
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(android.R.color.background_dark)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}