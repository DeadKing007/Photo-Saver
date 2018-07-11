package dead.photosaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {

    EditText Name;
    Button Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Save As");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Name=findViewById(R.id.Name);
        Save=findViewById(R.id.Save);


        Save.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.Save){
            String name=Name.getText().toString();
            if (name.isEmpty()){

                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            }else {

                Intent intent=new Intent();
                intent.putExtra("Name",name);
                setResult(RESULT_OK,intent);
                finish();
            }

        }
    }
}
