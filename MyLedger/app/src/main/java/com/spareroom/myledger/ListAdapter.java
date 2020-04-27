package com.spareroom.myledger;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {
    private static ClickListener clickListener;
    private ArrayList<ListInfo> _pending;
    private Context context;

     ListAdapter(Context context , ArrayList<ListInfo> _pending) {
        this.context = context;
        this._pending = _pending;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(context).inflate(R.layout.row_song,viewGroup,false);
        return new ListHolder(myView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListHolder listHolder, int i) {
        ListInfo listInfo = _pending.get(i);
        listHolder.billRow.setText("Bill No : "+listInfo.billRow);
        listHolder.amountRow.setText("Amount to "+listInfo.credit+" is : "+ listInfo.amountRow);
        listHolder.dateRow.setText("Date : "+listInfo.dateRow);
        listHolder.nameRow.setText("Name : "+listInfo.name);
        listHolder.itemView.setLongClickable(true);
    }

    @Override
    public int getItemCount() {
        return _pending.size();
    }

    class ListHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView billRow, amountRow, dateRow,nameRow;
        ListHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            billRow = itemView.findViewById(R.id.billRow);
            amountRow = itemView.findViewById(R.id.amountRow);
            dateRow = itemView.findViewById(R.id.dateRow);
            nameRow = itemView.findViewById(R.id.nameRow);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(),v);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener){
         ListAdapter.clickListener = clickListener;
    }

    public interface ClickListener{
         void onItemLongClick(int position , View v);
    }

}
