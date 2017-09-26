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
        EditText back = (EditText)findViewById(R.id.BackTxtbox);
        EditText sa = (EditText)findViewById(R.id.RequestArenaTxtbox);
        EditText stae = (EditText)findViewById(R.id.StartExploreTxtbox);
        EditText stoe = (EditText)findViewById(R.id.StopExploreTxtbox);
        EditText ssp = (EditText)findViewById(R.id.StartShortestPathTxtbox);

        up.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.forward),getString(R.string.forward)));
        left.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.turn_left),getString(R.string.turn_left)));
        right.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.turn_right),getString(R.string.turn_right)));
        back.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.back),getString(R.string.back)));
        sa.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.request_arena),getString(R.string.request_arena)));
        stae.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.start_explore),getString(R.string.start_explore)));
        stoe.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.stop_explore),getString(R.string.stop_explore)));
        ssp.setText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key), Context.MODE_PRIVATE).getString(getString(R.string.start_shortest),getString(R.string.start_shortest)));

        Button savingButton = (Button)findViewById(R.id.saveSetting);
        savingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText up = (EditText)findViewById(R.id.UpTxtbox);
                EditText left = (EditText)findViewById(R.id.LeftTxtbox);
                EditText right = (EditText)findViewById(R.id.RightTxtbox);
                EditText back = (EditText)findViewById(R.id.BackTxtbox);
                EditText sa = (EditText)findViewById(R.id.StartExploreTxtbox);
                EditText stae = (EditText)findViewById(R.id.StartExploreTxtbox);
                EditText stoe = (EditText)findViewById(R.id.StopExploreTxtbox);
                EditText ssp = (EditText)findViewById(R.id.StartShortestPathTxtbox);

                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.mdp_key), MODE_PRIVATE).edit();
                editor.putString(getString(R.string.forward), up.getText().toString());
                editor.putString(getString(R.string.turn_left), left.getText().toString());
                editor.putString(getString(R.string.turn_right), right.getText().toString());

                editor.putString(getString(R.string.request_arena), sa.getText().toString());
                editor.putString(getString(R.string.start_explore), stae.getText().toString());
                editor.putString(getString(R.string.stop_explore), stoe.getText().toString());
                editor.putString(getString(R.string.start_shortest), ssp.getText().toString());
                editor.apply();
                finish();
            }
        });

    }
}
