package com.ekoapp.ekoplayground.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ekoapp.ekoplayground.R;
import com.ekoapp.ekoplayground.R2;
import com.ekoapp.ekoplayground.activities.adapters.MessageAdapter;
import com.ekoapp.ekoplayground.activities.intents.MessageListIntent;
import com.ekoapp.ekoplayground.viewmodels.MessageListViewModel;
import com.uber.autodispose.AutoDispose;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessageListActivity extends EkoActivity {

    @BindView(R2.id.message_list_recycler_view)
    RecyclerView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        MessageListViewModel viewModel = ViewModelProviders.of(this)
                .get(MessageListViewModel.class);

        MessageAdapter adapter = new MessageAdapter(this);
        messageList.setAdapter(adapter);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getMessage(MessageListIntent.getTopicId(getIntent()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(adapter::submitList)
                .subscribeOn(Schedulers.io())
                .as(AutoDispose.autoDisposable(this))
                .subscribe();
    }
}
