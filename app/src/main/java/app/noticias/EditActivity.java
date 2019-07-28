package app.noticias;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditActivity extends AppCompatActivity {
    ImageButton imgBtnUser;
    String sessionId;
    String idNoticia;
    String usernameT;
    TextView userName;
    EditText title;
    TextView txtDate;
    EditText description;
    String image;
    Data actualizar;
    Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        init();
    }

    private void init(){
        imgBtnUser = findViewById(R.id.imgBtnUser);
        userName = findViewById(R.id.userName);
        sessionId = getIntent().getStringExtra("id");
        usernameT = getIntent().getStringExtra("name");
        userName.setText(usernameT);
        idNoticia = getIntent().getStringExtra("idNoticia");
        title = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        description = findViewById(R.id.txtDescription);
        title.setText(getIntent().getStringExtra("title"));
        txtDate.setText(getIntent().getStringExtra("date"));
        description.setText(getIntent().getStringExtra("description"));

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditActivity.this, date, calendario
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
        String formatoDeFecha = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.US);
        txtDate.setText(sdf.format(calendario.getTime()));
    }

    public void confirmar(View view) {
        actualizar = new Data();
        actualizar.setTitle(title.getText().toString().trim());
        actualizar.setDate(txtDate.getText().toString().trim());
        actualizar.setDescription(description.getText().toString().trim());
        //actualizar.setImage();
        Retrofit retrofit = Connection.getClient();
        DataService dataService = retrofit.create(DataService.class);
        Call<Data> call = dataService.update(Integer.parseInt(idNoticia), actualizar);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if(response.message().equals("OK")){
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Noticia editada", Toast.LENGTH_SHORT);
                    toast1.show();
                    Intent screen = new Intent(EditActivity.this, ReporteroActivity.class);
                    screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    screen.putExtra("name", usernameT);
                    screen.putExtra("id", sessionId);
                    startActivity(screen);
                }
            }
            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast toast1 = Toast.makeText(getApplicationContext(), "Error al editar", Toast.LENGTH_SHORT);
                toast1.show();
            }
        });


    }

    public void logOut(View view) {
        PopupMenu popup = new PopupMenu(EditActivity.this, imgBtnUser);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void outSession(MenuItem item) {
        Intent screen = new Intent(EditActivity.this, MainActivity.class);
        screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(screen);
    }

    public void volver(View view) {
        super.onBackPressed();
    }
}
