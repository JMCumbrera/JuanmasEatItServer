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
 * Clase utilizada para representar y manejar la vista de un elemento del menú en un RecyclerView
 */
public class MenuViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener
{
    // Atributos de clase
    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    // Constructor

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);

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
     * Clase que crea las opciones de actualizar y eliminar al mantener el clic sobre un elemento
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
