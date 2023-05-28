package com.dam.juanmaseatitserver;

import static com.dam.juanmaseatitserver.Common.Common.convertCodeToStatus;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitserver.Common.Common;
import com.dam.juanmaseatitserver.Model.Request;
import com.dam.juanmaseatitserver.ViewHolder.OrderViewHolder;
import com.dam.juanmaseatitserver.databinding.FragmentOrderStatusBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatusFragment extends Fragment {
    // Atributos de clase
    private FragmentOrderStatusBinding binding;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    MaterialSpinner spinner;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderStatusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        // Inicializamos
        recyclerView = binding.listOrders;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Cargamos los pedidos
        loadOrders();

        return root;
    }

    private String sanitizeKey(String key) {
        String sanitizedKey = key.replaceAll("[.#$\\[\\]]", "_");
        return sanitizedKey;
    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request model, int position) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                orderViewHolder.txtOrderAddress.setText(model.getAddress());
                orderViewHolder.txtOrderPhone.setText(model.getPhone());

                // Nuevos eventos de botón
                /*orderViewHolder.btnEdit.setOnClickListener(view -> {
                    if (position >= 0 && position < adapter.getItemCount()) {
                        showUpdateOrderDialog(adapter.getRef(position).getKey(), adapter.getItem(position), orderViewHolder);
                    }
                });*/
                orderViewHolder.btnEdit.setOnClickListener(view -> {
                    int currentPosition = orderViewHolder.getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        showUpdateOrderDialog(adapter.getRef(currentPosition).getKey(), adapter.getItem(currentPosition), orderViewHolder);
                    }
                });

                orderViewHolder.btnRemove.setOnClickListener(view -> deleteOrder(adapter.getRef(position).getKey()));

                orderViewHolder.btnDetail.setOnClickListener(view -> {
                    Intent orderDetail = new Intent(getContext(), OrderDetail.class);
                    Common.currentRequest = model;
                    orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                    startActivity(orderDetail);
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showUpdateOrderDialog(String key, Request item, OrderViewHolder orderViewHolder) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(binding.getRoot().getContext());
        alertDialog.setTitle("Actualizar pedido");
        alertDialog.setMessage("Por favor, elija un estado: ");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = (MaterialSpinner)view.findViewById(R.id.statusSpinner);
        spinner.setItems("Realizado", "En reparto", "Entregado");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("SI", (dialog, which) -> {
            dialog.dismiss();
            item.setStatus(String.valueOf(spinner.getSelectedIndex()));

            requests.child(localKey).setValue(item);
            adapter.notifyItemChanged(orderViewHolder.getAdapterPosition());
        });

        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }
}