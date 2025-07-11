package com.app

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.dao.id.EntityID


object Posts : IntIdTable("posts") {
    val title = varchar("title", 255)
    val content = text("content")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

object Comments : IntIdTable("comments") {
    val post = reference("post_id", Posts, onDelete = ReferenceOption.CASCADE)
    val authorName = varchar("author_name", 100)
    val content = text("content")
    val createdAt = datetime("created_at")
}

class PostEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PostEntity>(Posts)

    var title by Posts.title
    var content by Posts.content
    var createdAt by Posts.createdAt
    var updatedAt by Posts.updatedAt
    val comments by CommentEntity referrersOn Comments.post
}

class CommentEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommentEntity>(Comments)

    var post by PostEntity referencedOn Comments.post
    var authorName by Comments.authorName
    var content by Comments.content
    var createdAt by Comments.createdAt
}