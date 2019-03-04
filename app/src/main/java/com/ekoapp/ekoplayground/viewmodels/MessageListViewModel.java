package com.ekoapp.ekoplayground.viewmodels;

import android.app.Application;
import android.arch.paging.PagedList;
import android.arch.paging.RxPagedListBuilder;
import android.support.annotation.NonNull;

import com.ekoapp.ekoplayground.models.Direction;
import com.ekoapp.ekoplayground.requests.ImmutableGetMessage;
import com.ekoapp.ekoplayground.room.EkoDatabase;
import com.ekoapp.ekoplayground.room.daos.MessageDao;
import com.ekoapp.ekoplayground.room.entities.Message;
import com.ekoapp.ekoplayground.socket.EkoSocket;
import com.google.gson.JsonElement;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MessageListViewModel extends EkoViewModel {

    public MessageListViewModel(@NonNull Application application) {
        super(application);
    }

    public Flowable<PagedList<Message>> getMessage(String chatId) {
        int pageSize = 15;

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .build();

        MessageDao messageDao = EkoDatabase.get()
                .getMessageDao();

        return new RxPagedListBuilder<>(messageDao.getMessage(chatId), config)
                .setBoundaryCallback(new PagedList.BoundaryCallback<Message>() {
                    @Override
                    public void onZeroItemsLoaded() {
                        EkoSocket.call(ImmutableGetMessage.builder()
                                .chatId(chatId)
                                .messageNumber(0)
                                .direction(Direction.NEXT)
                                .limit(pageSize)
                                .build())
                                .map(JsonElement::getAsJsonArray)
                                .doOnSuccess(messageDao::insert)
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }

                    @Override
                    public void onItemAtFrontLoaded(@NonNull Message itemAtFront) {

                    }

                    @Override
                    public void onItemAtEndLoaded(@NonNull Message itemAtEnd) {

                    }
                }).buildFlowable(BackpressureStrategy.BUFFER);
    }
}
