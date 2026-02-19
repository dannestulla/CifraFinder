package br.gohan.cifrafinder.data

import br.gohan.cifrafinder.data.model.Album
import br.gohan.cifrafinder.data.model.Image
import br.gohan.cifrafinder.data.model.Item
import br.gohan.cifrafinder.data.model.SerpApiResponse
import br.gohan.cifrafinder.data.model.SpotifyJson
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class MainRepositoryTest {
    private lateinit var api: CifraApi
    private lateinit var mainRepository: MainRepository
    private lateinit var crashlytics: FirebaseCrashlytics

    @Before
    fun setup() {
        api = mockk()
        crashlytics = mockk(relaxed = true)

        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns crashlytics

        mainRepository = MainRepository(api)
    }

    @Test
    fun `WHEN getCurrentlyPlaying is called THEN return SpotifyJson`() = runTest {
        val albumMock = Album(listOf(Image("url")), "album")
        val spotifyJson = SpotifyJson(Item(listOf(), "name", 1, albumMock), 1)

        coEvery { api.getCurrentlyPlaying(any(), any()) } returns Response.success(spotifyJson)

        val result = mainRepository.getCurrentlyPlaying("123").body()
        coVerify(exactly = 1) { api.getCurrentlyPlaying(any(), any()) }
        assertEquals(spotifyJson, result)
    }

    @Test
    fun `WHEN getSerperSearchResult is called THEN return SerpApiResponse`() = runTest {
        val serpApiResponse = SerpApiResponse(listOf())

        coEvery { api.getSerperSearchResult(any(), any(), any()) } returns Response.success(serpApiResponse)

        val result = mainRepository.getSerperSearchResult("apiKey", "search").body()
        coVerify(exactly = 1) { api.getSerperSearchResult(any(), any(), any()) }
        assertEquals(serpApiResponse, result)
    }
}
