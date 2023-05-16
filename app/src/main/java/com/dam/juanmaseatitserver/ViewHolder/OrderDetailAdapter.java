package com.dam.juanmaseatitserver.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.juanmaseatitserver.Model.Order;
import com.dam.juanmaseatitserver.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {
    // Atributos de clase
    public TextView name, quantity, price, discount;

    // Constructores
    public MyViewHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.product_name);
        quantity = (TextView)itemView.findViewById(R.id.product_quantity);
        price = (TextView)itemView.findViewById(R.id.product_price);
        discount = (TextView)itemView.findViewById(R.id.product_discount);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {
    // Atributos de clase
    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) { this.myOrders = myOrders; }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = myOrders.get(position);
        holder.name.setText(String.format("Nombre: %s", order.getProductName()));
        holder.quantity.setText(String.format("Cantidad: %s", order.getQuantity()));
        holder.price.setText(String.format("Precio: %s", order.getPrice()));
        holder.discount.setText(String.format("Descuento: %s", order.getDiscount()));
    }

    @Override
    public int getItemCount() { return myOrders.size(); }
}
