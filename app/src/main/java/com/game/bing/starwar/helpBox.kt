package com.game.bing.starwar


import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.util.Linkify
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

internal object helpBox {


    fun show(callingActivity: Activity) {
        val help: View
        val helpText: TextView

        val text = SpannableString(callingActivity.getString(R.string.help))


        try {
            val inflater = callingActivity.layoutInflater
            help = inflater.inflate(R.layout.help_box, callingActivity.findViewById<View>(R.id.helpView) as ViewGroup)
            helpText = help.findViewById<View>(R.id.helpText) as TextView

            helpText.text = text
            Linkify.addLinks(helpText, Linkify.ALL)
            AlertDialog.Builder(callingActivity)
                    .setTitle("Help")
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .setView(help)
                    .show()
        } catch (e: InflateException) {
            //出錯時使用預設
            // help = helpText = new TextView(callingActivity);
        }


    }
}
