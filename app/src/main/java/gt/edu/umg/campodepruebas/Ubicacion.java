package gt.edu.umg.campodepruebas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback {

    EditText txtLatitud, txtLongitud;
    Button btnBuscar, btnRegresar;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnRegresar = findViewById(R.id.btnRegresar);

        // Inicializar el mapa
        SupportMapFragment fragmentoMapa = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragmentoMapa != null) {
            fragmentoMapa.getMapAsync(this);
        }

        // Configurar la acción del botón buscar
        btnBuscar.setOnClickListener(v -> buscarUbicacion());

        // Configurar la acción del botón regresar
        btnRegresar.setOnClickListener(v -> finish());  // Cierra la actividad actual
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Coordenadas de la ciudad de Guatemala
        LatLng guatemala = new LatLng(14.6349, -90.5069);

        // Mover la cámara a la ciudad de Guatemala
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guatemala, 12));
    }

    // Metodo para buscar y centrar el mapa en las coordenadas proporcionadas
    private void buscarUbicacion() {
        String latitudTexto = txtLatitud.getText().toString();
        String longitudTexto = txtLongitud.getText().toString();

        if (!latitudTexto.isEmpty() && !longitudTexto.isEmpty()) {
            try {
                double latitud = Double.parseDouble(latitudTexto);
                double longitud = Double.parseDouble(longitudTexto);

                // Crear LatLng con las coordenadas y mover el mapa hacia esa posición
                LatLng ubicacion = new LatLng(latitud, longitud);
                mMap.clear();  // Limpiar otros marcadores
                mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación seleccionada"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Coordenadas no válidas", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese ambas coordenadas", Toast.LENGTH_SHORT).show();
        }
    }
}
