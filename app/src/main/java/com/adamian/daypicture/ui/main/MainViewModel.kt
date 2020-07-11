package com.adamian.daypicture.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adamian.daypicture.model.DayPicture
import com.adamian.daypicture.repository.Repository
import com.adamian.daypicture.ui.main.state.MainStateEvent
import com.adamian.daypicture.ui.main.state.MainStateEvent.*
import com.adamian.daypicture.ui.main.state.MainViewState
import com.adamian.daypicture.util.AbsentLiveData
import com.adamian.daypicture.util.DataState

class MainViewModel : ViewModel(){

    private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

    val viewState: LiveData<MainViewState>
        get() = _viewState


    val dataState: LiveData<DataState<MainViewState>> = Transformations
        .switchMap(_stateEvent){stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>>{
        println("DEBUG: New StateEvent detected: $stateEvent")
        when(stateEvent){

            is GetDataEvent -> {
                return Repository.getPicture()
            }

            is None ->{
                return AbsentLiveData.create()
            }
        }
    }


    fun setPicture(dayPicture: DayPicture){
        val update = getCurrentViewStateOrNew()
        update.dayPicture = dayPicture
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): MainViewState {
        val value = viewState.value?.let{
            it
        }?: MainViewState()
        return value
    }

    fun setStateEvent(event: MainStateEvent){
        val state: MainStateEvent
        state = event
        _stateEvent.value = state
    }
}













