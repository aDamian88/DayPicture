package com.adamian.daypicture.repository

import androidx.lifecycle.LiveData
import com.adamian.daypicture.api.MyRetrofitBuilder
import com.adamian.daypicture.model.DayPicture
import com.adamian.daypicture.ui.main.state.MainViewState
import com.adamian.daypicture.util.*

object Repository {

    fun getPicture(): LiveData<DataState<MainViewState>> {
        return object: NetworkBoundResource<DayPicture, MainViewState>(){

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<DayPicture>) {
                result.value = DataState.data(
                    null,
                    MainViewState(
                        dayPicture = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<DayPicture>> {
                return MyRetrofitBuilder.apiService.getDayPicture()
            }

        }.asLiveData()
    }
}




























