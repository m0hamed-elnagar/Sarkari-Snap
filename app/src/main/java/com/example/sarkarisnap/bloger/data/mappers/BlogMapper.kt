package com.example.sarkarisnap.bloger.data.mappers

import com.example.sarkarisnap.bloger.data.dto.PostDto
import com.example.sarkarisnap.bloger.domain.Post
import org.jsoup.Jsoup
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
        url = dto.url,
        imageUrls = images,
        labels = dto.labels
    )
}
private fun String?.toDateOnly(): String {
    if (this.isNullOrBlank()) return ""
    return runCatching {
        val odt = OffsetDateTime.parse(this) // parse ISO-8601
        odt.format(DateTimeFormatter.ISO_LOCAL_DATE) // yyyy-MM-dd
    }.getOrDefault("")
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