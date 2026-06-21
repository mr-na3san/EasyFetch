package com.easyfetch.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import com.easyfetch.app.data.network.ExtractionApiService
import com.easyfetch.app.data.network.NetworkModule
import com.easyfetch.app.data.network.OpenGraphFetcher
import com.easyfetch.app.data.repository.ExtractionRepository
import com.easyfetch.app.data.repository.ExtractionRepositoryImpl
import com.easyfetch.app.data.repository.MediaStorageRepository
import com.easyfetch.app.data.repository.MediaStorageRepositoryImpl

class EasyFetchApplication : Application(), ImageLoaderFactory {

    lateinit var extractionRepository: ExtractionRepository
        private set

    lateinit var mediaStorageRepository: MediaStorageRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val httpClient = NetworkModule.provideOkHttpClient()
        val api = ExtractionApiService(BuildConfig.EXTRACTION_API_BASE_URL, httpClient)
        val openGraphFetcher = OpenGraphFetcher(httpClient)

        extractionRepository = ExtractionRepositoryImpl(api, openGraphFetcher)
        mediaStorageRepository = MediaStorageRepositoryImpl(applicationContext, httpClient)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components { add(VideoFrameDecoder.Factory()) }
            .build()
    }
}
