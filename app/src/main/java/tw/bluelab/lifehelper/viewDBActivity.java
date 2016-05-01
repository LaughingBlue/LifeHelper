package tw.bluelab.lifehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class viewDBActivity extends Activity {

    private static String DATABASE_TABLE = "foodrecord";
    private SQLiteDatabase db; //宣告資料庫變數
    private foodDBHelper dbHelper; //宣告資料庫幫助器變數

    Button btn_StartTime, btn_qEndTime;
    TextView txtV_qStartTime, txtV_qEndTime;
    Button btn_delete, btn_query;
    TextView txtV_result;
    Button btn_back;

    String StartTimeString="", EndTimeString=""; //分別為紀錄起始日期及結束日期之字串變數
    String str ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_db);

        try {
            btn_StartTime = (Button) findViewById(R.id.btn_StartTime);
            btn_qEndTime = (Button) findViewById(R.id.btn_qEndTime);
            txtV_qStartTime = (TextView) findViewById(R.id.txtV_qStartTime);
            txtV_qEndTime = (TextView) findViewById(R.id.txtV_qEndTime);
            btn_delete = (Button) findViewById(R.id.btn_delete);
            btn_query = (Button) findViewById(R.id.btn_query);
            txtV_result = (TextView) findViewById(R.id.txtV_result);
            btn_back = (Button) findViewById(R.id.btn_back);

            dbHelper = new foodDBHelper(this); //建立資料庫輔助類別物件
            db = dbHelper.getWritableDatabase(); // 透過輔助類別物件建立一個可以讀寫的資料庫

            // 清除主顯示區域
            txtV_result.setText("");

            // 將目前日期設定為預設選擇日期，並顯示在付款日文字方塊上
            displayTime();
        }
        catch(Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void displayTime()
    {
        Calendar dt = Calendar.getInstance();
        int year = dt.get(Calendar.YEAR);
        int monthOfYear = dt.get(Calendar.MONTH);
        int dayOfMonth = dt.get(Calendar.DAY_OF_MONTH);

        StartTimeString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
        txtV_qStartTime.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日");
        EndTimeString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
        txtV_qEndTime.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日");
    }

    protected void onStop() {
        super.onStop();
        db.close();
    }

    public void btnChoseStartTime_Click(View v)
    {
        try {
            txtV_result.setText(""); //清除主顯示區
            Calendar dt = Calendar.getInstance(); //取得一個日曆物件
            DatePickerDialog dDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    StartTimeString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的起始日期字串
                    txtV_qStartTime.setText(year+ "年" + (monthOfYear+1) +"月"+ dayOfMonth +"日"); //將所選日期顯示在標籤上
                }}, dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),dt.get(Calendar.DAY_OF_MONTH)); //以目前日期為預設日期
            dDialog.show(); //顯示日期選擇器對話方塊
        }
        catch(Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 選擇結束日期
    public void btnChoseEndTime_Click(View v)
    {
        try {
            txtV_result.setText(""); //清除主顯示區
            Calendar dt = Calendar.getInstance(); //取得一個日曆物件
            DatePickerDialog dDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth)
                {
                    EndTimeString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的結束日期字串
                    txtV_qEndTime.setText(year+ "年" + (monthOfYear+1) +"月"+ dayOfMonth +"日"); //將所選日期顯示在標籤上
                }}, dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),dt.get(Calendar.DAY_OF_MONTH)); //以目前日期為預設日期
            dDialog.show(); //顯示日期選擇器對話方塊
        }
        catch(Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 查詢紀錄按鈕事件處理方法
    public void btnQuery_Click(View v)
    {
        txtV_result.setText("");
        // 確保使用者有選擇起始日期及結束日期
        if(StartTimeString.length()==0) {
            Toast.makeText(this, "沒有選擇起始日期!", Toast.LENGTH_SHORT).show();
        }
        else if(EndTimeString.length()==0) {
            Toast.makeText(this, "沒有選擇結束日期!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            try  {
                    String commandString = "SELECT * FROM " + DATABASE_TABLE + " WHERE time between '" +
                            StartTimeString + "' and '" + EndTimeString +"' ORDER BY time" ;
                    Cursor cursor = db.rawQuery(commandString, null); //執行資料表查詢命令
                    if(cursor != null)
                    {
                        int n = cursor.getCount(); //取得資料筆數
                        String str = "在" + StartTimeString + "到" + EndTimeString + "共有"+ n + "筆紀錄:\n";
                        int totalAmount = 0; // 用於紀錄消費總金額
                        int totalCalorie = 0; // 用於紀錄消費總熱量

                        cursor.moveToFirst();  // 將指標移到第1筆紀錄
                        for (int i = 0; i < n; i++) { // 利用迴圈逐一讀取每一筆紀錄
                            totalAmount += Integer.parseInt(cursor.getString(3)); //讀取第3個欄位(即金額)，並加到總金額中
                            cursor.moveToNext();  // 移動到下一筆
                        }
                        str += "飲食花費共計 " + totalAmount +" 元\n"; // 將消費總金額串接到顯示字串(str)中

                        cursor.moveToFirst();
                        for (int i = 0; i < n; i++) { // 利用迴圈逐一讀取每一筆紀錄
                            totalCalorie += Integer.parseInt(cursor.getString(2)); //讀取第3個欄位(即熱量)，並加到總熱量中
                            cursor.moveToNext();  // 移動到下一筆
                        }
                        str += "飲食熱量共計 " + totalCalorie +" \n"; // 將總熱量串接到顯示字串(str)中

                        // 顯示消費紀錄之每一個欄位之抬頭
                        String[] colNames = {"編號", "品項", "熱量", "價格", "記入日期"};

                        for (int i = 0; i < colNames.length; i++)
                            str += String.format("%5s\t", colNames[i]); // 將每一個欄位的抬頭串接到顯示字串(str)中

                        str += "\n";
                        cursor.moveToFirst();  // 將指標移到第1筆紀錄

                        // 顯示欄位值
                        for (int i = 0; i < n; i++) {
                            str += String.format("%6s\t", (i+1));
                            str += String.format("%8s\t", cursor.getString(1));
                            str += String.format("%6s\t", cursor.getString(2));
                            str += String.format("%8s\t", cursor.getString(3));
                            str += String.format("%14s\t", cursor.getString(4));
                            str += "\n";
                            cursor.moveToNext();
                        }
                        cursor.close();
                        txtV_result.setText(str); //將顯示字串的內容顯示在主顯示區文字盒上
                    }
                    else { // 沒有回傳紀錄
                        txtV_result.setText("在" + StartTimeString + "到" + EndTimeString + "並沒有紀錄!\n");
                    }
            }
            catch(Exception ex)  {
                Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btnDelete_Click(View v)
    {
        txtV_result.setText("");

        // 以下確保使用者有選擇起始日期及結束日期
        if(StartTimeString.length()==0) {// 沒有選擇起始日期
            Toast.makeText(this, "沒有選擇起始日期!", Toast.LENGTH_SHORT).show();
        }
        else if(EndTimeString.length()==0) {  // 沒有選擇結束日期
            Toast.makeText(this, "沒有選擇結束日期!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            try  {
                // 以下為建立警示對話方塊，以讓使用者確認是否刪除紀錄
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                str = "確定刪除" + StartTimeString + "到" + EndTimeString + "間之紀錄?";
                builder.setTitle("確認對話方塊")
                        .setMessage(str)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() { // 建立"確定按鈕"
                            public void onClick(DialogInterface dialoginterface, int i) // 點選"確認按鈕"之事件處理方法
                            {
                                // 查詢刪除前之紀錄筆數
                                Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
                                int n1 = cursor.getCount(); //取得刪除前之紀錄筆數

                                // 刪除選定日期範圍內之紀錄
                                String commandString = "DELETE FROM " + DATABASE_TABLE +
                                        " WHERE time between '" + StartTimeString +
                                        "' and '" + EndTimeString +"'";
                                db.execSQL(commandString);

                                // 查詢刪除後之紀錄筆數
                                cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
                                int n2 = cursor.getCount(); //取得刪除後之紀錄筆數

                                // 在主顯示區顯示已成功刪除的紀錄筆數
                                txtV_result.setText("已成功刪除" + (n1-n2) + "筆紀錄!");
                            }
                        })
                        .setNegativeButton("取消", null) // 建立"取消按鈕"，若點選擇取消警示對話方塊
                        .show(); // 顯示所建立之警示對話方塊
            }
            catch(Exception ex)  {
                Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btn_back_Click(View view) {
        finish();
    }
}
