package com.native_call_test.native_call_test

import kotlinx.coroutines.delay

data class User(val id: Int, val name: String, val role: String)

class UserRepository {
    suspend fun fetchUser(id: Int): User {
        delay(1500)
        return if (id == 42) {
            User(id, "Leandro Simões", "Desenvolvedor Android")
        } else {
            User(id, "Usuário Desconhecido", "Visitante")
        }
    }
}