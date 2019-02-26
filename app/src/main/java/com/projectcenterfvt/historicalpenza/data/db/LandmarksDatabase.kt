package com.projectcenterfvt.historicalpenza.data.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.persistence.room.*
import android.content.Context
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.utils.Singleton

class LandmarksDatabase private constructor(context: Context) {

    val dao = getDatabase(context).landmarkDao

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

    companion object : Singleton<LandmarksDatabase, Context>(::LandmarksDatabase)
}

@Dao
interface LandmarkDao {
    @Query("select * from Landmark")
    fun getAll(): LiveData<List<LandmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(landmarks: List<LandmarkEntity>)

    @Query("DELETE FROM Landmark")
    fun deleteAll()
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