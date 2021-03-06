package com.projectcenterfvt.historicalpenza.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import android.content.Context
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.utils.Singleton

class LandmarksDatabase private constructor(context: Context) {

    private val dao = getDatabase(context).landmarkDao

    private val mapper = LandMarkMapper()

    @Transaction
    fun insertLandmarks(list: List<Landmark>) {
        val landmarks = list.map { mapper.mapFromDomain(it) }
        dao.deleteAll()
        dao.insertAll(landmarks)
    }

    fun getLandmarks() : LiveData<List<Landmark>> {
        return Transformations.map(dao.getAll()) {  list ->
            list.map { mapper.mapToDomain(it) }
        }
    }

    fun resetLandmarks() { dao.resetLandmarks() }

    fun openLandmark(id: Long) { dao.openLandmark(id) }

    companion object : Singleton<LandmarksDatabase, Context>(::LandmarksDatabase)
}

@Dao
interface LandmarkDao {
    @Query("select * from Landmarks")
    fun getAll(): LiveData<List<LandmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(landmarks: List<LandmarkEntity>)

    @Query("DELETE FROM Landmarks")
    fun deleteAll()

    @Query("UPDATE Landmarks SET isOpened = 0")
    fun resetLandmarks()

    @Query("UPDATE Landmarks SET isOpened = 1 WHERE id = :id")
    fun openLandmark(id: Long)
}

@Database(entities = [LandmarkEntity::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract val landmarkDao: LandmarkDao
}

private lateinit var INSTANCE: MyDatabase

fun getDatabase(context: Context): MyDatabase {
    synchronized(MyDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                    .databaseBuilder(
                            context.applicationContext,
                            MyDatabase::class.java,
                            "landmarks_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    return INSTANCE
}