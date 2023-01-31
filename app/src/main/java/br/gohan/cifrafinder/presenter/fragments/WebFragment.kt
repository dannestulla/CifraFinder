package br.gohan.cifrafinder.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.CifraScheduler
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import br.gohan.cifrafinder.presenter.MusicFetchViewModel
import br.gohan.cifrafinder.presenter.screens.WebScreen
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class WebFragment : Fragment() {
    private val viewModel: MusicFetchViewModel by activityViewModel()
    private lateinit var workManager: WorkManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                findNavController().popBackStack()
                            },
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_refresh),
                                contentDescription = "Content description for visually impaired"
                            )
                        }
                    }, content = { padding ->
                        Surface(
                            modifier = Modifier.padding(padding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            WebScreen(viewModel)
                        }
                    })

            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToast()
        workManager = WorkManager.getInstance(requireContext())
        val args = getUserArguments()
        if (args != null) {
            setSongRefreshCycle(args)
        }
    }

    private fun setSongRefreshCycle(args: CurrentSongModel) {
        val workId = createRefreshSchedule(getRemainingTime(args.durationMs, args.progressMs))
        setObservers(workId)
        viewModel.getSongUrl(args.searchString!!)
    }

    private fun getRemainingTime(durationMs: Long, progressMs: Long): Long =
        (durationMs - progressMs) + 5000L

    private fun setToast() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserArguments(): CurrentSongModel? =
        arguments?.getParcelable(CifraConstants.searchText)

    private fun setObservers(workId: UUID) {
        /*   viewModel.searchUrl.observe(viewLifecycleOwner) { searchUrl ->
               if (!searchUrl.isNullOrEmpty()) {
                   viewBinding.webView.loadUrl(searchUrl)
               }
           }*/
        workManager.getWorkInfoByIdLiveData(workId).observe(viewLifecycleOwner) {
            if (it.state.isFinished) {
                viewModel.getCurrentlyPlaying()
            }
        }
    }

    private fun createRefreshSchedule(refreshTime: Long): UUID {
        val wakeUpSchedule = OneTimeWorkRequestBuilder<CifraScheduler>()
            .setInitialDelay(refreshTime, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueue(wakeUpSchedule)
        return wakeUpSchedule.id
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCurrentlyPlaying()
    }
}