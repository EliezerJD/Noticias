package app.noticias;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LectorActivity extends AppCompatActivity {
    ImageButton imgBtnUser;
    TextView userName;
    String sessionId;
    String usernameT;
    ListView simpleList;
    ArrayAdapter<String> arrayAdapter;
    Data noticia;
    ArrayList<Data> noticias = new ArrayList<>();
    ArrayList<String> listaNoticias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);
        init();
    }

    private void init(){
        imgBtnUser = findViewById(R.id.imgBtnUser);
        simpleList = findViewById(R.id.listView);
        userName = findViewById(R.id.userName);
        sessionId = getIntent().getStringExtra("id");
        usernameT = getIntent().getStringExtra("name");
        userName.setText(usernameT);
        listarInicio();
    }

    private void listarInicio(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(userName.getWindowToken(), 0);
        Retrofit retrofit = Connection.getClient();
        DataService dataService = retrofit.create(DataService.class);
        Call<Data> call = dataService.getNoticias();
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                for(int x=0; x<response.body().getData().size();x++){
                    noticia = new Data();
                    noticia.setId(response.body().getData().get(x).getId());
                    noticia.setTitle(response.body().getData().get(x).getTitle());
                    noticia.setDescription(response.body().getData().get(x).getDescription());
                    noticia.setDate(response.body().getData().get(x).getDate());
                    noticia.setImage(response.body().getData().get(x).getImage());
                    noticia.setIdUser(response.body().getData().get(x).getIdUser());
                    noticias.add(noticia);
                    listaNoticias.add(response.body().getData().get(x).getTitle() + "\n" + "\n" + response.body().getData().get(x).getDate());
                }
                listar();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }
    public void listar(){
        arrayAdapter= new ArrayAdapter<String>(LectorActivity.this, R.layout.activity_listview, R.id.textView, listaNoticias);
        simpleList.setAdapter(arrayAdapter);
    }

    public void ver(View view){
        View item = (View) view.getParent();
        int pos = simpleList.getPositionForView(item);
        Intent screen = new Intent(LectorActivity.this, VerActivity.class);
        screen.putExtra("name", usernameT);
        screen.putExtra("title", noticias.get(pos).getTitle());
        screen.putExtra("date", noticias.get(pos).getDate());
        screen.putExtra("image", noticias.get(pos).getImage());
        screen.putExtra("description", noticias.get(pos).getDescription());
        startActivity(screen);
    }

    public void logOut(View view) {
        PopupMenu popup = new PopupMenu(LectorActivity.this, imgBtnUser);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void outSession(MenuItem item) {
        Intent screen = new Intent(LectorActivity.this, MainActivity.class);
        screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(screen);
    }
}
