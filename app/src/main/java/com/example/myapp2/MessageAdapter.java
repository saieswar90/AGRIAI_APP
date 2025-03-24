package com.example.myapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Set sender's name
        holder.senderTextView.setText(message.getSender());

        // Set message text
        holder.messageTextView.setText(message.getMessage());

        if ("User".equals(message.getSender())) {
            // User message: Align to the right, blue background, white text
            holder.messageTextView.setBackgroundResource(R.drawable.sent_message_background);
            holder.messageTextView.setTextColor(0xFFFFFFFF); // White text
            holder.messageTextView.setGravity(android.view.Gravity.END); // Align to the right
            holder.senderTextView.setGravity(android.view.Gravity.END); // Align sender name to the right
        } else {
            // Support message: Align to the left, light gray background, black text
            holder.messageTextView.setBackgroundResource(R.drawable.received_message_background);
            holder.messageTextView.setTextColor(0xFF000000); // Black text
            holder.messageTextView.setGravity(android.view.Gravity.START); // Align to the left
            holder.senderTextView.setGravity(android.view.Gravity.START); // Align sender name to the left
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}