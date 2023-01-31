package br.gohan.cifrafinder.data

class CifraRepository(
    private val cifraApi: CifraApi
) {
    suspend fun getCurrentlyPlaying(spotifyToken: String) =
        cifraApi.getCurrentlyPlaying(spotifyBaseUrl + spotifyEndPoint, "Bearer $spotifyToken")

    suspend fun getGoogleSearchResult(
        apiKey1: String,
        apiKey2: String,
        searchInput: String
    ) =
        cifraApi.getGoogleSearchResult(googleSearchUrl, apiKey1, apiKey2, searchInput)

    companion object {
        const val spotifyBaseUrl = "https://api.spotify.com/"
        const val spotifyEndPoint = "v1/me/player/currently-playing"
        const val googleSearchUrl = "https://customsearch.googleapis.com/customsearch/v1/"
    }
}