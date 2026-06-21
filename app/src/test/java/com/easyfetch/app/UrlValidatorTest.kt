package com.easyfetch.app

import com.easyfetch.app.data.model.Platform
import com.easyfetch.app.domain.UrlValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun detectsTikTokUrls() {
        assertEquals(Platform.TIKTOK, UrlValidator.detectPlatform("https://www.tiktok.com/@user/video/123"))
        assertEquals(Platform.TIKTOK, UrlValidator.detectPlatform("https://vm.tiktok.com/ABCDEF/"))
    }

    @Test
    fun detectsFacebookUrls() {
        assertEquals(Platform.FACEBOOK, UrlValidator.detectPlatform("https://www.facebook.com/watch/?v=123"))
        assertEquals(Platform.FACEBOOK, UrlValidator.detectPlatform("https://fb.watch/abc123/"))
    }

    @Test
    fun detectsInstagramUrls() {
        assertEquals(Platform.INSTAGRAM, UrlValidator.detectPlatform("https://www.instagram.com/reel/abc123/"))
    }

    @Test
    fun detectsXUrls() {
        assertEquals(Platform.X, UrlValidator.detectPlatform("https://x.com/user/status/123"))
        assertEquals(Platform.X, UrlValidator.detectPlatform("https://twitter.com/user/status/123"))
    }

    @Test
    fun detectsPinterestUrls() {
        assertEquals(Platform.PINTEREST, UrlValidator.detectPlatform("https://www.pinterest.com/pin/123/"))
        assertEquals(Platform.PINTEREST, UrlValidator.detectPlatform("https://pin.it/abc123"))
    }

    @Test
    fun detectsYouTubeUrls() {
        assertEquals(Platform.YOUTUBE, UrlValidator.detectPlatform("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals(Platform.YOUTUBE, UrlValidator.detectPlatform("https://youtu.be/dQw4w9WgXcQ"))
        assertEquals(Platform.YOUTUBE, UrlValidator.detectPlatform("https://www.youtube.com/shorts/abc123"))
    }

    @Test
    fun returnsNullForUnsupportedOrEmptyInput() {
        assertNull(UrlValidator.detectPlatform("https://www.example.com/watch?v=123"))
        assertNull(UrlValidator.detectPlatform(""))
        assertNull(UrlValidator.detectPlatform("not a url"))
    }

    @Test
    fun extractsFirstUrlFromSharedText() {
        val sharedText = "Check this out! https://www.tiktok.com/@user/video/123 it's great"
        assertEquals("https://www.tiktok.com/@user/video/123", UrlValidator.extractFirstUrl(sharedText))
    }
}
