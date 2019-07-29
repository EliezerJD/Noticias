package app.noticias;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DataService {

    @POST("login")
    Call<Data> login(@Body Data data);

    @GET("noticias")
    Call<Data> getNoticias();

    @GET("noticias/{idUser}")
    Call<Data> getNoticiasById(@Path("idUser") int idUser);

    @PUT("noticias/update/{id}")
    Call<Data> update(@Path("id")int id, @Body Data data);

    @PUT("noticias/del/{id}")
    Call<Data> eliminar(@Path("id") int id);

    @POST("noticias")
    Call<Data> add(@Body Data data);








}
