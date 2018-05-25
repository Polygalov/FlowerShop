package ua.com.adr.android.flowershop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ua.com.adr.android.flowershop.R;
import ua.com.adr.android.flowershop.model.pojo.Flower;
import ua.com.adr.android.flowershop.model.helper.Constants;

/**
 * Created by Andy on 20.05.2018.
 */

public class DetailActivity extends AppCompatActivity {

    private TextView mName, mId, mCategory, mInstruction, mPrice;
    private ImageView mPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        Flower flower = (Flower) intent.getSerializableExtra(Constants.REFERENCE.FLOWER);

        configViews(flower);
    }

    private void configViews(Flower flower) {
        mName = findViewById(R.id.flowerName);
        mId = findViewById(R.id.flowewrId);
        mCategory = findViewById(R.id.flowerCategory);
        mInstruction = findViewById(R.id.flowerInstruction);
        mPrice = findViewById(R.id.flowerPrice);
        mPhoto = findViewById(R.id.flowerPhoto);

        mName.setText(flower.getName());
        mId.setText(Integer.toString(flower.getProductId()));
        mCategory.setText(flower.getName());
        mInstruction.setText(flower.getInstructions());
        mPrice.setText(Double.toString((flower.getPrice())));
        Picasso.with(this).load(Constants.HTTP.BASE_URL + "/photos/" + flower.getPhoto()).into(mPhoto);
        //mPhoto.setImageResource(flower.getName());

    }
}
