package lab.poly.lab6_and103.adapter;


import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import lab.poly.lab6_and103.Home;
import lab.poly.lab6_and103.IClick;
import lab.poly.lab6_and103.R;
import lab.poly.lab6_and103.models.Fruit;
import lab.poly.lab6_and103.services.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private Context context;
    private List<Fruit> listFruit;
    private IClick iClick;

    public FruitAdapter(Context context, List<Fruit> listFlower, IClick iClick) {
        this.context = context;
        this.listFruit = listFlower;
        this.iClick = iClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_fruit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText("Tên: " + listFruit.get(position).getName());
        holder.tvPrice.setText("Giá: " + listFruit.get(position).getPrice());
        Glide.with(context).load(listFruit.get(position).getImage()).into(holder.ivImage);
        holder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                iClick.onDeleteClick(position);
            }

        });
        holder.ivUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                iClick.onEditClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFruit.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivDel, ivUpd, ivImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivDel = itemView.findViewById(R.id.btn_delete);
            ivUpd = itemView.findViewById(R.id.btn_edit);
            ivImage = itemView.findViewById(R.id.img);
        }
    }
}

