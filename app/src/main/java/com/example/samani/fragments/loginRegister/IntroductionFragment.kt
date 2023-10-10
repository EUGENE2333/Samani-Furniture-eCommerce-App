package com.example.samani.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.samani.R
import com.example.samani.activities.ShoppingActivity
import com.example.samani.databinding.FragmentIntoductionBinding
import com.example.samani.viewmodel.IntroductionViewModel
import com.example.samani.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENTS
import com.example.samani.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment: Fragment(R.layout.fragment_intoduction) {
    private lateinit var binding: FragmentIntoductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntoductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    ACCOUNT_OPTIONS_FRAGMENTS ->{
                        findNavController().navigate(it)
                    }
                    else -> Unit
                }
            }
        }

        binding.introductionFragmentButton.setOnClickListener {
            viewModel.startButtonCLick()
           findNavController().navigate(R.id.action_introductionFragment_to_accountsOptionsFragment)
        }
    }

}