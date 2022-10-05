package com.wasir.droid.currencyexchange.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.wasir.droid.currencyexchange.databinding.SimpleDialogLayoutBinding
import com.wasir.droid.currencyexchange.utils.clickWithDebounce

class ConversionSimpleDialog private constructor(private val dialogBuilder: SimpleBuilder) {
    private var context: Context? = null
    private var alertDialog: AlertDialog? = null
    private var binding: SimpleDialogLayoutBinding


    init {
        this.context = dialogBuilder.context
        binding = SimpleDialogLayoutBinding.inflate(LayoutInflater.from(context))
        alertDialog = AlertDialog.Builder(context).create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog?.setView(binding.root)
        alertDialog?.setCancelable(true)
        binding.doneBtn.clickWithDebounce {
            alertDialog?.dismiss()
        }
        alertDialog?.show()

        bindUIData()

    }

    private fun bindUIData() {
        binding.titleTv.text = dialogBuilder.dialogTitle
        binding.dialogContent.text = dialogBuilder.dialogMessage
    }

    class SimpleBuilder(val context: Context) {
        var dialogMessage: String? = null
        var dialogTitle: String? = null

        fun setMessage(message: String): SimpleBuilder {
            this.dialogMessage = message
            return this
        }

        fun setTitle(title: String): SimpleBuilder {
            this.dialogTitle = title
            return this
        }

        fun build(): ConversionSimpleDialog {
            return ConversionSimpleDialog(this)
        }

    }
}



