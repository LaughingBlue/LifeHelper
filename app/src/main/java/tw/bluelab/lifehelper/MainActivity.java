package tw.bluelab.lifehelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button btn_newRecord, btn_view, btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_newRecord=(Button)findViewById(R.id.btn_newRecord);
        btn_view=(Button)findViewById(R.id.btn_view);
        btn_exit=(Button)findViewById(R.id.btn_exit);
    }

    //進入新增飲食紀錄
    public void btn_newRecord_Click(View view){
        try {
            Intent it = new Intent(this, dietActivity.class);
            startActivity(it);
        }
        catch(Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //進入新增修改紀錄
    public void btn_view_Click(View view){
        try {
            Intent it = new Intent(this, viewDBActivity.class);
            startActivity(it);
        }
        catch(Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btn_exit_Click(View view) {
        finish();
    }
}
