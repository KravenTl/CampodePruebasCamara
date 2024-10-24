package gt.edu.umg.campodepruebas;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import gt.edu.umg.campodepruebas.BaseDatos.DbUbicacionesHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class Ubicacion extends AppCompatActivity {

    private FusedLocationProviderClient proveedorUbicacion;
    private TextView tvUbicacion;
    private Button btnObtenerUbicacion;
    private static final int CODIGO_SOLICITUD_UBICACION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        tvUbicacion = findViewById(R.id.tvUbicacion);
        proveedorUbicacion = LocationServices.getFusedLocationProviderClient(this);
        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbiacion);
        btnObtenerUbicacion.setOnClickListener(v -> obtenerUbicacionActual()); // Obtener ubicación al presionar el botón
    }

    private void obtenerUbicacionActual(){
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_SOLICITUD_UBICACION
            );
            return;
        }

        proveedorUbicacion.getLastLocation().addOnSuccessListener(this, location -> {
            if(location != null){
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                tvUbicacion.setText(
                        "Latitud: " + latitud + "\n" +
                                "Longitud: " + longitud
                );
                // Crear una instancia de DbUbicacion
                DbUbicacionesHelper dbUbicacion = new DbUbicacionesHelper(this);

                // Insertar la ubicación en la base de datos
                long resultado = dbUbicacion.insertarUbicacion(longitud, latitud);
                if (resultado != -1) {
                    Toast.makeText(this, "Ubicación guardada con éxito", Toast.LENGTH_SHORT).show();
                    // Aquí podrías realizar alguna otra acción, como limpiar campos o actualizar la UI
                } else {
                    Toast.makeText(this, "Error al guardar la ubicación", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(requestCode == CODIGO_SOLICITUD_UBICACION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                obtenerUbicacionActual();
            } else {
                tvUbicacion.setText("Permiso de ubicación denegado");
            }
        }
    }
}
