package br.gohan.cifrafinder.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.databinding.FragmentWebViewBinding
import br.gohan.cifrafinder.domain.CifraScheduler
import br.gohan.cifrafinder.domain.CurrentSong
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class WebFragment : Fragment() {

    private var _viewBinding: FragmentWebViewBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val viewModel: MusicFetchViewModel by activityViewModel()

    private lateinit var workManager: WorkManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentWebViewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
        setToast()
        workManager = WorkManager.getInstance(requireContext())
        val args = getUserArguments()
        if (args != null) {
            val id = createRefreshSchedule(getRemainingTime(args.durationMs, args.progressMs))
            setObservers(id)
            viewModel.getSongUrl(args.searchString)
        }
    }

    private fun getRemainingTime(durationMs: Long, progressMs: Long): Long = (durationMs - progressMs) + 5000L

    private fun setToast() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserArguments() : CurrentSong? = arguments?.getParcelable(CifraConstants.searchText)

    private fun setObservers(id: UUID) {
        viewModel.searchUrl.observe(viewLifecycleOwner) { searchUrl ->
            if (!searchUrl.isNullOrEmpty()) {
                viewBinding.webView.loadUrl(searchUrl)
            }
        }
        workManager.getWorkInfoByIdLiveData(id).observe(viewLifecycleOwner) {
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

    private fun applyBinding() = with(viewBinding) {
        refreshButton.setOnClickListener {
            findNavController().popBackStack()
        }
        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
        optionsButton.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCurrentlyPlaying()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}