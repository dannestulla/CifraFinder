package br.gohan.cifrafinder.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import br.gohan.cifrafinder.presenter.MusicFetchViewModel
import br.gohan.cifrafinder.presenter.screens.LoginScreen
import br.gohan.cifrafinder.presenter.screens.ui.theme.CifraFinderTheme
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class StartingFragment : Fragment() {

    private val viewModel: MusicFetchViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CifraFinderTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LoginScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun setObservers() {
        viewModel.currentSongModel.observe(viewLifecycleOwner) {
            if (it.searchString!!.isNotEmpty()) {
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

    private fun createBundle(searchText: CurrentSongModel): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(
            CifraConstants.searchText, searchText
        )
        return bundle
    }

}