package gt.edu.umg.campodepruebas.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import gt.edu.umg.campodepruebas.Entidades.GaleriaDb;

public class DbUbicacionesHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4; // Versión de la base de datos
    private static final String DB_NOMBRE = "dbUbicacion.db"; // Nombre de la base de datos
    public static final String TABLE_UBICACIONES = "ubicaciones"; // Nombre de la tabla

    public DbUbicacionesHelper(@Nullable Context context) {
        super(context, DB_NOMBRE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla ubicaciones
        db.execSQL("CREATE TABLE " + TABLE_UBICACIONES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "longitud REAL, " +
                "latitud REAL, " +
                "descripcion TEXT, " +
                "fecha TEXT, " +
                "foto BLOB" + // Columna para almacenar la imagen en formato BLOB
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Actualizar la base de datos
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UBICACIONES);
        onCreate(db);
    }

    // Método para insertar una ubicación
    public long insertarUbicacion(double longitud, double latitud, String descripcion, String fecha, Bitmap imagen) {
        SQLiteDatabase db = this.getWritableDatabase(); // Abrir la base de datos en modo escritura
        ContentValues values = new ContentValues(); // Crear un objeto ContentValues para almacenar los datos
        values.put("longitud", longitud); // Agregar la longitud
        values.put("latitud", latitud); // Agregar la latitud
        values.put("descripcion", descripcion); // Agregar la descripción
        values.put("fecha", fecha); // Agregar la fecha

        // Convertir la imagen a un array de bytes
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        values.put("foto", byteArray); // Agregar la imagen en formato BLOB

        long id = db.insert(TABLE_UBICACIONES, null, values); // Insertar los datos en la tabla
        db.close(); // Cerrar la base de datos
        return id; // Retornar el ID de la fila insertada
    }
}