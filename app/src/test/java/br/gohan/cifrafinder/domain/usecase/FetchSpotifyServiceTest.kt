package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.ArtistX
import br.gohan.cifrafinder.data.model.Item
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class FetchSpotifyServiceTest {
    private lateinit var repository: CifraRepository
    private lateinit var fetchSpotifyService: FetchSpotifyService

    @Before
    fun setup() {
        repository = mockk<CifraRepository>()
        fetchSpotifyService = FetchSpotifyService(repository)
    }

    @Test
    fun `WHEN response is successful THEN handleResponse returns currentSongModel`() {
        val result = fetchSpotifyService.handleResponse(Response.success(spotifyJsonMock))
        val expected = currentSongModelMock
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN invoke is successful THEN return currentSongModel`() = runBlocking {
        coEvery { repository.getCurrentlyPlaying("token") } returns Response.success(spotifyJsonMock)
        val result = fetchSpotifyService.invoke("token")
        val expected =  CurrentSongModel(searchStringMock, 5, 3)
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN setCurrentSongData is called THEN return currentSongModel`() {
        val result = fetchSpotifyService.setCurrentSongData(" artist songName ", spotifyJsonMock)
        val expected = currentSongModelMock
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN setSearchString is called THEN return searched artist and song`() {
        val result = fetchSpotifyService.setSearchString(spotifyJsonMock)
        val expected = searchStringMock
        assertEquals(expected, result)
    }

    private val spotifyJsonMock = SpotifyJson(Item(listOf(ArtistX("artist")), "songName", 5), 3)

    private val currentSongModelMock = CurrentSongModel(" artist songName ", 5, 3)

    private val searchStringMock = " artist songName "
}