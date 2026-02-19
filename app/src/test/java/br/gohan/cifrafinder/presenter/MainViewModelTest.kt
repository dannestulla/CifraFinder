package br.gohan.cifrafinder.presenter

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.model.SongData
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private val googleService = mockk<GoogleService>()
    private val spotifyService = mockk<SpotifyService>()
    private val savedState = SavedStateHandle()
    private val songDataMock = SongData(" songName - artist ", 1, 1, "imageUrl")
    private val dataStateMock = DataState("123")
    private val link = "link"
    private val spotifyTokenMock = "123"
    private lateinit var crashlytics: FirebaseCrashlytics

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        crashlytics = mockk(relaxed = true)
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns crashlytics

        viewModel = MainViewModel(spotifyService, googleService, savedState)
        coEvery { spotifyService.invoke(spotifyTokenMock) } returns Result.success(songDataMock)
        coEvery { googleService.invoke(any()) } returns Result.success(link)
    }

    @Test
    fun `WHEN startMusicFetch is called and spotify fails THEN show Snackbar`() = runTest {
        viewModel.update(dataStateMock)
        viewModel.update(WhatIsPlayingState("123"))
        coEvery { spotifyService.invoke(spotifyTokenMock) } returns Result.failure(Throwable("Error"))
        viewModel.events.test {
            viewModel.startMusicFetch()
            val event = awaitItem()
            assertTrue(event is AppEvents.ShowSnackbar)
        }
    }

    @Test
    fun `WHEN startMusicFetch is called and google fails THEN show Snackbar`() = runTest {
        viewModel.update(dataStateMock)
        viewModel.update(WhatIsPlayingState("123"))
        coEvery { spotifyService.invoke(spotifyTokenMock) } returns Result.success(songDataMock)
        coEvery { googleService.invoke(any()) } returns Result.failure(Throwable("Error"))
        viewModel.events.test {
            viewModel.startMusicFetch()
            val event = awaitItem()
            assertTrue(event is AppEvents.ShowSnackbar)
        }
    }

    @Test
    fun `WHEN getCurrentPlaying is called THEN return songDataModel`() = runTest {
        val result = viewModel.getCurrentPlaying(dataStateMock)
        coVerify(exactly = 1) { spotifyService.invoke(spotifyTokenMock) }
        assertEquals(songDataMock, result)
    }

    @Test
    fun `WHEN getTablatureLink is called THEN return link`() = runTest {
        val result = viewModel.getTablatureLink(spotifyTokenMock)
        coVerify(exactly = 1) { googleService.invoke(spotifyTokenMock) }
        assertEquals(link, result)
    }

    @Test
    fun `WHEN update is called THEN update correct state`() = runTest {
        viewModel.update(DataState())
        val result = viewModel.dataState.value
        val expected = DataState()
        assertEquals(expected, result)

        viewModel.update(WhatIsPlayingState())
        val result2 = viewModel.whatIsPlayingState.value
        val expected2 = WhatIsPlayingState()
        assertEquals(expected2, result2)

        viewModel.events.test {
            viewModel.update(AppEvents.SpotifyLogin)
            assertEquals(AppEvents.SpotifyLogin, awaitItem())
        }
    }
}
