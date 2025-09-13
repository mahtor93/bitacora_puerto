package com.tornerias.bitacora;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tornerias.bitacora.db.BitacoraDatabase;
import com.tornerias.bitacora.db.CartillaProtocolosEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class FormularioCartillaProtocolos extends AppCompatActivity {

    private EditText inputRegistroNumero;
    private EditText inputFechaRegistro;
    private EditText inputFechaSupervision;
    private Spinner spinnerBuzo1, spinnerBuzo2;
    private Spinner spinnerPinturaBase, spinnerPinturaEsquema;
    private List<String> nombresCompletos;
    private List<String> coloresPintura;
    private ArrayAdapter<String> adapterBuzo1, adapterBuzo2;
    private ArrayAdapter<String> adapterPinturaBase, adapterPinturaEsquema;
    private String selectedBuzo1 = null;
    private String selectedBuzo2 = null;
    private RadioGroup radioGroupEsquema;
    private RadioButton radPiloteA, radPiloteB, radPiloteD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_cartilla_protocolos);
        inputRegistroNumero = findViewById(R.id.input_registro_numero);

        cargarProximoNumeroRegistro();


        // Inicializar componentes de RadioGroup e ImageViews
        radioGroupEsquema = findViewById(R.id.radioGroupEsquema);
        radPiloteA = findViewById(R.id.radPiloteA);
        radPiloteB = findViewById(R.id.radPiloteB);
        radPiloteD = findViewById(R.id.radPiloteD);

        ImageView imagePiloteA = findViewById(R.id.imagePiloteA);
        ImageView imagePiloteB = findViewById(R.id.imagePiloteB);
        ImageView imagePiloteD = findViewById(R.id.imagePiloteD);

        // Hacer las imágenes clickeables para seleccionar el RadioButton correspondiente
        imagePiloteA.setOnClickListener(v -> radioGroupEsquema.check(R.id.radPiloteA));
        imagePiloteB.setOnClickListener(v -> radioGroupEsquema.check(R.id.radPiloteB));
        imagePiloteD.setOnClickListener(v -> radioGroupEsquema.check(R.id.radPiloteD));

        // Ajustar padding por los systemBars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnGuardar = findViewById(R.id.btnGuardar);

        // Referencias a los campos de fecha en el layout
        inputFechaRegistro = findViewById(R.id.input_fecha_registro);
        inputFechaSupervision = findViewById(R.id.input_fecha_supervision);

        // Referencias a los Spinners de buzos
        spinnerBuzo1 = findViewById(R.id.input_buzo_aplicador1);
        spinnerBuzo2 = findViewById(R.id.input_buzo_aplicador2);

        // Referencias a los Spinners de pintura
        spinnerPinturaBase = findViewById(R.id.input_pintura_base);
        spinnerPinturaEsquema = findViewById(R.id.input_pintura_esquema);

        // Inicializar la lista de nombres
        nombresCompletos = Arrays.asList("Luis Troncoso", "Manuel San Martín", "Nicolás Saez", "José Luis", "Fernando Marquez", "Luis Carlos Tornería");

        // Inicializar la lista de colores de pintura
        coloresPintura = Arrays.asList("Verde", "Rojo");

        // Configurar los Spinners de buzos
        configurarSpinnersBuzos();

        // Configurar los Spinners de pintura
        configurarSpinnersPintura();

        Calendar calendar = Calendar.getInstance();

        btnGuardar.setOnClickListener(v -> guardarRegistro());

        // Listener para la fecha de registro
        inputFechaRegistro.setOnClickListener(v -> {
            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String fecha = String.format("%02d/%02d/%04d", day, month + 1, year);
                        inputFechaRegistro.setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Listener para la fecha de supervisión
        inputFechaSupervision.setOnClickListener(v -> {
            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String fecha = String.format("%02d/%02d/%04d", day, month + 1, year);
                        inputFechaSupervision.setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    // Método para obtener la selección del esquema de pilote
    private int getSelectedEsquema() {
        int selectedId = radioGroupEsquema.getCheckedRadioButtonId();

        if (selectedId == R.id.radPiloteA) {
            return 1;
        } else if (selectedId == R.id.radPiloteB) {
            return 2;
        } else if (selectedId == R.id.radPiloteD) {
            return 3;
        } else {
            return 0; // Ninguno seleccionado
        }
    }


    private void cargarProximoNumeroRegistro() {
        new Thread(() -> {
            try {
                BitacoraDatabase db = BitacoraDatabase.getDatabase(getApplicationContext());
                Integer ultimoNumero = db.cartillaProtocolosDao().getMaxRegistroNumero();

                runOnUiThread(() -> {
                    if (ultimoNumero != null) {
                        // Si hay registros, incrementar el último número
                        inputRegistroNumero.setText(String.valueOf(ultimoNumero + 1));
                    } else {
                        // Si no hay registros, empezar desde 1
                        inputRegistroNumero.setText("1");
                    }

                    // 🔥 Hacer que el campo sea de solo lectura o editable según necesites
                    // inputRegistroNumero.setEnabled(false); // Descomenta si quieres que sea solo lectura
                    // inputRegistroNumero.setFocusable(false); // Descomenta si quieres que no sea focusable
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // En caso de error, poner 1 por defecto
                    inputRegistroNumero.setText("");
                    Toast.makeText(this, "Error al cargar número automático", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // Método para establecer una selección del esquema de pilote
    private void setSelectedEsquema(int esquema) {
        switch (esquema) {
            case 1:
                radPiloteA.setChecked(true);
                break;
            case 2:
                radPiloteB.setChecked(true);
                break;
            case 3:
                radPiloteD.setChecked(true);
                break;
            default:
                radioGroupEsquema.clearCheck();
                break;
        }
    }

    private void configurarSpinnersBuzos() {
        // Crear adaptadores con la lista completa inicialmente
        adapterBuzo1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(nombresCompletos));
        adapterBuzo2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(nombresCompletos));

        adapterBuzo1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterBuzo2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerBuzo1.setAdapter(adapterBuzo1);
        spinnerBuzo2.setAdapter(adapterBuzo2);

        // Agregar un elemento vacío al inicio
        adapterBuzo1.insert("Seleccione un buzo", 0);
        adapterBuzo2.insert("Seleccione un buzo", 0);

        spinnerBuzo1.setSelection(0);
        spinnerBuzo2.setSelection(0);

        // Listeners para manejar la selección exclusiva
        spinnerBuzo1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedBuzo1 = (String) parent.getItemAtPosition(position);
                    actualizarOpcionesSpinnersBuzos();
                } else {
                    selectedBuzo1 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBuzo1 = null;
            }
        });

        spinnerBuzo2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedBuzo2 = (String) parent.getItemAtPosition(position);
                    actualizarOpcionesSpinnersBuzos();
                } else {
                    selectedBuzo2 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBuzo2 = null;
            }
        });
    }

    private void guardarRegistro() {
        if (!validarCampos()) {
            return; // Detener si hay campos inválidos
        }
        // Obtener los datos desde los campos
        String fechaRegistroStr = inputFechaRegistro.getText().toString();
        String fechaSupervisionStr = inputFechaSupervision.getText().toString();
        String buzo1 = selectedBuzo1;
        String buzo2 = selectedBuzo2;
        String pinturaBase = spinnerPinturaBase.getSelectedItem().toString();
        String pinturaEsquema = spinnerPinturaEsquema.getSelectedItem().toString();
        int esquema = getSelectedEsquema();

        // Convertir strings a Date
        Date fechaRegistro = null;
        Date fechaSupervision = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            if (!fechaRegistroStr.isEmpty()) {
                fechaRegistro = dateFormat.parse(fechaRegistroStr);
            }
            if (!fechaSupervisionStr.isEmpty()) {
                fechaSupervision = dateFormat.parse(fechaSupervisionStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en el formato de fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener otros campos del formulario
        EditText inputRegistroNumero = findViewById(R.id.input_registro_numero);
        EditText inputElementoNumero = findViewById(R.id.input_elemento_numero);
        EditText inputDiametro = findViewById(R.id.input_diametro);
        EditText inputRollos = findViewById(R.id.input_rollos);
        EditText inputBuzoSupervisor = findViewById(R.id.input_buzo_supervisor);
        EditText inputHidrodinamicaSupervisor = findViewById(R.id.input_hidrodinamica_supervisor);

        Integer numeroRegistro = null;
        Integer numeroElemento = null;
        Integer diametro = null;
        Short rollos = null;

        try {
            if (!inputRegistroNumero.getText().toString().isEmpty()) {
                numeroRegistro = Integer.parseInt(inputRegistroNumero.getText().toString());
            }
            if (!inputElementoNumero.getText().toString().isEmpty()) {
                numeroElemento = Integer.parseInt(inputElementoNumero.getText().toString());
            }
            if (!inputDiametro.getText().toString().isEmpty()) {
                diametro = Integer.parseInt(inputDiametro.getText().toString());
            }
            if (!inputRollos.getText().toString().isEmpty()) {
                rollos = Short.parseShort(inputRollos.getText().toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en los campos numéricos", Toast.LENGTH_SHORT).show();
            return;
        }

        String buzoSupervisor = inputBuzoSupervisor.getText().toString();
        String hidrodinamicaSupervisor = inputHidrodinamicaSupervisor.getText().toString();

        // Convertir el esquema numérico a char (1->'A', 2->'B', 3->'D')
// Convertir el esquema numérico a char (1->'A', 2->'B', 3->'D')
        char esquemaChar;
        switch (esquema) {
            case 1:
                esquemaChar = 'A';
                Toast.makeText(this, "Seleccionado esquema: A", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                esquemaChar = 'B';
                Toast.makeText(this, "Seleccionado esquema: B", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                esquemaChar = 'D';
                Toast.makeText(this, "Seleccionado esquema: D", Toast.LENGTH_SHORT).show();
                break;
            default:
                esquemaChar = ' ';
                Toast.makeText(this, "Ningún esquema seleccionado", Toast.LENGTH_SHORT).show();
                break;
        }


        // Crear la entidad CartillaProtocolosEntity
        CartillaProtocolosEntity registro = new CartillaProtocolosEntity();
        registro.idRegistro = UUID.randomUUID(); // Generar UUID único
        registro.registroNumero = numeroRegistro;
        registro.elementoNumero = numeroElemento;
        registro.fechaRegistro = fechaRegistro;
        registro.pinturaEsquema = pinturaEsquema;
        registro.diametro = diametro;
        registro.esquemaPilote = esquemaChar;
        registro.pinturaBase = pinturaBase;
        registro.rollos = rollos != null ? rollos : (short) 0;
        registro.buzoAplicador1 = buzo1;
        registro.buzoAplicador2 = buzo2;
        registro.buzoSupervisor = buzoSupervisor;
        registro.hidrodinamicaSupervisor = hidrodinamicaSupervisor;
        registro.fechaSupervision = fechaSupervision;

        // Guardar en la BD (en un hilo aparte, no en el main thread)
        new Thread(() -> {
            try {
                BitacoraDatabase db = BitacoraDatabase.getDatabase(getApplicationContext());
                db.cartillaProtocolosDao().insert(registro);

                // Opcional: Mostrar mensaje de éxito
                runOnUiThread(() -> {
                    Toast.makeText(FormularioCartillaProtocolos.this, "Registro guardado exitosamente", Toast.LENGTH_SHORT).show();
                    // Opcional: Limpiar el formulario o cerrar la actividad
                    // finish();
                });

                Intent intent = new Intent(FormularioCartillaProtocolos.this, MainActivity.class);

                // Limpiar el stack de actividades para que MainActivity sea la única
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                // Iniciar MainActivity
                startActivity(intent);

                // Cerrar esta actividad
                finish();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(FormularioCartillaProtocolos.this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    private void configurarSpinnersPintura() {
        // Crear adaptadores para los spinners de pintura
        adapterPinturaBase = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(coloresPintura));
        adapterPinturaEsquema = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(coloresPintura));

        adapterPinturaBase.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPinturaEsquema.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPinturaBase.setAdapter(adapterPinturaBase);
        spinnerPinturaEsquema.setAdapter(adapterPinturaEsquema);

        // Agregar un elemento vacío al inicio
        adapterPinturaBase.insert("Seleccione color", 0);
        adapterPinturaEsquema.insert("Seleccione color", 0);

        spinnerPinturaBase.setSelection(0);
        spinnerPinturaEsquema.setSelection(0);
    }

    private void actualizarOpcionesSpinnersBuzos() {
        // Lista de nombres disponibles para cada spinner
        List<String> disponiblesBuzo1 = new ArrayList<>(nombresCompletos);
        List<String> disponiblesBuzo2 = new ArrayList<>(nombresCompletos);

        // Remover la selección del otro spinner
        if (selectedBuzo2 != null) {
            disponiblesBuzo1.remove(selectedBuzo2);
        }
        if (selectedBuzo1 != null) {
            disponiblesBuzo2.remove(selectedBuzo1);
        }

        // Actualizar spinner 1
        adapterBuzo1.clear();
        adapterBuzo1.add("Seleccione un buzo");
        adapterBuzo1.addAll(disponiblesBuzo1);
        if (selectedBuzo1 != null && disponiblesBuzo1.contains(selectedBuzo1)) {
            spinnerBuzo1.setSelection(adapterBuzo1.getPosition(selectedBuzo1));
        } else {
            spinnerBuzo1.setSelection(0);
            selectedBuzo1 = null;
        }

        // Actualizar spinner 2
        adapterBuzo2.clear();
        adapterBuzo2.add("Seleccione un buzo");
        adapterBuzo2.addAll(disponiblesBuzo2);
        if (selectedBuzo2 != null && disponiblesBuzo2.contains(selectedBuzo2)) {
            spinnerBuzo2.setSelection(adapterBuzo2.getPosition(selectedBuzo2));
        } else {
            spinnerBuzo2.setSelection(0);
            selectedBuzo2 = null;
        }
    }

    private boolean validarCampos() {
        // Validar campos numéricos primero
        if (!validarCamposNumericos()) return false;

        // Validar otros campos
        EditText inputBuzoSupervisor = findViewById(R.id.input_buzo_supervisor);
        EditText inputHidrodinamicaSupervisor = findViewById(R.id.input_hidrodinamica_supervisor);

        if (!validarCampoObligatorio(inputBuzoSupervisor, "Buzo supervisor")) return false;
        if (!validarCampoObligatorio(inputHidrodinamicaSupervisor, "Supervisor hidrodinámica")) return false;

        if (!validarFecha(inputFechaRegistro, "Fecha de registro")) return false;
        if (!validarFecha(inputFechaSupervision, "Fecha de supervisión")) return false;

        if (!validarSpinner(spinnerBuzo1, "Buzo aplicador 1")) return false;
        if (!validarSpinner(spinnerBuzo2, "Buzo aplicador 2")) return false;
        if (!validarSpinner(spinnerPinturaBase, "Pintura base")) return false;
        if (!validarSpinner(spinnerPinturaEsquema, "Pintura esquema")) return false;

        if (!validarEsquemaPilote()) return false;

        return true;
    }

    // 🔥 VALIDAR CAMPO DE TEXTO OBLIGATORIO
    private boolean validarCampoObligatorio(EditText campo, String nombreCampo) {
        if (campo.getText().toString().trim().isEmpty()) {
            campo.setError("Este campo es obligatorio");
            campo.requestFocus();
            Toast.makeText(this, "Por favor complete: " + nombreCampo, Toast.LENGTH_SHORT).show();
            return false;
        }
        campo.setError(null); // Limpiar error si es válido
        return true;
    }

    // 🔥 VALIDAR FECHA
    private boolean validarFecha(EditText campoFecha, String nombreCampo) {
        if (campoFecha.getText().toString().trim().isEmpty()) {
            campoFecha.setError("Este campo es obligatorio");
            campoFecha.requestFocus();
            Toast.makeText(this, "Por complete la fecha: " + nombreCampo, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar formato de fecha (opcional)
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateFormat.setLenient(false); // No permitir fechas inválidas como 31/02/2023
            dateFormat.parse(campoFecha.getText().toString());
        } catch (ParseException e) {
            campoFecha.setError("Formato de fecha inválido (dd/MM/yyyy)");
            campoFecha.requestFocus();
            Toast.makeText(this, "Formato de fecha inválido en: " + nombreCampo, Toast.LENGTH_SHORT).show();
            return false;
        }

        campoFecha.setError(null);
        return true;
    }

    // 🔥 VALIDAR SPINNER (no puede estar en la primera posición)
    private boolean validarSpinner(Spinner spinner, String nombreCampo) {
        if (spinner.getSelectedItemPosition() == 0) {
            // Mostrar error visual en el spinner
            TextView errorText = (TextView) spinner.getSelectedView();
            if (errorText != null) {
                errorText.setError("Selección requerida");
            }
            spinner.requestFocus();
            Toast.makeText(this, "Por favor seleccione: " + nombreCampo, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Limpiar error si es válido
        TextView selectedView = (TextView) spinner.getSelectedView();
        if (selectedView != null) {
            selectedView.setError(null);
        }
        return true;
    }

    // 🔥 VALIDAR ESQUEMA DE PILOTE (RadioGroup)
    private boolean validarEsquemaPilote() {
        if (getSelectedEsquema() == 0) {
            // Mostrar error en los RadioButtons
            radPiloteA.setError("Selección requerida");
            radPiloteB.setError("Selección requerida");
            radPiloteD.setError("Selección requerida");
            radioGroupEsquema.requestFocus();
            Toast.makeText(this, "Por favor seleccione un esquema de pilote", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Limpiar errores si es válido
        radPiloteA.setError(null);
        radPiloteB.setError(null);
        radPiloteD.setError(null);
        return true;
    }

    // 🔥 VALIDAR NÚMEROS (para campos numéricos)
    private boolean validarNumero(EditText campo, String nombreCampo) {
        if (!validarCampoObligatorio(campo, nombreCampo)) {
            return false;
        }

        try {
            String texto = campo.getText().toString();
            // Validar que sea un número positivo
            if (texto.startsWith("-")) {
                campo.setError("Debe ser un número positivo");
                campo.requestFocus();
                Toast.makeText(this, nombreCampo + " debe ser positivo", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Intentar parsear para verificar que es un número válido
            if (nombreCampo.toLowerCase().contains("rollos")) {
                Short.parseShort(texto);
            } else {
                Integer.parseInt(texto);
            }

        } catch (NumberFormatException e) {
            campo.setError("Número inválido");
            campo.requestFocus();
            Toast.makeText(this, nombreCampo + " debe ser un número válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        campo.setError(null);
        return true;
    }

    // 🔥 MÉTODO MEJORADO PARA VALIDAR CAMPOS NUMÉRICOS
    private boolean validarCamposNumericos() {
        EditText inputRegistroNumero = findViewById(R.id.input_registro_numero);
        EditText inputElementoNumero = findViewById(R.id.input_elemento_numero);
        EditText inputDiametro = findViewById(R.id.input_diametro);
        EditText inputRollos = findViewById(R.id.input_rollos);

        if (!validarNumero(inputRegistroNumero, "Número de registro")) return false;
        if (!validarNumero(inputElementoNumero, "Número de elemento")) return false;
        if (!validarNumero(inputDiametro, "Diámetro")) return false;
        if (!validarNumero(inputRollos, "Rollos")) return false;

        return true;
    }

    // 🔥 ACTUALIZAR LA FUNCIÓN PRINCIPAL DE VALIDACIÓN




}