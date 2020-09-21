package com.example.dashboardapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String mon,dayStr;
    private TextView textView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Content> contentList;
    private Adapter adapter;
    private Adapter.RecyclerViewOnClickListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String title = "Agent Dashboard".toUpperCase();
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        textView = findViewById(R.id.text);
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        swipeRefreshLayout = findViewById(R.id.swipe);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        if (month < 10) {
                            mon = "0" + month;
                        }
                        if (day < 10) {
                            dayStr = "0" + day;
                        }
                        if(day>=10){
                            dayStr = String.valueOf(day);
                        }
                        String date = dayStr + "-" + mon + "-" + year;
                        textView.setText(date);
                    }
                }, year, month, day);
                Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getData();
        recyclerView = findViewById(R.id.recycler);
        contentList = new ArrayList<>();

    }



    private void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxdI-hNvljA_oh-K0r-pMWcerUD2JWTu1WjxvyU/exec?action=getItems",
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

    private void parseItems(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String name = jsonObject1.getString("agent");
                String date = jsonObject1.getString("duration");
                String time = jsonObject1.getString("totaldial");
                String phone = jsonObject1.getString("uniquedial");
                String duration = jsonObject1.getString("lastdial");
                String firstdial = jsonObject1.getString("firstdial");
                int firstString = duration.indexOf(" ");
                String secondString = duration.substring(firstString + 1);

                Content content = new Content();
                content.setName(name.toUpperCase());
                content.setDate(date);
                content.setTime(time);
                content.setPhone(phone);
                content.setStatus(secondString);
                content.setFirstcall(firstdial);
                contentList.add(content);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        setOnClick();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new Adapter(getApplicationContext(), contentList, listener);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setOnClick() {
        listener = new Adapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivity.this, contentList.get(position).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AgentActivity.class);
                intent.putExtra("name", contentList.get(position).getName());
//                intent.putExtra("first", contentList.get(position).getDate());
//                intent.putExtra("second", contentList.get(position).getTime());
//                intent.putExtra("third", contentList.get(position).getPhone());
//                intent.putExtra("fourth", contentList.get(position).getStatus());
//                intent.putExtra("firstdial", contentList.get(position).getFirstcall());
                intent.putExtra("date", textView.getText());
                startActivity(intent);
            }
        };
    }


}