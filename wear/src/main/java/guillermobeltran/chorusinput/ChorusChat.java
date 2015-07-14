package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class ChorusChat extends Activity {
    String _task, _role;
    Button send;
    Spinner spinner;
    TextView mTextView, chatText;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _arrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _adapter;
    Boolean _canUpdate, _checkUpdate;
    int _size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _canUpdate = true;
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                chatText = (TextView) findViewById(R.id.TextArea);
                send = (Button) findViewById(R.id.send_button);

                //suggested responses
                spinner = (Spinner) findViewById(R.id.spinner);
                spinner.setPrompt("Reply...");
                if (chatText.getText().toString().contains("?")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.question_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.response_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                        if (parent.getItemAtPosition(position).equals("Custom response")) {
                            Intent intent = new Intent(getApplicationContext(), Microphone.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else if (parent.getItemAtPosition(position).equals("Reply...") == false) {
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
                                    intent.putExtra("Response", parent.getItemAtPosition(position).toString());
                                    intent.putExtra("caller", "Response");
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                if (getIntent().getStringExtra("caller").equals("ListenerServiceFromPhone")) {
                    chatText.setText(getIntent().getStringExtra("New Text"));
                    Log.i("test", "cc:" + getIntent().getStringExtra("New Text"));

                    if (chatText.getText().toString().contains("?")) {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.question_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } else {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.response_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                        //so ChorusChat doesn't open everytime a new message is posted
                    //finish();
                }
            }
        });
        //setChatLines();
    }

    //sets up the parameters to send to the server
    /*public HashMap<String, Object> setUpParams(HashMap<String, Object> params, String action) {
        params.put("action", action);
        params.put("role", _role);
        params.put("task", _task);
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        if (action != "post") {
            params.put("lastChatId", "-1");
        }
        return params;
    }*/

    //This sets the chat list so user can see all available chats.
    /*public void setChatLines() {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
        AQuery aq = new AQuery(this);

        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    try {
                        for (int n = 0; n < json.length(); n++) {
                            String[] lineInfo = json.get(n).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            _arrayList.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                        }
                        //((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                        //_chatList.setSelection(_chatList.getCount() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                update();
            }
        });
    }*/

    /*
    Recursive function that constantly checks the server to see if there is a change in the chat.
     */
    /*public void update() {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");

        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (json.length() > _chatLineInfoArrayList.size()) {
                        try {
                            String[] lineInfo = json.get(json.length() - 1).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            _adapter.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                            /*if (_chatList.getAdapter() == null) {
                                ((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                            }
                            _chatList.setSelection(_chatList.getCount() - 1);*/

                            //spinner automatic answers
                            /*if (chatLineInfo.get_chatLine().contains("?")) {
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.question_array, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                            } else {
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                        R.array.response_array, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (_canUpdate) {//in order to stop recursion once app is closed.
                    update();
                }
            }
        });
    }*/

    /*
    Sends the string to the server to add chat list.
     */
    /*public void postData(String words) {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "post");

        params.put("chatLine", words);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
            }
        });
    }*/

    //TODO: Set alarm manager to check for updates
    public void setAlarmManager() {
        _size = _chatLineInfoArrayList.size();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
//        broadcast_intent.putExtra("ArrayList", _chatLineInfoArrayList.size());
//        broadcast_intent.putExtra("ChatNum",_task);
//        broadcast_intent.putExtra("Role", _role);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, broadcast_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 100, pendingIntent);
    }

    public void stopAlarmManager() {
        Intent intentstop = new Intent(this, AlarmUpdateChatList.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                1234, intentstop, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);
    }

    public void onStop() {//stops the recursion.
        super.onStop();
        _canUpdate = false;
        setAlarmManager();
    }

    public void onResume() {
        super.onResume();
        stopAlarmManager();
    }
}