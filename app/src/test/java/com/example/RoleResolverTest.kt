package com.example

import com.example.data.auth.UserRole
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoleResolverTest {

    @Test
    fun `verify user role string mapping`() {
        assertEquals(UserRole.ATHLETE, UserRole.fromValue("athlete"))
        assertEquals(UserRole.ATHLETE, UserRole.fromValue(" ATHLETE "))
        
        assertEquals(UserRole.COACH, UserRole.fromValue("coach"))
        assertEquals(UserRole.SCOUT, UserRole.fromValue("scout"))
        assertEquals(UserRole.ANALYST, UserRole.fromValue("analyst"))
        assertEquals(UserRole.ANALYST, UserRole.fromValue("coach analyst"))
        
        assertEquals(UserRole.CLUB_ADMIN, UserRole.fromValue("club"))
        assertEquals(UserRole.CLUB_ADMIN, UserRole.fromValue("admin"))
        assertEquals(UserRole.CLUB_ADMIN, UserRole.fromValue("club_admin"))
        assertEquals(UserRole.CLUB_ADMIN, UserRole.fromValue("club admin"))
        
        assertEquals(UserRole.PLATFORM_ADMIN, UserRole.fromValue("platform_admin"))
        assertEquals(UserRole.PLATFORM_ADMIN, UserRole.fromValue("platform admin"))
        
        assertEquals(UserRole.NONE, UserRole.fromValue("unknown"))
        assertEquals(UserRole.NONE, UserRole.fromValue(null))
    }
}
