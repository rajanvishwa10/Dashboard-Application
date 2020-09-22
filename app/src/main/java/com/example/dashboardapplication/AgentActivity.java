package com.example.dashboardapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AgentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Content2> contentList;
    private Adapter2 adapter;
    private ProgressDialog progressDialog;
    public TextView textView, textView2, textView3, textView5, textView4, textView7, textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);

        String agentName = getIntent().getStringExtra("name");
        Objects.requireNonNull(getSupportActionBar()).setTitle(agentName);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Data");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        textView = findViewById(R.id.text1);
        textView2 = findViewById(R.id.text2);
        textView3 = findViewById(R.id.text3);
        textView4 = findViewById(R.id.text4);
        textView5 = findViewById(R.id.text5);
        textView6 = findViewById(R.id.last);
        textView7 = findViewById(R.id.firstcall);

        getData();
        recyclerView = findViewById(R.id.recycler2);
        contentList = new ArrayList<>();
    }

    private void getData() {
        String url = "https://script.google.com/macros/s/AKfycbyH3M-xgSzJc-_mmil9jlUeWAuleZ_o_-vskFUBDCN6tZw7Al0/exec?action=getItemByName&name=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + getIntent().getStringExtra("name") + "&date=" + getIntent().getStringExtra("date"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        RetryPolicy retryPolicy = new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    int count = 0;
    double totalDuration = 0;

    private void parseItems(String response) {
        try {
            progressDialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String time = jsonObject1.getString("date");
                String phone = jsonObject1.getString("phoneno");
                String status = jsonObject1.getString("status");
                int duration = jsonObject1.getInt("duration");

                int firstString = time.indexOf(" ");
                String finalTime = time.substring(firstString + 1);

                if (i == 0) {
                    textView6.setText(finalTime);
                    textView4.setText(finalTime);
                }
                if (duration > 0) {
                    count++;
                    totalDuration = totalDuration + duration;
                }

                Content2 content = new Content2();
                content.setTime(finalTime);
                textView7.setText(finalTime);
                content.setPhone(phone);
                content.setStatus(status);
                content.setDuration(String.valueOf(duration));
                contentList.add(content);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new Adapter2(getApplicationContext(), contentList);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        int last = adapter.getItemCount();
        textView2.setText(String.valueOf(last));
        textView5.setText(String.valueOf(count));
        textView.setText(String.format("%.2f", (double) totalDuration / 60));
        int temp = 0;
        for (int i = 0; i < contentList.size(); i++) {
            if ((contentList.get(i).getPhone()).equals(contentList.get(i++).getPhone())) {
                temp++;
            }

        }
        textView3.setText(String.valueOf(temp));
    }

}