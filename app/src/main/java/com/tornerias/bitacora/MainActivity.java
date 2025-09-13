package com.tornerias.bitacora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tornerias.bitacora.db.BitacoraDatabase;
import com.tornerias.bitacora.db.CartillaProtocolosEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView excel_files;
    ArrayList<String> filenames = new ArrayList<>();
    ArrayAdapter<String> adapter;

    // Nuevas variables para los registros de la base de datos
    private ListView lstRegistros;
    private ArrayAdapter<String> registrosAdapter;
    private ArrayList<String> registrosDisplay = new ArrayList<>();
    private List<CartillaProtocolosEntity> registrosCompletos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Cambiar el layout para usar un ListView para registros y otro para archivos
        // Necesitarás modificar tu activity_main.xml para tener ambos ListViews
        excel_files = findViewById(R.id.lst_excel_files);
        lstRegistros = findViewById(R.id.lst_registros);

        loadFilesFromAssets();
        cargarRegistrosDesdeBD();

        // Adaptador para archivos Excel
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filenames);
        excel_files.setAdapter(adapter);

        // Adaptador para registros de la base de datos
        registrosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registrosDisplay);
        lstRegistros.setAdapter(registrosAdapter);

        // Listener para archivos Excel (tu código existente)
        excel_files.setOnItemClickListener((parent, view, position, id) -> {
            String filename = filenames.get(position);

            Intent intent = null;

            if (filename.toLowerCase().contains("cartilla") &&
                    filename.toLowerCase().contains("protocolos") &&
                    filename.toLowerCase().contains("pilote")) {

                intent = new Intent(MainActivity.this, FormularioCartillaProtocolos.class);
            }

            if (intent != null) {
                intent.putExtra("filename", getOriginalFilename(filename));
                startActivity(intent);
            } else {
                Log.w("MainActivity", "No hay formulario asignado para este archivo: " + filename);
            }
        });

        // Listener para registros de la base de datos
        lstRegistros.setOnItemClickListener((parent, view, position, id) -> {
            if (position < registrosCompletos.size()) {
                CartillaProtocolosEntity registroSeleccionado = registrosCompletos.get(position);

                // Abrir el formulario con los datos del registro seleccionado
                Intent intent = new Intent(MainActivity.this, SelectedCartilla.class);
                intent.putExtra("registro_id", registroSeleccionado.idRegistro.toString());
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar registros cuando la actividad se reanude
        cargarRegistrosDesdeBD();
    }

    private void cargarRegistrosDesdeBD() {
        new Thread(() -> {
            try {
                BitacoraDatabase db = BitacoraDatabase.getDatabase(getApplicationContext());
                List<CartillaProtocolosEntity> registros = db.cartillaProtocolosDao().getAll();

                runOnUiThread(() -> {
                    // Limpiar listas ANTES de agregar nuevos elementos
                    registrosCompletos.clear();
                    registrosDisplay.clear();

                    // Formatear cada registro para mostrar
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                    for (CartillaProtocolosEntity registro : registros) {
                        registrosCompletos.add(registro);

                        String fechaStr = registro.fechaRegistro != null ?
                                dateFormat.format(registro.fechaRegistro) : "Sin fecha";

                        String elementoStr = registro.elementoNumero != null ?
                                String.valueOf(registro.elementoNumero) : "N/A";

                        String displayText = fechaStr + "_Cartilla_Pilote_" + elementoStr;
                        registrosDisplay.add(displayText);
                    }

                    registrosAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("MainActivity", "Error al cargar registros", e);
                runOnUiThread(() -> {
                    registrosDisplay.clear();
                    registrosDisplay.add("Error al cargar registros");
                    registrosAdapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private String getOriginalFilename(String cleanName) {
        String original = cleanName.replace(" ", "_") + ".xlsx";
        return original;
    }

    private void loadFilesFromAssets() {
        filenames.clear();
        try {
            String[] files = getAssets().list("");
            if (files != null) {
                for (String name : files) {
                    if (name.endsWith(".xlsx")) {
                        String cleanName = name.replace("_", " ").replace("#", "").replace(".xlsx","");
                        filenames.add(cleanName);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("Bitacora", "Error leyendo assets", e);
        }
    }
}