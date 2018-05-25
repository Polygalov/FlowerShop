package ua.com.adr.android.flowershop.model.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ua.com.adr.android.flowershop.R;
import ua.com.adr.android.flowershop.model.pojo.Flower;
import ua.com.adr.android.flowershop.model.helper.Constants;

/**
 * Created by Andy on 16.05.2018.
 */

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.Holder> {

    private static final String TAG = FlowerAdapter.class.getSimpleName();
    private final FlowerClickListener mListner;
    private List<Flower> mFlowers = new ArrayList<>();

    public FlowerAdapter(FlowerClickListener listener) {
        mFlowers = new ArrayList<>();
        mListner = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        Flower currFlower = mFlowers.get(position);

        holder.mName.setText(currFlower.getName());
        holder.mPrice.setText("$" + Double.toString(currFlower.getPrice()));

        Picasso.with(holder.itemView.getContext()).load(Constants.HTTP.BASE_URL + "/photos/" + currFlower.getPhoto()).into(holder.mPhoto);

    }

    @Override
    public int getItemCount() {
        return mFlowers.size();
    }

    public void addFlower(Flower flower) {
        Log.d(TAG, flower.getPhoto());
        mFlowers.add(flower);
        notifyDataSetChanged();
    }

    public Flower getSelectedFlower(int position) {
        return mFlowers.get(position);
    }

    public void reset() {
        mFlowers.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView mPhoto;
        private TextView mName, mPrice;

        public Holder(View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.flowerPhoto);
            mName = itemView.findViewById(R.id.flowerName);
            mPrice = itemView.findViewById(R.id.flowerPrice);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListner.onClick(getLayoutPosition());
        }
    }

    public interface FlowerClickListener {

        void onClick (int position);
    }

}
