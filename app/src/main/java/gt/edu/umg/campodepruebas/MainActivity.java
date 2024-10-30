package gt.edu.umg.campodepruebas;

import gt.edu.umg.campodepruebas.BaseDatos.DbUbicacionesHelper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnCamara, btnUbicacion, btnCrearBase, btnMapa, btnGuardar, btnGaleria;
    ImageView imageView;
    String rutaImagen; // Variable para almacenar la ruta de la última imagen
    EditText txtComentario, txtLatitudSi, txtLongitudSi;
    double latitud;
    double longitud;

    private FusedLocationProviderClient proveedorUbicacion;
    private static final int CODIGO_SOLICITUD_UBICACION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamara = findViewById(R.id.btnCamara);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnCrearBase = findViewById(R.id.btnCrearBase);
        btnMapa = findViewById(R.id.btnMapa);
        btnGuardar = findViewById(R.id.btnGuardar);
        txtComentario = findViewById(R.id.txtComentario);
        txtLatitudSi = findViewById(R.id.txtLatitudSi);
        txtLongitudSi = findViewById(R.id.txtLongitudSi);
        imageView = findViewById(R.id.imageView);
        btnGaleria = findViewById(R.id.btnGaleria);

        proveedorUbicacion = LocationServices.getFusedLocationProviderClient(this);

        btnCamara.setOnClickListener(v -> {
            try {
                abrirCamara();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnCrearBase.setOnClickListener(v -> {
            DbUbicacionesHelper dbHelper = new DbUbicacionesHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (db != null) {
                Toast.makeText(this, "Creando base de datos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        btnGaleria.setOnClickListener(view -> {
            Toast.makeText(this, "Abriendo Galeria", Toast.LENGTH_SHORT).show();
            Intent intentar = new Intent(this, Galeria.class);
            startActivity(intentar);
        });

        btnMapa.setOnClickListener(view -> {
            Toast.makeText(this, "Abriendo Mapa", Toast.LENGTH_SHORT).show();
            Intent intentar = new Intent(this, Ubicacion.class);
            startActivity(intentar);
        });

        btnUbicacion.setOnClickListener(v -> obtenerUbicacionActual());

        btnGuardar.setOnClickListener(v -> {
            if (!txtLatitudSi.getText().toString().isEmpty() && !txtLongitudSi.getText().toString().isEmpty()) {
                guardarUbicacion();
            } else {
                Toast.makeText(this, "Primero obtén la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                txtLatitudSi.setText(String.valueOf(latitud));
                txtLongitudSi.setText(String.valueOf(longitud));
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarUbicacion() {
        DbUbicacionesHelper dbUbicacion = new DbUbicacionesHelper(this);
        String comentario = txtComentario.getText().toString();
        String latitudStr = txtLatitudSi.getText().toString();
        String longitudStr = txtLongitudSi.getText().toString();
        double latitudVal = Double.parseDouble(latitudStr);
        double longitudVal = Double.parseDouble(longitudStr);
        String fechaActual = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        if (rutaImagen != null) {
            long resultado = dbUbicacion.insertarUbicacion(longitudVal, latitudVal, comentario, fechaActual, rutaImagen);
            if (resultado != -1) {
                Toast.makeText(this, "Información guardada con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar la información", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se ha tomado ninguna foto", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void abrirCamara() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagenArchivo = crearImagen();

        if (imagenArchivo != null) {
            Uri fotoUri = FileProvider.getUriForFile(this, "com.cdp.camara.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Ajusta la orientación de la imagen antes de mostrarla en el ImageView
            Bitmap imagenAjustada = ajustarOrientacion(rutaImagen);
            imageView.setImageBitmap(imagenAjustada);
        }
    }

    protected File crearImagen() throws IOException {
        String nombreImagen = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaImagen = imagen.getAbsolutePath(); // Almacena la ruta de la imagen
        return imagen;
    }

    // Metodo para ajustar la orientación de la imagen según los datos EXIF
    public Bitmap ajustarOrientacion(String rutaImagen) {
        Bitmap bitmap = BitmapFactory.decodeFile(rutaImagen);
        try {
            ExifInterface exif = new ExifInterface(rutaImagen);
            int orientacion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientacion) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }

            // Crear un nuevo Bitmap con la orientación corregida
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
