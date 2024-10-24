package com.example.controlmaterial11;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Registro_Activity extends AppCompatActivity {

    // Declarar las vistas y el botón
    private EditText txtNombreUsuario, txtIdEmpleado, txtClave;
    private Button btnRegistrar;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Inicializar las vistas del layout
        txtNombreUsuario = findViewById(R.id.txtnombreusuario);
        txtIdEmpleado = findViewById(R.id.txt_idempleado);
        txtClave = findViewById(R.id.txtclave);
        btnRegistrar = findViewById(R.id.btnregistrar);

        // Inicializar el DBHelper
        dbHelper = new DBHelper(this);

        // Configurar la acción del botón "Registrar"
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados en los campos de texto
                String nombreUsuario = txtNombreUsuario.getText().toString().trim();
                String idEmpleado = txtIdEmpleado.getText().toString().trim();
                String clave = txtClave.getText().toString().trim();

                // Verificar si los campos están vacíos
                if (nombreUsuario.isEmpty() || idEmpleado.isEmpty() || clave.isEmpty()) {
                    Toast.makeText(Registro_Activity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Llamar al método para insertar el usuario en la base de datos
                    boolean exito = dbHelper.insertarUsuario(nombreUsuario, clave, idEmpleado);

                    // Mostrar un mensaje dependiendo del resultado
                    if (exito) {
                        Toast.makeText(Registro_Activity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        finish(); // Finalizar la actividad de registro
                    } else {
                        Toast.makeText(Registro_Activity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
