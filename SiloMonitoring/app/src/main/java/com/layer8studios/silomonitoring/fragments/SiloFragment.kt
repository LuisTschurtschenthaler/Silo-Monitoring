package com.layer8studios.silomonitoring.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.activities.CreateSiloActivity
import com.layer8studios.silomonitoring.activities.MainActivity
import com.layer8studios.silomonitoring.databinding.FragmentSiloBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.ARG_SILO
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils
import com.layer8studios.silomonitoring.utils.dateFormatter
import java.time.LocalDate
import kotlin.math.ceil


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
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_message))
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            Preferences.removeSilo(silo!!)
                            (activity as MainActivity).removeItem(silo!!)
                        }
                        .create()
                        .show()
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
        }
    }


    private fun update() {
        if(silo == null)
            return

        println(silo)

        binding.toolbar.title = "${silo?.name} (${silo?.content})"

        fun formatText(number: Double): String {
            return String.format("%.2f", number).replace(".", ",")
        }

        val contentLeft = Utils.getContentLeft(silo!!)
        fillLevelPercent = (contentLeft / silo?.capacity!!) * 100

        val daysLeft = ceil(contentLeft / silo?.needPerDay!!).toLong()
        val refillDate = LocalDate.now().plusDays(daysLeft)

        binding.textViewFillLevelKg.text = if(contentLeft > 0.0) "${formatText(contentLeft)} kg ${getString(R.string.left)}" else getString(R.string.empty)
        binding.textViewFillLevelPercentage.text = "${formatText(fillLevelPercent)} %"
        binding.textViewCapacity.text = "${formatText(silo?.capacity!!)} kg"
        binding.textViewDate.text = dateFormatter.format(refillDate)
        binding.textViewNeedPerDay.text = "${formatText(silo?.needPerDay!!)} kg"

        var progress = fillLevelPercent.toInt()
        if(progress >= 95)
            progress = 95
        else if(progress <= 5)
            progress = 5
        binding.waveView.setProgress(progress)

        val green = ContextCompat.getColor(requireContext(), R.color.green)
        val orange = ContextCompat.getColor(requireContext(), R.color.orange)
        val red = ContextCompat.getColor(requireContext(), R.color.red)

        val color = if(progress > 50)
            getColor(orange, green, 100 - progress)
        else getColor(orange, red, progress)

        binding.waveView.background = ColorDrawable(color)
        binding.waveView.foreground = ColorDrawable(android.R.color.transparent)

    }

    private fun getColor(colorStart: Int, colorEnd: Int, percent: Int): Int {
        return Color.rgb(
            interpolate(Color.red(colorStart), Color.red(colorEnd), percent),
            interpolate(Color.green(colorStart), Color.green(colorEnd), percent),
            interpolate(Color.blue(colorStart), Color.blue(colorEnd), percent)
        )
    }

    private fun interpolate(colorStart: Int, colorEnd: Int, percent: Int): Int {
        return (Math.min(colorStart, colorEnd) * (100 - percent) + Math.max(colorStart, colorEnd) * percent) / 100
    }

}