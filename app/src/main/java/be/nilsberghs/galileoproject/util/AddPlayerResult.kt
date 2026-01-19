package be.nilsberghs.galileoproject.util

import be.nilsberghs.galileoproject.data.Player

sealed class AddPlayerResult {
    object Success : AddPlayerResult()
    object AlreadyExists : AddPlayerResult()
    data class DeletedExists(val player: Player) : AddPlayerResult()
}