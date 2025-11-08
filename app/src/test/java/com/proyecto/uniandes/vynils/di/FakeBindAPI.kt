package com.proyecto.uniandes.vynils.di

import com.proyecto.uniandes.vynils.data.model.RequestAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseAlbum
import com.proyecto.uniandes.vynils.data.model.ResponseArtist
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
object FakeBindAPI {
    private val artists = listOf(
        ResponseArtist(id = 1, name = "Shakira", image = "https://example.com/shakira.jpg"),
        ResponseArtist(id = 2, name = "Juanes", image = "https://example.com/juanes.jpg"),
        ResponseArtist(id = 3, name = "Carlos Vives", image = "https://example.com/carlosvives.jpg")
    )
    private val albums = mutableListOf(
        ResponseAlbum(
            id = 1,
            name = "Buscando América",
            cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
            releaseDate = "1984-08-01T00:00:00.000Z",
            description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino.",
            genre = "Salsa",
            recordLabel = "Elektra"
        ),
        ResponseAlbum(
            id = 2,
            name = "Poeta del pueblo",
            cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg",
            releaseDate = "1998-07-15T00:00:00.000Z",
            description = "Recopilación de grandes éxitos de Joan Sebastian, conocido como 'El Poeta del Pueblo'.",
            genre = "Regional Mexicano",
            recordLabel = "Musart"
        ),
        ResponseAlbum(
            id = 3,
            name = "A Day at the Races",
            cover = "https://upload.wikimedia.org/wikipedia/en/7/79/A_Day_at_the_Races_%28album%29.jpg",
            releaseDate = "1976-12-10T00:00:00.000Z",
            description = "Es el quinto álbum de estudio de la banda de rock británica Queen, lanzado en diciembre de 1976.",
            genre = "Rock",
            recordLabel = "EMI"
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
                    cover = album.cover,
                    releaseDate = album.releaseDate,
                    description = album.description,
                    genre = album.genre,
                    recordLabel = album.recordLabel
                )
                albums.add(created)
                return Response.success(created)
            }

            override suspend fun getAlbumById(id: Int): Response<ResponseAlbum> {
                val album = albums.find { it.id == id }
                return if (album != null) {
                    Response.success(album)
                } else {
                    Response.error(404, null)
                }
            }

            override suspend fun getAllArtist(): Response<List<ResponseArtist>> {
                return Response.success(artists)
            }

        }
    }
}
