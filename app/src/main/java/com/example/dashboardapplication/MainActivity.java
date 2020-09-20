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

public class MainActivity extends AppCompatActivity {
    String mon, dayStr;
    private TextView textView;
    private RequestQueue requestQueue;
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
        getSupportActionBar().setTitle(title);
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
//                        if (day < 10) {
//                            dayStr = "0" + day;
//                        }
                        String date = day + "-" + mon + "-" + year;
                        textView.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }


        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getData();
        recyclerView = findViewById(R.id.recycler);
        contentList = new ArrayList<>();

    }

    private void filter(String text) {
        List<Content> filteredlist = new ArrayList<>();
        for (Content item : filteredlist) {
            if (item.getDate().contains(text)) {
                filteredlist.add(item);
            }
        }
        adapter.filterlist(filteredlist);
    }

    private void getData() {
//        String url = "https://sheet.best/api/sheets/4ce1a216-43af-48e5-946a-9ffe26f351d7";
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject jsonObject = response.getJSONObject(i);
//                                Content content = new Content();
//                                content.setName(jsonObject.getString("Name"));
//                                content.setDate(jsonObject.getString("Date"));
//                                content.setTime(jsonObject.getString("Time"));
//                                content.setPhone(jsonObject.getString("Phone"));
//                                String duration = jsonObject.getString("Duration");
//                                String[] duratn = duration.split(" ");
//                                content.setStatus(duratn[0]);
//                                contentList.add(content);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        setOnClick();
//                        layoutManager = new LinearLayoutManager(getApplicationContext());
//                        adapter = new Adapter(getApplicationContext(), contentList, listener);
//                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                        recyclerView.setLayoutManager(layoutManager);
//                        recyclerView.setAdapter(adapter);
//                    }
//
//
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
//        requestQueue1.add(jsonObjectRequest);

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
                //String[] split = duration.split(" ");
                //String secondString = split[1];
                int firstString = duration.indexOf(" ");
                //int secondInt = duration.indexOf("5");
                String secondString = duration.substring(firstString+1);
                Content content = new Content();
                content.setName(name);
                content.setDate(date);
                content.setTime(time);
                content.setPhone(phone);
                content.setStatus(secondString);
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
                Toast.makeText(MainActivity.this, contentList.get(position).getName().toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }


}