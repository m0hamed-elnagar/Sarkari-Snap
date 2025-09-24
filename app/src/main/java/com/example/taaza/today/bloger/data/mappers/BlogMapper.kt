package com.example.taaza.today.bloger.data.mappers

import android.util.Log
import com.example.taaza.today.bloger.data.database.PostEntity
import com.example.taaza.today.bloger.data.dto.PageDto
import com.example.taaza.today.bloger.data.dto.PostDto
import com.example.taaza.today.bloger.domain.Page
import com.example.taaza.today.bloger.domain.Post
import org.jsoup.Jsoup
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun toDomain(dto: PostDto): Post {
    val images = extractAllImages(dto.content)
    val plainText = parsePlainText(dto.content)
    val cleanHtml = runCatching {
        val doc = Jsoup.parse(dto.content)
        doc.select("img").remove()          // drop images
        doc.select("style, script").remove() // drop styles/scripts if any
        doc.body().html()                   // now works
    }.getOrDefault("")

    return Post(
        id = dto.id,
        title = dto.title,
        description = plainText,
        content = cleanHtml,
        date = dto.updated.toDateOnly(),
        rowDate = dto.updated,
        url = dto.url,
        imageUrls = images,
        labels = dto.labels
    )
}

private fun String?.toDateOnly(): String {
  this?.log()
    if (this.isNullOrBlank()) return ""
    return runCatching {
        val odt = OffsetDateTime.parse(this) // parse ISO-8601
        odt.format(DateTimeFormatter.ISO_LOCAL_DATE) // yyyy-MM-dd
    }.getOrDefault("")
}
fun String.log() {


    Log.d("Date", "plusSeconds: $this")


}


private fun parsePlainText(html: String): String {
    return runCatching {
        Jsoup.parse(html).wholeText()
            .replace("\\s+".toRegex(), " ") // collapse multiple spaces/newlines
            .trim()
    }.getOrDefault("")
}

private fun extractAllImages(html: String): List<String> =
    runCatching {
        Jsoup.parse(html)
            .select("img[src]")
            .mapNotNull { it.attr("abs:src") }   // ← every img
    }.getOrDefault(emptyList())

fun Post.toPostEntity() = PostEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    content = this.content,
    date = this.date,
    rowDate = this.rowDate,
    url = this.url,
    imageUrls = this.imageUrls,
    labels = this.labels
)

fun PostEntity.toPost() = Post(
    id = this.id,
    title = this.title,
    description = this.description,
    content = this.content,
    date = this.date,
    rowDate = this.rowDate,
    url = this.url,
    imageUrls = this.imageUrls,
    labels = this.labels
)
fun PageDto.toPage(): Page {
    val images = extractAllImages(content)
    val plainText = parsePlainText(content)
    val cleanHtml = runCatching {
        val doc = Jsoup.parse(content)
        doc.select("img").remove()          // drop images
        doc.select("style, script").remove() // drop styles/scripts if any
        doc.body().html()
    }.getOrDefault("")

    return Page(
        id = id,
        title = title,
        url = url,
        description = plainText,
        content = cleanHtml,
        imageUrls = images,
        date = updated.toDateOnly()
    )
}
