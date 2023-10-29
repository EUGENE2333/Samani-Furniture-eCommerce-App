package com.example.samani.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.samani.R
import com.example.samani.adapters.HomeViewPagerAdapter
import com.example.samani.databinding.FragmentHomeBinding
import com.example.samani.fragments.categories.*
import com.example.samani.util.showBottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home){

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        val categoriesFragments = listOf<Fragment>(
            MainCategoryFragment(), // 0
            ChairFragment(), // 1
            SofaFragment(),  //2
            TableFragment(),  // 3
            CupboardFragment(),  //4
            BedFragment(),  //5
            OttomanFragment(), //6
            MoreFurnitureFragment() //7

        )

        binding.viewPagerHome.isUserInputEnabled = false

        val viewPager2Adapter = HomeViewPagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){ tab, position ->
            when(position){
                0 -> tab.text = "Home"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Sofa"
                3 -> tab.text = "Table"
                4 -> tab.text = "Cupboard"
                5 -> tab.text = "Bed"
                6 -> tab.text = "Ottoman"
                7 -> tab.text = "More"
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}
