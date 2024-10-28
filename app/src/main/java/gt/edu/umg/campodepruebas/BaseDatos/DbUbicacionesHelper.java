package gt.edu.umg.campodepruebas.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbUbicacionesHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3; // Actualizar la versión de la base de datos
    private static final String DB_NOMBRE = "dbUbicacion.db";
    public static final String TABLE_UBICACIONES = "ubicaciones";

    // Constructor
    public DbUbicacionesHelper(@Nullable Context context) {
        super(context, DB_NOMBRE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_UBICACIONES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "longitud REAL, "
                + "latitud REAL, "
                + "descripcion TEXT, "  // Campo de descripción
                + "fecha TEXT" +       // Nuevo campo para la fecha
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UBICACIONES);
        onCreate(db);
    }

    // Método para insertar una ubicación en la base de datos
    public long insertarUbicacion(double longitud, double latitud, String descripcion, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("longitud", longitud);
        values.put("latitud", latitud);
        values.put("descripcion", descripcion); // Inserta el nuevo campo
        values.put("fecha", fecha);             // Inserta la fecha

        // Insertar fila
        long resultado = db.insert(TABLE_UBICACIONES, null, values);
        db.close(); // Cerrar conexión a la base de datos
        return resultado;
    }
}
