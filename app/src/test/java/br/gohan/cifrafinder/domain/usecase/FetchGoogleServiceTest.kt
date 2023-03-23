package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import br.gohan.cifrafinder.data.model.VItems
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import retrofit2.Response

class FetchGoogleServiceTest() {
    private lateinit var repository: CifraRepository
    private lateinit var fetchGoogleService: FetchGoogleService

    @Before
    fun setup() {
        repository = mockk()
        fetchGoogleService = FetchGoogleService(repository)
    }

    @Test
    fun `WHEN invoke is called THEN return link`() = runBlocking {
        coEvery { repository.getGoogleSearchResult(any(), any(), any()) } returns Response.success(googleJsonMock)
        val result = fetchGoogleService.invoke("search")
        val expected = "link"
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN filterSearch is called THEN remove unwanted characters from string`() {
        val result = fetchGoogleService.filterSearch("Zezé de Camargo e Luciano - Ao vivo - música")
        val expected = "Zezé de Camargo e Luciano  música"
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN handleResult is called and response is successfull THEN return link`() {
        val result = fetchGoogleService.handleResult(Response.success(googleJsonMock))
        val expected = "link"
        assertEquals(result, expected)
    }

    private val googleJsonMock = GoogleJson(listOf(VItems("link")))
}