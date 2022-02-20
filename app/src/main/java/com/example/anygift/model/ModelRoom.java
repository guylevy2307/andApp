package com.example.anygift.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ModelRoom {

    public List<GiftCard> getAllGiftCards(){
        return AppLocalDb.db.giftCardDao().getAll();
    }

    public interface AddGiftCardListener{
        void onComplete();
    }
    public void addGiftCard(final GiftCard giftCard, final Model.AddGiftCardListener listener){
        class MyAsyncTask extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.giftCardDao().insertAll(giftCard);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null){
                    listener.onComplete();
                }
            }
        };
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

}
