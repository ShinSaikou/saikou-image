package com.shinsaikou.image

import io.kotest.matchers.shouldBe
import okio.Buffer
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

/**
 * The format sniffer replaces the native decoder upstream called, so every signature it claims to know
 * is pinned here. The table has to agree with the reader's own probe.
 */
class ImageUtilTest {

    private fun header(vararg bytes: Int): ByteArray = ByteArray(32) { index ->
        if (index < bytes.size) bytes[index].toByte() else 0
    }

    private fun ftyp(brand: String): ByteArray {
        val header = header(0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70)
        brand.forEachIndexed { index, char -> header[8 + index] = char.code.toByte() }
        return header
    }

    private fun sniff(bytes: ByteArray): ImageUtil.ImageType? =
        ImageUtil.findImageType(ByteArrayInputStream(bytes))

    @Test
    fun `recognises the formats the reader routes`() {
        sniff(header(0xFF, 0xD8, 0xFF, 0xE0)) shouldBe ImageUtil.ImageType.JPEG
        sniff(header(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) shouldBe ImageUtil.ImageType.PNG
        sniff(header(0x47, 0x49, 0x46, 0x38, 0x37, 0x61)) shouldBe ImageUtil.ImageType.GIF
        sniff(header(0x47, 0x49, 0x46, 0x38, 0x39, 0x61)) shouldBe ImageUtil.ImageType.GIF
        sniff(
            header(0x52, 0x49, 0x46, 0x46, 0x24, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50),
        ) shouldBe ImageUtil.ImageType.WEBP
    }

    @Test
    fun `recognises the formats no decoder is registered for`() {
        sniff(ftyp("avif")) shouldBe ImageUtil.ImageType.AVIF
        sniff(ftyp("avis")) shouldBe ImageUtil.ImageType.AVIF
        sniff(ftyp("heic")) shouldBe ImageUtil.ImageType.HEIF
        sniff(ftyp("mif1")) shouldBe ImageUtil.ImageType.HEIF
        sniff(header(0xFF, 0x0A)) shouldBe ImageUtil.ImageType.JXL
        sniff(
            header(0x00, 0x00, 0x00, 0x0C, 0x4A, 0x58, 0x4C, 0x20, 0x0D, 0x0A, 0x87, 0x0A),
        ) shouldBe ImageUtil.ImageType.JXL
    }

    @Test
    fun `anything else is not an image`() {
        sniff(header(0x50, 0x4B, 0x03, 0x04)) shouldBe null // a zip archive
        sniff(ftyp("mp42")) shouldBe null
        sniff(ByteArray(0)) shouldBe null
    }

    @Test
    fun `a mime type wins over the bytes and an unknown one falls back`() {
        val png = header(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
        ImageUtil.getExtensionFromMimeType("image/gif") { ByteArrayInputStream(png) } shouldBe "gif"
        ImageUtil.getExtensionFromMimeType(null) { ByteArrayInputStream(png) } shouldBe "png"
        ImageUtil.getExtensionFromMimeType("text/html") { ByteArrayInputStream(png) } shouldBe "png"
        ImageUtil.getExtensionFromMimeType(null) { ByteArrayInputStream(ByteArray(4)) } shouldBe "jpg"
    }

    @Test
    fun `gif is animated and a still image is not`() {
        val gif = Buffer().write(header(0x47, 0x49, 0x46, 0x38, 0x39, 0x61))
        val jpeg = Buffer().write(header(0xFF, 0xD8, 0xFF, 0xE0))
        ImageUtil.isAnimatedAndSupported(gif) shouldBe true
        ImageUtil.isAnimatedAndSupported(jpeg) shouldBe false
    }

    @Test
    fun `a name with a known extension is an image without reading bytes`() {
        ImageUtil.isImage("page.jpg") shouldBe true
        ImageUtil.isImage("page.webp") shouldBe true
        ImageUtil.isImage("page.txt") shouldBe false
        ImageUtil.isImage(null) shouldBe false
    }
}
