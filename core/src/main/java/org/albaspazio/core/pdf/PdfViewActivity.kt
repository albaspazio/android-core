package org.albaspazio.core.pdf

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pdf_view.*
import org.albaspazio.core.R

class PdfViewActivity: AppCompatActivity() {

    var error_message:String = ""
    val FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        val pdfFile     = intent.getStringExtra("pdfAssetName")
        val title       = intent.getStringExtra("title")
        error_message   = intent.getStringExtra("error_message") ?: "Generic Error"
        
        if(pdfFile == null || pdfFile.isEmpty())
            Toast.makeText(this@PdfViewActivity, resources.getString(R.string.pdf_error_message, error_message, pdfFile), Toast.LENGTH_LONG).show()
        else {

            val bundle = Bundle()
            bundle.putString("pdf_file", pdfFile)
            bundle.putString("title", title)
            bundle.putString("error_message", error_message)
            val fragobj = PdfRendererBasicFragment()
            fragobj.setArguments(bundle)

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragobj, FRAGMENT_PDF_RENDERER_BASIC)
                .commit()
        }
    }
}