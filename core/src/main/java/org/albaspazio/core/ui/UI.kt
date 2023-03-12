package org.albaspazio.core.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast

import org.albaspazio.core.databinding.InputDialogBinding


fun showToast(text:String, ctx: Context, duration:Int= Toast.LENGTH_SHORT, gravity:Int= Gravity.CENTER) {
    val t = Toast.makeText(ctx, text, duration)
    t.setGravity(gravity, 0, 0)
    t.show()
}

fun showAlert(activity: Activity, title:String, message:String):AlertDialog{


    val builder: AlertDialog.Builder = activity.let {
        AlertDialog.Builder(it)
    }
    builder.setTitle(title)
           .setMessage(message)
           .setCancelable(false)
           .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()}

    val dialog: AlertDialog = builder.create()
    dialog.show()
    return dialog
}

fun show1MethodDialog(activity: Activity, title:String, message:String, oklab:String = "OK", okclb:() -> Unit):AlertDialog{

    val builder: AlertDialog.Builder = activity.let {
        AlertDialog.Builder(it)
    }
    builder.setTitle(title)
           .setMessage(message)
           .setCancelable(false)
           .setPositiveButton(oklab)  { _, _ -> okclb()}

    val dialog: AlertDialog = builder.create()
    dialog.show()
    return dialog
}

fun show2ChoisesDialog(activity: Activity, title:String, message:String, oklab:String = "OK", canlab:String = "CANCEL", okclb:() -> Unit, canclb:() -> Unit = {}):AlertDialog{

    val builder: AlertDialog.Builder = activity.let {
        AlertDialog.Builder(it)
    }
    builder.setTitle(title)
           .setMessage(message)
           .setCancelable(false)
           .setPositiveButton(oklab)  { _, _ -> okclb()}
           .setNegativeButton(canlab) { dialog, _ ->
               if(canclb == {}) dialog.dismiss()
               else             canclb()
           }
    val dialog: AlertDialog = builder.create()
    dialog.show()
    return dialog
}

fun showInputDialog(activity: Activity, title:String, message:String, oklab:String = "OK", canlab:String = "CANCEL", okclb:(m:String) -> Unit, canclb:() -> Unit = {}):AlertDialog{

    val binding:InputDialogBinding = InputDialogBinding.inflate(activity.layoutInflater)

    val dialog: AlertDialog = activity.let {
        val builder = AlertDialog.Builder(it)
        builder.setView(binding.root)
        builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    dialog.show()
    binding.label.text = message
    binding.btOk.setOnClickListener{
        val email = binding.inputText.text
        okclb(email.toString())
    }

    return dialog
}

fun RadioGroup.getSelectedID():Int{
    return indexOfChild(findViewById(checkedRadioButtonId))
}

fun ImageView.loadDrawableFromName(name:String, ctx: Context, visible:Int=View.VISIBLE){
    val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
    this.setImageResource(resId)
    this.visibility = visible
}