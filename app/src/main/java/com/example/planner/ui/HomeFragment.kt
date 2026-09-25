package com.example.planner.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.planner.R
import com.example.planner.data.utils.imageBase64ToBitmap
import com.example.planner.data.utils.imageBitMapToBase64
import com.example.planner.databinding.FragmentHomeBinding
import com.example.planner.ui.viewmodel.UserRegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val userRegistrationViewModel: UserRegistrationViewModel by activityViewModels ()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false   )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserves()
        with(binding){
            //TODO: lógica da tela de home
            btnNewPlannerAcitivy.setOnClickListener {
                UpdatePlannerActivityDialogFragment().show(
                    childFragmentManager,
                    UpdatePlannerActivityDialogFragment.TAG
                )
            }
        }
    }

    private fun setupObserves() {
        lifecycleScope.launch {
            launch {
                userRegistrationViewModel.profile.collect {  profile ->
                    binding.tvUserName.text = getString(R.string.ola_usuario, profile.name)
                    imageBase64ToBitmap(base64String = profile.image)?.let{ imageBitmap ->
                        binding.ivUserPhoto.setImageBitmap(imageBitmap)
                    }
                }
            }
            launch {
                userRegistrationViewModel.isTokenValid.distinctUntilChanged {
                    old, new -> old == new
                }.collect { isTokenValid ->
                    if(isTokenValid == false) showNewTokenSnackBar() //snakebar mensagem pro user visualmente

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNewTokenSnackBar(){
        Snackbar.make(requireView(), "Oops... o seu token expirou", Snackbar.LENGTH_INDEFINITE)
            .setAction("OBTER NOVO TOKEN"){
                userRegistrationViewModel.obtainNewToken()
            }
            .setActionTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lime_300
                )
            ).show()
    }

}