package com.example.dashboardapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String mon, dayStr;
    private TextView textView, textView2;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private List<Content> contentList;
    private Adapter adapter;
    private Adapter.RecyclerViewOnClickListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Recycler();
        String title = "Agent Dashboard".toUpperCase();
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        textView = findViewById(R.id.text);
        textView2 = findViewById(R.id.time);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Data");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        swipeRefreshLayout = findViewById(R.id.swipe);

        getRefreshTime();

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
                        if (day >= 10) {
                            dayStr = String.valueOf(day);
                        }
                        String date = dayStr + "-" + mon + "-" + year;
                        textView.setText(date);
                        postDate(date);
                        recyclerView.invalidate();
                    }
                }, year, month, day);
                Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

//        textView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                postDate(editable);
//                //textView.setText(editable);
//            }
//        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressDialog.show();
                getRefreshTime();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getData();
    }

    private void Recycler() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        contentList = new ArrayList<>();
    }

    private void postDate(String editable) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyH3M-xgSzJc-_mmil9jlUeWAuleZ_o_-vskFUBDCN6tZw7Al0/exec?action=getItemByDate&name=Master Sheet"
                + "&date=" + editable,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeOut = 1000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

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
        progressDialog.dismiss();
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

        adapter = new Adapter(getApplicationContext(), contentList, listener);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();

    }


    private void setOnClick() {
        listener = new Adapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivity.this, contentList.get(position).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AgentActivity.class);
                intent.putExtra("name", contentList.get(position).getName());
                intent.putExtra("date", textView.getText());
                startActivity(intent);
                getRefreshTime();
            }
        };

    }

    public void getRefreshTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");
        Date date = new Date();
        String strDate = formatter.format(date);
        String[] splitStr = strDate.split(" ");
        textView2.setText("Last Refresh " + splitStr[1]);
    }

}