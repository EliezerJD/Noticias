package app.noticias;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connection {
    private static Retrofit retrofit = null;
    public static final String API_URL= "http://192.168.43.81:8000/api/";
    public static final String IP= "http://192.168.43.81";

    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
