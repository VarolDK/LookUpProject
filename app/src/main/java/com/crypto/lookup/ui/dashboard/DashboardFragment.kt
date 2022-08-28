package com.crypto.lookup.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.lookup.databinding.FragmentDashboardBinding
import com.crypto.lookup.ui.login.UserViewModel

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var sharedViewModel: UserViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardViewModel.initializeAdaptersAndUser(sharedViewModel.getCurrentUser(), sharedViewModel)

        val layoutManagerAddCoin = LinearLayoutManager(context)
        val layoutManagerSubscribed = LinearLayoutManager(context)

        binding.recyclerViewCoins.layoutManager = layoutManagerAddCoin
        binding.recyclerViewCoins.adapter = dashboardViewModel.addCoinPriceAdapter

        binding.recyclerViewSubscribed.layoutManager = layoutManagerSubscribed
        binding.recyclerViewSubscribed.adapter = dashboardViewModel.subscribedCoinAdapter

//        dashboardViewModel.setSubscribedCoinsData()
        dashboardViewModel.setCoinListDataFirebase()

        dashboardViewModel.subscribedCoinData.observe(viewLifecycleOwner) {
            dashboardViewModel.setSubscribedCoinAdapterData(it.coins)
        }

        dashboardViewModel.coinListData.observe(viewLifecycleOwner) {
            dashboardViewModel.setCoinAdapterData(it.coins)
            binding.dashboardPB.isVisible = false
            binding.subsLayout.isVisible = true
        }

        sharedViewModel.user.observe(viewLifecycleOwner) {
            dashboardViewModel.updateCurrentUser(sharedViewModel.getCurrentUser())
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                dashboardViewModel.addCoinPriceAdapter.filter.filter(query)
                dashboardViewModel.subscribedCoinAdapter.filter.filter(query)
                return false
            }
        })


        binding.dashboardAll.setOnClickListener{
            binding.subsLayout.isVisible = false
            binding.coinLayout.isVisible = true
        }

        binding.dashboardSub.setOnClickListener{
            binding.coinLayout.isVisible = false
            binding.subsLayout.isVisible = true
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}