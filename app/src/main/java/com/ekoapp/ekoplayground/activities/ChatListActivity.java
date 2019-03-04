package com.ekoapp.ekoplayground.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ekoapp.ekoplayground.R;
import com.ekoapp.ekoplayground.R2;
import com.ekoapp.ekoplayground.activities.adapters.ChatAdapter;
import com.ekoapp.ekoplayground.viewmodels.ChatListViewModel;
import com.uber.autodispose.AutoDispose;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatListActivity extends EkoActivity {

    @BindView(R2.id.chat_list_recycler_view)
    RecyclerView chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ChatListViewModel viewModel = ViewModelProviders.of(this)
                .get(ChatListViewModel.class);

        ChatAdapter adapter = new ChatAdapter(this);
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getChat()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(adapter::submitList)
                .subscribeOn(Schedulers.io())
                .as(AutoDispose.autoDisposable(this))
                .subscribe();
    }
}
