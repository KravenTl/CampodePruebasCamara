package gt.edu.umg.campodepruebas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gt.edu.umg.campodepruebas.Adaptadores.ListaFotosAdapter;
import gt.edu.umg.campodepruebas.BaseDatos.DbUbicacionesHelper;
import gt.edu.umg.campodepruebas.Entidades.FotosUbi;

public class Galeria extends AppCompatActivity {

    private ListaFotosAdapter adapter;
    private DbUbicacionesHelper dbUbicacionesHelper;
    private RecyclerView recyclerViewFotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        // Inicialización del RecyclerView y el helper de base de datos
        recyclerViewFotos = findViewById(R.id.ListadeFotos);
        recyclerViewFotos.setLayoutManager(new LinearLayoutManager(this));
        dbUbicacionesHelper = new DbUbicacionesHelper(this);

        // Cargar las fotos de la base de datos
        loadFotos();
    }

    private void loadFotos() {
        ArrayList<FotosUbi> listaFotos = new ArrayList<>();
        Cursor cursor = dbUbicacionesHelper.getAllFotos();

        if (cursor.moveToFirst()) {
            do {
                FotosUbi fotosUbi = new FotosUbi();
                fotosUbi.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                fotosUbi.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                fotosUbi.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                fotosUbi.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                fotosUbi.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                fotosUbi.setFoto(cursor.getString(cursor.getColumnIndexOrThrow("foto"))); // Ruta de la imagen
                listaFotos.add(fotosUbi);
            } while (cursor.moveToNext());
        } else {
            // Mostrar un mensaje si no se encontraron datos
            Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

        // Configurar el adaptador con los datos obtenidos y asignarlo al RecyclerView
        adapter = new ListaFotosAdapter(listaFotos, this);
        recyclerViewFotos.setAdapter(adapter);
    }
}