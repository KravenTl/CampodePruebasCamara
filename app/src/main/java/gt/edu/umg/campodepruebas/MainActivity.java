package gt.edu.umg.campodepruebas;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;

import gt.edu.umg.campodepruebas.BaseDatos.DbUbicacionesHelper;

public class MainActivity extends AppCompatActivity {

    Button btnCamara, btnUbicacion, btnCrearBase, btnMapa, btnGuardar, btnGaleria;
    ImageView imageView;
    String rutaimagen;
    EditText txtComentario, txtLatitudSi, txtLongitudSi; // Cambiar el nombre de la variable para los TextFields
    double latitud;
    double longitud;

    // Para la ubicación
    private FusedLocationProviderClient proveedorUbicacion;
    private static final int CODIGO_SOLICITUD_UBICACION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamara= findViewById(R.id.btnCamara);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnCrearBase = findViewById(R.id.btnCrearBase);
        btnMapa = findViewById(R.id.btnMapa);
        btnGuardar = findViewById(R.id.btnGuardar);
        txtComentario = findViewById(R.id.txtComentario);
        txtLatitudSi = findViewById(R.id.txtLatitudSi);
        txtLongitudSi = findViewById(R.id.txtLongitudSi);
        imageView=findViewById(R.id.imageView);
        btnGaleria = findViewById(R.id.btnGaleria);

        proveedorUbicacion = LocationServices.getFusedLocationProviderClient(this);

        btnCamara.setOnClickListener(v -> {
            try {
                abrircamara();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //Aca se crea la mera database

        btnCrearBase.setOnClickListener(v -> {
            DbUbicacionesHelper dbHelper = new DbUbicacionesHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (db != null) {
                Toast.makeText(this, "Creando base de datos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        //Con este boton se abre el galeria activity

        btnGaleria.setOnClickListener(view -> {
            Toast.makeText(this, "Abriendo Galeria", Toast.LENGTH_SHORT).show();
            Intent intentar = new Intent(this, Galeria.class);
            startActivity(intentar);
        });

        //Con este boton se abre el mapa activity

        btnMapa.setOnClickListener(view -> {
            Toast.makeText(this, "Abriendo Mapa", Toast.LENGTH_SHORT).show();
            Intent intentar = new Intent(this, Ubicacion.class);
            startActivity(intentar);
        });


        //El boton llama la metodo ubicar

        btnUbicacion.setOnClickListener(v -> obtenerUbicacionActual());

        //El boton llama la metodo guardar

        btnGuardar.setOnClickListener(v -> {
            if (!txtLatitudSi.getText().toString().isEmpty() && !txtLongitudSi.getText().toString().isEmpty()) {
                guardarUbicacion();
            } else {
                Toast.makeText(this, "Primero obtén la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Aqui se obtiene la ubicacion y los permisos

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_SOLICITUD_UBICACION);
            return;
        }

        proveedorUbicacion.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                txtLatitudSi.setText(String.valueOf(latitud)); // Establecer latitud en EditText
                txtLongitudSi.setText(String.valueOf(longitud)); // Establecer longitud en EditText
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Este metodo guarda lo obtenido

    private void guardarUbicacion() {
        DbUbicacionesHelper dbUbicacion = new DbUbicacionesHelper(this);
        String comentario = txtComentario.getText().toString();
        String latitudStr = txtLatitudSi.getText().toString();
        String longitudStr = txtLongitudSi.getText().toString();

        // Convertir las cadenas a valores numéricos
        double latitudVal = Double.parseDouble(latitudStr);
        double longitudVal = Double.parseDouble(longitudStr);

        // Obtener la fecha actual
        String fechaActual = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        long resultado = dbUbicacion.insertarUbicacion(longitudVal, latitudVal, comentario, fechaActual);

        if (resultado != -1) {
            Toast.makeText(this, "Información guardada con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar la información", Toast.LENGTH_SHORT).show();
        }
    }

    //Aqui se validan los permisos

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_SOLICITUD_UBICACION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrircamara() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagenArchivo = crearImagen();

        if (imagenArchivo != null) {
            Uri fotoUri = FileProvider.getUriForFile(this, "com.cdp.camara.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaimagen);
            imageView.setImageBitmap(imgBitmap);
        }
    }

    protected File crearImagen() throws IOException {
        String nombreImagen = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaimagen = imagen.getAbsolutePath();
        return imagen;
    }
}
