package com.deviation.soundrecorder.removeDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.deviation.soundrecorder.R
import com.deviation.soundrecorder.database.SoundRecorderDatabase


class RemoveDialogFragment : DialogFragment() {

    private lateinit var viewModel: RemoveViewModel

    companion object {
        private const val ARG_ITEM_PATH = "recording_item_path"
        private const val ARG_ITEM_ID = "recording_item_id"
    }

    fun newInstance(itemId: Long, itemPath: String?): RemoveDialogFragment {
        val f = RemoveDialogFragment()
        val b = Bundle()
        b.putLong(ARG_ITEM_ID, itemId)
        b.putString(ARG_ITEM_PATH, itemPath)

        f.arguments = b
        return f
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = SoundRecorderDatabase.getInstance(application).recordDatabaseDao
        val viewModelFactory = RemoveViewModelFactory(
            database,
            application
        )
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RemoveViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val itemPath = arguments?.getString(ARG_ITEM_PATH)
        val itemId = arguments?.getLong(ARG_ITEM_ID)

        return AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_text_delete)
            .setPositiveButton(R.string.dialog_action_yes) { dialog, which ->
                try {
                    itemId?.let { viewModel.removeItem(it) }
                    itemPath?.let { viewModel.removeFile(it) }
                } catch (e: java.lang.Exception) {
                    Log.e("deleteFileDialog", "exception", e)
                }
                dialog.cancel()
            }
            .setNegativeButton(
                R.string.dialog_action_no
            ) { dialog, which ->
                dialog.cancel()
            }
            .create()
    }

}