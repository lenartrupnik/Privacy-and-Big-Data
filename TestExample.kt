/*
 *  Copyright (c) 2020 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.en.pia

import org.junit.Assert.assertEquals
import org.junit.Test

class ExamplePIATest {
    @Test

    fun randomSimpleComparison3() {
        val a = 1
        val b = 2

        assertEquals(a, b)
    }

    @Test
    fun randomSimpleComparison2() {
        val a = 2
        val b = 2

        assertEquals(a, b)
    }
}
