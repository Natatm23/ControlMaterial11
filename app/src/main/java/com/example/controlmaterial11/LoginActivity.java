package com.example.controlmaterial11;

import android.content.SharedPreferences;
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
    private Button btnIngresar, btnRegistrarme; // Declaración del botón de registrarse
    private DBHelper dbHelper;

    public static int idUsuario; // Variable estática para almacenar el ID del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Inicializar el DBHelper para interactuar con la base de datos
        dbHelper = new DBHelper(this);

        // Inicializar las vistas
        txtUsuario = findViewById(R.id.txtusuario);
        txtClave = findViewById(R.id.txtclave);
        btnIngresar = findViewById(R.id.btningresar);
        btnRegistrarme = findViewById(R.id.btnregistrarme); // Inicializar el botón de registrarse

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
                        // Obtener el id_usuario y el nombre del usuario
                        idUsuario = dbHelper.obtenerIdUsuario(usuario);

                        // Guardar el idUsuario en SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("nombre_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("Id_Usuario", idUsuario); // Guardar el idUsuario
                        editor.apply();

                        // Si las credenciales son correctas, redirigir a otra actividad
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        // Crear el intent para pasar a la siguiente actividad
                        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);

                        // Pasar el nombre de usuario a la siguiente actividad
                        intent.putExtra("nombre_usuario", usuario);

                        // Iniciar la nueva actividad
                        startActivity(intent);
                        finish(); // Finalizar la actividad de inicio de sesión
                    } else {
                        // Si las credenciales son incorrectas, mostrar un mensaje de error
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Configurar la acción del botón "Registrarme"
        btnRegistrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(LoginActivity.this, Registro_Activity.class);
                startActivity(intent);
            }
        });
    }
}
