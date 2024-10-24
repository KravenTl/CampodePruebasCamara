package gt.edu.umg.campodepruebas;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

import gt.edu.umg.campodepruebas.BaseDatos.DbUbicacionesHelper;

public class MainActivity extends AppCompatActivity {

    Button btnCamara, btnUbicacion, btnCrearBase;
    ImageView imageView;
    String rutaimagen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamara= findViewById(R.id.btnCamara);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnCrearBase = findViewById(R.id.btnCrearBase);
        imageView=findViewById(R.id.imageView);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    abrircamara();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Crear la base de datos
        btnCrearBase.setOnClickListener(v -> {
            DbUbicacionesHelper dbHelper = new DbUbicacionesHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (db != null) {
                Toast.makeText(this, "Creando base de datos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        btnUbicacion.setOnClickListener(view ->  {
                Toast.makeText(this,"Abriendo ubicacion",Toast.LENGTH_SHORT).show();
                Intent intentar = new Intent(this, Ubicacion.class);
                startActivity(intentar);
        });

    }

    private void abrircamara() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagenArchivo = null;

        // La creación del archivo puede lanzar IOException, por eso va dentro del try
        imagenArchivo = crearImagen();

        if (imagenArchivo != null) {
            Uri fotoUri = FileProvider.getUriForFile(this, "com.cdp.camara.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1 && resultCode== RESULT_OK){
            //Bundle extras = data.getExtras();
            //Bitmap imgBitmap= (Bitmap) extras.get("data");
            Bitmap imgBitmap= BitmapFactory.decodeFile(rutaimagen);
            imageView.setImageBitmap(imgBitmap);
        }
    }

    protected File crearImagen() throws IOException {
        String nombreImagen = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Aquí podría lanzarse una IOException, por eso el método declara "throws IOException"
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaimagen = imagen.getAbsolutePath();
        return imagen;
    }


}