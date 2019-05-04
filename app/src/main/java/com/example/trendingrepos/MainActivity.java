package com.example.trendingrepos;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ExampleAdapter mExampleAdapter;
    private ArrayList<ExampleItem> mExampleList;
    String url = "https://api.github.com/search/repositories?q=created:>2017-10-22&sort=stars&order=desc";
    int pageNumber=1;
    int totalPreviousCount=0;
    boolean isLoading=false;
    int totalItemCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mExampleList = new ArrayList<>();
        mExampleAdapter = new ExampleAdapter(MainActivity.this, mExampleList);
        mRecyclerView.setAdapter(mExampleAdapter);

        parseJSON();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy>0){
                    RecyclerView.LayoutManager firstManager=recyclerView.getLayoutManager();
                    totalItemCount=firstManager.getItemCount();
                    int totalVisibleChildCount=firstManager.getChildCount();
                    View visibleChild = recyclerView.getChildAt(0);
                    int positionOfFirstVisibleChild = recyclerView.getChildAdapterPosition(visibleChild);
                    int last=positionOfFirstVisibleChild+totalVisibleChildCount;
                    Toast.makeText(MainActivity.this,"page num"+pageNumber+" total item"+totalItemCount+"last"+last,Toast.LENGTH_LONG).show();
                    if (positionOfFirstVisibleChild+totalVisibleChildCount==totalItemCount&&!isLoading){
                        pageNumber++;
                        isLoading=true;
                        url="https://api.github.com/search/repositories?q=created:>2017-10-22&sort=stars&order=desc&page="+pageNumber;
                        parseJSON();
                        totalPreviousCount=totalPreviousCount+totalItemCount;
                    }
                }
            }
        });
    }


    private void parseJSON() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject items = jsonArray.getJSONObject(i);

                                JSONObject owner=items.getJSONObject("owner");
                                String ownerName=owner.getString("login");
                                String imageUrl = owner.getString("avatar_url");
                                int starCount = items.getInt("stargazers_count");
                                String repoName = items.getString("name");
                                String repoDescription = items.getString("description");
                                if (pageNumber>1){
                                    mExampleList.add(totalItemCount+i,new ExampleItem(imageUrl, ownerName, starCount,repoDescription,repoName));
                                }
                                else {
                                    mExampleList.add(new ExampleItem(imageUrl, ownerName, starCount,repoDescription,repoName));
                                }
                            }
                            mExampleAdapter.notifyDataSetChanged();
                            isLoading=false;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }


}
