package com.example.myapp2;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private ApiService apiService;

    // Handler for periodic polling
    private Handler handler = new Handler();
    private Runnable pollMessagesRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://chatapp1-0t8r.onrender.com/") // Replace with your ngrok URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Fetch existing messages
        fetchMessages();

        // Start periodic polling
        startPolling();

        // Send message
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                Message message = new Message("User", messageText);
                sendMessage(message);
                messageInput.setText("");
            }
        });
    }

    private void fetchMessages() {
        apiService.getMessages().enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    messageAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void sendMessage(Message message) {
        apiService.sendMessage(message).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchMessages(); // Refresh the chat after sending
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startPolling() {
        pollMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMessages(); // Fetch messages every 2 seconds
                handler.postDelayed(this, 2000); // Repeat every 2 seconds
            }
        };
        handler.post(pollMessagesRunnable); // Start polling
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(pollMessagesRunnable); // Stop polling when activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPolling(); // Restart polling when activity resumes
    }
}

