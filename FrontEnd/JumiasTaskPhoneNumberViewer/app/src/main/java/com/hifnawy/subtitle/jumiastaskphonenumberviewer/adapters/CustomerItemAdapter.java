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

/**
 * The CustomerItemAdapter class is a RecyclerView Adapter which handles item insertion, deletion
 * and updates in a RecyclerView
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 27th 2022
 */
public class CustomerItemAdapter extends RecyclerView.Adapter<ItemHolder> {

  /** the context of an adapter, to use in item animations */
  private final Context context;

  /** an inflater object to inflate layout XML of an item */
  private LayoutInflater layoutInflater;

  /** the actual list of items */
  private ArrayList<Customer> items;

  /** the underlying RecyclerView that is using this adapter */
  private RecyclerView recyclerView;

  /**
   * initialize context, items list and layout inflater of the adapter
   *
   * @param context - the context of the adapter to be initialized
   */
  public CustomerItemAdapter(Context context) {
    if (context != null) {
      this.layoutInflater = LayoutInflater.from(context);
    }

    this.context = context;
    this.items = new ArrayList<>();
  }

  /**
   * called whenever an item is about to be visible in the RecyclerView, used to inflate the item's
   * layout
   *
   * @param parent
   * @param viewType
   * @return
   */
  @NonNull
  @Override
  public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ItemHolder(layoutInflater.inflate(R.layout.customer_viewholder, parent, false));
  }

  /**
   * called when a RecyclerView adds and adapter to itself, used to get back reference of said
   * RecyclerView in this adapter
   *
   * @param recyclerView
   */
  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerView = recyclerView;
  }

  /**
   * called when a item view has been inflated and it's view holder is initialized
   *
   * @param holder
   * @param position
   */
  @Override
  public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
    Customer customer = (Customer) items.get(holder.getAdapterPosition());
    holder.customerID.setText(customer.getId() + "");
    holder.customerName.setText(String.valueOf(customer.getName()));
    holder.customerPhone.setText(String.valueOf(customer.getPhone()));

    // add a random color tint to the icon to make some variety
    holder.customerIcon.setColorFilter(
        Color.argb(
            80, new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)),
        PorterDuff.Mode.SRC_ATOP);

    Animation animation =
        AnimationUtils.loadAnimation(context, R.anim.single_item_animation_rise_up);

    holder.itemView.startAnimation(animation);
  }

  /** @return items list size */
  @Override
  public int getItemCount() {
    return items.size();
  }

  /** @param customer - a customer to add to the items list */
  public void add(Customer customer) {
    this.items.add(customer);

    notifyDataSetChanged();
  }

  /** @param customers - a list of customers to append to the items list */
  public void addAll(ArrayList<Customer> customers) {
    this.items.addAll(customers);

    notifyDataSetChanged();
  }

  /**
   * clears the items list and inserts objects into it
   *
   * @param objects - objects to be inserted into the items list
   */
  public void setItems(ArrayList<Customer> objects) {
    this.items.clear();

    this.items = objects;

    notifyDataSetChanged();
  }

  /** clears items list */
  public void clear() {
    this.items.clear();

    notifyDataSetChanged();
  }

  /** @return get items list */
  public ArrayList<Customer> getItems() {
    return this.items;
  }

  /**
   * refresh the adapter by calling #notifyDataSetChanged and then start an animation that applies
   * to all the list items
   */
  public void refreshAndAnimate() {
    notifyDataSetChanged();
    this.recyclerView.setLayoutAnimation(
        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down));
    this.recyclerView.animate();
  }
}
