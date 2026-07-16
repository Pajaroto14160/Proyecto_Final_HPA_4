package com.utp.mediconecta.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        HospitalEntity::class,
        PharmacyEntity::class,
        SpecialtyEntity::class,
        DoctorEntity::class,
        AppointmentEntity::class,
        MedicalHistoryEntity::class,
        ExamEntity::class,
        MedicationEntity::class,
        HospitalInventoryEntity::class,
        PharmacyInventoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun directoryDao(): DirectoryDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun historyDao(): HistoryDao
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mediconecta.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
