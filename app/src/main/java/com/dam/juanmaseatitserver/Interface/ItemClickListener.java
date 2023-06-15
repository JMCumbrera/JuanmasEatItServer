package com.dam.juanmaseatitserver.Interface;

import android.view.View;

/**
 * Interfaz que define la lógica para manejar el comportamiento de la aplicación cuando se
 * haga clic en un elemento en concreto
 */
public interface ItemClickListener {
    /**
     * Este método se invocará cuando se produzca un clic en un elemento de la interfaz de usuario
     * @param view Vista en la que se produjo el clic
     * @param position Posición del elemento en cuestión
     * @param isLongClick Parámetro booleano, el cual indica si el clic fué largo o corto
     */
    void onClick(View view, int position, boolean isLongClick);
}
