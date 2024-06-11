package com.example.uasproject.utils;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class CountDownViewModel extends ViewModel {
    private CountDownTimer countDownTimer;
    private MutableLiveData<String> timeRemaining = new MutableLiveData<>();
    private long remainingTimeMillis = 0;

    public void startTimer(long durationMillis) {
        countDownTimer = new CountDownTimer(remainingTimeMillis > 0 ? remainingTimeMillis : durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;

                seconds = seconds % 60;
                minutes = minutes % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                timeRemaining.postValue(time);
            }

            @Override
            public void onFinish() {
                remainingTimeMillis = 0;
                timeRemaining.postValue("00:00:00");
            }
        };
        countDownTimer.start();
    }

    public LiveData<String> getTimeRemaining() {
        return timeRemaining;
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTimer();
    }

}
