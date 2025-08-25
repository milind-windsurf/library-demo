package io.pillopl.library.lending.patron.model

import io.pillopl.library.lending.book.model.AvailableBook
import io.pillopl.library.lending.book.model.BookOnHold
import spock.lang.Specification

import static io.pillopl.library.lending.book.model.BookFixture.circulatingAvailableBook
import static io.pillopl.library.lending.book.model.BookFixture.circulatingBookOnHold
import static io.pillopl.library.lending.patron.model.PatronFixture.anyBookId
import static io.pillopl.library.lending.patron.model.PatronFixture.anyBranch

class PatronHoldsTest extends Specification {

    def 'should count zero holds for empty set'() {
        given:
            PatronHolds patronHolds = new PatronHolds(new HashSet<>())
        when:
            int count = patronHolds.count()
        then:
            count == 0
    }

    def 'should count holds correctly'() {
        given:
            Set<Hold> holds = [
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch())
            ] as Set
            PatronHolds patronHolds = new PatronHolds(holds)
        when:
            int count = patronHolds.count()
        then:
            count == 3
    }

    def 'should return false when not at maximum holds after holding'() {
        given:
            Set<Hold> holds = [
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch())
            ] as Set
            PatronHolds patronHolds = new PatronHolds(holds)
            AvailableBook book = circulatingAvailableBook()
        when:
            boolean isMaximum = patronHolds.maximumHoldsAfterHolding(book)
        then:
            !isMaximum
    }

    def 'should return true when at maximum holds after holding'() {
        given:
            Set<Hold> holds = [
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch())
            ] as Set
            PatronHolds patronHolds = new PatronHolds(holds)
            AvailableBook book = circulatingAvailableBook()
        when:
            boolean isMaximum = patronHolds.maximumHoldsAfterHolding(book)
        then:
            isMaximum
    }

    def 'should return false when already at maximum holds'() {
        given:
            Set<Hold> holds = [
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch()),
                new Hold(anyBookId(), anyBranch())
            ] as Set
            PatronHolds patronHolds = new PatronHolds(holds)
            AvailableBook book = circulatingAvailableBook()
        when:
            boolean isMaximum = patronHolds.maximumHoldsAfterHolding(book)
        then:
            !isMaximum
    }

    def 'should return true when book is on hold'() {
        given:
            BookOnHold bookOnHold = circulatingBookOnHold()
            Hold hold = new Hold(bookOnHold.getBookId(), bookOnHold.getHoldPlacedAt())
            PatronHolds patronHolds = new PatronHolds([hold] as Set)
        when:
            boolean hasHold = patronHolds.a(bookOnHold)
        then:
            hasHold
    }

    def 'should return false when book is not on hold'() {
        given:
            BookOnHold bookOnHold = circulatingBookOnHold()
            Hold differentHold = new Hold(anyBookId(), anyBranch())
            PatronHolds patronHolds = new PatronHolds([differentHold] as Set)
        when:
            boolean hasHold = patronHolds.a(bookOnHold)
        then:
            !hasHold
    }

    def 'should return false when no holds exist'() {
        given:
            BookOnHold bookOnHold = circulatingBookOnHold()
            PatronHolds patronHolds = new PatronHolds(new HashSet<>())
        when:
            boolean hasHold = patronHolds.a(bookOnHold)
        then:
            !hasHold
    }
}
