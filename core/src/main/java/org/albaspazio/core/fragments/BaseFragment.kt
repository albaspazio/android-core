package org.albaspazio.core.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.reactivex.disposables.CompositeDisposable


//--------------------------------------------------------------------------------------------------
// abstract layer containing behaviour common to all fragments:
// - manage page orientation
// - show/hide android controls
//--------------------------------------------------------------------------------------------------

abstract class BaseFragment(
        @LayoutRes
        val layout: Int,
        val landscape: Boolean,
        val hideAndroidControls: Boolean
) : Fragment() {

    open val LOG_TAG = BaseFragment::class.java.simpleName

    protected val disposable = CompositeDisposable()
    protected lateinit var mMainView:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(layout, container, false)
        return mMainView
    }

    override fun onStart() {
        super.onStart()

//        if (landscape != (requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)) {
//            requireActivity().requestedOrientation = if (landscape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            return
//        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent("NAVIGATION_UPDATE")
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    override fun onPause(){
        super.onPause()
        disposable.clear()
    }
}
