package mdp.cz3004.ntu.com.mdpapp_group25.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import mdp.cz3004.ntu.com.mdpapp_group25.R;

public class StringCommandsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_string_commands);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = height/2;
        params.width = width/2;
        params.y = -10;
        this.getWindow().setAttributes(params);

        EditText up = (EditText)findViewById(R.id.UpTxtbox);
        EditText left = (EditText)findViewById(R.id.LeftTxtbox);
        EditText right = (EditText)findViewById(R.id.RightTxtbox);
        EditText sa = (EditText)findViewById(R.id.SendArenaTxtbox);

        up.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.forward),"FC"));
        left.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.turn_left),"TLC"));
        right.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.turn_right),"TRC"));
        sa.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.send_arena),"SA"));

        Button savingButton = (Button)findViewById(R.id.saveSetting);
        savingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText up = (EditText)findViewById(R.id.UpTxtbox);
                EditText left = (EditText)findViewById(R.id.LeftTxtbox);
                EditText right = (EditText)findViewById(R.id.RightTxtbox);
                EditText sa = (EditText)findViewById(R.id.SendArenaTxtbox);

                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.mdp_key), MODE_PRIVATE).edit();
                editor.putString(getString(R.string.forward), up.getText().toString());
                editor.putString(getString(R.string.turn_left), left.getText().toString());
                editor.putString(getString(R.string.turn_right), right.getText().toString());
                editor.putString(getString(R.string.send_arena), sa.getText().toString());
                editor.apply();
                finish();
            }
        });

    }
}
