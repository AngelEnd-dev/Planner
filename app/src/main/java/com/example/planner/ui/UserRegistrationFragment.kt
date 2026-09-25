package com.example.planner.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.utils.imageBitMapToBase64
import com.example.planner.data.utils.imageUriToBitmap
import com.example.planner.databinding.FragmentUserRegistrationBinding
import com.example.planner.ui.viewmodel.UserRegistrationViewModel
import kotlinx.coroutines.launch
//tela de cadastro

class UserRegistrationFragment : Fragment() {

    private var _binding: FragmentUserRegistrationBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }

    private val userRegistrationViewModel by activityViewModels<UserRegistrationViewModel>()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val imageBitMap = requireContext().imageUriToBitmap(uri)
                imageBitMap?.let {
                    val imageBase64 = imageBitMapToBase64(bitmap = imageBitMap)
                    userRegistrationViewModel.updateProfile(image = imageBase64)
                    binding.ivADDPhoto.setImageURI(uri)
                }

            } else
                Toast.makeText(
                    requireContext(),
                    "Oops...Nenhuma foto selecionada.",
                    Toast.LENGTH_SHORT
                ).show()

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserve()

        with(binding) {
            ivADDPhoto.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            tieName.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(
                    name = text.toString()
                )
            }
            tieEmail.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(
                    email = text.toString()
                )
            }
            tiePhone.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(
                    phone = text.toString()
                )
            }

            btnSaveUser.setOnClickListener {
                userRegistrationViewModel.saveProfile(
                    onCompleted = {
                    navController.navigate(R.id.action_userRegistrationFragment_to_homeFragment)
                    }
                )
            }
        }
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            userRegistrationViewModel.isProfileValid.collect { isProfileValid ->
                binding.btnSaveUser.isEnabled = isProfileValid

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
//