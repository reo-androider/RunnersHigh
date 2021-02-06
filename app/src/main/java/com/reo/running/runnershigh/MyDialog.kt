package com.reo.running.runnershigh

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import org.w3c.dom.Text

class MyDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            val builder = AlertDialog.Builder(it)
//            builder.setMessage("ランニング中に気づいたこと等はあるかな？")
//                .setPositiveButton("保存") { _, _ ->
//                    dismiss()
//                }
//                .setNegativeButton("キャンセル") { _, _ ->
//                    dismiss()
//                }
//            builder.show()
//        } ?: throw  IllegalStateException("Activity cannot be null")
        val dialog:Dialog
        dialog = Dialog(requireContext())
        val dw = dialog.window
        dw?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.setContentView(R.layout.alarm_dialog)

        return dialog
    }

}