package be.nilsberghs.galileoproject.domain

sealed class AddPlayerResult {
    object Success : AddPlayerResult()
    object AlreadyExists : AddPlayerResult()
    data class DeletedExists(val player: Player) : AddPlayerResult()
}