package ua.com.adr.android.flowershop.controller;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.com.adr.android.flowershop.model.callback.FlowerService;
import ua.com.adr.android.flowershop.model.helper.Constants;

/**
 * Created by Andy on 17.05.2018.
 */

public class RestManager {

    private FlowerService mFlowerService;

    public FlowerService getFlowerService() {
        if (mFlowerService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mFlowerService = retrofit.create(FlowerService.class);
        }

        return mFlowerService;
    }
}
