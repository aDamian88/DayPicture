package com.adamian.daypicture.ui.main.state

sealed class MainStateEvent {

    class GetDataEvent(
        val dataId: String
    ): MainStateEvent()

    class None: MainStateEvent()


}