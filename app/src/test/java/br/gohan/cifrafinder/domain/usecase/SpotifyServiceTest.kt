package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.ArtistX
import br.gohan.cifrafinder.data.model.Item
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.SongData
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class SpotifyServiceTest {
    private lateinit var repository: CifraRepository
    private lateinit var spotifyService: SpotifyService

    @Before
    fun setup() {
        repository = mockk<CifraRepository>()
        spotifyService = SpotifyService(repository)
    }

    @Test
    fun `WHEN response is successful THEN handleResponse returns currentSongModel`() {
        val result = spotifyService.handleResponse(Response.success(spotifyJsonMock))
        val expected = songDataMock
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN invoke is successful THEN return currentSongModel`() = runBlocking {
        coEvery { repository.getCurrentlyPlaying("token") } returns Response.success(spotifyJsonMock)
        val result = spotifyService.invoke("token")
        val expected =  SongData(searchStringMock, 5, 3)
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN setCurrentSongData is called THEN return currentSongModel`() {
        val result = spotifyService.createModel(spotifyJsonMock)
        val expected = songDataMock
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN setSearchString is called THEN return searched artist and song`() {
        val result = spotifyService.setSearchString(spotifyJsonMock)
        val expected = searchStringMock
        assertEquals(expected, result)
    }

    private val spotifyJsonMock = SpotifyJson(Item(listOf(ArtistX("artist")), "songName", 5), 3)

    private val songDataMock = SongData(" artist songName ", 5, 3)

    private val searchStringMock = " songName - artist "
}