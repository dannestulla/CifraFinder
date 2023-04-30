package br.gohan.cifrafinder.data

import br.gohan.cifrafinder.data.model.GoogleJson
import br.gohan.cifrafinder.data.model.Item
import br.gohan.cifrafinder.data.model.SpotifyJson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CifraRepositoryTest() {
    private var api = mockk<CifraApi>()
    private var cifraRepository = CifraRepository(api)

    @Test
    fun `WHEN getCurrently playing is called THEN return SpotifyJson`() = runTest {
        coEvery { api.getCurrentlyPlaying(any(), any()) } returns Response.success(
            SpotifyJson(
                Item(
                    listOf(), "name", 1
                ), 1
            )
        )
        val expected = SpotifyJson(Item(listOf(), "name", 1), 1)
        val result = cifraRepository.getCurrentlyPlaying("123").body()
        coVerify(exactly = 1) { api.getCurrentlyPlaying(any(), any()) }
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN getGoogleSearchResult is called THEN return GoogleJson`() = runTest {
        coEvery { api.getGoogleSearchResult(any(), any(), any(), any()) } returns Response.success(
            GoogleJson(
                listOf()
            )
        )
        val expected =GoogleJson(listOf())
        val result = cifraRepository.getGoogleSearchResult("123", "123", "123").body()
        coVerify(exactly = 1) { api.getGoogleSearchResult(any(), any(), any(), any()) }
        assertEquals(expected, result)
    }
}