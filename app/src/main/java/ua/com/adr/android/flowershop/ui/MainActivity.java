package ua.com.adr.android.flowershop.ui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.com.adr.android.flowershop.R;
import ua.com.adr.android.flowershop.controller.RestManager;
import ua.com.adr.android.flowershop.model.adapter.FlowerAdapter;
import ua.com.adr.android.flowershop.model.database.FlowerDatabase;
import ua.com.adr.android.flowershop.model.helper.Constants;
import ua.com.adr.android.flowershop.model.helper.Utils;
import ua.com.adr.android.flowershop.model.pojo.Flower;

public class MainActivity extends AppCompatActivity implements FlowerAdapter.FlowerClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RestManager mManager;
    private FlowerAdapter mFlowerAdapter;
    private FlowerDatabase mDatabase;
    private Button mReload;
    private ProgressDialog mDialog;
    private String groupSorted = "Id";
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.toolbar_title);
        configViews();

        whiteNotificationBar(mRecyclerView);

        mManager = new RestManager();
        mDatabase = new FlowerDatabase(this);

        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFloferFeed();
            }
        });

        spinnerInit();
    }

    private void spinnerInit() {
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.sortlist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        groupSorted = "Id";
                        loadFloferFeed();
                        break;
                    case 1:
                        groupSorted = "Name A_z";
                        loadFloferFeed();
                        break;
                    case 2:
                        groupSorted = "Name Z_a";
                        loadFloferFeed();
                        break;
                    case 3:
                        groupSorted = "Price_inc";
                        loadFloferFeed();
                        break;
                    case 4:
                        groupSorted = "Price_red";
                        loadFloferFeed();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
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

        SortBy(flowerList, groupSorted);

        for (int i =0; i < flowerList.size(); i++) {
             Flower flower = flowerList.get(i);
             mFlowerAdapter.addFlower(flower);
                Log.d(TAG, flower.getName() + "||" + flower.getInstructions());
        }

        mDialog.dismiss();
    }

    private void SortBy(List<Flower> flowerList, String groupSort) {

        switch (groupSort) {
            case "Id":
                Collections.sort(flowerList, new Comparator<Flower>() {
                    @Override
                    public int compare(Flower lhs, Flower rhs) {
                        return ((Integer)lhs.getProductId()).compareTo((Integer)rhs.getProductId());
                    }
                });
                break;
            case "Name A_z":
                Collections.sort(flowerList, new Comparator<Flower>() {
                    @Override
                    public int compare(Flower lhs, Flower rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;
            case "Name Z_a":
                Collections.sort(flowerList, new Comparator<Flower>() {
                    @Override
                    public int compare(Flower lhs, Flower rhs) {
                        return rhs.getName().compareTo(lhs.getName());
                    }
                });
                break;
            case "Price_inc":
                Collections.sort(flowerList, new Comparator<Flower>() {
                    @Override
                    public int compare(Flower lhs, Flower rhs) {
                        return ((Double)lhs.getPrice()).compareTo((Double) rhs.getPrice());
                    }
                });
                break;
            case "Price_red":
                Collections.sort(flowerList, new Comparator<Flower>() {
                    @Override
                    public int compare(Flower lhs, Flower rhs) {
                        return ((Double)rhs.getPrice()).compareTo((Double) lhs.getPrice());
                    }
                });
                break;
        }

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

                    SortBy(flowerList, groupSorted);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mFlowerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mFlowerAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

}
