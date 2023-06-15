package com.dam.juanmaseatitserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dam.juanmaseatitserver.Model.Request;
import com.dam.juanmaseatitserver.Model.User;

// Usuario actual logueado
public class Common {
    // Atributos de clase
    public static User currentUser;
    public static Request currentRequest;
    public static final String UPDATE = "Actualizar";
    public static final String DELETE = "Borrar";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    /**
     * Método que convierte los estados de los pedidos de Int a Strings legibles
     * @param code Código que representa el estado en el que se encuentra el pedido del cliente
     * @return Cadena String con el estado del pedido en cuestión
     */
    public static String convertCodeToStatus(String code) {
        if (code.equals("0"))
            return "Realizado";
        else if (code.equals("1"))
            return "En reparto";
        else
            return "Entregado";
    }

    /**
     * Método que comprobará la conexión a internet del dispositivo
     * @param context Contexto en el que ejecutaremos el método
     * @return El método devolverá true si existe conexión a internet o false en caso contrario
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
