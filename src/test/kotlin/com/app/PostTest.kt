package com.app

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class PostTest {
    @Test
    fun testCreateAndGetPost() = testApplication {
        application {
            module()
        }

        val client = createClient {}

        // 1. สร้างโพสต์ใหม่
        val postResponse = client.post("/posts") {
            contentType(ContentType.Application.Json)
            setBody("""{ "title": "Test Post", "content": "This is a test." }""")
        }

        assertEquals(HttpStatusCode.Created, postResponse.status)

        // 2. ดึงโพสต์ทั้งหมด
        val getAll = client.get("/posts")
        assertEquals(HttpStatusCode.OK, getAll.status)
        assertTrue(getAll.bodyAsText().contains("Test Post"))

        // 3. ดึงโพสต์เดี่ยว
        val getSingle = client.get("/posts/1")
        assertEquals(HttpStatusCode.OK, getSingle.status)
        assertTrue(getSingle.bodyAsText().contains("This is a test."))
    }
}
