package org.albaspazio.core.accessory

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.Toast


fun showToast(text:String, ctx: Context, duration:Int= Toast.LENGTH_SHORT, gravity:Int= Gravity.CENTER) {
    val t = Toast.makeText(ctx, text, duration)
    t.setGravity(gravity, 0, 0)
    t.show()
}

fun showAlert(activity: Activity?, title:String, message:String){

    val builder: AlertDialog.Builder? = activity?.let {
        AlertDialog.Builder(it)
    }
    builder?.setMessage(message)?.setTitle(title)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()

}
