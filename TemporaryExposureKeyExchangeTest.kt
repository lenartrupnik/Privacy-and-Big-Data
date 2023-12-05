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

/**
 * In this test we want to show the architecture with which a malicious party can identify positive
 * individuals from a smaller community e.g. company/school/residence. This architecture is based
 * on the fact that in smaller communities the malicious party can easily match users and their
 * IP addresses especially in places like companies. This test simulates the behaviour of every
 * required step in order to identify positive individuals.
 *
 * DISCLAIMER: Currently the app can be downloaded from Google Play but it's not actually operating.
 * This means that connection to BE
 */
class TemporaryExposureKeyExchangeTest {

    // Simulating an in-memory database to store TEKs with matched IP addresses
    private val tekDatabase: MutableMap<ByteArray, UserInfo> = mutableMapOf()
    data class UserInfo(val name: String, val ipAddress: String)
    // Simulating TEK generation
    fun generateUniqueTEK():ByteArray {
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

    // Function to generate a random name
    fun generateRandomName(): String {
        val names = listOf("Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Henry", "Ivy", "Jack",
            "Edgar", "Yasmin", "Ronin", "Reece", "Dulce", "Isaac", "Peter", "Lawson", "Daxton")
        return names.shuffled().first()
    }

    // Simulating the network sending TEKs as a tuple with TEK and IP address
    fun sendTEKs(teks: List<Pair<ByteArray, UserInfo>>): Boolean {
        // Represent a user sending TEKs in a period of one day by calling integrated API which is
        // currently not working.
        //TODO: implement PostKeysRequest API call when BE is operating
        return true
    }

    // Simulating the malicious party intercepting TEKs and storing them in a database. Here we assume
    // this is a possibility for malicious party and we discuss details in our PIA.
    fun interceptAndStoreTEKs(tekList: List<Pair<ByteArray, UserInfo>>) {
        for ((tek, userInfo) in tekList) {
            tekDatabase[tek] = userInfo
        }
    }

    // Simulating the BE connection to retrieve a DK
    fun fetchDailyDKs(tek: ByteArray): ByteArray {
        // In the application this function would call an BE API and retrieve daily DKs that are
        // shared. In general DKs are gathered through function >>processExposureKeySets <<.
        // TODO: implement processExposureKeySets function for retrieving DKS when BE is operating.
        return tek
    }

    // Simulating the false party checking if received DK/TEK match with those in the database
    fun matchDKAndRetrieveIP(tekOrDK: ByteArray): UserInfo? {
        return tekDatabase[tekOrDK]
    }

    @Test
    fun testTEKExchangeAndVerification() {
        // Simulate 20 users with unique TEKs and IP addresses
        val users = (1..20).map {
            val tek = generateUniqueTEK()
            val name = generateRandomName()
            val ipAddress = generateRandomIPAddress()
            Pair(tek, UserInfo(name, ipAddress))
        }
        // Store the TEKs and IP addresses in the database
        interceptAndStoreTEKs(users)

        // Simulate an infected person with a unique TEK and unique IP address.
        val positiveTEK = generateUniqueTEK()
        val positiveName = generateRandomName()
        val positiveIPAddress = generateRandomIPAddress()
        val positiveDK = positiveTEK


        // Simulate sending TEKs, including fake ones
        val tekAndUserInfoList = listOf(Pair(positiveTEK, UserInfo(positiveName, positiveIPAddress)))
        val sendResult = sendTEKs(tekAndUserInfoList)
        Assert.assertTrue("TEKs should be sent successfully", sendResult)

        // Simulate a malicious party intercepting and storing TEKs matched with IP addresses of sending devices
        interceptAndStoreTEKs(tekAndUserInfoList)

        // Simulate the BE connection to get a DK
        // NOTE: Actual simulation of receiving DKs from BE is currently not possible
        // since the BE is not supported.
        val retrievedDK = fetchDailyDKs(positiveDK)

        // Simulate the false party checking if received DK/TEK match with those in the database
        val matchedUser = matchDKAndRetrieveIP(retrievedDK)
        if (matchedUser != null) {
            Assert.assertNotEquals("Person ID is revealed based on TEK, DK and IP match",
                matchedUser.ipAddress,
                positiveIPAddress)
        }
    }
}
