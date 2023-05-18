package com.dam.juanmaseatitserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.juanmaseatitserver.Common.Common;
import com.dam.juanmaseatitserver.Interface.ItemClickListener;
import com.dam.juanmaseatitserver.Model.Category;
import com.dam.juanmaseatitserver.Model.Request;
import com.dam.juanmaseatitserver.ViewHolder.MenuViewHolder;
import com.dam.juanmaseatitserver.databinding.FragmentHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Optional;
import java.util.UUID;

public class HomeFragment extends Fragment {
    // Attributos de clase
    private FragmentHomeBinding binding;
    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    MaterialEditText edtName;
    Button btnUpload, btnSelect;
    Category newCategory;
    Uri saveUri;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // Inicializamos la BD
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        // Cargamos el menú
        recycler_menu = binding.recyclerHome;
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext()){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("TAG", "meet a IOOBE in RecyclerView");
                }
            }
        };
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu(root.getContext());

        return root;
    }

    private void loadMenu(Context context) {
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(category.limitToLast(50), Category.class)
                        .setLifecycleOwner(getViewLifecycleOwner())
                        .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.txtMenuName.setText(model.getName());

                Picasso.with(context).load(model.getImage())
                        .into(holder.imageView);

                holder.setItemClickListener((view, position1, isLongClick) -> {
                    // Get CategoryId and send to new Activity
                    Intent foodList = new Intent(context, FoodList.class);

                    // Because CategoryId is key, so we just get the key of this item
                    foodList.putExtra("CategoryId", adapter.getRef(position1).getKey());
                    startActivity(foodList);
                });
            }
        };

        recycler_menu.setAdapter(adapter);
    }

    // onContextItemSelected de Home (borrar en caso que no funcione)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }

        //return super.onContextItemSelected(item);
        return true;
    }

    /**
     * Con este método puedes borrar una categoría, desde el dialog. Este se abrirá sobre la categoría
     * que seleccionemos, y nos dará las opciones de actualizar y borrar
     * @param key Parámetro de tipo String relacionado con una categoría, y que nos permitirá
     *            encontrarla y borrarla
     */
    private void deleteCategory(String key) {
        category.child(key).removeValue();
        Toast.makeText(root.getContext(), "¡Categoría eliminada!", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(String key, Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(root.getContext());
        alertDialog.setTitle("Actualizar categoría");
        alertDialog.setMessage("Por favor, rellene toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        // Establecemos un nombre por defecto
        edtName.setText(item.getName());

        // Evento para el botón
        btnSelect.setOnClickListener(view -> {
            // Esto permitirá al usuario elegir una imagen de la galería y salvar su URI
            chooseImage();
        });

        btnUpload.setOnClickListener(view -> changeImage(item));

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.baseline_shopping_cart_24);

        // Establecemos el botón
        alertDialog.setPositiveButton("SÍ", (dialog, which) -> {
            dialog.dismiss();

            // Actualizamos la información
            item.setName(edtName.getText().toString());
            category.child(key).setValue(item);
        });

        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleecione una imagen"), Common.PICK_IMAGE_REQUEST);
    }

    /**
     * Este método tiene la utilidad de permitir cambiar la imagen de una categoría
     * @param item Parámetro de tipo Category que corresponde a la categoría cuya imagen queremos cambiar
     */
    private void changeImage(final Category item) {
        if (saveUri != null) {
            ProgressDialog mDialog = new ProgressDialog(root.getContext());
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        mDialog.dismiss();
                        Toast.makeText(root.getContext(), "¡Imagen subida!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> item.setImage(uri.toString()));
                    }).addOnFailureListener(exception -> {
                        mDialog.dismiss();
                        Toast.makeText(root.getContext(), "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        mDialog.setMessage("Subido " + progress + "%");
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
