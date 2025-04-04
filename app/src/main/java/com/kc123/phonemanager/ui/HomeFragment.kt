package com.kc123.phonemanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.kc123.phonemanager.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCard(view, R.id.card_feature1, "App Shortcut", R.drawable.ic_shortcut)
        setupCard(view, R.id.card_feature2, "App Icon", R.drawable.ic_apps)
        setupCard(view, R.id.card_feature3, "App Title", R.drawable.ic_text)
        setupCard(view, R.id.card_feature4, "App Lock", R.drawable.ic_lock)
        setupCard(view, R.id.card_feature5, "Help", R.drawable.ic_help)
        setupCard(view, R.id.card_feature6, "Float", R.drawable.ic_button)

        val card1 = view.findViewById<View>(R.id.card_feature1)
        val card2 = view.findViewById<View>(R.id.card_feature2)
        val card3 = view.findViewById<View>(R.id.card_feature3)
        val card4 = view.findViewById<View>(R.id.card_feature4)
        val card5 = view.findViewById<View>(R.id.card_feature5)
        val card6 = view.findViewById<View>(R.id.card_feature6)

        card1.setOnClickListener {
            Toast.makeText(requireContext(), "Feature 1 clicked", Toast.LENGTH_SHORT).show()
            // Or navigate: findNavController().navigate(R.id.action_home_to_feature1)
        }

        card2.setOnClickListener {
            Toast.makeText(requireContext(), "Feature 2 clicked", Toast.LENGTH_SHORT).show()
        }

        card3.setOnClickListener {
            Toast.makeText(requireContext(), "Feature 3 clicked", Toast.LENGTH_SHORT).show()
        }

        card4.setOnClickListener {
            Toast.makeText(requireContext(), "Feature 4 clicked", Toast.LENGTH_SHORT).show()
        }

        card5.setOnClickListener {
            Toast.makeText(requireContext(), "Feature 5 clicked", Toast.LENGTH_SHORT).show()
        }

        card6.setOnClickListener {
            Toast.makeText(requireContext(), "Feature 6 clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCard(parent: View, cardId: Int, title: String, iconResId: Int) {
        val card = parent.findViewById<View>(cardId)

        val titleView = card.findViewById<TextView>(R.id.featureTitle)
        val iconView = card.findViewById<ImageView>(R.id.featureIcon)

        titleView.text = title
        iconView.setImageResource(iconResId)

        card.setOnClickListener {
            Toast.makeText(requireContext(), "$title clicked", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}