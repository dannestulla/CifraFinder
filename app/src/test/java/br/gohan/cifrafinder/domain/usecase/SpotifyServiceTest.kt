package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.Album
import br.gohan.cifrafinder.data.model.ArtistX
import br.gohan.cifrafinder.data.model.Image
import br.gohan.cifrafinder.data.model.Item
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.SongData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SpotifyServiceTest {
    private lateinit var repository: MainRepository
    private lateinit var spotifyService: SpotifyService
    private lateinit var context: Context
    private lateinit var crashlytics: FirebaseCrashlytics

    @Before
    fun setup() {
        repository = mockk()
        context = mockk(relaxed = true)
        crashlytics = mockk(relaxed = true)

        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns crashlytics

        spotifyService = SpotifyService(repository, context)
    }

    @Test
    fun `WHEN response is successful THEN handleResponse returns currentSongModel`() {
        val result = spotifyService.handleResponse(Response.success(spotifyJsonMock))
        assertEquals(songDataMock, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN invoke is successful THEN return currentSongModel`() = runTest {
        coEvery { repository.getCurrentlyPlaying("token") } returns Response.success(spotifyJsonMock)
        val result = spotifyService.invoke("token")
        assertTrue(result.isSuccess)
        assertEquals(songDataMock, result.getOrNull())
    }

    @Test
    fun `WHEN setCurrentSongData is called THEN return currentSongModel`() {
        val result = spotifyService.createModel(spotifyJsonMock)
        assertEquals(songDataMock, result)
    }

    @Test
    fun `WHEN setSearchString is called THEN return searched artist and song`() {
        val result = spotifyService.setSearchString(spotifyJsonMock)
        val expected = " songName - artist "
        assertEquals(expected, result)
    }

    private val albumMock = Album(listOf(Image("imageUrl")), "albumName")

    private val spotifyJsonMock = SpotifyJson(
        Item(listOf(ArtistX("artist")), "songName", 5, albumMock),
        3
    )

    private val songDataMock = SongData(" songName - artist ", 5, 3, "imageUrl")
}
