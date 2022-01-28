package com.hifnawy.subtitle.jumiastaskphonenumberviewer.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hifnawy.subtitle.jumiastaskphonenumberviewer.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The ItemHolder class is a ViewHolder which represents an item in a RecyclerView
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 27th 2022
 */
public class ItemHolder extends RecyclerView.ViewHolder {

  /** root layout of the view holder */
  public final ConstraintLayout rootLayout;

  /** textView containing customer ID in the view holder */
  public final TextView customerID;

  /** textView containing customer name in the view holder */
  public final TextView customerName;

  /** textView containing customer phone in the view holder */
  public final TextView customerPhone;

  /** textView containing customer icon in the view holder */
  public final ImageView customerIcon;

  /**
   * initializes the view holder of a RecyclerView item
   *
   * @param itemView - the item to initialize the view holder for
   */
  public ItemHolder(@NonNull View itemView) {
    super(itemView);
    rootLayout = itemView.findViewById(R.id.rootLayout);

    customerID = itemView.findViewById(R.id.customerID);
    customerName = itemView.findViewById(R.id.customerName);
    customerPhone = itemView.findViewById(R.id.customerPhone);
    customerIcon = itemView.findViewById(R.id.customerIcon);
  }
}
