package gt.edu.umg.campodepruebas.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gt.edu.umg.campodepruebas.Entidades.FotosUbi;
import gt.edu.umg.campodepruebas.R;

public class ListaFotosAdapter extends RecyclerView.Adapter<ListaFotosAdapter.FotoViewHolder> {

    private ArrayList<FotosUbi> listaFotos;
    private Context context;

    public ListaFotosAdapter(ArrayList<FotosUbi> listaFotos, Context context) {
        this.listaFotos = listaFotos;
        this.context = context;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_fotos, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        FotosUbi fotoUbi = listaFotos.get(position);

        holder.viewId.setText(String.valueOf(fotoUbi.getId()));
        holder.viewLongitud.setText(String.valueOf(fotoUbi.getLongitud()));
        holder.viewLatitud.setText(String.valueOf(fotoUbi.getLatitud()));
        holder.viewDescripcion.setText(fotoUbi.getDescripcion());
        holder.viewFecha.setText(fotoUbi.getFecha());

        // Cargar imagen usando Glide desde la ruta de archivo
        Glide.with(context)
                .load(fotoUbi.getFoto())
                .centerCrop()
                .into(holder.imageView);

        // Configurar clic largo para mostrar opciones de actualización/eliminación
        holder.itemView.setOnLongClickListener(v -> {
            showOptionsDialog(context, fotoUbi);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    // Mostrar diálogo de opciones
    private void showOptionsDialog(Context context, FotosUbi fotoUbi) {
        String[] options = {"Cambiar Estado"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opciones de Foto")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showStatusUpdateDialog(context, fotoUbi);
                    }
                })
                .show();
    }

    // Método para mostrar diálogo de actualización de estado
    private void showStatusUpdateDialog(Context context, FotosUbi fotoUbi) {
        String[] estados = {"EN PROCESO", "RESUELTO", "CANCELADO"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Actualizar Estado")
                .setItems(estados, (dialog, which) -> {
                    String nuevoEstado = estados[which];
                    Toast.makeText(context, "Estado cambiado a: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                })
                .show();
    }

    public class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView viewId, viewLatitud,viewLongitud,viewDescripcion, viewFecha;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.viewFoto);
            viewId = itemView.findViewById(R.id.ViewId);
            viewLatitud = itemView.findViewById(R.id.ViewLatitud);
            viewLongitud = itemView.findViewById(R.id.ViewLongitud);
            viewDescripcion = itemView.findViewById(R.id.ViewDescripcion);
            viewFecha = itemView.findViewById(R.id.ViewFecha);
        }
    }
}