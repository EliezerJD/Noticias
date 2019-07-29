package app.noticias;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    Spinner tipoUusuario;
    String tipos[] = {"Lector","Reportero"};
    EditText nombre;
    EditText email;
    EditText contraseña;
    EditText confirm;
    TextView errorRegister;
    TextView warnNameR;
    TextView warnEmailR;
    TextView warnPassR;
    TextView warnCPassR;
    ImageView warnImgNameR;
    ImageView warnImgEmailR;
    ImageView warnImgPassR;
    ImageView warnImgCPassR;
    TextView errorPass;
    TextView mailError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        nombre = findViewById(R.id.campoNombre);
        email = findViewById(R.id.campoEmail);
        contraseña = findViewById(R.id.campoContra);
        confirm = findViewById(R.id.campoConfirm);
        tipoUusuario = findViewById(R.id.tipoUsuario);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.spinneritem, R.id.txt, tipos);
        tipoUusuario.setAdapter(adapter);
        errorPass = findViewById(R.id.errorPass);
        errorRegister= findViewById(R.id.errorRegister);
        warnNameR = findViewById(R.id.warnNameR);
        warnEmailR = findViewById(R.id.warnEmailR);
        warnPassR = findViewById(R.id.warnPassR);
        warnCPassR = findViewById(R.id.warnCPassR);
        warnImgNameR = findViewById(R.id.warnImgNameR);
        warnImgEmailR = findViewById(R.id.warnImgEmailR);
        warnImgPassR = findViewById(R.id.warnImgPassR);
        warnImgCPassR = findViewById(R.id.warnImgCPassR);
        mailError = findViewById(R.id.warnEmailR2);
        warnNameR.setVisibility(View.GONE);
        warnEmailR.setVisibility(View.GONE);
        warnPassR.setVisibility(View.GONE);
        warnCPassR.setVisibility(View.GONE);
        warnImgNameR.setVisibility(View.GONE);
        warnImgEmailR.setVisibility(View.GONE);
        warnImgPassR.setVisibility(View.GONE);
        warnImgCPassR.setVisibility(View.GONE);
        errorRegister.setVisibility(View.GONE);
        errorPass.setVisibility(View.GONE);
        mailError.setVisibility(View.GONE);
    }

    public void registrar(View view) {
        if(validarCamposRegister()){
            Data data = new Data();
            data.setName(nombre.getText().toString().trim());
            data.setEmail(email.getText().toString().trim());
            data.setPassword(contraseña.getText().toString().trim());
            data.setCpassword(confirm.getText().toString().trim());
            if(tipoUusuario.getSelectedItem().toString().trim().equals("Lector")){
                data.setType("0");
            }else if(tipoUusuario.getSelectedItem().toString().trim().equals("Reportero")){
                data.setType("1");
            }
            register(data);
        }
    }

    private void register(Data d) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(nombre.getWindowToken(), 0);
        Retrofit retrofit = Connection.getClient();
        DataService dataService = retrofit.create(DataService.class);
        Call<Data> call = dataService.register(d);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if(response.message().equals("OK")){
                    errorRegister.setVisibility(View.GONE);
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Registrado correctamente", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast1.show();
                    Intent screen = new Intent(RegisterActivity.this, MainActivity.class);
                    screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(screen);
                }else{
                    errorRegister();
                }
            }
            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                errorRegister();
            }
        });
    }

    public boolean validarCamposRegister(){
        warnNameR.setVisibility(View.GONE);
        warnEmailR.setVisibility(View.GONE);
        warnPassR.setVisibility(View.GONE);
        warnCPassR.setVisibility(View.GONE);
        warnImgNameR.setVisibility(View.GONE);
        warnImgEmailR.setVisibility(View.GONE);
        warnImgPassR.setVisibility(View.GONE);
        warnImgCPassR.setVisibility(View.GONE);
        errorRegister.setVisibility(View.GONE);
        errorPass.setVisibility(View.GONE);
        mailError.setVisibility(View.GONE);
        if (nombre.getText().toString().trim().equals("")) {
            warnNameR.setVisibility(View.VISIBLE);
            warnImgNameR.setVisibility(View.VISIBLE);
            return false;
        } else if (email.getText().toString().trim().equals("")) {
            warnEmailR.setVisibility(View.VISIBLE);
            warnImgEmailR.setVisibility(View.VISIBLE);
            return false;
        } else if (contraseña.getText().toString().trim().equals("")) {
            warnPassR.setVisibility(View.VISIBLE);
            warnImgPassR.setVisibility(View.VISIBLE);
            return false;
        } else if (confirm.getText().toString().trim().equals("")) {
            warnCPassR.setVisibility(View.VISIBLE);
            warnImgCPassR.setVisibility(View.VISIBLE);
            return false;
        } else if (confirmarMail(email.getText().toString().trim()) == false) {
            warnImgEmailR.setVisibility(View.VISIBLE);
            mailError.setVisibility(View.VISIBLE);
            return false;
        } else if (!contraseña.getText().toString().trim().equals(confirm.getText().toString().trim())) {
            errorPass.setVisibility(View.VISIBLE);
            contraseña.setText("");
            confirm.setText("");
            return false;
        } else {
            errorPass.setVisibility(View.GONE);
            warnNameR.setVisibility(View.GONE);
            warnEmailR.setVisibility(View.GONE);
            warnPassR.setVisibility(View.GONE);
            warnCPassR.setVisibility(View.GONE);
            warnImgNameR.setVisibility(View.GONE);
            warnImgEmailR.setVisibility(View.GONE);
            warnImgPassR.setVisibility(View.GONE);
            warnImgCPassR.setVisibility(View.GONE);
            mailError.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean confirmarMail(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher mather = pattern.matcher(email);
        if (mather.find() == true) {
            return true;
        } else {
            return false;
        }
    }
    private void errorRegister() {
        errorRegister.setVisibility(View.VISIBLE);
    }



    public void volver(View view) {
        super.onBackPressed();
    }

}
