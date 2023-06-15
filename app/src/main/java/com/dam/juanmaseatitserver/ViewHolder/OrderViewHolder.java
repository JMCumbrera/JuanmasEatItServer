package com.dam.juanmaseatitserver.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitserver.R;

/**
 * Clase usada para representar y manejar la vista de un elemento de pedido en un RecyclerView
 */
public class OrderViewHolder extends RecyclerView.ViewHolder {
    // Atributos de clase
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    public Button btnEdit, btnRemove, btnDetail;

    // Constructor
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
    }
}
