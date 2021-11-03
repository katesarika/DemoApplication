package com.example.demoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Episode> mEpisodeList;
    private AdapterShows mShowsAdapter;
    private Integer showID,showSeason,i;
    private String showName,showType;
    private SQLiteDatabase sqLiteDatabase;
    private SqLiteHelper dbHandler;
    private Bitmap showUrl;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        dbHandler = new SqLiteHelper(MainActivity.this);
        // creating new array list.
        mEpisodeList = new ArrayList<>();
        //table count
        int table_counts = dbHandler.getTableCount();
        dbHandler.close();
        //Toast.makeText(getApplicationContext(), ""+table_counts, Toast.LENGTH_SHORT).show();
        enableSwipeToDeleteAndUndo();
        //for reload api data
        if(table_counts==0){
        getShowsData();}
        else{setDataInRecyclerView();}
    }
    private void getShowsData() {
        if (DeviceManager.isNetworkAvailable(MainActivity.this)) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.tvmaze.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface retrofitAPI = retrofit.create(ApiInterface.class);


            Call<ShowsModel> call = retrofitAPI.getEpisodeList("girls", "episodes");
            call.enqueue(new Callback<ShowsModel>() {
                @Override
                public void onResponse(Call<ShowsModel> call, Response<ShowsModel> response) {
                    progressDialog.dismiss();
                    if (response != null) {
                        try {
                            ShowsModel model = response.body();
                            mEpisodeList = model.getEmbedded().getEpisodes();
                            String[] items = new String[mEpisodeList.size()];

                            for (i = 0; i < mEpisodeList.size(); i++) {
                                //Storing names to string array
                                items[i] = String.valueOf(mEpisodeList.get(i).getId());
                                items[i] = mEpisodeList.get(i).getName();
                                //items[i] = mEpisodeList.get(i).getUrl();
                                items[i] = mEpisodeList.get(i).getType();
                                items[i] = String.valueOf(mEpisodeList.get(i).getSeason());

                                showID = model.getEmbedded().getEpisodes().get(i).getId();
                                showName = model.getEmbedded().getEpisodes().get(i).getName();
                                // showUrl = model.getEmbedded().getEpisodes().get(i).getUrl();
                                showType = model.getEmbedded().getEpisodes().get(i).getType();
                                showSeason = model.getEmbedded().getEpisodes().get(i).getSeason();
                               // dbHandler.addNewData(showID, showName, showType, showSeason);

                                String query = "Select * From MyShowTable where eId = '"+showID+"'";
                                if(dbHandler.getData(query).getCount()>0){
                                    Toast.makeText(getApplicationContext(), "Already Exist!", Toast.LENGTH_SHORT).show();
                                }else{
                                    dbHandler.addNewData(showID,showName,showType,showSeason);
                                    Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                                    }

                            Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
                        } }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                   else {
                       // Toast.makeText(MainActivity.this, "400", Toast.LENGTH_LONG).show();
                    }
                   setDataInRecyclerView();
                }

                @Override
                public void onFailure(Call<ShowsModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else
            Toast.makeText(MainActivity.this, "Please connect device to internet", Toast.LENGTH_SHORT).show();

    }

    private void setDataInRecyclerView() {
        mEpisodeList = dbHandler.readData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mShowsAdapter = new AdapterShows(MainActivity.this, (ArrayList<Episode>) mEpisodeList);
        recyclerView.setAdapter(mShowsAdapter);
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                int checkingId;
                final Episode item = mShowsAdapter.getData().get(position);
                checkingId = mEpisodeList.get(position).getId();
                mShowsAdapter.removeItem(position);
                delete(checkingId);
                mShowsAdapter.notifyDataSetChanged();

               /* final int position = viewHolder.getAdapterPosition();
                final Episode item = mShowsAdapter.getData().get(position);
                mShowsAdapter.removeItem(position);*/


                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mShowsAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
    //DELETE METHOD
    public void delete(int id) {
        long result = dbHandler.DeleteRowAfterSwipe(id );
        if (result > 0) {
            //Deleted toast appears but it doesn't get removed from database?
          //  Toast.makeText(MainActivity.this, "Deleted",Toast.LENGTH_SHORT).show();
        } else {
           // Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_SHORT).show();
        }
    }
}