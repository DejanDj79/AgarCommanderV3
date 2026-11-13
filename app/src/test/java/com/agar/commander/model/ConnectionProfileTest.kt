package com.agar.commander.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectionProfileTest {
    @Test
    fun defaultProfileUsesRobotWifiEndpoint() {
        val profile = ConnectionProfile()

        assertTrue(profile.isValid)
        assertEquals("ws://192.168.0.61:9090", profile.endpoint)
    }

    @Test
    fun endpointTrimsHostWhitespace() {
        val profile = ConnectionProfile(host = " 10.77.0.10 ", port = 9090)

        assertEquals("ws://10.77.0.10:9090", profile.endpoint)
    }

    @Test
    fun invalidHostOrPortIsRejected() {
        assertFalse(ConnectionProfile(host = " ").isValid)
        assertFalse(ConnectionProfile(port = 0).isValid)
        assertFalse(ConnectionProfile(port = 65_536).isValid)
        assertTrue(ConnectionProfile(port = 1).isValid)
        assertTrue(ConnectionProfile(port = 65_535).isValid)
    }
}
