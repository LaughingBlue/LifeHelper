package tw.bluelab.lifehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class foodDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Foodrecord.db"; // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1; // 建立"資料庫版本"常數

    public foodDBHelper(Context context) { //資料庫幫助類別建構子
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // 執行父類別(SQLiteOpenHelper)之建構子
    }

    @Override // 複寫onCreate方法，在建立資料庫時，就會執行這個方法
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE foodrecord (_id integer primary key autoincrement, "
                + "foodname nvarchar(30) not null, calorie int not null, cost int null, "
                + "time date not null)");
    }

    @Override // 複寫onUpgrade方法，在資料庫有新版本時(DATABASE_VERSION有增加時)，就執行此方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS foodrecord");
        onCreate(db);
    }
}
