package app.noticias;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReporteroActivity extends AppCompatActivity {
    ImageButton imgBtnUser;
    FloatingActionButton btnAdd;
    TabLayout tab;
    TextView encabezado;
    TextView descripcion;
    ListView simpleList;
    ListView simpleListNew;
    TextView userName;
    String sessionId;
    String usernameT;
    Data noticia;
    Data noticiapropia;
    ArrayList<Data> noticias = new ArrayList<>();
    ArrayList<Data> noticiasPropias = new ArrayList<>();
    ArrayList<String> listaNoticias = new ArrayList<>();
    ArrayList<String> listaNoticiasPropias = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapterNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportero);
        init();
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                simpleList.setVisibility(View.VISIBLE);
                simpleListNew.setVisibility(View.GONE);
                if (tab.getPosition() == 0) {
                    encabezado.setText("Noticias");
                    descripcion.setText("Aquí puedes ver las últimas noticias.");
                    listar();
                } else if (tab.getPosition() == 1) {
                    simpleListNew.setVisibility(View.VISIBLE);
                    simpleList.setVisibility(View.GONE);
                    encabezado.setText("Tus noticias");
                    descripcion.setText("Aquí puedes ver, editar y eliminar tus noticias.");
                    listarNew();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void init(){
        imgBtnUser = findViewById(R.id.imgBtnUser);
        btnAdd = findViewById(R.id.btnAdd);
        tab = findViewById(R.id.tab);
        simpleList = findViewById(R.id.listView);
        simpleListNew = findViewById(R.id.listViewNew);
        encabezado = findViewById(R.id.encabezado);
        descripcion = findViewById(R.id.descripcion);
        userName = findViewById(R.id.userName);
        sessionId = getIntent().getStringExtra("id");
        usernameT = getIntent().getStringExtra("name");
        userName.setText(usernameT);
        simpleListNew.setVisibility(View.GONE);
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
                listarPropias();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    private void listarPropias(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(userName.getWindowToken(), 0);
        Retrofit retrofit = Connection.getClient();
        DataService dataService = retrofit.create(DataService.class);
        Call<Data> call = dataService.getNoticiasById(Integer.parseInt(sessionId));
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                for(int x=0; x<response.body().getData().size();x++){
                    noticiapropia = new Data();
                    noticiapropia.setId(response.body().getData().get(x).getId());
                    noticiapropia.setTitle(response.body().getData().get(x).getTitle());
                    noticiapropia.setDescription(response.body().getData().get(x).getDescription());
                    noticiapropia.setDate(response.body().getData().get(x).getDate());
                    noticiapropia.setImage(response.body().getData().get(x).getImage());
                    noticiapropia.setIdUser(response.body().getData().get(x).getIdUser());
                    noticiasPropias.add(noticiapropia);
                    listaNoticiasPropias.add(response.body().getData().get(x).getTitle() + "\n" + "\n" + response.body().getData().get(x).getDate());
                }
                Collections.reverse(listaNoticiasPropias);
                Collections.reverse(noticiasPropias);
                listar();

            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                System.out.println(t.getMessage());

            }
        });
    }

    public void listar(){
        arrayAdapter= new ArrayAdapter<String>(ReporteroActivity.this, R.layout.activity_listview, R.id.textView, listaNoticias);
        simpleList.setAdapter(arrayAdapter);
    }

    public void listarNew(){
        arrayAdapterNew= new ArrayAdapter<String>(ReporteroActivity.this, R.layout.activity_listview_new, R.id.textView, listaNoticiasPropias);
        simpleListNew.setAdapter(arrayAdapterNew);
    }



    public void logOut(View view) {
        PopupMenu popup = new PopupMenu(ReporteroActivity.this, imgBtnUser);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void outSession(MenuItem item) {
        Intent screen = new Intent(ReporteroActivity.this, MainActivity.class);
        screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(screen);
    }

    public void ver(View view) {
        View item = (View) view.getParent();
        int pos = simpleList.getPositionForView(item);
        System.out.println(pos+"");
        Intent screen = new Intent(ReporteroActivity.this, VerActivity.class);
        screen.putExtra("name", usernameT);
        screen.putExtra("title", noticias.get(pos).getTitle());
        screen.putExtra("date", noticias.get(pos).getDate());
        screen.putExtra("image", noticias.get(pos).getImage());
        screen.putExtra("description", noticias.get(pos).getDescription());
        startActivity(screen);
    }

    public void verNew(View view) {
        View item = (View) view.getParent();
        int pos = simpleListNew.getPositionForView(item);
        Intent screen = new Intent(ReporteroActivity.this, VerActivity.class);
        screen.putExtra("name", usernameT);
        screen.putExtra("title", noticiasPropias.get(pos).getTitle());
        screen.putExtra("date", noticiasPropias.get(pos).getDate());
        screen.putExtra("image", noticiasPropias.get(pos).getImage());
        screen.putExtra("description", noticiasPropias.get(pos).getDescription());
        startActivity(screen);
    }
    public void editar(View view) {
        View item = (View) view.getParent();
        int pos = simpleListNew.getPositionForView(item);
        Intent screen = new Intent(ReporteroActivity.this, EditActivity.class);
        screen.putExtra("name", usernameT);
        screen.putExtra("id", sessionId);
        screen.putExtra("title", noticiasPropias.get(pos).getTitle());
        screen.putExtra("date", noticiasPropias.get(pos).getDate());
        screen.putExtra("image", noticiasPropias.get(pos).getImage());
        screen.putExtra("description", noticiasPropias.get(pos).getDescription());
        screen.putExtra("idNoticia", noticiasPropias.get(pos).getId());
        startActivity(screen);

    }

    public void borrar(View view) {
        View item = (View) view.getParent();
        final int pos = simpleListNew.getPositionForView(item);
        Retrofit retrofit = Connection.getClient();
        DataService dataService = retrofit.create(DataService.class);
        Call<Data> call = dataService.eliminar(Integer.parseInt(noticiasPropias.get(pos).getId()));
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Toast toast1 = Toast.makeText(getApplicationContext(), "Eliminado correctamente", Toast.LENGTH_SHORT);
                toast1.show();
                String index = noticiasPropias.get(pos).getId();
                noticiasPropias.remove(pos);
                listaNoticiasPropias.remove(pos);
                eliminardeTodos(index);
            }
            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }


    public void agregar(View view) {
        Intent screen = new Intent(ReporteroActivity.this, AddActivity.class);
        screen.putExtra("name", usernameT);
        screen.putExtra("id", sessionId);
        startActivity(screen);
    }
    private void eliminardeTodos(String id){
        for(int x=0; x<listaNoticias.size();x++){
            if(noticias.get(x).getId().equals(id)){
                noticias.remove(x);
                listaNoticias.remove(x);
                break;
            }
        }
        arrayAdapter.notifyDataSetChanged();
        arrayAdapterNew.notifyDataSetChanged();
    }
}
