/*
 *  Copyright (c) 2020 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.en.pia

import org.junit.Test
import java.security.SecureRandom
import java.util.Random
import org.junit.Assert

class TemporaryExposureKeyExchangeTest {
    // Simulating TEK generation
    // Simulating an in-memory database to store TEKs with matched IP addresses
    private val tekDatabase: MutableMap<ByteArray, String> = mutableMapOf()

    fun generateTEK():ByteArray {
        val random = SecureRandom()
        val tek = ByteArray(16)
        random.nextBytes(tek)
        return tek
    }

    // Function to generate a random IP address
    // It's purpose is to make a test better representable
    fun generateRandomIPAddress(): String {
        val random = Random()
        return "${random.nextInt(256)}.${random.nextInt(256)}.${random.nextInt(256)}.${random.nextInt(256)}"
    }

    // Simulating the network sending TEKs as a tuple with TEK and IP address
    fun sendTEKs(teks: List<Pair<ByteArray, String>>): Boolean {
        // Represent a user sending TEKs in a period of one day by calling integrated API which is
        // currently not working.
        return true
    }

    // Simulating the malicious party intercepting TEKs and storing them in a database.
    fun interceptAndStoreTEKs(tekList: List<Pair<ByteArray, String>>) {
        for ((tek, ipAddress) in tekList) {
            tekDatabase[tek] = ipAddress
        }
    }

    // Simulating the BE connection to retrieve a DK
    fun fetchDailyDKs(tek: ByteArray): ByteArray {
        // In actual application this function would call an BE API and retrieve daily DKs that are
        // shared.
        return tek
    }

    // Simulating the false party checking if received DK/TEK match with those in the database
    fun checkMatchAndRetrieveIP(tekOrDK: ByteArray): String? {
        return tekDatabase[tekOrDK]
    }

    @Test
    fun testTEKExchangeAndVerification() {
        // Simulate an infected person with a unique TEK and unique IP address.
        val personTEK = generateTEK()
        val senderIPAddress = "personIPAddress"
        val personDK = personTEK


        // Simulate sending TEKs, including fake ones
        val tekAndIPList = listOf(Pair(personTEK, senderIPAddress))
        val sendResult = sendTEKs(tekAndIPList)
        Assert.assertTrue("TEKs should be sent successfully", sendResult)

        // Simulate a malicious party intercepting and storing TEKs matched with IP addresses of sending devices
        interceptAndStoreTEKs(tekAndIPList)

        // Simulate the BE connection to get a DK
        // NOTE: Actual simulation of receiving DKs from BE is currently not possible
        // since the BE is not supported.
        val retrievedDK = fetchDailyDKs(personDK)

        // Simulate the false party checking if received DK/TEK match with those in the database
        val matchedIP = checkMatchAndRetrieveIP(retrievedDK)
        Assert.assertEquals(matchedIP, senderIPAddress)
    }
}
