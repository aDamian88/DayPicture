package com.adamian.daypicture.ui.main.state

sealed class MainStateEvent {

    class GetUserEvent(
        val userId: String
    ): MainStateEvent()

    class None: MainStateEvent()


}