package ua.com.adr.android.flowershop.model.callback;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import ua.com.adr.android.flowershop.model.pojo.Flower;

/**
 * Created by Andy on 17.05.2018.
 */

public interface FlowerService {

    @GET ("/feeds/flowers.json")
    Call<List<Flower>> getAllFlowers();
}
