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

    def 'should throw exception when to is before from'() {
        given:
            Instant from = Instant.now()
            Instant to = from.minus(1, ChronoUnit.DAYS)
        when:
            new HoldDuration(from, to)
        then:
            thrown(IllegalStateException)
    }

    def 'should allow same from and to dates'() {
        given:
            Instant fromAndTo = Instant.now()
        when:
            HoldDuration duration = new HoldDuration(fromAndTo, fromAndTo)
        then:
            duration.from == fromAndTo
            duration.getTo().get() == fromAndTo
            !duration.isOpenEnded()
    }

    def 'should handle null to date for open ended duration'() {
        given:
            Instant from = Instant.now()
        when:
            HoldDuration duration = new HoldDuration(from, null)
        then:
            duration.from == from
            duration.isOpenEnded()
            duration.getTo().isEmpty()
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
