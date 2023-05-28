package com.dam.juanmaseatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import com.dam.juanmaseatitserver.Common.Common;
import com.dam.juanmaseatitserver.Interface.ItemClickListener;
import com.dam.juanmaseatitserver.Model.Category;
import com.dam.juanmaseatitserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.juanmaseatitserver.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class Home extends AppCompatActivity {
    // Atributos de clase
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding homeBinding;
    TextView txtFullName;
    private DrawerLayout drawer;

    // Firebase
    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    // View
    //RecyclerView recycler_menu;
    //RecyclerView.LayoutManager layoutManager;

    // Add New Menu Layout
    MaterialEditText edtName;
    Button btnUpload, btnSelect;

    Category newCategory;
    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        Toolbar toolbar = homeBinding.appBarHome.toolbar;
        toolbar.setTitle("Gestión de Menú");
        setSupportActionBar(homeBinding.appBarHome.toolbar);

        // Inicializamos Firebase
        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        
        homeBinding.appBarHome.fab.setOnClickListener(view -> {
            showDialog();
        });

        drawer = homeBinding.drawerLayout;
        NavigationView navigationView = homeBinding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, /*R.id.nav_gallery,*/ R.id.nav_order_status)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Estableceremos el nombre para el usuario
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

       // Inicializamos la vista (View)
       //recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
       //recycler_menu.setHasFixedSize(true);
       //layoutManager = new LinearLayoutManager(this);
       //recycler_menu.setLayoutManager(layoutManager);

        //loadMenu();
    }

    /**
     * Método que mostrará una ventana que nos permitirá añadir categorías nuevas. Podremos acceder
     * a esta ventana pulsando el botón de la esquina inferior derecha de la pantalla.
     */
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Añadir categoría nueva");
        alertDialog.setMessage("Por favor, rellene toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        // Evento para el botón
        btnSelect.setOnClickListener(view -> {
            // Esto permitirá al usuario elegir una imagen de la galería y salvar su URI
            chooseImage();
        });

        btnUpload.setOnClickListener(view -> uploadImage());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.baseline_shopping_cart_24);

        // Establecemos el botón
        alertDialog.setPositiveButton("SÍ", (dialog, which) -> {
            dialog.dismiss();

            // Aquí creamos una categoría nueva
            if (newCategory != null) {
                categories.push().setValue(newCategory);
                Toast.makeText(Home.this, "", Toast.LENGTH_SHORT).show();
                Snackbar.make(drawer, "La categoría nueva " + newCategory.getName() + " fue añadida", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    /**
     * Este método nos otorga la capacidad de poder elegir imágenes desde las carpetas de nuestro
     * dispositivo Android
     */
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleecione una imagen"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (saveUri != null) {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "¡Imagen subida!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Establecemos valor para newCategory si la imagen es subida, de modo que obtenemos un enlace de descarga
                                    newCategory = new Category(edtName.getText().toString(), uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Subido " + progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("¡Imagen seleccionada!");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);

        homeBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.nav_menu):
                        navController.navigate(R.id.nav_home);
                        Toast.makeText(Home.this, "Menu", Toast.LENGTH_SHORT).show();
                        return true;
                    case (R.id.nav_orders):
                        navController.navigate(R.id.nav_order_status);
                        Toast.makeText(Home.this, "Order status", Toast.LENGTH_SHORT).show();
                        return true;
                    case (R.id.nav_sign_out):
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case (R.id.nav_change_pwd):
                        showChangePasswordDialog();
                        return true;
                    default: return false;
                }
            }
        });
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CAMBIAR CONTRASEÑA");
        alertDialog.setMessage("Por favor, rellene toda la información");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        MaterialEditText edtPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtPassword);
        MaterialEditText edtNewPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtNewPassword);
        MaterialEditText edtRepeatPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);

        // Botón
        alertDialog.setPositiveButton("CAMBIAR", (dialog, which) -> {
            // Cambiamos la contraseña aquí

            android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
            waitingDialog.show();

            if (edtPassword.getText().toString().isEmpty() ||
                    edtNewPassword.getText().toString().isEmpty() ||
                    edtRepeatPassword.getText().toString().isEmpty()) {
                waitingDialog.dismiss();
                Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Comprobamos la contraseña anterior
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())) {
                    // Comprobamos la contraseña nueva y la repetimos
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
                        Map<String, Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("Password", edtNewPassword.getText().toString());

                        // Llevamos la actualización
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(task -> {
                                    waitingDialog.dismiss();
                                    Toast.makeText(Home.this, "La contraseña fue actualizada", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "Las contraseñas introducidas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    // Métodos para actualizar / borrar categorías
    // TODO: No funcionan los dos onContextItemSelected a la vez, mover al HomeFragment
    //@Override
    //public boolean onContextItemSelected(@NonNull MenuItem item) {
//
    //    if (adapter == null) {
    //        Toast.makeText(this, "El adapter es nulo", Toast.LENGTH_SHORT).show();
    //        return false;
    //    }
//
    //    if (item.getTitle().equals(Common.UPDATE)) {
    //        showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
    //    } else if (item.getTitle().equals(Common.DELETE)) {
    //        deleteCategory(adapter.getRef(item.getOrder()).getKey());
    //    }
//
    //    //return super.onContextItemSelected(item);
    //    return true;
    //}

    //private void deleteCategory(String key) {
    //    categories.child(key).removeValue();
    //    Toast.makeText(this, "¡Categoría eliminada!", Toast.LENGTH_SHORT).show();
    //}

    //private void showUpdateDialog(String key, Category item) {
    //    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
    //    alertDialog.setTitle("Actualizar categoría");
    //    alertDialog.setMessage("Por favor, rellene toda la información");
//
    //    LayoutInflater inflater = this.getLayoutInflater();
    //    View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);
//
    //    edtName = add_menu_layout.findViewById(R.id.edtName);
    //    btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
    //    btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
//
    //    // Establecemos un nombre por defecto
    //    edtName.setText(item.getName());
//
    //    // Evento para el botón
    //    btnSelect.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View view) {
    //            // Esto permitirá al usuario elegir una imagen de la galería y salvar su URI
    //            chooseImage();
    //        }
    //    });
//
    //    btnUpload.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View view) {
    //            changeImage(item);
    //        }
    //    });
//
    //    alertDialog.setView(add_menu_layout);
    //    alertDialog.setIcon(R.drawable.baseline_shopping_cart_24);
//
    //    // Establecemos el botón
    //    alertDialog.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
    //        @Override
    //        public void onClick(DialogInterface dialog, int which) {
    //            dialog.dismiss();
//
    //            // Actualizamos la información
    //            item.setName(edtName.getText().toString());
    //            categories.child(key).setValue(item);
    //        }
    //    });
//
    //    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
    //        @Override
    //        public void onClick(DialogInterface dialog, int which) {
//
    //            dialog.dismiss();
    //        }
    //    });
//
    //    alertDialog.show();
    //}
}