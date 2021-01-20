package org.albaspazio.core.pdf

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pdf_view.*
import org.albaspazio.core.R

class PdfViewActivity: AppCompatActivity() {

    var error_message:String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        val pdfManual = intent.getStringExtra("pdfAssetName")
        error_message = intent.getStringExtra("error_message") ?: "Errore generico"
        
        if(pdfManual == null || pdfManual.isEmpty())
            Toast.makeText(this@PdfViewActivity, error_message, Toast.LENGTH_LONG).show()
        else
            showPdfFromAssets(pdfManual)
    }

    private fun showPdfFromAssets(pdfName: String) {
//        pdfView.fromAsset(pdfName)
//            .password(null) // if password protected, then write password
//            .defaultPage(0) // set the default page to open
//            .onPageError { page, _ ->   Toast.makeText(this@PdfViewActivity, "Error at page: $page", Toast.LENGTH_LONG).show()  }
//            .load()
    }

}