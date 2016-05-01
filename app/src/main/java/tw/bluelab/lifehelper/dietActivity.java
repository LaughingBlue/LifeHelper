package tw.bluelab.lifehelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class dietActivity extends Activity {

    private static String DATABASE_TABLE = "foodrecord";
    private SQLiteDatabase db;  //宣告資料庫變數
    private foodDBHelper dbHelper; //宣告資料庫幫助器變數
    Cursor cursor;

    private EditText edTxt_foodname, edTxt_calorie, edTxt_cost, edTxt_time;
    private Button btn_foodUpdate;

    String foodnameStr="";
    int calorie;
    int cost;
    String timeString=""; // 儲存日期變數

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        try {
            edTxt_foodname = (EditText)findViewById(R.id.edTxt_foodname);
            edTxt_calorie = (EditText)findViewById(R.id.edTxt_calorie);
            edTxt_cost = (EditText)findViewById(R.id.edTxt_cost);
            edTxt_time = (EditText)findViewById(R.id.edTxt_time);
            btn_foodUpdate = (Button)findViewById(R.id.btn_foodUpdate);

            dbHelper = new foodDBHelper(this); //建立資料庫輔助類別物件
            db = dbHelper.getWritableDatabase(); // 透過輔助類別物件建立一個可以讀寫的資料庫

            cursor = db.rawQuery(
                    "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                            DATABASE_TABLE + "'", null);
            if(cursor != null){
                if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
                {
                    db.execSQL("CREATE TABLE foodrecord (_id integer primary key autoincrement, "
                            + "foodname nvarchar(30) not null, calorie int not null, cost int null, "
                            + "time date not null)");
                }
            }
            displayNowTime();
        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        db.close(); // 關閉資料庫
    }

    public void displayNowTime(){
        Calendar dt = Calendar.getInstance(); //取得一個日曆物件
        int year = dt.get(Calendar.YEAR);     //取得目前日期的年份
        int monthOfYear = dt.get(Calendar.MONTH);   //取得目前日期的月份
        int dayOfMonth = dt.get(Calendar.DAY_OF_MONTH); //取得目前日期的每月的第幾天

        timeString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的消費日期字串
        edTxt_time.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日"); //將所選日期顯示在付款日文字方塊上
    }

    public void edTxt_time_Click(View view){
        edTxt_time.setText(""); //清除主顯示區
        Calendar dt = Calendar.getInstance(); //取得一個日曆物件
        DatePickerDialog dDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            // 覆寫選擇日期後的事件處理方法onDateSet()，所傳入的是所選擇的年、月、日
            // 注意:月份的索引值從0開始，因此正確的月份為(monthOfYear+1)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){//傳入使用者所選的時間資訊(年、月、日)
                timeString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的日期字串
                edTxt_time.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日"); //將所選日期顯示在標籤上
            }}, dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),dt.get(Calendar.DAY_OF_MONTH)); //以目前日期為預設日期
        dDialog.show(); //顯示日期選擇器對話方塊
    }

    public void btnAdd_Click(View view) {
        foodnameStr = edTxt_foodname.getText().toString();
        String calorieStr = edTxt_calorie.getText().toString();
        String costStr = edTxt_cost.getText().toString(); // 取得金額

        // 以下確保使用者有輸入金額、及選擇日期
        if(foodnameStr.length()==0) {
            Toast.makeText(this, "名稱空白", Toast.LENGTH_LONG).show();
        }
        else if(calorieStr.length()==0) {
            Toast.makeText(this, "熱量空白", Toast.LENGTH_LONG).show();
        }
        else if (costStr.length()==0) {
            Toast.makeText(this, "金額空白", Toast.LENGTH_LONG).show();
        }
        else if (timeString.length()==0)  {
            Toast.makeText(this, "日期空白", Toast.LENGTH_LONG).show();
        }
        else {// 若使用者有正確輸入則進行以下處理
            try {
                calorie=Integer.parseInt(calorieStr);
                cost=Integer.parseInt(costStr); // 將消費金額字串轉換成整數存入cost變數中
                String commandString="INSERT INTO " + DATABASE_TABLE + " (foodname, calorie, cost, time) VALUES " +
                        "( '" + foodnameStr + "' , '" + calorie + "', '" + cost + "', '" + timeString + "')";
                db.execSQL(commandString); //執行SQL資料插入指令

                Toast.makeText(this, "成功儲存新的紀錄!", Toast.LENGTH_LONG).show();
                // 清除所輸入的字
                edTxt_foodname.setText("");
                edTxt_calorie.setText("");
                edTxt_cost.setText("");
                edTxt_time.setText("");
                displayNowTime(); //顯示目前日期於TextView
            }
            catch(Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void btn_back_Click(View view) {
        finish();
    }
}
