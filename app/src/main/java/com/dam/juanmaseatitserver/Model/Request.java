package com.dam.juanmaseatitserver.Model;

import java.util.List;

/**
 * Clase encargada de los datos de envío tales como la dirección, el teléfono, etc
 */
public class Request {
    // Atributos relacionados con el envío
    private String phone, name, address, total, status, comment;
    private List<Order> foods;

    // Constructores
    public Request() {}

    public Request(String phone, String name, String address, String total, String comment, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.foods = foods;
        // Será 0 poer defecto
        // 0: Pedido realizado, 1: Pedido en envío, 2: Pedido enviado
        this.status = "0";
        this.comment = comment;
    }

    // Getters y Setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }

    public List<Order> getFoods() { return foods; }
    public void setFoods(List<Order> foods) { this.foods = foods; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
