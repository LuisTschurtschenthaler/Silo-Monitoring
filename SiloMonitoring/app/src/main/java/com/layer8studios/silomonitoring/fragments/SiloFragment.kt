package com.layer8studios.silomonitoring.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.activities.CreateSiloActivity
import com.layer8studios.silomonitoring.databinding.FragmentSiloBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.dateFormatter
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class SiloFragment
    : Fragment() {

    companion object {

        fun newInstance(silo: Silo): SiloFragment {
            val fragment = SiloFragment()
            val args = Bundle().apply {
                putParcelable(ARG_SILO, silo)
            }
            fragment.arguments = args
            return fragment
        }
    }


    private lateinit var binding: FragmentSiloBinding
    private var silo: Silo? = null
    private var fillLevelPercent = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null)
            silo = requireArguments().getParcelable(ARG_SILO)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSiloBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.silo_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(requireContext(), CreateSiloActivity::class.java).apply {
                        putExtra(ARG_SILO, silo)
                    }
                    startActivityForResult(intent, 1)
                }

                R.id.action_delete -> {
                    // TODO
                }
            }
            true
        }
        update()
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            silo = data?.getParcelableExtra(ARG_SILO)
            update()
            println("YESSSSSSS")
        }
    }


    private fun update() {
        binding.toolbar.title = "${silo?.name} (${silo?.content})"

        fun formatText(number: Double): String {
            return String.format("%.2f", number).replace(".", ",")
        }

        val today = LocalDate.now()
        val lastRefillDate = LocalDate.of(silo?.lastRefillDateYear!!, silo?.lastRefillDateMonth!!, silo?.lastRefillDateDay!!)
        val days = ChronoUnit.DAYS.between(lastRefillDate, today)
        val fillLevel = silo?.lastRefillQuantity!! - (days * silo?.needPerDay!!)
        fillLevelPercent = (fillLevel / silo?.capacity!!) * 100

        val daysLeft = (fillLevel / silo?.needPerDay!!).toLong()
        val refillDate = today.plusDays(daysLeft)

        binding.textViewFillLevelKg.text = "${formatText(fillLevel)} kg"
        binding.textViewFillLevelPercentage.text = "${formatText(fillLevelPercent)} %"
        binding.textViewCapacity.text = "${formatText(silo?.capacity!!)} kg"
        binding.textViewDate.text = dateFormatter.format(refillDate)
        binding.textViewNeedPerDay.text = "${formatText(silo?.needPerDay!!)} kg"

        val progress = fillLevelPercent.toInt()
        binding.waveView.setProgress(progress)
    }
}