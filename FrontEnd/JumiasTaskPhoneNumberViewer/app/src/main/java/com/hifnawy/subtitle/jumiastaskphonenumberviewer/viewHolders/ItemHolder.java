package com.hifnawy.subtitle.jumiastaskphonenumberviewer.viewHolders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hifnawy.subtitle.jumiastaskphonenumberviewer.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ItemHolder extends RecyclerView.ViewHolder {

  public final ConstraintLayout rootLayout;
  public final TextView customerID;
  public final TextView customerName;
  public final TextView customerPhone;

  public ItemHolder(@NonNull View itemView) {
    super(itemView);
    rootLayout = itemView.findViewById(R.id.rootLayout);

    customerID = itemView.findViewById(R.id.customerID);
    customerName = itemView.findViewById(R.id.customerName);
    customerPhone = itemView.findViewById(R.id.customerPhone);
  }
}
