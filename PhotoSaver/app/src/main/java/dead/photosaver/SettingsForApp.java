package dead.photosaver;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SettingsForApp extends AppCompatActivity {

    TextView Storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_for_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Storage=findViewById(R.id.Storage);
        String storageLocation="/Device storage/Photo Saver";
        Storage.setText(storageLocation);
        getSupportActionBar().setTitle("Storage Location");
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
