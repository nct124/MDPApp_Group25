package mdp.cz3004.ntu.com.mdpapp_group25.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import mdp.cz3004.ntu.com.mdpapp_group25.R;
import mdp.cz3004.ntu.com.mdpapp_group25.other.Constants;

public class FinalMDFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_final_mdf);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = height/2;
        params.width = width;
        params.y = -10;
        this.getWindow().setAttributes(params);

        Intent i = getIntent();
        TextView fmdf1 = (TextView)findViewById(R.id.FMDF1);
        TextView fmdf2 = (TextView)findViewById(R.id.FMDF2);
        fmdf1.setText(i.getStringExtra(Constants.MDF1tag));
        fmdf2.setText(i.getStringExtra(Constants.MDF2tag));
    }
}
