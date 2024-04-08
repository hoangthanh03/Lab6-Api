package lab.poly.lab6_and103;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lab.poly.lab6_and103.adapter.FruitAdapter;
import lab.poly.lab6_and103.models.Fruit;
import lab.poly.lab6_and103.models.Users;
import lab.poly.lab6_and103.services.APIService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity implements IClick {
    File file;
    RecyclerView rccFruit;
    FruitAdapter fruitAdapter;
    List<Fruit> listFruit;
    FloatingActionButton btnAdd;
    EditText edtSearch;
    ImageView imgFruit_add;
    ImageView imgFruit_ud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rccFruit = findViewById(R.id.rccFruit);
        edtSearch = findViewById(R.id.edtSearch);
        btnAdd = findViewById(R.id.btnAdd);
        ImageView avatar = findViewById(R.id.avartar);
        TextView tvHello = findViewById(R.id.tvHl);
        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        String url = sharedPreferences.getString("avartar", "");
        Glide.with(this).load(url).into(avatar);
        tvHello.setText("Hello, " + sharedPreferences.getString("username", ""));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rccFruit.setLayoutManager(layoutManager);
        listFruit = new ArrayList<>();
        fruitAdapter = new FruitAdapter(this, listFruit, this);
        rccFruit.setAdapter(fruitAdapter);

        getListFruit();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAdd();
            }
        });

    }

    private void getListFruit() {
        Gson gson = new GsonBuilder().setLenient().create();

        //khởi tạo retrofit Clinet
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        //tạo interface
        APIService apiService = retrofit.create(APIService.class);
        //tạo đối tượng Call
        Call<List<Fruit>> objCall = apiService.getListFruit();
        objCall.enqueue(new Callback<List<Fruit>>() {
            @Override
            public void onResponse(Call<List<Fruit>> call, Response<List<Fruit>> response) {
                if (response.isSuccessful()) {
                    listFruit.clear();
                    listFruit.addAll(response.body());
                    fruitAdapter.notifyDataSetChanged();
                    Log.e("Home", "Success");
                }
            }

            @Override
            public void onFailure(Call<List<Fruit>> call, Throwable t) {

                Log.e("Home", t.getMessage());
            }
        });
    }

    private void showDialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtPrice = view.findViewById(R.id.edtPrice);
        imgFruit_add = view.findViewById(R.id.imgFruit_add);
        Button btnAdd = view.findViewById(R.id.btnAdd_add);

        imgFruit_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageAdd();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), edtName.getText().toString());
                RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), edtPrice.getText().toString());
                MultipartBody.Part multipartBody = null;
                if (file != null) {
                    RequestBody requesrFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multipartBody = MultipartBody.Part.createFormData("image", file.getName(), requesrFile);
                    //"avatar" là cùng tên với key trong mutipart
                } else {
                    multipartBody = null;
                }
                Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                APIService apiService = retrofit.create(APIService.class);
                Call<Fruit> call = apiService.addFruit(name, price, multipartBody);
                call.enqueue(new Callback<Fruit>() {
                    @Override
                    public void onResponse(Call<Fruit> call, Response<Fruit> response) {
                        if (response.isSuccessful()) {
                            Log.d("Add", "Add success");
                            getListFruit();
                            dialog.dismiss();
                            file = null;
                        }
                    }

                    @Override
                    public void onFailure(Call<Fruit> call, Throwable t) {
                        Log.d("Add", "Add fail");
                    }
                });
            }
        });


        dialog.show();


    }

    //Hàm chọn hình
    private void chooseImageAdd() {
        if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getImageAdd.launch(intent);

        } else {
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
    //Hàm chọn hình
    private void chooseImageUpdate() {
        if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getImageUpdate.launch(intent);

        } else {
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    //hàm kết quả sau khi lấy hình
    ActivityResultLauncher<Intent> getImageAdd = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Uri path = o.getData().getData();
                        file = createFileFormUri(path, "image");
                        //Glide để load hình
                        Glide.with(Home.this).load(file).thumbnail(Glide.with(Home.this).load(R.drawable.ing)).centerCrop()//ceter cắt ảnh
                                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgFruit_add);
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> getImageUpdate = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Uri path = o.getData().getData();
                        file = createFileFormUri(path, "image");
                        //Glide để load hình
                        Glide.with(Home.this).load(file).thumbnail(Glide.with(Home.this).load(R.drawable.ing)).centerCrop()//ceter cắt ảnh
                                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgFruit_ud);

                    }
                }
            }
    );

    //hàm tạo file hình từ uri
    private File createFileFormUri(Uri path, String name) {
        File _file = new File(Home.this.getCacheDir(), name + ".png");
        try {
            InputStream in = Home.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onEditClick(int position) {
        Fruit flower = listFruit.get(position);
        if (position != RecyclerView.NO_POSITION) {
            // Gọi phương thức xóa của Adapter khi người dùng chọn xóa
            showUpdateDialog(flower);
            Toast.makeText(Home.this, "Cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        //comfirm Delete
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Xóa");
        builder.setMessage("Bạn muốn xóa");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (position != RecyclerView.NO_POSITION) {
                    // Gọi phương thức xóa của Adapter khi người dùng chọn xóa
                    deleteFlower(position);
                    Toast.makeText(Home.this, "Xóa", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }


    public void deleteFlower(int position) {
        //khởi tạo retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Delete Fruit", "Deleting fruit at position: " + position);
        //tạo interface
        APIService apiService = retrofit.create(APIService.class);
        // Gọi phương thức Retrofit để thực hiện yêu cầu DELETE tới máy chủ
        Fruit fruitToDelete = listFruit.get(position);
        Call<Void> call = apiService.deleteFruit(fruitToDelete.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa hoa khỏi danh sách và cập nhật RecyclerView
                    listFruit.remove(position);
                    fruitAdapter.notifyDataSetChanged();
                    Toast.makeText(Home.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý khi nhận được phản hồi lỗi từ máy chủ
                    Log.e("Delete", "Failed to delete: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Xử lý khi gặp lỗi trong quá trình gửi yêu cầu
                Log.e("Delete", "Error deleting: " + t.getMessage());
            }
        });
    }

    // Phương thức để hiển thị dialog cập nhật
    private void showUpdateDialog(Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

        // Inflate layout cho dialog
        LayoutInflater Inflater = LayoutInflater.from(this);
        View view = Inflater.from(this).inflate(R.layout.dialog_update, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Khởi tạo EditText và gán giá trị hiện tại của sản phẩm vào
        EditText editTextName = view.findViewById(R.id.edtName_up);
        EditText editTextPrice = view.findViewById(R.id.edtPrice_up);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnCancel = view.findViewById(R.id.btnCancel_up);
        imgFruit_ud = view.findViewById(R.id.imgFruit_ud);


        editTextName.setText(fruit.getName());
        editTextPrice.setText("" + fruit.getPrice());
        Glide.with(this).load(fruit.getImage()).into(imgFruit_ud);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        imgFruit_ud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageUpdate();
            }
        });
        // Xử lý sự kiện khi nhấn nút cập nhật trong dialog
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), editTextName.getText().toString());
                RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), editTextPrice.getText().toString());
                MultipartBody.Part multipartBody = null;
                if (file != null) {
                    RequestBody requesrFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multipartBody = MultipartBody.Part.createFormData("image", file.getName(), requesrFile);
                    //"image" là cùng tên với key trong mutipart
                } else {
                    multipartBody = null;
                }

                // Tạo đối tượng được cập nhật
                Fruit updateFlower = new Fruit();
                updateFlower.setName(editTextName.getText().toString());
                updateFlower.setPrice(Integer.parseInt(editTextPrice.getText().toString()));

                Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                APIService apiService = retrofit.create(APIService.class);
                Call<Fruit> call = apiService.updateFruit(fruit
                        .get_id(), name, price, multipartBody);

                call.enqueue(new Callback<Fruit>() {
                    @Override
                    public void onResponse(Call<Fruit> call, Response<Fruit> response) {
                        if (response.isSuccessful()) {
                            // Cập nhật thành công
                            Call<List<Fruit>> call1 = apiService.getListFruit();
                            call1.enqueue(new Callback<List<Fruit>>() {
                                @Override
                                public void onResponse(Call<List<Fruit>> call1, Response<List<Fruit>> response) {
                                    if (response.isSuccessful()) {
                                        List<Fruit> fetchedFlowers = response.body();
                                        if (fetchedFlowers != null) {
                                            listFruit.clear(); // Xóa dữ liệu cũ
                                            listFruit.addAll(fetchedFlowers); // Thêm dữ liệu mới
                                            fruitAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                            dialog.dismiss();
                                            file = null;
                                        }
                                    } else {
                                        Log.e("Home", "Failed to get flower: " + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Fruit>> call, Throwable t) {
                                    Log.e("Home", t.getMessage());
                                }
                            });

                        } else {
                            Toast.makeText(Home.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Fruit> call, Throwable t) {
                        // Xử lý khi gữp lỗi trong quá trình gửi yêu cầu
                        Log.e("Update flower", "Error updating flower: " + t.getMessage());
                    }
                });
            }
        });
        // Hiển thị dialog
        dialog.show();
    }
}