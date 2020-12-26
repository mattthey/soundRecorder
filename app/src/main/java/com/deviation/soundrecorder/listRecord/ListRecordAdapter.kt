package com.deviation.soundrecorder.listRecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.deviation.soundrecorder.R
import com.deviation.soundrecorder.database.RecordEntity
import com.deviation.soundrecorder.player.PlayerFragment
import com.deviation.soundrecorder.removeDialog.RemoveDialogFragment
import java.io.File
import java.util.concurrent.TimeUnit


class ListRecordAdapter : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {
    var data = listOf<RecordEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val context = holder.itemView.context
        val recordingItem = data[position]

        val itemDuration: Long = recordingItem.length
        val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes))

        holder.vName.text = recordingItem.name
        holder.vLength.text = String.format("%02d:%02d", minutes, seconds)

        holder.cardView.setOnClickListener {
            val filePath = recordingItem.filePath

            val file = File(filePath)
            if (file.exists()) {
                try {
                    playRecord(filePath, context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(context, "Аудиофайл не найден", Toast.LENGTH_SHORT).show()
            }
        }

        holder.cardView.setOnLongClickListener {
            removeItemDialog(recordingItem, context)
            false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var vName: TextView = itemView.findViewById(R.id.file_name_text)
        var vLength: TextView = itemView.findViewById(R.id.file_length_text)


        var cardView: View = itemView.findViewById(R.id.card_view)

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_record, parent, false)

                return ViewHolder(
                    view
                )
            }
        }

    }

    private fun playRecord(filePath: String, context: Context?) {
        val playerFragment: PlayerFragment = PlayerFragment().newInstance(filePath)
        val transaction: FragmentTransaction = (context as FragmentActivity)
            .supportFragmentManager
            .beginTransaction()
        playerFragment.show(transaction, "dialog_playback")
    }

    private fun removeItemDialog(recordingItem: RecordEntity, context: Context?) {
        val removeDialogFragment: RemoveDialogFragment =
            RemoveDialogFragment().newInstance(recordingItem.id, recordingItem.filePath)
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        removeDialogFragment.show(transaction, "dialog_remove")
    }
}