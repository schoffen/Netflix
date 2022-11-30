package com.example.netflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflix.model.Category
import com.example.netflix.model.Movie
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        callback.onPreExecute()

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            var buffer: BufferedInputStream? = null

            try {
                val requestURL = URL(url) // abrir URL
                urlConnection =
                    requestURL.openConnection() as HttpsURLConnection // abrir conexão
                urlConnection.readTimeout = 2000 // tempo leitura (2s)
                urlConnection.connectTimeout = 2000 // tempo conexão (2s)

                val statusCode = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // sequencia bytes
                //val jsonAsString = stream.bufferedReader().use { it.readText() } // bytes -> String
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                val categories = toCategories(jsonAsString)

                handler.post { callback.onResult(categories) }
            } catch (e: IOException) {
                val message = e.message ?: "erro desconhecido"
                handler.post { callback.onFailure(message) }
            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)

            val categoryTitle = jsonCategory.getString("title")

            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()

            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)

                val movieId = jsonMovie.getInt("id")
                val movieCoverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(movieId, movieCoverUrl))
            }

            categories.add(Category(categoryTitle, movies))
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }

        return String(baos.toByteArray())
    }

}