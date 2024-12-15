package com.furkandonertas.androidbitirmeproje.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // DAO'ya erişim sağlayan abstract fonksiyon
    abstract fun favoriteEventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton olarak veritabanı nesnesini oluştur
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "favorite_event_database"
                )
                    .fallbackToDestructiveMigration() // Şema değişikliklerinde veritabanını sıfırlar
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // İlk çalıştırmada yapılacak işlemler
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
