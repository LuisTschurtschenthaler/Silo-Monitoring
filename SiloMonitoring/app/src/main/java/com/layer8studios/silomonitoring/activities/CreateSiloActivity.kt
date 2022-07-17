package com.layer8studios.silomonitoring.activities

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.databinding.ActivityCreateSiloBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.dateFormatter
import java.time.LocalDate
import java.util.*


class CreateSiloActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityCreateSiloBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateSiloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { super.onBackPressed() }

        val maxDay = LocalDate.now().minusDays(1)
        binding.textViewSiloLastDeliveryDate.text = dateFormatter.format(maxDay)

        binding.buttonSelectDate.setOnClickListener {
            val today = LocalDate.parse(binding.textViewSiloLastDeliveryDate.text, dateFormatter)

            val cal = Calendar.getInstance()
            cal.set(maxDay.year, maxDay.monthValue, maxDay.dayOfMonth)

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                binding.textViewSiloLastDeliveryDate.text = dateFormatter.format(LocalDate.of(year, month, dayOfMonth))
            }, today.year, today.monthValue, today.dayOfMonth)
            datePicker.datePicker.maxDate = cal.timeInMillis
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
            isEmpty(binding.textEditLastDeliveryQuantity, binding.textInputLayoutLastDeliveryQuantity)

            try {
                val name = binding.textEditSiloName.text.toString()
                val capacity = binding.textEditSiloCapacity.text.toString().toDouble()
                val content = binding.textEditSiloContent.text.toString()
                val needPerDay = binding.textEditNeedPerDay.text.toString().toDouble()
                val lastDeliveryQuantity = binding.textEditLastDeliveryQuantity.text.toString().toDouble()
                val lastRefillDate = LocalDate.parse(binding.textViewSiloLastDeliveryDate.text, dateFormatter)

                val isBigger = { one: Double, two: Double, txtLayout: TextInputLayout ->
                    if(one > two) {
                        txtLayout.error = getString(R.string.too_big)
                        isError = true
                    }
                    else txtLayout.error = null
                }
                isBigger(lastDeliveryQuantity, capacity, binding.textInputLayoutLastDeliveryQuantity)
                isBigger(needPerDay, capacity, binding.textInputLayoutNeedPerDay)

                if(!isError) {
                    val silo = Silo(name, capacity, content, needPerDay, lastDeliveryQuantity, lastRefillDate.year, lastRefillDate.monthValue, lastRefillDate.dayOfMonth)
                    Preferences.addSilo(silo)
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (ex: Exception) {
            }
        }

    }

}