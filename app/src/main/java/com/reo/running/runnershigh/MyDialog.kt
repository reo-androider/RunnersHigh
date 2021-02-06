package com.reo.running.runnershigh

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import org.w3c.dom.Text

class MyDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.F)

                .setPositiveButton("保存",
                DialogInterface.OnClickListener { dialog, id ->

                })
                .setNegativeButton("キャンセル",DialogInterface.OnClickListener { dialog, id ->

                })
            builder.show()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }
}