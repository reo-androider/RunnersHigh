package com.reo.running.runnershigh

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import java.lang.IllegalStateException

class DialogMaker:DialogFragment() {
    val distanceAmount = arguments?.getString("amount")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("ランニングを終了しますか？")
                    .setPositiveButton("YES",
                    DialogInterface.OnClickListener{ dialog, id ->
                        findNavController().navigate(R.id.action_dialogMaker_to_fragmentResult)
                    })
                    .setNegativeButton("CANCEL",
                            DialogInterface.OnClickListener{ dialog, which ->
                                dialog.dismiss()
                            })
            builder.create()
        }?:throw IllegalStateException("Activity cannot be null")
    }
}