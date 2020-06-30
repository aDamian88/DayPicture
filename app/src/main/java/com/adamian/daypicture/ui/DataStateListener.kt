package com.adamian.daypicture.ui

import com.adamian.daypicture.util.DataState

interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)
}