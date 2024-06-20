package main.adapters

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import main.data_classes.Config
import java.io.InputStream

class JsonConfigAdapter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(Config::class.java)

    fun fromJson(inputStream: InputStream): Config {
        val json = inputStream.bufferedReader().readText()
        return adapter.fromJson(json)!!
    }
}