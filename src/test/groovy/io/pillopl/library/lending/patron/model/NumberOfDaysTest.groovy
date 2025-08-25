package io.pillopl.library.lending.patron.model

import spock.lang.Specification

class NumberOfDaysTest extends Specification {

    def 'should create NumberOfDays with positive value'() {
        when:
            NumberOfDays days = NumberOfDays.of(5)
        then:
            days.days == 5
    }

    def 'should throw exception for zero days'() {
        when:
            NumberOfDays.of(0)
        then:
            thrown(IllegalArgumentException)
    }

    def 'should throw exception for negative days'() {
        when:
            NumberOfDays.of(days)
        then:
            thrown(IllegalArgumentException)
        where:
            days << [-1, -5, -10, -100]
    }

    def 'should correctly compare with isGreaterThan'() {
        given:
            NumberOfDays days = NumberOfDays.of(actualDays)
        when:
            boolean result = days.isGreaterThan(comparedDays)
        then:
            result == expected
        where:
            actualDays | comparedDays | expected
            5          | 3            | true
            5          | 5            | false
            5          | 7            | false
            1          | 0            | true
            10         | 9            | true
            10         | 10           | false
            10         | 11           | false
    }

    def 'should handle large number of days'() {
        when:
            NumberOfDays days = NumberOfDays.of(365)
        then:
            days.days == 365
            days.isGreaterThan(364)
            !days.isGreaterThan(365)
            !days.isGreaterThan(366)
    }

    def 'should handle minimum valid value'() {
        when:
            NumberOfDays days = NumberOfDays.of(1)
        then:
            days.days == 1
            days.isGreaterThan(0)
            !days.isGreaterThan(1)
            !days.isGreaterThan(2)
    }

    def 'should be immutable value object'() {
        given:
            NumberOfDays days1 = NumberOfDays.of(5)
            NumberOfDays days2 = NumberOfDays.of(5)
            NumberOfDays days3 = NumberOfDays.of(3)
        expect:
            days1 == days2
            days1 != days3
            days1.hashCode() == days2.hashCode()
    }
}
