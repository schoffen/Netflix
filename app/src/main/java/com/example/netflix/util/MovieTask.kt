package com.example.netflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflix.model.Category
import com.example.netflix.model.Movie
import com.example.netflix.model.MovieDetail
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onPreExecute()
        fun onResult(movieDetail: MovieDetail)
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

                if (statusCode == 400) {
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonAsString = toString(buffer)

                    val json = JSONObject(jsonAsString)
                    val message = json.getString("message")
                    throw IOException(message)

                } else if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // sequencia bytes
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                val movieDetail = toMovieDetail(jsonAsString)

                handler.post { callback.onResult(movieDetail) }
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

    private fun toMovieDetail(jsonAsString: String) : MovieDetail {
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")

        val jsonMovies = json.getJSONArray("movie")
        val similars = mutableListOf<Movie>()
        for (i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            similars.add(Movie(id = similarId, coverUrl = similarCoverUrl))
        }

        val movie = Movie(id, coverUrl, title, desc, cast)

        return MovieDetail(movie, similars)
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