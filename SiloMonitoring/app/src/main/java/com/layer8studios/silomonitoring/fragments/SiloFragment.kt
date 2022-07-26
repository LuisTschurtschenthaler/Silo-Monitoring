package com.layer8studios.silomonitoring.fragments

import android.app.AlertDialog
import android.content.Intent
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
import com.layer8studios.silomonitoring.activities.ViewHistoryActivity
import com.layer8studios.silomonitoring.databinding.FragmentSiloBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.*
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
                R.id.action_view_history -> {
                    val intent = Intent(requireContext(), ViewHistoryActivity::class.java).apply {
                        putExtra(ARG_SILO, silo)
                    }
                    startActivity(intent)
                }

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

        binding.toolbar.title = "${silo?.name} (${silo?.content})"

        val contentLeft = Utils.getContentLeft(silo!!)
        fillLevelPercent = (contentLeft / silo?.capacity!!) * 100

        val daysLeft = ceil(contentLeft / silo?.needPerDay!!).toLong()
        val refillDate = LocalDate.now().plusDays(daysLeft)

        binding.textViewFillLevelKg.text = if(contentLeft > 0.0)
                "${Utils.formatText(contentLeft)} kg (${Utils.formatText(fillLevelPercent)}%) ${getString(R.string.left)}"
            else getString(R.string.empty)
        binding.textViewCapacity.text = "${Utils.formatText(silo?.capacity!!)} kg"
        binding.textViewDate.text = dateFormatter.format(refillDate)
        binding.textViewNeedPerDay.text = "${Utils.formatText(silo?.needPerDay!!)} kg"

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
            ColorUtils.getInterpolatedColor(orange, green, 100 - progress)
        else ColorUtils.getInterpolatedColor(orange, red, progress)
        binding.waveView.background = ColorDrawable(color)
    }



}