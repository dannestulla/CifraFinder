package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.SerpApiResponse
import br.gohan.cifrafinder.data.model.SerpApiResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import retrofit2.Response

class GoogleServiceTest {
    private lateinit var repository: MainRepository
    private lateinit var googleService: GoogleService
    private lateinit var context: Context
    private lateinit var crashlytics: FirebaseCrashlytics

    @Before
    fun setup() {
        repository = mockk()
        context = mockk(relaxed = true)
        crashlytics = mockk(relaxed = true)

        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns crashlytics

        googleService = GoogleService(repository, context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN invoke is called THEN return link`() = runTest {
        coEvery { repository.getSerperSearchResult(any(), any()) } returns Response.success(serpApiResponseMock)
        val result = googleService.invoke("search")
        assertTrue(result.isSuccess)
        assertEquals("link", result.getOrNull())
    }

    @Test
    fun `WHEN filterSearch is called THEN remove unwanted characters from string`() {
        val result = googleService.filterSearch("Zezé de Camargo e Luciano - Ao vivo - música")
        val expected = "Zezé de Camargo e Luciano  música"
        assertEquals(expected, result)
    }

    @Test
    fun `WHEN handleResult is called and response is successful THEN return link`() {
        val result = googleService.handleResponse(Response.success(serpApiResponseMock))
        val expected = "link"
        assertEquals(expected, result)
    }

    private val serpApiResponseMock = SerpApiResponse(listOf(SerpApiResult("link")))
}
