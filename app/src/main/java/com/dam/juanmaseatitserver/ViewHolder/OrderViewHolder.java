package com.dam.juanmaseatitserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitserver.Interface.ItemClickListener;
import com.dam.juanmaseatitserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    // Atributos de clase
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    public Button btnEdit, btnRemove, btnDetail;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);

        //itemView.setOnClickListener(this);
        //itemView.setOnLongClickListener(this);
        //itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    //@Override
    //public void onClick(View view) {
         //itemClickListener.onClick(view, getAdapterPosition(), false);
    //}

    //@Override
    //public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
    //    contextMenu.setHeaderTitle("Seleccione la acción");
//
    //    contextMenu.add(0, 0, getAdapterPosition(), "Actualizar");
    //    contextMenu.add(0, 1, getAdapterPosition(), "Borrar");
//
    //}

    //@Override
    //public boolean onLongClick(View view) {
    //    itemClickListener.onClick(view, getAdapterPosition(), true);
    //    return false;
    //}
}
