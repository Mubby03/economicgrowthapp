package com.example.economicgrowthapp.chatbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chatbot extends AppCompatActivity {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    RecyclerView recyclerView;
    ImageView welcomeTextView;
    EditText messageEditText;
    TextView macroconAI;
    ImageButton sendButton;
    List<Message> messagesList;
    MessageAdapter messageAdapter;
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        macroconAI = findViewById(R.id.macroconAI);
        sendButton = findViewById(R.id.send_btn);
        messagesList = new ArrayList<>();

        //setup recycler View
        messageAdapter = new MessageAdapter(messagesList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
// Load messages from SharedPreferences when the activity is created
        loadMessagesFromSharedPreferences();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = messageEditText.getText().toString().trim();
                addToChat(question, Message.SENT_BY_ME);
                messageEditText.setText("");
                callAPI(question);
                // Fade out welcomeTextView
                fadeOutView(welcomeTextView);
                // Fade out macroconAIlogo
                fadeOutView(macroconAI);
            }
        });
    }
    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                // After adding the message to messagesList, save it to SharedPreferences
                saveMessagesToSharedPreferences();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    void saveMessagesToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("chat_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert messagesList to a JSON string
        Gson gson = new Gson();
        String messagesJson = gson.toJson(messagesList);

        // Save the JSON string to SharedPreferences
        editor.putString("messages", messagesJson);
        editor.apply();
    }
    // Load chat messages from SharedPreferences
    void loadMessagesFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("chat_data", Context.MODE_PRIVATE);
        String messagesJson = sharedPreferences.getString("messages", "");

        // Convert JSON string back to a List<Message>
        Gson gson = new Gson();
        Type messageType = new TypeToken<List<Message>>() {
        }.getType();
        List<Message> loadedMessages = gson.fromJson(messagesJson, messageType);

        if (loadedMessages != null) {
            messagesList.addAll(loadedMessages);
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        }
    }
    void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
        messagesList.remove(messagesList.size() - 1);
    }
    void callAPI(String question) {
        messagesList.add(new Message("Typing...", Message.SENT_BY_BOT));
        //okhttp
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");

            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);
            messageArr.put(obj);
            jsonBody.put("messages", messageArr);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("\n" +
                        "https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    addResponse("Failed to load response due to" + response.body().string());
                }
            }
        });
    }
    private void fadeOutView(final View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500); // Adjust duration as needed
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(fadeOut);
    }
}

