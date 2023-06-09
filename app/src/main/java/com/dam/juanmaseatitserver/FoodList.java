package com.dam.juanmaseatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitserver.Common.Common;
import com.dam.juanmaseatitserver.Model.Food;
import com.dam.juanmaseatitserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import java.util.UUID;

/**
 * Clase encargada de mostrar la lista de platos en cada categoría.
 */
public class FoodList extends AppCompatActivity {
    // Atributos de clase
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ConstraintLayout rootLayout;
    FloatingActionButton fab;

    // Firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    // Añadir plato nuevo
    MaterialEditText edtName, edtDescription, edtPrice, edtDiscount;
    Button btnSelect, btnUpload;
    Food newFood;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Firebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference("Food");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Inicializamos
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (ConstraintLayout) findViewById(R.id.rootLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddFoodDialog());

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())
            loadListFood(categoryId);
    }

    /**
     * Método diseñado para mostrar un cuadro de diálogo que nos otorgará la capacidad de
     * añadir un plato nuevo a la carta del restaurante
     */
    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Añadir plato nuevo");
        alertDialog.setMessage("Por favor, rellene toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);


        // Evento para el botón
        btnSelect.setOnClickListener(view -> chooseImage());

        btnUpload.setOnClickListener(view -> uploadImage());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.baseline_shopping_cart_24);

        // Establecemos el botón
        alertDialog.setPositiveButton("SÍ", (dialog, which) -> {
            dialog.dismiss();

            // Aquí creamos una categoría nueva
            if (newFood != null) {
                foodList.push().setValue(newFood);
                Snackbar.make(rootLayout, "El plato nuevo " + newFood.getName() + " fue añadido", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    /**
     * Este método permite subir una imagen elegida desde el dispositivo Android, al
     * crear un plato nuevo
     */
    private void uploadImage() {
        if (saveUri != null) {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        mDialog.dismiss();
                        Toast.makeText(FoodList.this, "¡Imagen subida!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Establecemos valor para newCategory si la imagen es subida, de modo que obtenemos un enlace de descarga
                            newFood = new Food();
                            newFood.setName(edtName.getText().toString());
                            newFood.setDescription(edtDescription.getText().toString());
                            newFood.setPrice(edtPrice.getText().toString());
                            newFood.setDescription(edtDescription.getText().toString());
                            newFood.setMenuId(categoryId);
                            newFood.setImage(uri.toString());
                        });
                    }).addOnFailureListener(exception -> {
                        mDialog.dismiss();
                        Toast.makeText(FoodList.this, "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        mDialog.setMessage("Subido " + progress + "%");
                    });
        }
    }

    /**
     * Método que otorga la capacidad de elegir una imagen para el plato nuevo que se vaya a crear
     */
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), Common.PICK_IMAGE_REQUEST);
    }

    /**
     * Este método carga y muestra la lista de platos en función de la categoría seleccionada
     * @param categoryId Identificador de la categoría seleccionada
     */
    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getName());
                foodViewHolder.food_price.setText(String.format("%s €", food.getPrice().toString()));
                Picasso.with(getBaseContext())
                        .load(food.getImage())
                        .into(foodViewHolder.food_image);

                foodViewHolder.setItemClickListener((view, position, isLongClick) -> {});
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("¡Imagen seleccionada!");
        }
    }

    /**
     * Este método se llama cuando se selecciona un elemento del contexto del menú, y según se
     * seleccione un elemento del menñu u otro se llevará a cabo una determinada acción
     * @param item El elemento del contexto del menú que se seleccionó
     * @return Valor booleano devuelto por el método base (super) de la clase padre
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Método que nos permite hacer efectivo el borrado de un plato
     * @param key Parámetro que nos permitirá localizar el plato a borrar
     */
    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    /**
     * Método que mostrará un cuadro de diálogo para poder editar los campos de un plato
     * @param key
     * @param item Plato de comida que va a ser editado
     */
    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Editar plato");
        alertDialog.setMessage("Por favor, rellene toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount);

        // Establecemos un valor por defecto para la vista
        edtName.setText(item.getName());
        edtDiscount.setText(item.getDiscount());
        edtPrice.setText(item.getPrice());
        edtDescription.setText(item.getDescription());

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);


        // Evento para el botón
        btnSelect.setOnClickListener(view -> chooseImage());

        btnUpload.setOnClickListener(view -> changeImage(item));

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.baseline_shopping_cart_24);

        // Establecemos el botón
        alertDialog.setPositiveButton("SÍ", (dialog, which) -> {
            dialog.dismiss();

            // Actualizamos la información
            item.setName(edtName.getText().toString());
            item.setPrice(edtPrice.getText().toString());
            item.setDiscount(edtDiscount.getText().toString());
            item.setDescription(edtDescription.getText().toString());

            foodList.child(key).setValue(item);
            Snackbar.make(rootLayout, "El plato" + item.getName() + " fue editado", Snackbar.LENGTH_SHORT)
                    .show();
        });

        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    /**
     * Este método tiene la utilidad de permitir cambiar la imagen de un plato de comida
     * @param item Parámetro de tipo Food que corresponde al plato cuya imagen queremos cambiar
     */
    private void changeImage(final Food item) {
        if (saveUri != null) {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        mDialog.dismiss();
                        Toast.makeText(FoodList.this, "¡Imagen cambiada!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> item.setImage(uri.toString()));
                    }).addOnFailureListener(exception -> {
                        mDialog.dismiss();
                        Toast.makeText(FoodList.this, "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        mDialog.setMessage("Subido " + progress + "%");
                    });
        }
    }
}