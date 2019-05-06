package com.example.trendingrepos;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Math;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ExampleAdapter mExampleAdapter;
    private ArrayList<ExampleItem> mExampleList;
    String url;
    int pageNumber=1;
    boolean isLoading=false;
    LocalDate thirty_days_ago;

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


        thirty_days_ago = LocalDate.now().minusDays( 30 ); //get the date 30 day ago
        url="https://api.github.com/search/repositories?q=created:>"+thirty_days_ago+"&sort=stars&order=desc";


        parseJSON(); //parse JSON and display on CardView

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) { // Detect when the user scrolls
                super.onScrolled(recyclerView, dx, dy);

                if (dy>0){      //Detect if the user scrolls up
                    RecyclerView.LayoutManager mLayoutManager=recyclerView.getLayoutManager();
                    int totalVisibleChildCount=mLayoutManager.getChildCount();  //get number of visible repos on the screen

                    View firstVisibleChild = recyclerView.getChildAt(0); // get first visible child on the screen
                    int positionOfFirstVisibleChild = recyclerView.getChildAdapterPosition(firstVisibleChild);// get the position of first visible child on the screen

                    //Call parseJSON from next URL if the last loaded item is already visible on the screen,
                    if (positionOfFirstVisibleChild+totalVisibleChildCount==mExampleList.size()&&!isLoading){
                        pageNumber++;
                        isLoading=true;
                        url="https://api.github.com/search/repositories?q=created:>"+thirty_days_ago+"&sort=stars&order=desc&page="+pageNumber;
                        parseJSON();
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

                                mExampleList.add(new ExampleItem(imageUrl, ownerName, formatAndAddSuffix(starCount),repoDescription,repoName));
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

        public String formatAndAddSuffix(int number) {
          char abbrev[] = {' ', 'K', 'M', 'B', 'T'};
          double orderIncludingInfinity = Math.floor(Math.log10(Math.abs(number)) / 3);
          double order = Math.max(0, Math.min(orderIncludingInfinity, abbrev.length -1 ));
          char suffix = abbrev[(int)order];
          DecimalFormat df = new DecimalFormat("#.#");
          return df.format(number / Math.pow(10, order * 3))+suffix;
        }
}
