package com.app

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Application.blogRoutes() {
    routing {
        route("/posts") {
            get {
                val posts = transaction { PostEntity.all().map { it.toDTO() } }
                call.respond(HttpStatusCode.OK, posts)
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val post = transaction { id?.let { PostEntity.findById(it)?.toDTO(true) } }
                if (post != null) call.respond(HttpStatusCode.OK, post)
                else call.respond(HttpStatusCode.NotFound, "Post not found")
            }

            post {
                val input = call.receive<PostInput>()
                val post = transaction {
                    PostEntity.new {
                        title = input.title
                        content = input.content
                        createdAt = LocalDateTime.now()
                        updatedAt = LocalDateTime.now()
                    }.toDTO()
                }
                call.respond(HttpStatusCode.Created, post)
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val input = call.receive<PostInput>()
                val updated = transaction {
                    id?.let {
                        PostEntity.findById(it)?.apply {
                            title = input.title
                            content = input.content
                            updatedAt = LocalDateTime.now()
                        }?.toDTO()
                    }
                }
                if (updated != null) call.respond(HttpStatusCode.OK, updated)
                else call.respond(HttpStatusCode.NotFound, "Post not found")
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val deleted = transaction {
                    id?.let {
                        PostEntity.findById(it)?.apply {
                            comments.forEach { it.delete() }
                            delete()
                        }
                    } != null
                }
                if (deleted) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.NotFound, "Post not found")
            }

            post("{id}/comments") {
                val id = call.parameters["id"]?.toIntOrNull()
                val input = call.receive<CommentInput>()
                val comment = transaction {
                    id?.let {
                        PostEntity.findById(it)?.let { post ->
                            CommentEntity.new {
                                this.post = post
                                authorName = input.authorName
                                content = input.content
                                createdAt = LocalDateTime.now()
                            }.toDTO()
                        }
                    }
                }
                if (comment != null) call.respond(HttpStatusCode.Created, comment)
                else call.respond(HttpStatusCode.NotFound, "Post not found")
            }
        }
    }
}