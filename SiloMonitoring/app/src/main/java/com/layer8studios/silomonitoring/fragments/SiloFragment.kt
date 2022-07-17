package com.layer8studios.silomonitoring.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.layer8studios.silomonitoring.R
import com.layer8studios.silomonitoring.databinding.FragmentSiloBinding
import com.layer8studios.silomonitoring.models.Silo


class SiloFragment
    : Fragment() {

    companion object {
        private const val ARG_SILO = "ARG_SILO"

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

        binding.toolbar.title = "${silo?.name} (${silo?.content})"
        binding.toolbar.inflateMenu(R.menu.silo_menu)

        binding.cardViewEdit.setOnClickListener {
            // TODO
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_edit -> {
                // TODO
            }

            R.id.action_delete -> {
                // TODO
            }
        }
        return true
    }


}