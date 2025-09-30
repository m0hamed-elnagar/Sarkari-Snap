package com.rawderm.taaza.today.bloger.data.mappers

import android.util.Log
import com.rawderm.taaza.today.bloger.data.database.PostEntity
import com.rawderm.taaza.today.bloger.data.dto.PageDto
import com.rawderm.taaza.today.bloger.data.dto.PostDto
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
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

    // Extract direct video URLs instead of using token approach
    val videoUrl = extractDirectVideoUrl(dto.content)

    Log.d("VideoDebug", "Video URL: $videoUrl")

    return Post(
        id = dto.id,
        title = dto.title,
        description = plainText,
        content = cleanHtml,
        date = dto.updated.toDateOnly(),
        rowDate = dto.updated,
        url = dto.url,
        imageUrls = images,
        videoUrl = videoUrl,
        labels = dto.labels
    )
}

// Extract direct video URLs from embed codes or iframe sources
fun extractDirectVideoUrl(postContent: String): String? {
    val doc = Jsoup.parse(postContent)

    // Look for video tags with src attribute
    doc.select("video[src]").firstOrNull()?.let { video ->
        val src = video.attr("abs:src")
        Log.d("VideoExtraction", "Found video tag with src: $src")
        return src.takeIf { it.isNotBlank() }
    }

    // Look for iframe sources (common for embedded videos)
    doc.select("iframe[src]").firstOrNull()?.let { iframe ->
        val src = iframe.attr("abs:src")
        Log.d("VideoExtraction", "Found iframe with src: $src")

        // Check if it's a video URL
        if (isVideoUrl(src)) {
            return src
        }
    }

    // Look for source tags inside video tags
    doc.select("video source[src]").firstOrNull()?.let { source ->
        val src = source.attr("abs:src")
        Log.d("VideoExtraction", "Found video source with src: $src")
        return src.takeIf { it.isNotBlank() }
    }

    // Look for data-src or other video attributes
    doc.select("[data-src*='video'], [src*='video']").firstOrNull()?.let { elem ->
        val src = elem.attr("abs:src").ifBlank { elem.attr("abs:data-src") }
        if (src.isNotBlank() && isVideoUrl(src)) {
            Log.d("VideoExtraction", "Found video via data-src: $src")
            return src
        }
    }

    Log.d("VideoExtraction", "No direct video URL found in content")
    return null
}

// Check if URL is likely a video file
private fun isVideoUrl(url: String): Boolean {
    val videoPatterns = listOf(
        "\\.mp4", "\\.webm", "\\.ogg", "\\.mov", "\\.m3u8",
        "\\.3gp", "\\.mkv", "video/", "stream", "youtube",
        "vimeo", "dailymotion"
    )
    return videoPatterns.any { pattern ->
        url.contains(pattern, ignoreCase = true)
    }
}

private fun String?.toDateOnly(): String {
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

// Enhanced token extraction to handle more cases
fun extractBloggerVideoToken(postContent: String): String? {
    // Try multiple patterns to extract the token
    val patterns = listOf(
        """contentid\\*=\s*["']([\w]+)["']""".toRegex(),
        """contentid\s*=\s*["']([\w]+)["']""".toRegex(),
        """token["']?\s*:\s*["']([\w]+)["']""".toRegex()
    )

    for (pattern in patterns) {
        val match = pattern.find(postContent)
        if (match != null) {
            Log.d("TokenExtraction", "Found token with pattern: ${pattern.pattern} -> ${match.groupValues[1]}")
            return match.groupValues[1]
        }
    }

    Log.d("TokenExtraction", "No token found in content")
    return null
}

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
// plain Kotlin, no Android context needed
fun extractBloggerVideoToken2(postContent: String): String? {
    val regex = """contentid\\*=\s*["']([\w]+)["']""".toRegex()
    return regex.find(postContent)?.groupValues?.get(1)
}