package com.game.bing.starwar


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.util.Linkify
import android.view.View
import android.widget.Button
import android.widget.TextView

internal object aboutBox {


    private fun versionMame(context: Context): String {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            return "UnKnown"
        }

    }

    fun show(callingActivity: Activity) {
        val help: View
        val helpText: TextView
        val titleText: TextView
        val check: Button

        val text = SpannableString("\n" + "Version" + versionMame(callingActivity) + "\n\n" +
                callingActivity.getString(R.string.aboutContent) + "\n" + callingActivity.getString(R.string.aboutContent2) + "\n\n\n\n\n")

        //        LayoutInflater inflater = callingActivity.getLayoutInflater();
        //        help = inflater.inflate(R.layout.help_box,(ViewGroup) callingActivity.findViewById(R.id.helpView));
        //        helpText = (TextView) help.findViewById(R.id.helpText);
        //
        //        helpText.setText(text);
        //        Linkify.addLinks(helpText,Linkify.ALL);
        //        helpText.setLinkTextColor(Color.BLUE);
        //        new AlertDialog.Builder(callingActivity)
        //                .setTitle("About")
        //                .setCancelable(true)
        //                .setPositiveButton("OK",null)
        //                .setView(help)
        //                .show();
        val dialog = Dialog(callingActivity)
        dialog.setContentView(R.layout.help_box)

        titleText = dialog.findViewById<View>(R.id.titleView) as TextView
        titleText.setText(R.string.about)
        helpText = dialog.findViewById<View>(R.id.helpText) as TextView
        helpText.setTextColor(Color.GREEN)





        Linkify.addLinks(helpText, Linkify.ALL)
        helpText.setLinkTextColor(Color.BLUE)
        helpText.typeface = Typeface.createFromAsset(callingActivity.resources.assets, "fonts/wt040.ttf")



        helpText.text = text
        //
        //        check = dialog.findViewById(R.id.button);
        //        check.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                dialog.dismiss();
        //            }
        //        });
        //


        dialog.show()
    }
}
