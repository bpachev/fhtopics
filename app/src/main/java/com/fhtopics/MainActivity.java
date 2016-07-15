package com.fhtopics;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
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
    TopicListAdapter adapter;
    ExpandableListView eList;

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


            adapter = new TopicListAdapter(this.topic_lists, this.topic_names, this.keys, getApplicationContext());
            this.eList = (ExpandableListView)findViewById(R.id.lvExp);
            this.eList.setAdapter(adapter);

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

class TopicListAdapter extends BaseExpandableListAdapter
{
    HashMap<Integer, ArrayList<String>> topic_lists;
    HashMap<Integer, String> topic_names;
    ArrayList<Integer> keys;
    Context context;

    public TopicListAdapter(HashMap<Integer, ArrayList<String>> topic_lists, HashMap<Integer, String> topic_names, ArrayList<Integer> keys, Context context) {
        this.topic_lists = topic_lists;
        this.topic_names = topic_names;
        this.keys = keys;
        this.context = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return keys.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return topic_lists.get(keys.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return topic_names.get(keys.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return topic_lists.get(keys.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }
}