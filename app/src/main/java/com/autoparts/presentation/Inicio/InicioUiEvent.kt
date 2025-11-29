package com.autoparts.presentation.Inicio

interface InicioUiEvent {
    data class LoadUser(val userId: String) : InicioUiEvent
    data class EmailChanged(val email: String) : InicioUiEvent
    data class PhoneNumberChanged(val phoneNumber: String) : InicioUiEvent
    data class SearchQueryChanged(val query: String) : InicioUiEvent
    data object Save : InicioUiEvent
    data object Logout : InicioUiEvent
    data object UserMessageShown : InicioUiEvent
    data object showDialogEdit: InicioUiEvent
    data object hideDialogEdit: InicioUiEvent
    data object LoadProductos : InicioUiEvent
}

