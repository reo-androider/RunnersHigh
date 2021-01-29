package com.reo.running.runnershigh

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

class DialogMaker:DialogFragment() {
    val readDao = MyApplication.db.recordDao()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage("ランニングを終了しますか？")
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