package com.proyecto.uniandes.vynils.di

import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.network.VinylApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import retrofit2.Response
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestAlbumModule {

    private val albums = mutableListOf(
        ResponseAlbum(
            id = 1,
            name = "Buscando América",
            cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg"
        ),
        ResponseAlbum(
            id = 2,
            name = "Poeta del pueblo",
            cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg"
        ),
        ResponseAlbum(
            id = 3,
            name = "A Day at the Races",
            cover = "https://upload.wikimedia.org/wikipedia/en/7/79/A_Day_at_the_Races_%28album%29.jpg"
        )
    )

    private var nextId = 4

    @Provides
    @Singleton
    fun provideFakeVinylApiService(): VinylApiService {
        return object : VinylApiService {
            override suspend fun getAllAlbums(): Response<List<ResponseAlbum>> {
                return Response.success(albums.toList())
            }

            override suspend fun createAlbum(album: RequestAlbum): Response<ResponseAlbum> {
                val created = ResponseAlbum(
                    id = nextId++,
                    name = album.name,
                    cover = album.cover
                )
                albums.add(created)
                return Response.success(created)
            }
        }
    }
}
