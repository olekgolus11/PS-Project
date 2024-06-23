package main.adapters

import com.squareup.moshi.*
import java.sql.Timestamp

class TimestampAdapter {
    @ToJson
    fun toJson(timestamp: Timestamp): String {
        return timestamp.toString()
    }

    @FromJson
    fun fromJson(timestamp: String): Timestamp {
        return Timestamp.valueOf(timestamp)
    }
}