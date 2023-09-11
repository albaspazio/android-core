/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import org.albaspazio.core.R
import org.albaspazio.core.ui.showToast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * This fragment has a big [ImageView] that shows PDF pages, and 2 [Button]s to move between pages.
 * We use a [PdfRenderer] to render PDF pages as [Bitmap]s.
 */
class PdfRendererBasicFragment : Fragment(), View.OnClickListener {

    private var FILENAME    = "sample.pdf"
    private var TITLE       = "TITLE"
    private var ERROR_MSG   = "Error"

    // Key string for saving the state of current page index.
    private val STATE_CURRENT_PAGE_INDEX = "current_page_index"
    private val TAG = "PdfRendererBasicFrg"

    //The initial page index of the PDF.
    private val INITIAL_PAGE_INDEX = 0

    // File descriptor of the PDF.
    private lateinit var fileDescriptor: ParcelFileDescriptor

    // [PdfRenderer] to render the PDF.
    private lateinit var pdfRenderer: PdfRenderer

    // Page that is currently shown on the screen.
    private lateinit var currentPage: PdfRenderer.Page

    // [ImageView] that shows a PDF page as a [Bitmap].
    private lateinit var imageView: ImageView

    // [Button] to move to the previous page.
    private lateinit var btnPrevious: Button

    // [Button] to move to the next page.
    private lateinit var btnNext: Button

    // PDF page index.
    private var pageIndex: Int = INITIAL_PAGE_INDEX

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        arguments?.let {
            FILENAME    = it.getString("pdf_file").toString()
            TITLE       = it.getString("title").toString()
            ERROR_MSG   = it.getString("error_message").toString()
        }
        return inflater.inflate(R.layout.fragment_pdf_renderer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView   = view.findViewById(R.id.image)
        btnPrevious = view.findViewById<Button>(R.id.previous).also { it.setOnClickListener(this) }
        btnNext     = view.findViewById<Button>(R.id.next).also { it.setOnClickListener(this)}

        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        pageIndex = savedInstanceState?.getInt(STATE_CURRENT_PAGE_INDEX, INITIAL_PAGE_INDEX)  ?: INITIAL_PAGE_INDEX
    }

    override fun onStart() {
        super.onStart()
        try {
            openRenderer(activity)
            showPage(pageIndex)
        } catch (e: IOException) {
            showToast(resources.getString(R.string.pdf_error_message, ERROR_MSG, FILENAME), requireContext())
            requireActivity().finish()
            Log.d(TAG, e.toString())
        }
    }

    override fun onStop() {
        try {
            closeRenderer()
        } catch (e: IOException) {
            Log.d(TAG, e.toString())
        }
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_PAGE_INDEX, currentPage.index)
        super.onSaveInstanceState(outState)
    }

  /**
   * Sets up a [PdfRenderer] and related resources.
   */
    @Throws(IOException::class)
    private fun openRenderer(context: Context?) {
        if (context == null) return

        // In this sample, we read a PDF from the assets directory.
        val file = File(context.cacheDir, FILENAME)

        if (file.exists()) file.delete()    // delete it, otherwise in case of an update that change the pdf, I would load the previous one

        // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into the cache directory.
        val asset = context.assets.open(FILENAME)
        val output = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var size = asset.read(buffer)
        while (size != -1) {
          output.write(buffer, 0, size)
          size = asset.read(buffer)
        }
        asset.close()
        output.close()

        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        // This is the PdfRenderer we use to render the PDF.
        pdfRenderer = PdfRenderer(fileDescriptor)
        currentPage = pdfRenderer.openPage(pageIndex)
    }

  /**
   * Closes the [PdfRenderer] and related resources.
   *
   * @throws IOException When the PDF file cannot be closed.
   */
    @Throws(IOException::class)
    private fun closeRenderer() {
      if(this::pdfRenderer.isInitialized) {
          currentPage.close()
          pdfRenderer.close()
          fileDescriptor.close()
      }
    }

  /**
   * Shows the specified page of PDF to the screen.
   *
   * @param index The page index.
   */
    private fun showPage(index: Int) {
        if (pdfRenderer.pageCount <= index) return

        // Make sure to close the current page before opening another one.
        currentPage.close()
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index)
        // Important: the destination bitmap must be ARGB (not RGB).
        val bitmap = createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        // We are ready to show the Bitmap to user.
        imageView.setImageBitmap(bitmap)
        updateUi()
    }

  /**
   * Updates the state of 2 control buttons in response to the current page index.
   */
    private fun updateUi() {
        val index             = currentPage.index
        val pageCount         = pdfRenderer.pageCount
        btnPrevious.isEnabled = (0 != index)
        btnNext.isEnabled     = (index + 1 < pageCount)
        activity?.title       = getString(R.string.pdf_title_with_index, TITLE, index + 1, pageCount)
    }

  /**
   * Returns the page count of of the PDF.
   */
  fun getPageCount() = pdfRenderer.pageCount

    override fun onClick(view: View) {
        when (view.id) {
            R.id.previous -> showPage(currentPage.index - 1)
            R.id.next     -> showPage(currentPage.index + 1)
        }
    }
}