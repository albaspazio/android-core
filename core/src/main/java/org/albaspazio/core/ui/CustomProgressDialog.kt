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

import org.albaspazio.core.R
import org.albaspazio.core.databinding.ProgressDialogViewBinding

class CustomProgressDialog(private val context: Context) {

    private lateinit var binding: ProgressDialogViewBinding

    lateinit var dialog: CustomDialog

    private lateinit var progressBar:ProgressBar

    fun show(): Dialog {
        return show(null, null, null, null)
    }

    fun show(title: CharSequence?, oklabel: CharSequence?, cancellabel: CharSequence?, cancelclk: View.OnClickListener?): Dialog {

        val inflater = (context as Activity).layoutInflater
        binding = ProgressDialogViewBinding.inflate(inflater)

        if (title != null)          binding.cpTitle.text = title

        // Card Color
        binding.cpCardview.setCardBackgroundColor(Color.parseColor("#70000000"))

        // Progress Bar Color
        setColorFilter(binding.cpPbar.indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null))
        progressBar = binding.cpPbar

        // Text Color
        binding.cpTitle.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        dialog.setContentView(binding.root)

        binding.btLeft.visibility = View.GONE
//        dialog.btRight.visibility = View.GONE

        if (oklabel != null)        binding.btLeft.text  = oklabel
        if (cancellabel != null)    binding.btRight.text = cancellabel

        binding.btRight.setOnClickListener(cancelclk)

        dialog.show()
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

        binding.btLeft.visibility    = View.VISIBLE //Install Manually
        binding.btRight.visibility   = View.VISIBLE //Download Again
        binding.cpTitle.text        = context.resources.getString(R.string.download_complete_title)

        binding.btLeft.setOnClickListener(clklist)
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