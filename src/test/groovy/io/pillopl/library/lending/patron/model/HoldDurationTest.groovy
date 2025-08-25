package io.pillopl.library.lending.patron.model

import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit

class HoldDurationTest extends Specification {

    def 'should create open ended hold duration'() {
        given:
            Instant from = Instant.now()
        when:
            HoldDuration duration = HoldDuration.openEnded(from)
        then:
            duration.from == from
            duration.isOpenEnded()
            duration.getTo().isEmpty()
    }

    def 'should create open ended hold duration with current time'() {
        when:
            HoldDuration duration = HoldDuration.openEnded()
        then:
            duration.isOpenEnded()
            duration.getTo().isEmpty()
            duration.from != null
    }

    def 'should create close ended hold duration'() {
        given:
            Instant from = Instant.now()
            NumberOfDays days = NumberOfDays.of(5)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, days)
        then:
            duration.from == from
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.getTo().get() == from.plus(5, ChronoUnit.DAYS)
    }

    def 'should create close ended hold duration with current time'() {
        given:
            NumberOfDays days = NumberOfDays.of(3)
        when:
            HoldDuration duration = HoldDuration.closeEnded(days)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.from != null
    }

    def 'should create close ended hold duration with int days'() {
        when:
            HoldDuration duration = HoldDuration.closeEnded(7)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.from != null
    }

    def 'should handle edge case with minimum duration'() {
        given:
            Instant from = Instant.now()
            NumberOfDays oneDay = NumberOfDays.of(1)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, oneDay)
        then:
            duration.from == from
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.getTo().get() == from.plus(1, ChronoUnit.DAYS)
    }

    def 'should handle multiple factory method calls consistently'() {
        given:
            Instant baseTime = Instant.now()
        when:
            HoldDuration openEnded1 = HoldDuration.openEnded(baseTime)
            HoldDuration openEnded2 = HoldDuration.openEnded(baseTime)
        then:
            openEnded1.from == openEnded2.from
            openEnded1.isOpenEnded() == openEnded2.isOpenEnded()
            openEnded1.getTo().isEmpty()
            openEnded2.getTo().isEmpty()
    }

    def 'should handle very large durations'() {
        given:
            Instant from = Instant.now()
            NumberOfDays days = NumberOfDays.of(365)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, days)
        then:
            duration.from == from
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.getTo().get() == from.plus(365, ChronoUnit.DAYS)
    }
}
