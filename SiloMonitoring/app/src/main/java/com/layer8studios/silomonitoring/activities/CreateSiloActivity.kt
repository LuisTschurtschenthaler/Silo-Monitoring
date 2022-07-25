package com.layer8studios.silomonitoring.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.databinding.ActivityCreateSiloBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.models.SiloHistoryEntry
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils.toDate
import com.layer8studios.silomonitoring.utils.Utils.toLocalDate
import com.layer8studios.silomonitoring.utils.dateFormatter
import java.time.LocalDate
import java.util.*


class CreateSiloActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityCreateSiloBinding
    private var silo: Silo? = null
    private var isEditingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateSiloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { super.onBackPressed() }

        silo = intent.getParcelableExtra(ARG_SILO)
        isEditingMode = (silo != null)

        val maxDay = LocalDate.now()
        if(isEditingMode) {
            binding.toolbar.title = getString(R.string.edit_silo)
            binding.textViewButton.text = getString(R.string.apply_changes)

            binding.textEditSiloName.setText(silo!!.name)
            binding.textEditSiloCapacity.setText(silo!!.capacity.toString())
            binding.textEditSiloContent.setText(silo!!.content)
            binding.textEditNeedPerDay.setText(silo!!.needPerDay.toString())

            binding.textViewSiloLastRefill.visibility = View.GONE
            binding.textEditLastDeliveryQuantity.visibility = View.GONE
            binding.textViewSiloLastDeliveryDate.visibility = View.GONE
            binding.buttonSelectDate.visibility = View.GONE
        }
        else {
            binding.toolbar.title = getString(R.string.create_silo)
            binding.textViewButton.text = getString(R.string.create)
            binding.textViewSiloLastDeliveryDate.text = dateFormatter.format(maxDay)
        }

        binding.buttonSelectDate.setOnClickListener {
            val today = LocalDate.parse(binding.textViewSiloLastDeliveryDate.text, dateFormatter)
            val calendar = Calendar.getInstance().apply {
                set(today.year, today.monthValue, today.dayOfMonth)
            }

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                binding.textViewSiloLastDeliveryDate.text = dateFormatter.format(LocalDate.of(year, month, dayOfMonth))
            }, today.year, today.monthValue, today.dayOfMonth)
            datePicker.datePicker.maxDate = calendar.timeInMillis
            datePicker.show()
        }

        binding.buttonCreate.setOnClickListener {
            var isError = false
            val isEmpty = { txtEdit: TextInputEditText, txtLayout: TextInputLayout ->
                if(txtEdit.text.isNullOrEmpty()) {
                    txtLayout.error = getString(R.string.empty_field)
                    isError = true
                }
                else txtLayout.error = null
            }

            isEmpty(binding.textEditSiloName, binding.textInputLayoutSiloName)
            isEmpty(binding.textEditSiloCapacity, binding.textInputLayoutSiloCapacity)
            isEmpty(binding.textEditSiloContent, binding.textInputLayoutSiloContent)
            isEmpty(binding.textEditNeedPerDay, binding.textInputLayoutNeedPerDay)
            if(!isEditingMode) isEmpty(binding.textEditLastDeliveryQuantity, binding.textInputLayoutLastDeliveryQuantity)

            val isNull = { txtEdit: TextInputEditText, txtLayout: TextInputLayout ->
                try {
                    if (txtEdit.text.toString().toDouble() <= 0.0) {
                        txtLayout.error = getString(R.string.must_not_be_null)
                        isError = true
                    } else txtLayout.error = null
                } catch (ex: Exception) { }
            }

            isNull(binding.textEditSiloCapacity, binding.textInputLayoutSiloCapacity)
            isNull(binding.textEditNeedPerDay, binding.textInputLayoutNeedPerDay)
            if(!isEditingMode) isNull(binding.textEditLastDeliveryQuantity, binding.textInputLayoutLastDeliveryQuantity)
            if(isError) return@setOnClickListener

            try {
                val name = binding.textEditSiloName.text.toString()
                val capacity = binding.textEditSiloCapacity.text.toString().toDouble()
                val content = binding.textEditSiloContent.text.toString()
                val needPerDay = binding.textEditNeedPerDay.text.toString().toDouble()
                val lastRefillQuantity = if(isEditingMode) 0.0 else binding.textEditLastDeliveryQuantity.text.toString().toDouble()
                val lastRefillDate = if(isEditingMode) LocalDate.now() else LocalDate.parse(binding.textViewSiloLastDeliveryDate.text, dateFormatter)

                val isBigger = { one: Double, two: Double, txtLayout: TextInputLayout ->
                    if(one > two) {
                        txtLayout.error = getString(R.string.too_big)
                        isError = true
                    }
                    else txtLayout.error = null
                }
                if(!isEditingMode) isBigger(lastRefillQuantity, capacity, binding.textInputLayoutLastDeliveryQuantity)
                isBigger(needPerDay, capacity, binding.textInputLayoutNeedPerDay)

                if(!isError) {
                    val today = LocalDate.now()

                    if(isEditingMode) {
                        val newSilo = silo!!.copy().apply {
                            this.name = name
                            this.capacity = capacity
                            this.content = content
                            this.needPerDay = needPerDay
                        }

                        if(newSilo.needPerDay != silo!!.needPerDay) {
                            if(newSilo.emptyingHistory.last().date == today.toDate())
                                newSilo.emptyingHistory.removeLast()
                            newSilo.emptyingHistory.add(
                                SiloHistoryEntry(today.toDate(), needPerDay)
                            )
                        }

                        Preferences.replaceSilo(silo!!, newSilo)
                        val intent = Intent().apply {
                            putExtra(ARG_SILO, newSilo)
                        }
                        setResult(RESULT_OK, intent)
                    }
                    else {
                        val lastRefill = SiloHistoryEntry(lastRefillDate.toDate(), lastRefillQuantity, true)
                        val newSilo = Silo(name, capacity, content, needPerDay, lastRefillQuantity, lastRefillDate.toDate(), mutableListOf(lastRefill))

                        for(epochDay in lastRefillDate.toEpochDay()..today.toEpochDay()) {
                            val date = LocalDate.ofEpochDay(epochDay)
                            if(date != lastRefillDate) {
                                newSilo.emptyingHistory.add(
                                    SiloHistoryEntry(date.toDate(), needPerDay)
                                )
                            }
                        }

                        Preferences.addSilo(newSilo)
                        setResult(RESULT_OK)
                    }

                    finish()
                }
            } catch(ex: Exception) {
                println(ex)
            }
        }

    }

}