package com.example.kishorebaktha.shoppinglist4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {

    TextView item,budget,priority;

    public FirebaseViewHolder(View itemView) {
        super(itemView);

        item=(TextView)itemView.findViewById(R.id.item2);
        budget=(TextView)itemView.findViewById(R.id.budget2);
        priority=(TextView)itemView.findViewById(R.id.priority2);

        //listener set on ENTIRE ROW, you may set on individual components within a row.
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

    }
    private FirebaseViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(FirebaseViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}