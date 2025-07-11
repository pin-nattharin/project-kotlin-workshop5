package com.app

import kotlinx.serialization.Serializable

@Serializable
data class PostDTO(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val comments: List<CommentDTO> = emptyList()
)

@Serializable
data class CommentDTO(
    val id: Int,
    val authorName: String,
    val content: String,
    val createdAt: String
)

@Serializable
data class PostInput(val title: String, val content: String)

@Serializable
data class CommentInput(val authorName: String, val content: String)

fun PostEntity.toDTO(includeComments: Boolean = false): PostDTO = PostDTO(
    id.value, title, content, createdAt.toString(), updatedAt.toString(),
    if (includeComments) comments.map { it.toDTO() } else emptyList()
)

fun CommentEntity.toDTO(): CommentDTO = CommentDTO(
    id.value, authorName, content, createdAt.toString()
)