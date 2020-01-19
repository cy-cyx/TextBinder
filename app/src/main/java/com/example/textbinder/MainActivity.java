package com.example.textbinder;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textbinder.googleplay.GooglePlayActivity;

public class MainActivity extends AppCompatActivity {

    private enum Type {
        service_start(0, "startService"),
        service_bind(1, "bindService,同进程内"),
        service_bind_process(2, "bindService,不同进程内，跨进程通信"),
        google_play(3, "谷歌支付，跨进程通信的运用");

        public int id;
        public String desc;

        Type(int id, String desc) {
            this.id = id;
            this.desc = desc;
        }
    }

    private Type types[] = {Type.service_start, Type.service_bind, Type.service_bind_process, Type.google_play};

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = new RecyclerView(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MyAdpter());
        setContentView(recyclerView);
    }

    protected void click(Type type) {
        switch (type) {
            case service_start:
                ServiceFactory.textStartService(this);
                break;
            case service_bind:
                ServiceFactory.textBindService(this);
                break;
            case service_bind_process:
                ServiceFactory.textBindServiceProcess(this);
                break;
            case google_play:
                startActivity(new Intent(this, GooglePlayActivity.class));
                break;
        }
    }

    private class MyAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Button button = new Button(parent.getContext());
            return new RecyclerView.ViewHolder(button) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((Button) holder.itemView).setText(types[position].desc);
            ((Button) holder.itemView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click(types[position]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return types.length;
        }
    }
}
