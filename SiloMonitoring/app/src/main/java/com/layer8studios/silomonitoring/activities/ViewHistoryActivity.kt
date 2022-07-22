package com.layer8studios.silomonitoring.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.layer8studios.silomonitoring.adapters.ViewHistoryAdapter
import com.layer8studios.silomonitoring.databinding.ActivityViewHistoryBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.Utils.toLocalDate


class ViewHistoryActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityViewHistoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { super.onBackPressed() }

        val silo = intent?.getParcelableExtra<Silo>(ARG_SILO)
        silo?.emptyingHistory?.sortByDescending { it.date.toLocalDate() }
        binding.recyclerViewHistory.adapter = ViewHistoryAdapter(this, silo)
    }

}