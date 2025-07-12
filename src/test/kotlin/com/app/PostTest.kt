package com.app

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import kotlin.test.*
//import org.junit.BeforeTest
import java.time.LocalDateTime

class PostTest {
    @BeforeTest
    fun setup() {
        // ใช้ H2 DB in-memory สำหรับการทดสอบ
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Posts, Comments)
        }
    }

    @Test
    fun `create post successfully`() {
        val post = transaction {
            PostEntity.new {
                title = "Test Title"
                content = "Test Content"
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
        }
        assertEquals("Test Title", post.title)
    }

    @Test
    fun `update post content`() {
        val post = transaction {
            PostEntity.new {
                title = "Old"
                content = "Old Content"
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
        }
        transaction {
            post.content = "New Content"
            post.flush()
        }
        assertEquals("New Content", transaction { post.content })
    }

    @Test
    fun `delete post should delete comments too`() {
        val post = transaction {
            PostEntity.new {
                title = "Post"
                content = "with comment"
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
        }

        transaction {
            CommentEntity.new {
                this.post = post
                authorName = "User"
                content = "Nice!"
                createdAt = LocalDateTime.now()
            }
        }

        transaction {
            post.delete()
        }

        val remaining = transaction { CommentEntity.all().toList() }
        assertTrue(remaining.isEmpty())
    }

    @Test
    fun `create comment successfully`() {
        val post = transaction {
            PostEntity.new {
                title = "Post"
                content = "Content"
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
        }

        val comment = transaction {
            CommentEntity.new {
                this.post = post
                authorName = "User"
                content = "This is a comment"
                createdAt = LocalDateTime.now()
            }
        }

        assertEquals("User", comment.authorName)
    }

    @Test
    fun `get post with comments`() {
        val postId = transaction {
            val post = PostEntity.new {
                title = "Post with comment"
                content = "Content"
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }

            CommentEntity.new {
                this.post = post
                authorName = "Bob"
                content = "Wow"
                createdAt = LocalDateTime.now()
            }

            post.id.value
        }

        val postWithComments = transaction {
            PostEntity.findById(postId)?.comments?.toList()
        }

        assertEquals(1, postWithComments?.size)
        assertEquals("Wow", postWithComments?.first()?.content)
    }
}
