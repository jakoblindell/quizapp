package com.jaken.quizapp.util

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jaken.quizapp.R
import java.lang.Exception


interface SimpleDialog {
    fun title(resources: Resources): String = this.model(resources).title
    fun message(resources: Resources): String? = this.model(resources).message
    fun cancelAction(resources: Resources): String? = this.model(resources).cancelAction
    fun confirmAction(resources: Resources): String = this.model(resources).confirmAction
    fun secondaryConfirmAction(resources: Resources): String? = this.model(resources).confirmSecondAction

    fun model(resources: Resources): SimpleDialogModel
}

data class SimpleDialogModel(
    val title: String,
    val message: String? = null,
    val cancelAction: String? = null,
    val confirmAction: String,
    val confirmSecondAction: String? = null)


/**
 * Maps a string id to a string.
 *
 * @param res Resources used to get the localized string based on the id.
 * @param formatArgs Any number of arguments used to format the string.
 *
 * @return The formatted localized string, or an empty string if the id was not found.
 */
fun Int.map(res: Resources, vararg formatArgs: Any?): String = try {
    res.getString(this, *formatArgs)
} catch (error: Exception) {
    res.getString(R.string.empty)
}

/**
 * Helper to observe and show dialogs in an activity based on a live data state
 */
fun AppCompatActivity.observe(
    dialogState: MutableLiveData<SimpleDialog?>,
    lifecycleOwner: LifecycleOwner,
    onConfirm: ((SimpleDialog) -> Unit)? = null,
    onSecondConfirm: ((SimpleDialog) -> Unit)? = null,
    onCancel: ((SimpleDialog) -> Unit)? = null) {
    dialogState.observe(lifecycleOwner) {
        it?.let { dialog ->
            MaterialAlertDialogBuilder(this)
                .setTitle(dialog.title(resources))
                .setMessage(dialog.message(resources))
                .setNegativeButton(dialog.cancelAction(resources)) { _, _ ->
                    dialogState.postValue(null)
                    onCancel?.invoke(dialog)
                }
                .setPositiveButton(dialog.confirmAction(resources)) { _, _ ->
                    dialogState.postValue(null)
                    onConfirm?.invoke(dialog)
                }
                .setNeutralButton(dialog.secondaryConfirmAction(resources)) { _, _ ->
                    dialogState.postValue(null)
                    onSecondConfirm?.invoke(dialog)
                }
                .setOnCancelListener {
                    dialogState.postValue(null)
                    onCancel?.invoke(dialog)
                }
                .show()
        }
    }
}

fun Fragment.observe(
    dialogState: MutableLiveData<SimpleDialog?>,
    lifecycleOwner: LifecycleOwner,
    onConfirm: ((SimpleDialog) -> Unit)? = null,
    onSecondConfirm: ((SimpleDialog) -> Unit)? = null,
    onCancel: ((SimpleDialog) -> Unit)? = null) {
    dialogState.observe(lifecycleOwner) {
        it?.let { dialog ->
            val context = context ?: return@let
            MaterialAlertDialogBuilder(context)
                .setTitle(dialog.title(resources))
                .setMessage(dialog.message(resources))
                .setNegativeButton(dialog.cancelAction(resources)) { _, _ ->
                    dialogState.postValue(null)
                    onCancel?.invoke(dialog)
                }
                .setPositiveButton(dialog.confirmAction(resources)) { _, _ ->
                    dialogState.postValue(null)
                    onConfirm?.invoke(dialog)
                }
                .setNeutralButton(dialog.secondaryConfirmAction(resources)) { _, _ ->
                    dialogState.postValue(null)
                    onSecondConfirm?.invoke(dialog)
                }
                .setOnCancelListener {
                    dialogState.postValue(null)
                    onCancel?.invoke(dialog)
                }
                .show()
        }
    }
}