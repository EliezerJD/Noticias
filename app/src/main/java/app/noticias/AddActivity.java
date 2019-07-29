package app.noticias;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddActivity extends AppCompatActivity {
    ImageButton imgBtnUser;
    String sessionId;
    String usernameT;
    TextView userName;
    EditText title;
    EditText txtDate;
    EditText description;
    String url;
    File file;
    Data agregar;
    Calendar calendario = Calendar.getInstance();
    ImageView img;
    String urlServer = "http://192.168.1.65/apiFotos/upload.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
    }

    private void init(){
        img = findViewById(R.id.img);
        img.setVisibility(View.GONE);
        imgBtnUser = findViewById(R.id.imgBtnUser);
        userName = findViewById(R.id.userName);
        sessionId = getIntent().getStringExtra("id");
        usernameT = getIntent().getStringExtra("name");
        userName.setText(usernameT);
        title = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        description = findViewById(R.id.txtDescription);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarInput();
        }
    };

    private void actualizarInput() {
        String formatoDeFecha = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.US);
        txtDate.setText(sdf.format(calendario.getTime()));
    }

    public void agregar(View view) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(urlServer);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody foto = new FileBody(file, "image/jpeg");
        mpEntity.addPart("fotoUp", foto);
        httppost.setEntity(mpEntity);
        HttpResponse response = httpclient.execute(httppost);
        String responseBody = EntityUtils.toString(response.getEntity());
        if(responseBody.equals("success")){
            httpclient.getConnectionManager().shutdown();
            addNoticia();
        }


    }

    public void volver(View view) {
        super.onBackPressed();
    }

    public void logOut(View view) {
        PopupMenu popup = new PopupMenu(AddActivity.this, imgBtnUser);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void outSession(MenuItem item) {
        Intent screen = new Intent(AddActivity.this, MainActivity.class);
        screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(screen);
    }

    public void takePhoto(View view) {
        url = UUID.randomUUID().toString()+".jpg";
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intento1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getExternalFilesDir(null), url);
        intento1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intento1, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Bitmap bitmap1 = BitmapFactory.decodeFile(getExternalFilesDir(null)+"/"+url);
            img.setImageBitmap(bitmap1);
            img.setVisibility(View.VISIBLE);
        }
    }

    private void addNoticia(){
        agregar = new Data();
        agregar.setTitle(title.getText().toString().trim());
        agregar.setDate(txtDate.getText().toString().trim());
        System.out.println(txtDate.getText().toString().trim());
        agregar.setDescription(description.getText().toString().trim());
        agregar.setImage("/imagenes/"+ url);
        agregar.setIdUser(sessionId);
        Retrofit retrofit = Connection.getClient();
        DataService dataService = retrofit.create(DataService.class);
        Call<Data> call = dataService.add(agregar);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Toast toast1 = Toast.makeText(getApplicationContext(), "Noticia agregada", Toast.LENGTH_SHORT);
                toast1.show();
                Intent screen = new Intent(AddActivity.this, ReporteroActivity.class);
                screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                screen.putExtra("name", usernameT);
                screen.putExtra("id", sessionId);
                startActivity(screen);
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast toast1 = Toast.makeText(getApplicationContext(), "Error al agregar la noticia", Toast.LENGTH_SHORT);
                toast1.show();
                Intent screen = new Intent(AddActivity.this, ReporteroActivity.class);
                screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                screen.putExtra("name", usernameT);
                screen.putExtra("id", sessionId);
                startActivity(screen);
            }
        });

    }
}
