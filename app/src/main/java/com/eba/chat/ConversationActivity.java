package com.eba.chat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.eba.BuildConfig;
import com.eba.activities.BaseActivity;
import com.eba.R;
import com.eba.chat.adapters.ConversationAdapter;
import com.eba.chat.helper.TaskHelper;
import com.eba.chat.models.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

public class ConversationActivity extends BaseActivity {

    public static final String TAG = "chatFragment";
    private RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;
    private EditText text;
    private ImageButton send;
    private DatabaseReference dbReference;
    private AIRequest aiRequest = new AIRequest();
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;
    private String uuid = UUID.randomUUID().toString();

    public static void start(Context context) {
        Intent intent = new Intent(context, ConversationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_conversation);
        initChatBot();

        mRecyclerView = findViewById(R.id.recyclerView);
        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).setStackFromEnd(true);
        mAdapter = new ConversationAdapter(this, new ArrayList<Conversation>(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        fetchConversation();

        mRecyclerView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }

        text = findViewById(R.id.et_message);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                        } catch (IllegalArgumentException e) {
                            //do nothin
                        }
                    }
                }, 500);
            }
        });

        send = findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().toString().equals("")) {
                    Conversation conversation = new Conversation();
                    String message = text.getText().toString();
                    conversation.setTime(Calendar.getInstance().getTimeInMillis());
                    conversation.setSenderID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    conversation.setText(message);
                    conversation.setSeen(false);
                    mAdapter.addMessage(conversation);
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            } catch (IllegalArgumentException e) {
                                //do nothin
                            }
                        }
                    }, 500);
                    saveData(conversation);
                    aiRequest.setQuery(message);
                    text.setText("");
                    new RequestTask(aiDataService, customAIServiceContext).execute(aiRequest);
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    //client lib
    public void initChatBot(){
        Log.i("Base activity","Initializing chat bot");
        AIConfiguration config = new AIConfiguration(BuildConfig.AI_CLIENT_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
    }

    private void fetchConversation(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // New data at this path. This method will be called after every change in the
                // data at this path or a subpath.
                int offset = mAdapter.getItemCount(), counter = 1;
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    if (counter > offset){
                        Conversation conversation = child.getValue(Conversation.class);
                        mAdapter.addMessage(conversation);
                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                                }catch(Exception e){
                                    //do nothin
                                }
                            }
                        }, 500);
                        if (!conversation.isSeen() && !conversation.getSenderID().equals(
                                FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            conversation.setSeen(true);
                            Map<String, Object> map = new HashMap<>();
                            map.put("seen", true);
                            child.getRef().updateChildren(map);
                        }

                    }
                    counter += 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveData(Conversation data){
        dbReference = FirebaseDatabase.getInstance().getReference("messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String messageId = dbReference.push().getKey();
        dbReference.child(messageId).setValue(data);
        fetchConversation();
    }



    private void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Variables
                final Status status = response.getStatus();
                final Result result = response.getResult();
                String speech = result.getFulfillment().getSpeech();
                final Metadata metadata = result.getMetadata();
                final HashMap<String, JsonElement> params = result.getParameters();

                // Logging
                Log.d(TAG, "onResult");
                Log.i(TAG, "Received success response");
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
                Log.i(TAG, "Action: " + result.getAction());
                Log.i(TAG, "Speech: " + speech);

                String intent = null;
                if (metadata != null && metadata.getIntentName() != null) {
                    intent = metadata.getIntentName().toLowerCase();
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + intent);

                }

                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        String key = entry.getKey().toLowerCase();
                        String value = entry.getValue().toString().toLowerCase();
                        Log.i(TAG, String.format("%s: %s", key, value));
                    }
                    if (intent != null && intent.contains("reminders.add")) {
                        if (params.get("date-time") != null){
                            TaskHelper.addAlarm(getBaseContext(), params);
                        }
                    }
                }
                String calcStr = "";
//                if (intent != null){
//                    if (intent.contains("account.balance")){
//                        Log.d(TAG, "getting balance");
//                        calcStr = AnalysisHelper.getBal(mpesaHelper, params);
//                    }else if (intent.contains("account.earning")){
//                        Log.d(TAG, "getting earnings");
//                        calcStr = AnalysisHelper.getEarning(mpesaHelper, params);
//                    }else if (intent.contains("account.spending")){
//                        Log.d(TAG, "getting Spendings");
//                        calcStr = AnalysisHelper.getSpendings(mpesaHelper, params);
//                    }else if(intent.contains("transfer.amount")){
//                        Log.d(TAG, "getting last trans amount");
//                        calcStr = mpesaHelper.getLastTransaction().toPresentableString();
//                    }else if (intent.contains("transfer.sender")){
//                        Log.d(TAG, "getting last amount received");
//                        calcStr = mpesaHelper.getLastReceived().toPresentableString();
//                    }
//                }
                if (!calcStr.isEmpty()){
                    speech = calcStr;
                }

                //Update view to bot says
                Conversation reply = new Conversation();
                reply.setSenderID("254");
                reply.setText(speech);
                reply.setTime(Calendar.getInstance().getTimeInMillis());
                reply.setSeen(false);
                saveData(reply);
            }
        });
    }

    private void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,error.toString());
            }
        });
    }

    public class RequestTask  extends AsyncTask<AIRequest, Void, AIResponse> {

        private AIDataService aiDataService;
        private AIServiceContext customAIServiceContext;
        private AIError aiError;

        RequestTask(AIDataService aiDataService, AIServiceContext customAIServiceContext){
            this.aiDataService = aiDataService;
            this.customAIServiceContext = customAIServiceContext;
        }

        @Override
        protected AIResponse doInBackground(AIRequest... aiRequests) {
            final AIRequest request = aiRequests[0];
            try {
                Log.d(TAG, "AI request: "+request.toString());
                return aiDataService.request(request, customAIServiceContext);
            } catch (AIServiceException e) {
                e.printStackTrace();
                aiError = new AIError(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            if (aiResponse != null) {
                onResult(aiResponse);
            } else {
                onError(aiError);
            }
        }
    }
}
