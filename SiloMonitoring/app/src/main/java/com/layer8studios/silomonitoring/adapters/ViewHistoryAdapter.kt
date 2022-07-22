package com.layer8studios.silomonitoring.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.databinding.ListItemViewHistoryBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.Utils
import com.layer8studios.silomonitoring.utils.Utils.toLocalDate
import com.layer8studios.silomonitoring.utils.dateFormatter


class ViewHistoryAdapter(
    private val context: Context,
    private val silo: Silo?
) : RecyclerView.Adapter<ViewHistoryAdapter.ViewHolder>() {

    private var history = silo?.emptyingHistory!!


    inner class ViewHolder(
        private val binding: ListItemViewHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageButtonEdit.setOnClickListener {
                // TODO
            }

            binding.imageButtonDelete.setOnClickListener {
                // TODO
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

}