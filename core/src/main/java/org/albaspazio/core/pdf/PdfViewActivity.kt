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
//        setContentView(R.layout.activity_pdf_view)
//        setContentView(R.layout.fragment_pdf_renderer)
        setContentView(R.layout.activity_pdf_viewer)

        val pdfFile = intent.getStringExtra("pdfAssetName")
        val title = intent.getStringExtra("title")
        error_message = intent.getStringExtra("error_message") ?: "Generic Error"
        
        if(pdfFile == null || pdfFile.isEmpty())
            Toast.makeText(this@PdfViewActivity, "$error_message displaying $pdfFile", Toast.LENGTH_LONG).show()
        else {

            val bundle = Bundle()
            bundle.putString("pdf_file", pdfFile)
            bundle.putString("title", title)
            val fragobj = PdfRendererBasicFragment()
            fragobj.setArguments(bundle)

            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragobj, FRAGMENT_PDF_RENDERER_BASIC)
                .commit()

            // showPdfFromAssets(pdfManual)
        }
    }

    private fun showPdfFromAssets(pdfName: String) {
//        pdfView.fromAsset(pdfName)
//            .password(null) // if password protected, then write password
//            .defaultPage(0) // set the default page to open
//            .onPageError { page, _ ->   Toast.makeText(this@PdfViewActivity, "Error at page: $page", Toast.LENGTH_LONG).show()  }
//            .load()
    }

}