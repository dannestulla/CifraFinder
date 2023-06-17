package br.gohan.cifrafinder.presenter

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.model.SongData
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.model.ScreenState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

@ExperimentalCoroutinesApi
class CifraViewModelTest {

    private lateinit var viewModel: CifraViewModel
    private val googleService = mockk<GoogleService>()
    private val spotifyService = mockk<SpotifyService>()
    private val savedState = SavedStateHandle()
    private val songDataMock = SongData("",1,1)
    private val dataStateMock = DataState("123")
    private val link = "link"
    private val spotifyTokenMock = "123"

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        viewModel = CifraViewModel(spotifyService, googleService, savedState)
        coEvery { spotifyService.invoke(spotifyTokenMock) } returns songDataMock
        coEvery { googleService.invoke(spotifyTokenMock) } returns link
    }

    /*@Test
    fun `WHEN startMusicFetch is called successfully THEN navigate to WebScreen`() = runTest() {
        viewModel.update(DataState(spotifyTokenMock))
        viewModel.events.test {
            viewModel.startMusicFetch()
            assertEquals(Events.WebScreen,awaitItem())
        }
    }*/

    @Test
    fun `WHEN startMusicFetch is called and no song is playing THEN show Snackbar`() = runTest() {
        viewModel.update(dataStateMock)
        viewModel.update(ScreenState("123"))
        coEvery { spotifyService.invoke(spotifyTokenMock) } returns null
        viewModel.events.test {
            viewModel.startMusicFetch()
            assertEquals(Events.ShowSnackbar(R.string.toast_no_song_being_played),awaitItem())
        }
    }

    @Test
    fun `WHEN startMusicFetch is called and tablature is null THEN show Snackbar`() = runTest {
        viewModel.update(dataStateMock)
        viewModel.update(ScreenState("123"))
        coEvery { googleService.invoke(any()) } returns null
        viewModel.events.test {
            viewModel.startMusicFetch()
            assertEquals(Events.ShowSnackbar(R.string.toast_google_search_error),awaitItem())
        }
    }

    @Test
    fun `WHEN getCurrentPlaying is called THEN return songDataModel`() = runTest {
        val result = viewModel.getCurrentPlaying(dataStateMock)
        coVerify(exactly = 1) { spotifyService.invoke(spotifyTokenMock)}
        assertEquals(songDataMock, result)
    }

    @Test
    fun `WHEN getTablatureLink is called THEN return link`() = runTest {
        val result = viewModel.getTablatureLink(spotifyTokenMock)
        coVerify(exactly = 1) { googleService.invoke(spotifyTokenMock)}
        assertEquals(link, result)
    }

    @Test
    fun `WHEN update is called THEN update correct state`() = runTest() {
        viewModel.update(DataState())
        val result = viewModel.dataState.value
        val expected = DataState()
        assertEquals(expected, result)

        viewModel.update(ScreenState())
        val result2 = viewModel.screenState.value
        val expected2 = ScreenState()
        assertEquals(expected2, result2)

        viewModel.events.test {
            viewModel.update(Events.FirstScreen)
            assertEquals(Events.FirstScreen,awaitItem())
        }
    }
}