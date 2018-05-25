package ua.com.adr.android.flowershop.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.com.adr.android.flowershop.R;
import ua.com.adr.android.flowershop.controller.RestManager;
import ua.com.adr.android.flowershop.model.pojo.Flower;
import ua.com.adr.android.flowershop.model.adapter.FlowerAdapter;
import ua.com.adr.android.flowershop.model.helper.Constants;
import ua.com.adr.android.flowershop.model.database.FlowerDatabase;
import ua.com.adr.android.flowershop.model.helper.Utils;

public class MainActivity extends AppCompatActivity implements FlowerAdapter.FlowerClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RestManager mManager;
    private FlowerAdapter mFlowerAdapter;
    private FlowerDatabase mDatabase;
    private Button mReload;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configViews();

        mManager = new RestManager();
        mDatabase = new FlowerDatabase(this);

        loadFloferFeed();

        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFloferFeed();
            }
        });
    }

    private void loadFloferFeed() {

        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Loading Flower Data...");
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
        mDialog.show();

        mFlowerAdapter.reset();
        if (getNetworkAvailability()) {
            getFeed();
        } else {
            getFeedFromDataBase();
        }
    }

    private void getFeedFromDataBase() {
        List<Flower> flowerList = mDatabase.getFlowers();

        for (int i =0; i < flowerList.size(); i++) {
             Flower flower = flowerList.get(i);
             mFlowerAdapter.addFlower(flower);
                Log.d(TAG, flower.getName() + "||" + flower.getInstructions());
        }

        mDialog.dismiss();
    }

    private void configViews() {
        mReload = findViewById(R.id.reload);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mFlowerAdapter = new FlowerAdapter(this);
        mRecyclerView.setAdapter(mFlowerAdapter);
    }

    @Override
    public void onClick(int position) {
        Flower selectedFlower = mFlowerAdapter.getSelectedFlower(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Constants.REFERENCE.FLOWER, selectedFlower);
        startActivity(intent);
    }

    public void getFeed() {
        Call<List<Flower>> listCall = mManager.getFlowerService().getAllFlowers();
        listCall.enqueue(new Callback<List<Flower>>() {
            @Override
            public void onResponse(Call<List<Flower>> call, Response<List<Flower>> response) {

                if (response.isSuccessful()) {
                    List<Flower> flowerList = response.body();

                    for (int i = 0; i < flowerList.size(); i++) {
                        Flower flower = flowerList.get(i);
                        SaveIntoDatabase task = new SaveIntoDatabase();
                        task.execute(flower);
                        mFlowerAdapter.addFlower(flower);
                    }
                }
                else {
                    int sc = response.code();
                    switch (sc) {

                    }
                }
                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Flower>> call, Throwable t) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    public class SaveIntoDatabase extends AsyncTask<Flower, Flower, Boolean> {

        private final String TAG = SaveIntoDatabase.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Flower... params) {

            Flower flower = params[0];

            try {
                InputStream stream = new URL(Constants.HTTP.BASE_URL + "/photos/" + flower.getPhoto()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                flower.setPicture(bitmap);
                publishProgress(flower);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Flower... values) {
            super.onProgressUpdate(values);
            mDatabase.addFlower(values[0]);
            Log.d(TAG, "Values Got " + values[0].getName());
        }

    }
}
