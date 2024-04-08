package lab.poly.lab6_and103.services;

import java.util.List;

import lab.poly.lab6_and103.models.Fruit;
import lab.poly.lab6_and103.models.Users;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    public static String BASE_URL = "http://192.168.1.75:3000/";
@GET("api/fruits")
    Call<List<Fruit>> getListFruit();
    @GET("api/search")
    Call<List<Fruit>> searchFlower(@Query("key") String key);
    @Multipart
    @POST("api/fruits")
    Call<Fruit> addFruit(@Part("name") RequestBody name, @Part("price") RequestBody price, @Part MultipartBody.Part image);

    @Multipart
    @PUT("api/fruits/{id}")
    Call<Fruit> updateFruit(@Path("id") String id, @Part("name") RequestBody name, @Part("price") RequestBody price, @Part MultipartBody.Part image);

    @DELETE("api/fruits/{id}")
    Call<Void> deleteFruit(@Path("id") String id);

    @Multipart
    @POST("api/register")
    Call<Users> register(@Part("username") RequestBody username, @Part("password") RequestBody password, @Part("email") RequestBody email, @Part("name") RequestBody name, @Part MultipartBody.Part avartar);

    @POST("api/login")
    Call<Users> login(@Body Users users);



}
