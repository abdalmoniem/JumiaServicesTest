package com.hifnawy.subtitle.jumiastaskphonenumberviewer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hifnawy.subtitle.jumiastaskphonenumberviewer.R;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.model.Customer;
import com.hifnawy.subtitle.jumiastaskphonenumberviewer.viewHolders.ItemHolder;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerItemAdapter extends RecyclerView.Adapter<ItemHolder> {

  private final Context mContext;
  private LayoutInflater mInflater;
  private ArrayList<Customer> mItems;
  private RecyclerView recyclerView;

  public CustomerItemAdapter(Context context) {
    if (context != null) {
      this.mInflater = LayoutInflater.from(context);
    }

    this.mContext = context;
    this.mItems = new ArrayList<>();
  }

  @NonNull
  @Override
  public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ItemHolder(mInflater.inflate(R.layout.customer_viewholder, parent, false));
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerView = recyclerView;
  }

  @Override
  public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
    Customer customer = (Customer) mItems.get(holder.getAdapterPosition());
    holder.customerID.setText(customer.getId() + "");
    holder.customerName.setText(String.valueOf(customer.getName()));
    holder.customerPhone.setText(String.valueOf(customer.getPhone()));

    holder.customerIcon.setColorFilter(
        Color.argb(
            80, new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)),
        PorterDuff.Mode.SRC_ATOP);

    Animation animation =
        AnimationUtils.loadAnimation(mContext, R.anim.single_item_animation_rise_up);

    holder.itemView.startAnimation(animation);
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  public void add(Customer customer) {
    this.mItems.add(customer);

    notifyDataSetChanged();
  }

  public void addAll(ArrayList<Customer> customers) {
    this.mItems.addAll(customers);

    notifyDataSetChanged();
  }

  public void setDataSet(ArrayList<Customer> objects) {
    this.mItems.clear();

    this.mItems = objects;

    notifyDataSetChanged();
  }

  public void clear() {
    this.mItems.clear();

    notifyDataSetChanged();
  }

  public ArrayList<Customer> getDataSet() {
    return this.mItems;
  }

  public void refreshAndAnimate() {
    notifyDataSetChanged();
    this.recyclerView.setLayoutAnimation(
        AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down));
    this.recyclerView.animate();
  }
}
