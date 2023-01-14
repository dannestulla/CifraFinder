package com.example.cifrafinder.presenter.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cifrafinder.CifraConstants
import com.example.cifrafinder.R
import com.example.cifrafinder.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LoginFragment : Fragment() {

    private var _viewBinding: FragmentLoginBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel: LoginViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
        setObservers()
    }

    private fun setObservers() {
        viewModel.currentlyPlaying.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_cifraMenuFragment_to_cifraWebFragment,
                    createBundle(it)
                )
            }
        }
        viewModel.spotifyToken.observe(viewLifecycleOwner) { token ->
            if (token.isNullOrBlank().not()) {
                viewModel.getCurrentlyPlaying()
            }
        }
    }

    private fun createBundle(searchText: String): Bundle {
        val bundle = Bundle()
        bundle.putString(
            CifraConstants.searchText, searchText
        )
        return bundle
    }

    private fun applyBinding() = with(viewBinding) {
        startSearchButton.setOnClickListener {
            if (viewModel.spotifyToken.value?.isNotEmpty() == true) {
                viewModel.getCurrentlyPlaying()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}