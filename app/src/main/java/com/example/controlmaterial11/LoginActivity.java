package com.example.controlmaterial11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.controlmaterial11.DBHelper;


import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Declarar variables
    private EditText txtUsuario, txtClave;
    private Button btnIngresar;
    private DBHelper dbHelper;

    public static int idUsuario; // Variable estática para almacenar el ID del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Inicializar el DBHelper para interactuar con la base de datos
        dbHelper = new DBHelper(this);

        dbHelper.insertarUsuario("nata", "123"); //usuario de prueba

        // Inicializar las vistas
        txtUsuario = findViewById(R.id.txtusuario);
        txtClave = findViewById(R.id.txtclave);
        btnIngresar = findViewById(R.id.btningresar);

        // Configurar la acción del botón de inicio de sesión
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el texto de usuario y clave
                String usuario = txtUsuario.getText().toString().trim();
                String clave = txtClave.getText().toString().trim();

                // Verificar si los campos están vacíos
                if (usuario.isEmpty() || clave.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar si el usuario existe en la base de datos
                    boolean existeUsuario = dbHelper.verificarUsuario(usuario, clave);

                    if (existeUsuario) {
                        // Obtener el id_usuario
                        idUsuario = dbHelper.obtenerIdUsuario(usuario);

                        // Si las credenciales son correctas, redirigir a otra actividad
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                        startActivity(intent);
                        finish(); // Finalizar la actividad de inicio de sesión
                    } else {
                        // Si las credenciales son incorrectas, mostrar un mensaje de error
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}