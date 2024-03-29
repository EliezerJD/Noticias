package app.noticias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class VerActivity extends AppCompatActivity {
    ImageButton imgBtnUser;
    TextView userName;
    TextView title;
    TextView date;
    TextView description;
    TextView image;
    String imageUrl;
    ImageView img;


    String usernameT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);
        title = findViewById(R.id.txtTitulo);
        img = findViewById(R.id.img);
        date = findViewById(R.id.txtFecha);
        description = findViewById(R.id.txtDescripcion);
        userName = findViewById(R.id.userName);
        imgBtnUser = findViewById(R.id.imgBtnUser);
        userName = findViewById(R.id.userName);
        usernameT = getIntent().getStringExtra("name");
        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        imageUrl = getIntent().getStringExtra("image");
        description.setText(getIntent().getStringExtra("description"));
        userName.setText(usernameT);
        System.out.println(imageUrl);
        //Picasso.with(VerActivity.this).load("http://192.168.1.65/apiFotos"+imageUrl).into(img);
        Picasso.with(VerActivity.this).load(Connection.IP+"/apiFotos"+imageUrl).into(img);
    }

    public void volver(View view){
        super.onBackPressed();
    }

    public void logOut(View view){
        PopupMenu popup = new PopupMenu(VerActivity.this, imgBtnUser);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void outSession(MenuItem item) {
        Intent screen = new Intent(VerActivity.this, MainActivity.class);
        screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(screen);

    }
}
