package com.tornerias.bitacora;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class selectedFile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selected_file);

        // primero seteamos el layout, recién ahí podemos buscar el TextView
        TextView txt_selected_file = findViewById(R.id.txt_selected_file);

        // recuperamos el filename del intent
        String filename = getIntent().getStringExtra("filename");

        if (filename != null && !filename.isEmpty()) {
            txt_selected_file.setText(filename);
        } else {
            txt_selected_file.setText("Ninguno");
        }

        // ajustes de padding por las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
