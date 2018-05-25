package ua.com.adr.android.flowershop.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ua.com.adr.android.flowershop.model.helper.Constants;
import ua.com.adr.android.flowershop.model.helper.Utils;
import ua.com.adr.android.flowershop.model.pojo.Flower;

/**
 * Created by Andy on 20.05.2018.
 */

public class FlowerDatabase extends SQLiteOpenHelper {
    private static final String TAG = FlowerDatabase.class.getSimpleName();

    public FlowerDatabase(Context context) {
        super(context, Constants.DATABASE.DB_NAME, null, Constants.DATABASE.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.DATABASE.DROP_QUERY);
        this.onCreate(db);
    }

    public void addFlower(Flower flower) {

        Log.d(TAG, "Values Got " + flower.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATABASE.PRODUCT_ID, flower.getProductId());
        values.put(Constants.DATABASE.CATEGORY, flower.getCategory());
        values.put(Constants.DATABASE.PRICE, flower.getPrice()+"");
        values.put(Constants.DATABASE.INSTRUCTIONS, flower.getInstructions());
        values.put(Constants.DATABASE.NAME, flower.getName());
        values.put(Constants.DATABASE.PHOTO_URL, flower.getPhoto());
        values.put(Constants.DATABASE.PHOTO, Utils.getPictureByteOfArray(flower.getPicture()));

        try {
            db.insert(Constants.DATABASE.TABLE_NAME, null, values);
        } catch (Exception e) {

        }
        db.close();
    }

    public List<Flower> getFlowers() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(Constants.DATABASE.GET_FLOWERS_QUERY, null);

        List<Flower> flowerList = new ArrayList<>();

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    Flower flower = new Flower();

                    flower.setName(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.NAME)));
                    flower.setInstructions(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.INSTRUCTIONS)));
                    flower.setCategory(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.CATEGORY)));
                    flower.setPicture(Utils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.PHOTO))));
                    flower.setPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.PRICE))));
                    flower.setProductId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.PRODUCT_ID))));
                    flower.setPhoto(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.PHOTO_URL)));

                    flowerList.add(flower);

                } while (cursor.moveToNext());
            }
        }
        return flowerList;
    }

}
