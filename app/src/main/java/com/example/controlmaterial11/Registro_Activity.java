package com.example.controlmaterial11;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Registro_Activity extends AppCompatActivity {

    // Declarar las vistas y el botón
    private EditText txtNombreUsuario, txtIdEmpleado, txtClave;
    private Button btnRegistrar;
    private DBHelper dbHelper;
    private Spinner spinnerDepartamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Inicializar el DBHelper
        dbHelper = new DBHelper(this); // Mover esta línea aquí

        // Inicializar las vistas del layout
        txtNombreUsuario = findViewById(R.id.txtnombreusuario);
        txtIdEmpleado = findViewById(R.id.txt_idempleado);
        txtClave = findViewById(R.id.txtclave);
        btnRegistrar = findViewById(R.id.btnregistrar);
        spinnerDepartamentos = findViewById(R.id.spinner_REGISTRO); // Inicializar el spinner

        // Obtener los departamentos de la base de datos
        List<String> departamentos = dbHelper.getDepartamentos();

        // Crear un adaptador para el spinner usando el diseño personalizado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // Usar el archivo de diseño del spinner
                departamentos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartamentos.setAdapter(adapter);

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
