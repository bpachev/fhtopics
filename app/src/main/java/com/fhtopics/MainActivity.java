package com.fhtopics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    HashMap<Integer, ArrayList<String>> topic_lists;
    HashMap<Integer, String> topic_names;
    ArrayList<Integer> keys;
    final static String tag = "mainactivity";
    TextView topicText;
    Button topicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topicText = (TextView)findViewById(R.id.topic);
        topicButton = (Button)findViewById(R.id.random_topic_button);
        this.keys = new ArrayList<Integer>();
        this.topic_names = new HashMap<Integer, String>();
        this.topic_lists = new HashMap<Integer, ArrayList<String>>();

        try {
            JSONObject data = new JSONObject(loadJSONFromAsset());
            JSONArray keys = data.getJSONArray("keys");
            for (int i = 0; i < keys.length(); i++)
            {
                this.keys.add((Integer) keys.get(i));
            }

            JSONObject tnames = data.getJSONObject("topic_names");
            JSONObject tlists = data.getJSONObject("topic_lists");


            for (Integer key : this.keys)
            {
                String skey = Integer.toString(key);
                dump(tnames.getString(skey));
                this.topic_names.put(key, tnames.getString(skey));

                ArrayList<String> tarr = new ArrayList<String>();
                JSONArray arr = tlists.getJSONArray(skey);
                for (int i = 0; i < arr.length(); i++)
                {
                    tarr.add((String)arr.get(i));
                }
                this.topic_lists.put(key, tarr);
            }

            topicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newRandomTopic();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void newRandomTopic()
    {
        Random gen = new Random();
        int index = gen.nextInt(this.keys.size());
        int key = this.keys.get(index);

        ArrayList<String> group = this.topic_lists.get(key);
        String subTopic =  group.get(gen.nextInt(group.size()));
        String topic = this.topic_names.get(key);
        topicText.setText(topic+" : "+subTopic);
    }

    public void dump(String msg)
    {
        Log.d(tag, msg);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("fhlist.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
