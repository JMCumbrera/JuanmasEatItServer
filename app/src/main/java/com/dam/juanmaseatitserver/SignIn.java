package com.dam.juanmaseatitserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dam.juanmaseatitserver.Common.Common;
import com.dam.juanmaseatitserver.Model.User;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Objects;
import io.paperdb.Paper;

/**
 * Clase necesaria para iniciar sesión en la aplicación
 */
public class SignIn extends AppCompatActivity {
    // Atributos de clase
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    MaterialCheckBox cKbRemember;
    TextView txtForgotPwd;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        cKbRemember = (MaterialCheckBox)findViewById(R.id.ckbRemember);
        txtForgotPwd = (TextView)findViewById(R.id.txtForgotPwd);

        // Inicializamos Paper (para la función de recordar usuario)
        Paper.init(this);

        // Inicializamos  Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPwd.setOnClickListener(view -> showForgotPwdDialog());

        btnSignIn.setOnClickListener(view -> signInUser(edtPhone.getText().toString(), edtPassword.getText().toString()));
    }

    /**
     * Método que permite iniciar sesión sesión en la aplicación
     * @param phone Teléfono del usuario
     * @param password Contraseña del usuario
     */
    private void signInUser(String phone, String password) {
        if (Common.isConnectedToInternet(getBaseContext())) {
            // Guardamos el usuario y la contraseña (Paper)
            if (cKbRemember.isChecked()) {
                Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
            }

            ProgressDialog mDialog = new ProgressDialog(SignIn.this);
            mDialog.setMessage("Por favor, espere");
            mDialog.show();

            final String localPhone = phone;
            final String localPassword = password;

            if (edtPassword.getText().toString().isEmpty() ||
                    edtPhone.getText().toString().isEmpty()) {
                mDialog.dismiss();
                Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Si ninguno de los campos está en blanco, seguimos
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Comprueba si el usuario no existe en la BD
                        if (dataSnapshot.child(localPhone).exists()) {
                            // Conseguimos la info. del usuario
                            mDialog.dismiss();
                            User user = dataSnapshot.child(localPhone).getValue(User.class);

                            // Usamos el set del atributo Phone (teléfono), para establecerlo
                            user.setPhone(localPhone);

                            // Ahora comprobamos que el usuario pertenezca al staff
                            if (Boolean.parseBoolean(user.getIsStaff())) {
                                if (user.getPassword().equals(localPassword)) {
                                    Intent login = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(login);
                                    finish();

                                    table_user.removeEventListener(this);
                                } else
                                    Toast.makeText(SignIn.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(SignIn.this, "Por favor, inicie sesión con una cuenta con estatus de staff", Toast.LENGTH_SHORT).show();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "El usuario no existe en la BD", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        } else {
            Toast.makeText(this, "Por favor, compruebe su conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método que muestra un cuadro de diálogo que nos permite recuperar nuestra contraseña,
     * haciendo uso de nuestro código de seguridad
     */
    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Olvidó su conbtraseña?");
        builder.setMessage("Teclee su código de seguridad");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.baseline_security_24);

        MaterialEditText edtPhone = (MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
        MaterialEditText edtSecureCode = (MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("SI", (dialog, which) -> {
            // Comprobamos que el usuario esté disponible
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                        if (edtPhone.getText().toString().isEmpty() || edtSecureCode.getText().toString().isEmpty()) {
                            Toast.makeText(SignIn.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                        } else {
                            if (user != null && user.getSecureCode().equals(edtSecureCode.getText().toString()))
                                Toast.makeText(SignIn.this, "Su contraseña es: " + user.getPassword(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignIn.this, "Código de seguridad incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignIn.this, "El usuario no existe en la BD", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        builder.setNegativeButton("NO", (dialog, which) -> {});

        builder.show();
    }
}