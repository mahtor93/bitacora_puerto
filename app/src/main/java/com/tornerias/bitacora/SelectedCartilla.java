package com.tornerias.bitacora;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.tornerias.bitacora.db.BitacoraDatabase;
import com.tornerias.bitacora.db.CartillaProtocolosEntity;
import com.tornerias.bitacora.excel.ExcelHandlerCartilla;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SelectedCartilla extends AppCompatActivity {

    private TextView txtIdRegistro, txtRegistroNumero, txtElementoNumero, txtFechaRegistro;
    private TextView txtPinturaEsquema, txtDiametro, txtEsquemaPilote, txtPinturaBase;
    private TextView txtRollos, txtBuzoAplicador1, txtBuzoAplicador2;
    private TextView txtBuzoSupervisor, txtHidrodinamicaSupervisor, txtFechaSupervision;

    private static final int STORAGE_PERMISSION_CODE = 1001;
    private CartillaProtocolosEntity registroActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selected_cartilla);
        Button btnExportar = findViewById(R.id.btn_exportar_excel);
        btnExportar.setOnClickListener(v -> {
            if (registroActual != null) {
                // Generar nombre del archivo: fecha + nombre plantilla + elemento
                String nombreArchivo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date()) + "_Cartilla_Protocolos" + registroActual.elementoNumero + ".xlsx";

                // Llamar al helper que hace todo el trabajo con Excel
// Llamar al helper que hace todo el trabajo con Excel
                ExcelHandlerCartilla.rellenarPlantilla(
                        this,
                        "Cartilla_de_Protocolos_Pilote_#.xlsx", // plantilla en assets
                        nombreArchivo,
                        registroActual.registroNumero,
                        registroActual.elementoNumero,
                        registroActual.fechaSupervision,
                        registroActual.pinturaEsquema,
                        registroActual.diametro,
                        registroActual.esquemaPilote,
                        registroActual.rollos,
                        registroActual.buzoAplicador1,
                        registroActual.buzoAplicador2,
                        registroActual.buzoSupervisor,
                        registroActual.hidrodinamicaSupervisor,
                        registroActual.fechaSupervision
                        // aquí puedes agregar todos los demás campos según tu mapeo de celdas
                );


                Toast.makeText(this, "Archivo exportado a Documentos/bitacora", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay registro seleccionado", Toast.LENGTH_SHORT).show();
            }
        });
        // Inicializar vistas
        inicializarVistas();

        // Obtener el ID del registro desde el Intent
        String registroId = getIntent().getStringExtra("registro_id");
        if (registroId != null) {
            cargarRegistro(UUID.fromString(registroId));
        } else {
            Toast.makeText(this, "Error: No se proporcionó ID de registro", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void inicializarVistas() {
        txtRegistroNumero = findViewById(R.id.txt_registro_numero);
        txtElementoNumero = findViewById(R.id.txt_elemento_numero);
        txtFechaRegistro = findViewById(R.id.txt_fecha_registro);
        txtPinturaEsquema = findViewById(R.id.txt_pintura_esquema);
        txtDiametro = findViewById(R.id.txt_diametro);
        txtEsquemaPilote = findViewById(R.id.txt_esquema_pilote);
        txtPinturaBase = findViewById(R.id.txt_pintura_base);
        txtRollos = findViewById(R.id.txt_rollos);
        txtBuzoAplicador1 = findViewById(R.id.txt_buzo_aplicador1);
        txtBuzoAplicador2 = findViewById(R.id.txt_buzo_aplicador2);
        txtBuzoSupervisor = findViewById(R.id.txt_buzo_supervisor);
        txtHidrodinamicaSupervisor = findViewById(R.id.txt_hidrodinamica_supervisor);
        txtFechaSupervision = findViewById(R.id.txt_fecha_supervision);
    }

    private void cargarRegistro(UUID registroId) {
        new Thread(() -> {
            try {
                BitacoraDatabase db = BitacoraDatabase.getDatabase(getApplicationContext());
                CartillaProtocolosEntity registro = db.cartillaProtocolosDao().getById(registroId);

                if (registro != null) {
                    runOnUiThread(() -> {
                        mostrarRegistro(registro);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SelectedCartilla.this, "Registro no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(SelectedCartilla.this, "Error al cargar el registro", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void mostrarRegistro(CartillaProtocolosEntity registro) {
        this.registroActual = registro;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Mostrar todos los datos
        txtRegistroNumero.setText(registro.registroNumero != null ? registro.registroNumero.toString() : "N/A");
        txtElementoNumero.setText(registro.elementoNumero != null ? registro.elementoNumero.toString() : "N/A");
        txtFechaRegistro.setText(registro.fechaRegistro != null ? dateFormat.format(registro.fechaRegistro) : "N/A");
        txtPinturaEsquema.setText(registro.pinturaEsquema != null ? registro.pinturaEsquema : "N/A");
        txtDiametro.setText(registro.diametro != null ? registro.diametro.toString() : "N/A");
        txtEsquemaPilote.setText(String.valueOf(registro.esquemaPilote));
        txtPinturaBase.setText(registro.pinturaBase != null ? registro.pinturaBase : "N/A");
        txtRollos.setText(String.valueOf(registro.rollos));
        txtBuzoAplicador1.setText(registro.buzoAplicador1 != null ? registro.buzoAplicador1 : "N/A");
        txtBuzoAplicador2.setText(registro.buzoAplicador2 != null ? registro.buzoAplicador2 : "N/A");
        txtBuzoSupervisor.setText(registro.buzoSupervisor != null ? registro.buzoSupervisor : "N/A");
        txtHidrodinamicaSupervisor.setText(registro.hidrodinamicaSupervisor != null ? registro.hidrodinamicaSupervisor : "N/A");
        txtFechaSupervision.setText(registro.fechaSupervision != null ? dateFormat.format(registro.fechaSupervision) : "N/A");
    }
}