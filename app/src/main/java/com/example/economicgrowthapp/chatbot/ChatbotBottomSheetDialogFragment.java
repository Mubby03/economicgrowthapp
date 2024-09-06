package com.example.economicgrowthapp.chatbot;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class ChatbotBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendButton;
    TextView macroconAI;
    List<Message> messagesList;
    MessageAdapter messageAdapter;
    ConstraintLayout bottom_layout;
    ImageView welcomeTextView;
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_chatbot, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        welcomeTextView = view.findViewById(R.id.welcome_text);
        sendButton = view.findViewById(R.id.send_btn);
        macroconAI = view.findViewById(R.id.macroconAI);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        messagesList = new ArrayList<>();
        messageEditText.setVisibility(View.INVISIBLE);
        // Setup recycler View
        messageAdapter = new MessageAdapter(messagesList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bottom_layout.setVisibility(View.INVISIBLE);

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
        // Initialize and set up your chatbot UI component here
        // Retrieve data from the arguments bundle
        // Toast.makeText(getContext(), searched_message, Toast.LENGTH_LONG).show();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String searched_message = bundle.getString("searched_message");
            // Display the toast message or use the data as needed
            messageEditText.setText("Give a detailed summary of as a reference with accurate reasons in Nigeria for the rise and fall " + searched_message + " \n");
            // Click the send button automatically
            sendButton.performClick();
            // Make the messageEditText visible
            messageEditText.setVisibility(View.VISIBLE);
        }
        return view;
    }

    void addToChat(String message, String sentBy) {
        messagesList.add(new Message(message, sentBy));
        messageAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messagesList.size() - 1);
    }

    void addResponse(String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addToChat(response, Message.SENT_BY_BOT);
            }
        });
    }

    void callAPI(String question) {
        messagesList.add(new Message("...", Message.SENT_BY_BOT));
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
            public void onFailure(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull IOException e) {
                addResponse("Failed to load response due to" + e.getMessage());
            }
            @Override
            public void onResponse(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        String responseBody = response.body().string(); // Read response body
                        jsonObject = new JSONObject(responseBody); // Parse response body to JSON object
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                        // Log the response JSON
                        Log.d(TAG, "onResponse: " + responseBody);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Log error message
                    Log.e(TAG, "Failed to load response due to " + response.body().string());
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