package lab.poly.lab6_and103;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import lab.poly.lab6_and103.models.Users;
import lab.poly.lab6_and103.services.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextInputEditText edUsername = findViewById(R.id.ed_username);
        TextInputEditText edPassword = findViewById(R.id.ed_password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Users user = new Users();
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                user.setUsername(username);
                user.setPassword(password);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                APIService apiService = retrofit.create(APIService.class);
                Call<Users> call = apiService.login(user);
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        if (response.isSuccessful()) {
                            Users users = response.body();
                            Log.d("Login", "User: " + users.getEmail());
                            Log.d("Login", "Login success");
                            SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", users.getUsername());
                            editor.putString("avartar", users.getAvartar());
                            editor.commit();
                            startActivity(new Intent(Login.this, Home.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        Log.d("Login", "Login fail");
                    }
                });
            }
        });

    }
}