package com.layer8studios.silomonitoring.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.databinding.ListItemViewHistoryBinding
import com.layer8studios.silomonitoring.dialogs.DialogCreateEntry
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.receivers.NotificationReceiver
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils
import com.layer8studios.silomonitoring.utils.Utils.toLocalDate
import com.layer8studios.silomonitoring.utils.dateFormatter


class ViewHistoryAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private var silo: Silo?
) : RecyclerView.Adapter<ViewHistoryAdapter.ViewHolder>(), DialogCreateEntry.OnDialogCloseListener {

    private var history = silo?.emptyingHistory!!


    inner class ViewHolder(
        private val binding: ListItemViewHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageButtonEdit.setOnClickListener {
                val position = adapterPosition
                val item = history[position]
                DialogCreateEntry(this@ViewHistoryAdapter, silo!!, item)
                    .show(fragmentManager, "")
            }

            binding.imageButtonDelete.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.delete_title))
                    .setMessage(context.getString(R.string.delete_message_entry))
                    .setNegativeButton(context.getString(R.string.cancel), null)
                    .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                        val position = adapterPosition
                        history.removeAt(position)
                        notifyItemRemoved(position)

                        Preferences.setHistory(silo!!, history)
                        Toast.makeText(context, context.getString(R.string.element_was_removed), Toast.LENGTH_SHORT).show()
                    }
                    .create()
                    .show()
            }
        }

        fun bind(position: Int) {
            val item = history[position]
            binding.textViewDetails.text = if(item.wasAdded) "+" else "-"
            binding.textViewDate.text = dateFormatter.format(item.date.toLocalDate())

            if(item.wasAdded) {
                binding.textViewSymbol.text = "+"
                binding.textViewSymbol.setTextColor(context.resources.getColor(R.color.green))
                binding.textViewDetails.text = "${Utils.formatText(item.amount)} ${context.getString(R.string.kg_were_refilled)}"
            }
            else {
                binding.textViewSymbol.text = "-"
                binding.textViewSymbol.setTextColor(context.resources.getColor(R.color.red))
                binding.textViewDetails.text = "${Utils.formatText(item.amount)} ${context.getString(R.string.kg_have_been_taken_out)}"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemViewHistoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = history.size

    override fun onDialogClosed(silo: Silo) {
        val newSilo = Utils.checkSilo(silo)
        Preferences.replaceSilo(silo, newSilo)
        setSilo(silo)
        NotificationReceiver.reschedule(context.applicationContext, silo)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setSilo(silo: Silo) {
        Utils.sortHistory(silo)

        this.silo = silo
        this.history = silo.emptyingHistory
        notifyDataSetChanged()
    }

}