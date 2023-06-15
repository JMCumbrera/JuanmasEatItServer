package com.dam.juanmaseatitserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitserver.Common.Common;
import com.dam.juanmaseatitserver.Interface.ItemClickListener;
import com.dam.juanmaseatitserver.R;

/**
 * Clase encargada de mantener y gestionar la vista de cada elemento de la lista de platos
 * en un RecyclerView
 */
public class FoodViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener
{
    // Atributos de clase
    public TextView food_name, food_price;
    public ImageView food_image;
    private ItemClickListener itemClickListener;

    // Constructor
    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name = (TextView)itemView.findViewById(R.id.food_name);
        food_price = (TextView)itemView.findViewById(R.id.food_price);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

        itemView.setOnClickListener(this);
    }

    // Métodos
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Este método se invocará cuando se produzca un clic en un elemento de la interfaz de usuario
     * @param view Vista en la que se hizo clic
     */
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    /**
     * Clase que crea las opciones que se mostrarán al mantener un clic sobre un plato concreto,
     * las cuales serán Actualizar y Eliminar
     * @param contextMenu El contexto del menú que se está construyendo
     * @param view La vista para la que se está construyendo el contexto del menú
     * @param menuInfo Información adicional sobre el elemento para el que se debe mostrar
     *                 el contexto del menú. Esta información variará dependiendo de la clase de
     *                 la vista
     */
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Seleccione una acción");

        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
