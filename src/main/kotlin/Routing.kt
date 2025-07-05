package com.nattharin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val content: String,
    val isDone: Boolean
)

@Serializable
data class TaskRequest(
    val content: String,
    val isDone: Boolean
)

object TaskRepository {
    private val tasks = mutableListOf<Task>(
        Task(id = 1, content = "Learn Ktor", isDone = true),
        Task(id = 2, content = "Build a REST API", isDone = false),
        Task(id = 3, content = "Write Unit Tests", isDone = false)
    )
    private var nextId = tasks.maxOfOrNull { it.id }?.plus(1) ?: 1

    fun getAll(): List<Task> = tasks

    fun getById(id: Int): Task? = tasks.find { it.id == id }

    fun add(task: Task): Task {
        val taskWithId = task.copy(id = nextId++)
        tasks.add(taskWithId)
        return taskWithId
    }

    fun update(id: Int, updatedTask: Task): Boolean {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            tasks[index] = updatedTask.copy(id = id)
            return true
        }
        return false
    }

    fun delete(id: Int): Boolean = tasks.removeIf { it.id == id }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello ณัฐรินทร์ อาณัติธนันท์กุล!")
        }
        get("/tasks") {
            val tasks = TaskRepository.getAll()
            call.respond(tasks)
        }

        get("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task ID")
                return@get
            }
            val task = TaskRepository.getById(id)
            if (task != null) {
                call.respond(task)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }

        post("/tasks") {
            val taskRequest = call.receive<TaskRequest>()
            val task = Task(id = 0, content = taskRequest.content, isDone = taskRequest.isDone)
            val saved = TaskRepository.add(task)
            call.respond(HttpStatusCode.Created, saved)
        }

        put("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task ID")
                return@put
            }
            val taskRequest = call.receive<TaskRequest>()
            val updated = Task(id = id, content = taskRequest.content, isDone = taskRequest.isDone)

            if (TaskRepository.update(id, updated)) {
                call.respond(HttpStatusCode.OK, updated)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }

        delete("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task ID")
                return@delete
            }

            if (TaskRepository.delete(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
    }
}
