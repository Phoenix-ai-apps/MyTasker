package com.b2b.sampleb2b.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.b2b.sampleb2b.AppExecutors;
import com.b2b.sampleb2b.db.converter.ObjectConverter;
import com.b2b.sampleb2b.db.dao.FolderDao;
import com.b2b.sampleb2b.db.dao.SubFolderDao;
import com.b2b.sampleb2b.db.dao.TaskDetailsDao;
import com.b2b.sampleb2b.db.entities.FolderEntity;
import com.b2b.sampleb2b.db.entities.SubFolderEntity;
import com.b2b.sampleb2b.db.entities.TaskDetailsEntity;
import com.b2b.sampleb2b.helper.ApplicationHelper;
import com.b2b.sampleb2b.helper.HelperInterface;

import java.util.List;

import static com.b2b.sampleb2b.constants.AllConstants.DATABASE_VERSION;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
@Database(entities = {FolderEntity.class, TaskDetailsEntity.class,
                      SubFolderEntity.class}
                    , version = DATABASE_VERSION)
@TypeConverters({ObjectConverter.class})
public abstract class MyTaskDatabase extends RoomDatabase implements HelperInterface {
    private static MyTaskDatabase taskDatabase;

    public abstract FolderDao      getFolderDao();
    public abstract SubFolderDao   getSubFolderDao();
    public abstract TaskDetailsDao getTaskDetailsDao();

    private final MutableLiveData<Boolean> isDBCreated = new MutableLiveData<>();

    public static MyTaskDatabase getInstance(final Context context, AppExecutors appExecutors){
        if(taskDatabase == null){
            synchronized (MyTaskDatabase.class){
                if(taskDatabase == null){
                    taskDatabase = buildDatabase(context.getApplicationContext(), appExecutors);
                    taskDatabase.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return taskDatabase;
    }

    private static MyTaskDatabase buildDatabase(final Context context, final AppExecutors appExecutors){
        return Room.databaseBuilder(context, MyTaskDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        appExecutors.getExeDiskIO().execute(()->{
                            // Add a delay to simulate a long-running operation
                            addDelay();
                            // Generate the data for pre-population
                            MyTaskDatabase database = MyTaskDatabase.getInstance(context, appExecutors);
                            List<FolderEntity>      folderEntities      = null; //DataGenerator.generateProducts();
                            List<TaskDetailsEntity> taskDetailsEntities = null; //DataGenerator.generateCommentsForProducts(products);
                            List<SubFolderEntity>   subFolderEntities   = null; //DataGenerator.generateCommentsForProducts(products);

                            insertData(database, folderEntities, subFolderEntities, taskDetailsEntities);
                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
                //.fallbackToDestructiveMigration() // This method clear the DB and create new DB from Scratch.
                                                 // So data will be wipedout. --Use this mtd in special scenarios.
                .addMigrations(MIGRATION_1_2)
                //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build();
    }

    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        isDBCreated.postValue(true);
    }

    private static void insertData(final MyTaskDatabase database,
                                   final List<FolderEntity>      folderEntities,
                                   final List<SubFolderEntity>   subFolderEntities,
                                   final List<TaskDetailsEntity> detailsEntities) {
        database.runInTransaction(() -> {
           // database.getFolderDao().insertAllFolder(folderEntities);
           // database.getTaskDetailsDao().insertAllTaskDetails(detailsEntities);
           // database.getSubFolderDao().insertAllSubFolder(subFolderEntities);
        });
    }

    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }

    //Migrations
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {  
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users "
                    + " ADD COLUMN last_update INTEGER");
        }
    };

    public LiveData<Boolean> getDatabaseCreated() {
        return isDBCreated;
    }

    @Override
    public ApplicationHelper getHelper() {
        return ApplicationHelper.getInstance();
    }
}
