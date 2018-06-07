package team.circleofcampus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import team.circleofcampus.pojo.CampusCircle;
import team.circleofcampus.pojo.SocietyCircle;
import team.circleofcampus.pojo.User;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库名
     */
    private static final String DATABASE_NAME = "OrmliteDemo.db";

    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 构造方法
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 通过ormlite的TableUtils类创建数据表
     * @param sqLiteDatabase
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {
            // 创建"tb_user"用户表
            TableUtils.createTable(connectionSource, User.class);
            // 创建"t_camus_circle"校园圈表
            TableUtils.createTable(connectionSource, CampusCircle.class);
            // 创建"t_society_circle"社团圈表
            TableUtils.createTable(connectionSource, SocietyCircle.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库版本更新方法
     * @param sqLiteDatabase
     * @param connectionSource
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {}

}
