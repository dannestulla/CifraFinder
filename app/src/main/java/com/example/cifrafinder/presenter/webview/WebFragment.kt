package com.example.cifrafinder.presenter.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cifrafinder.CifraConstants
import com.example.cifrafinder.databinding.FragmentWebViewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class WebFragment : Fragment() {

    private var _viewBinding: FragmentWebViewBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val viewModel: WebViewModel by viewModel()

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
        setSearchObserver()
        setToast()
        val searchQuery = getTrackArguments()
        if (searchQuery != null) {
            viewModel.getSongUrl(searchQuery)
        }
    }

    private fun setToast() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun getTrackArguments(): String? = arguments?.getString(CifraConstants.searchText)

    private fun setSearchObserver() =
        viewModel.searchUrl.observe(viewLifecycleOwner) { searchUrl ->
            if (searchUrl.isNotEmpty()) {
                viewBinding.webView.loadUrl(searchUrl)
            }
        }

    private fun applyBinding() = with(viewBinding) {
        refreshButton.setOnClickListener {
            findNavController().popBackStack()
        }
        webView.webViewClient = WebViewClient()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}