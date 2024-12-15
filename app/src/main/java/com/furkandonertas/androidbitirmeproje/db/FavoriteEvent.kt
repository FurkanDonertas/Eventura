package com.furkandonertas.androidbitirmeproje.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomWarnings
import kotlinx.coroutines.flow.Flow

// Veritabanı tablosu için `FavoriteEvent` sınıfı
@Entity(tableName = "favorites")
data class FavoriteEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Boş olmaması gereken alan
    val date: String?, // Nullable çünkü bazen tarih bilgisi olmayabilir
    val time: String?, // Nullable çünkü saat bilgisi opsiyonel
    val location: String?, // Nullable çünkü etkinlik yeri opsiyonel olabilir
    val imageUrl: String?, // Görsel URL'si
    val url: String? // Etkinlik URL'si
)

// DAO (Data Access Object) işlemleri
@Dao
interface FavoriteEventDao {

    // Favori etkinliği ekleme
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: FavoriteEvent): Long

    // Tüm favori etkinlikleri listeleme
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEvent>>

    // Belirli bir ID'ye sahip favori etkinliği getir
    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: Int): FavoriteEvent?

    // Favori etkinliği sil
    @Delete
    suspend fun delete(event: FavoriteEvent): Int

    // Belirli bir ID'ye sahip favori etkinliği sil
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    // Tüm favorileri sil
    @Query("DELETE FROM favorites")
    suspend fun deleteAll(): Int



}
