package com.layer8studios.silomonitoring.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.adapters.ViewHistoryAdapter
import com.layer8studios.silomonitoring.databinding.ActivityViewHistoryBinding
import com.layer8studios.silomonitoring.databinding.DialogEditEntryBinding
import com.layer8studios.silomonitoring.dialogs.DialogEditEntry
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.Utils.toLocalDate


class ViewHistoryActivity
    : AppCompatActivity(), DialogEditEntry.OnDialogCloseListener {

    private lateinit var binding: ActivityViewHistoryBinding
    private lateinit var adapter: ViewHistoryAdapter
    private var silo: Silo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { super.onBackPressed() }
        binding.toolbar.inflateMenu(R.menu.silo_history)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_add_item -> {
                    DialogEditEntry(this, silo)
                        .show(supportFragmentManager, "")
                }
            }
            true
        }

        silo = intent?.getParcelableExtra(ARG_SILO)
        adapter = ViewHistoryAdapter(this, silo)
        binding.recyclerViewHistory.adapter = adapter
    }

    override fun onDialogClosed(silo: Silo) {
        adapter.setSilo(silo)
    }

}