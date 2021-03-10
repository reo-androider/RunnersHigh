package com.reo.running.runnershigh.fragments.profile.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.databinding.FragmentPhotoBinding
import com.reo.running.runnershigh.recyclerview.photolist.PhotoListAdapter

class PhotoFragment : Fragment() {
    private lateinit var binding:FragmentPhotoBinding

    private val viewModel: PhotoListViewModel by viewModels {
        PhotoListViewModel.Companion.Factory(MyApplication.db.runResultDao())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoBinding.inflate(layoutInflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            viewModel.runResultList.observe(viewLifecycleOwner) {
                mainRecyclerView.run {
                    adapter = PhotoListAdapter(it)
                    layoutManager = LinearLayoutManager(requireContext())
                    returnButton.setOnClickListener {
                        findNavController().navigate(R.id.action_fragmentPhoto_to_navi_setting)
                    }
                }
            }
        }
    }
}