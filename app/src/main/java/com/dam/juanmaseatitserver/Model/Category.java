package com.dam.juanmaseatitserver.Model;

/**
 * Clase encargada de representar las categorías en las que los diversos platos del restaurante
 * pueden entrar
 */
public class Category {
    // Atributos de clase
    private String Name;
    private String Image;

    // Constructores
    public Category() {}

    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    // Getters y Setters
    public String getName() { return Name; }
    public void setName(String name) { Name = name; }

    public String getImage() { return Image; }
    public void setImage(String image) { Image = image; }
}
