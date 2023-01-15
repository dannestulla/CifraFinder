package br.gohan.cifrafinder.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.databinding.FragmentLoginBinding
import br.gohan.cifrafinder.domain.CurrentSong
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class StartingFragment : Fragment() {

    private var _viewBinding: FragmentLoginBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel: MusicFetchViewModel by activityViewModel()

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

    private fun setObservers() = with(viewModel) {
        currentSong.observe(viewLifecycleOwner) {
            if (it.searchString.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_cifraMenuFragment_to_cifraWebFragment,
                    createBundle(it)
                )
            }
        }
        spotifyToken.observe(viewLifecycleOwner) { token ->
            if (token.isNullOrBlank().not()) {
                viewModel.getCurrentlyPlaying()
            }
        }
    }

    private fun createBundle(searchText: CurrentSong): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(
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