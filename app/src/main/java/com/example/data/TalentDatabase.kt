package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AthleteEntity::class,
        MatchLogEntity::class,
        ClubEntity::class,
        CareerStintEntity::class,
        TrainingSessionEntity::class,
        DevelopmentGoalEntity::class,
        PhysicalMeasurementEntity::class,
        AvailabilityEntity::class,
        WorkloadEntity::class,
        AchievementEntity::class,
        OpportunityEntity::class,
        ScoutActivityEntity::class,
        EvidenceEntity::class,
        RiskProtectionEntity::class,
        InsuranceClaimEntity::class,
        DocumentEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        CalendarEventEntity::class,
        PaymentEntity::class,
        PrivacySettingEntity::class,
        OrganizationEntity::class,
        TeamEntity::class,
        OrganizationMembershipEntity::class,
        ConnectionEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class TalentDatabase : RoomDatabase() {
    abstract fun athleteDao(): AthleteDao
    abstract fun matchLogDao(): MatchLogDao
    abstract fun clubDao(): ClubDao
    abstract fun careerDao(): CareerDao
    abstract fun trainingDao(): TrainingDao
    abstract fun developmentDao(): DevelopmentDao
    abstract fun physicalDao(): PhysicalDao
    abstract fun availabilityDao(): AvailabilityDao
    abstract fun workloadDao(): WorkloadDao
    abstract fun achievementDao(): AchievementDao
    abstract fun opportunityDao(): OpportunityDao
    abstract fun scoutActivityDao(): ScoutActivityDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun riskProtectionDao(): RiskProtectionDao
    abstract fun insuranceClaimDao(): InsuranceClaimDao
    abstract fun documentDao(): DocumentDao
    abstract fun messageDao(): MessageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun calendarDao(): CalendarDao
    abstract fun paymentDao(): PaymentDao
    abstract fun privacyDao(): PrivacyDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun teamDao(): TeamDao
    abstract fun organizationMembershipDao(): OrganizationMembershipDao
    abstract fun connectionDao(): ConnectionDao

    companion object {
        @Volatile
        private var INSTANCE: TalentDatabase? = null

        fun getDatabase(context: Context): TalentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TalentDatabase::class.java,
                    "talent_graph_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
