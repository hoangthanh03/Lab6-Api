package lab.poly.lab6_and103;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
import retrofit2.http.Multipart;

public class Register extends AppCompatActivity {
    File file;
    ImageView avatar;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextInputEditText edUsername = findViewById(R.id.ed_username);
        TextInputEditText edName = findViewById(R.id.ed_name);
        TextInputEditText edEmail = findViewById(R.id.ed_email);
        TextInputEditText edPassword = findViewById(R.id.ed_password);
        btnRegister = findViewById(R.id.btn_register_register);
        avatar = findViewById(R.id.avatar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), edUsername.getText().toString().trim());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), edPassword.getText().toString());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), edEmail.getText().toString());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), edName.getText().toString());
                MultipartBody.Part multipartBody = null;
                if (file != null) {
                    RequestBody requesrFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multipartBody = MultipartBody.Part.createFormData("avartar", file.getName(), requesrFile);
                    //"avatar" là cùng tên với key trong mutipart
                } else {
                    multipartBody = null;
                }
                Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                APIService apiService = retrofit.create(APIService.class);
                Call<Users> call = apiService.register(_username, _password, _email, _name, multipartBody);
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        if (response.isSuccessful()) {
                            Log.d("Register", "Register success");
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        Log.d("Register", "Register fail");
                    }
                });
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Register", "Choose image");
                chooseImage();
            }
        });

    }

    //Hàm chọn hình
    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(Register.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getImage.launch(intent);

        } else {
            ActivityCompat.requestPermissions(Register.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    //hàm kết quả sau khi lấy hình
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Uri path = o.getData().getData();
                        ;
                        file = createFileFormUri(path, "avartar");
                        //Glide để load hình
                        Glide.with(Register.this).load(file).thumbnail(Glide.with(Register.this).load(R.drawable.ing)).centerCrop()//ceter cắt ảnh
//                                .circleCrop()//bo tròn hình
                                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(avatar);
                    }
                }
            }
    );

    //hàm tạo file hình từ uri
    private File createFileFormUri(Uri path, String name) {
        File _file = new File(Register.this.getCacheDir(), name + ".png");
        try {
            InputStream in = Register.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Log.d("123123", "createFileFormUri: " + _file);
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}