package com.layer8studios.silomonitoring.dialogs

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.databinding.DialogCreateEntryBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.models.SiloHistoryEntry
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils
import com.layer8studios.silomonitoring.utils.Utils.toDate
import com.layer8studios.silomonitoring.utils.dateFormatter
import java.time.LocalDate
import java.util.*


class DialogCreateEntry(
    private val dialogCloseListener: OnDialogCloseListener,
    private val silo: Silo?
) : DialogFragment() {

    interface OnDialogCloseListener {
        fun onDialogClosed(silo: Silo)
    }

    private lateinit var binding: DialogCreateEntryBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogCreateEntryBinding.inflate(layoutInflater)

        binding.radioButtons.setOnCheckedChangeListener { _, id ->
            binding.radioButtonAdd.isChecked = false
            binding.radioButtonRemove.isChecked = false

            when(id) {
                R.id.radio_button_add -> binding.radioButtonAdd.isChecked = true
                R.id.radio_button_remove -> binding.radioButtonRemove.isChecked = true
            }
        }

        val yesterday = LocalDate.now().minusDays(1)
        binding.textViewSelectedDate.text = dateFormatter.format(yesterday)

        binding.buttonSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                set(yesterday.year, yesterday.monthValue, yesterday.dayOfMonth)
            }

            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                binding.textViewSelectedDate.text = dateFormatter.format(LocalDate.of(year, month, dayOfMonth))
            }, yesterday.year, yesterday.monthValue, yesterday.dayOfMonth).apply {
                datePicker.maxDate = calendar.timeInMillis
                show()
            }
        }

        val builder = AlertDialog.Builder(context).apply {
            setView(binding.root)
            setTitle(R.string.create_entry)
            setNegativeButton(getString(R.string.cancel), null)
            setPositiveButton(getString(R.string.add), null)
        }

        return builder.create().apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val setError = { error: String? ->
                        binding.textInputLayoutAmount.error = error
                    }

                    val contentLeft = Utils.getContentLeft(silo!!)
                    val amount = binding.textEditAmount.text.toString()
                    if(amount.isEmpty())
                        setError(getString(R.string.empty_field))
                    else if(amount.toDouble() <= 0.0)
                        setError(getString(R.string.must_not_be_null))
                    else if(amount.toDouble() > silo.capacity)
                        setError(getString(R.string.too_big))
                    else if(binding.radioButtonAdd.isChecked && contentLeft + amount.toDouble() > silo.capacity)
                        setError(getString(R.string.too_much_added))
                    else if(binding.radioButtonRemove.isChecked && contentLeft - amount.toDouble() < 0.0)
                        setError(getString(R.string.too_much_removed))
                    else {
                        val entry = SiloHistoryEntry(
                            LocalDate.parse(binding.textViewSelectedDate.text, dateFormatter).toDate(),
                            amount.toDouble(),
                            binding.radioButtonAdd.isChecked
                        )

                        val newSilo = silo.copy()
                        newSilo.emptyingHistory.add(entry)

                        Preferences.replaceSilo(silo, newSilo)
                        Toast.makeText(requireContext(), getString(R.string.element_was_added), Toast.LENGTH_SHORT).show()
                        dismiss()

                        dialogCloseListener.onDialogClosed(newSilo)
                    }
                }
            }
        }
    }

}