package com.wasir.droid.currencyexchange.presentation.dialogs

import android.app.AlertDialog
import android.content.Context

class CurrencyChooseDialog private constructor(private val dialogBuilder: CurrencyChooseDialogBuilder) {
    private var context: Context? = null
    private var alertDialogBuilder: AlertDialog.Builder? = null

    interface OnCurrencyChangeLister {
        fun onCurrencyChange(currency: String)
    }

    init {
        this.context = dialogBuilder.context
        alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder?.setCancelable(true)

        alertDialogBuilder?.setTitle(dialogBuilder.dialogTitle)
        alertDialogBuilder?.setItems(
            dialogBuilder.currencyList
        ) { p0, p1 ->
            dialogBuilder.currencyList?.get(p1)?.let {
                dialogBuilder.onCurrencyChangeLister?.onCurrencyChange(it)
            }
        }

        val dialog = alertDialogBuilder?.create()
        dialog?.show()
    }

    class CurrencyChooseDialogBuilder(val context: Context) {
        var currencyList: Array<String>? = null
        var dialogTitle: String? = null
        var onCurrencyChangeLister: OnCurrencyChangeLister? = null
        fun setCurrencySelectListener(onCurrencyChangeLister: OnCurrencyChangeLister): CurrencyChooseDialogBuilder {
            this.onCurrencyChangeLister = onCurrencyChangeLister
            return this
        }

        fun setCurrencies(currencyList: List<String>): CurrencyChooseDialogBuilder {
            this.currencyList = currencyList.toTypedArray()
            return this
        }

        fun setTitle(title: String): CurrencyChooseDialogBuilder {
            this.dialogTitle = title
            return this
        }

        fun build(): CurrencyChooseDialog {
            return CurrencyChooseDialog(this)
        }

    }
}