package com.easyfetch.app.domain

import com.easyfetch.app.data.model.Platform

object UrlValidator {

    private val urlExtractionPattern = Regex("(https?://\\S+)")

    private val platformPatterns: Map<Platform, Regex> = linkedMapOf(
        Platform.TIKTOK to Regex(
            "^(https?://)?([a-z0-9-]+\\.)?tiktok\\.com/\\S+",
            RegexOption.IGNORE_CASE
        ),
        Platform.FACEBOOK to Regex(
            "^(https?://)?([a-z0-9-]+\\.)?(facebook\\.com|fb\\.watch)/\\S+",
            RegexOption.IGNORE_CASE
        ),
        Platform.INSTAGRAM to Regex(
            "^(https?://)?([a-z0-9-]+\\.)?instagram\\.com/\\S+",
            RegexOption.IGNORE_CASE
        ),
        Platform.X to Regex(
            "^(https?://)?([a-z0-9-]+\\.)?(twitter\\.com|x\\.com)/\\S+",
            RegexOption.IGNORE_CASE
        ),
        Platform.PINTEREST to Regex(
            "^(https?://)?([a-z0-9-]+\\.)?(pinterest\\.[a-z.]+|pin\\.it)/\\S+",
            RegexOption.IGNORE_CASE
        ),
        Platform.YOUTUBE to Regex(
            "^(https?://)?([a-z0-9-]+\\.)?(youtube\\.com|youtu\\.be)/\\S+",
            RegexOption.IGNORE_CASE
        )
    )

    fun detectPlatform(input: String): Platform? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null
        return platformPatterns.entries.firstOrNull { (_, regex) -> regex.matches(trimmed) }?.key
    }

    fun isSupportedUrl(input: String): Boolean = detectPlatform(input) != null

    fun extractFirstUrl(text: String): String? = urlExtractionPattern.find(text)?.value
}
