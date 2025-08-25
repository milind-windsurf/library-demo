package io.pillopl.library.lending.patron.model

import io.pillopl.library.lending.book.model.AvailableBook
import io.vavr.control.Either
import spock.lang.Specification

import static io.pillopl.library.lending.book.model.BookFixture.circulatingAvailableBook
import static io.pillopl.library.lending.book.model.BookFixture.restrictedAvailableBook
import static io.pillopl.library.lending.patron.model.PatronFixture.*
import static io.pillopl.library.lending.patron.model.PlacingOnHoldPolicy.*

class PlacingOnHoldPolicyTest extends Specification {

    def 'regularPatronCannotHoldRestrictedBooks should reject regular patron holding restricted book'() {
        given:
            Patron regularPatron = regularPatron()
            AvailableBook restrictedBook = restrictedAvailableBook()
        when:
            Either<Rejection, Allowance> result = regularPatronCannotHoldRestrictedBooks.apply(regularPatron, restrictedBook)
        then:
            result.isLeft()
            result.getLeft().reason.reason.contains("Regular patrons cannot hold restricted books")
    }

    def 'regularPatronCannotHoldRestrictedBooks should allow regular patron holding circulating book'() {
        given:
            Patron regularPatron = regularPatron()
            AvailableBook circulatingBook = circulatingAvailableBook()
        when:
            Either<Rejection, Allowance> result = regularPatronCannotHoldRestrictedBooks.apply(regularPatron, circulatingBook)
        then:
            result.isRight()
    }

    def 'regularPatronCannotHoldRestrictedBooks should allow researcher patron holding restricted book'() {
        given:
            Patron researcherPatron = researcherPatron()
            AvailableBook restrictedBook = restrictedAvailableBook()
        when:
            Either<Rejection, Allowance> result = regularPatronCannotHoldRestrictedBooks.apply(researcherPatron, restrictedBook)
        then:
            result.isRight()
    }

    def 'onlyResearcherPatronsCanPlaceOpenEndedHolds should reject regular patron'() {
        given:
            Patron regularPatron = regularPatron()
            AvailableBook book = circulatingAvailableBook()
        when:
            Either<Rejection, Allowance> result = onlyResearcherPatronsCanPlaceOpenEndedHolds.apply(regularPatron, book)
        then:
            result.isLeft()
            result.getLeft().reason.reason.contains("Regular patrons cannot place open ended holds")
    }

    def 'onlyResearcherPatronsCanPlaceOpenEndedHolds should allow researcher patron'() {
        given:
            Patron researcherPatron = researcherPatron()
            AvailableBook book = circulatingAvailableBook()
        when:
            Either<Rejection, Allowance> result = onlyResearcherPatronsCanPlaceOpenEndedHolds.apply(researcherPatron, book)
        then:
            result.isRight()
    }

    def 'regularPatronMaximumNumberOfHoldsPolicy should reject when at maximum holds'() {
        given:
            Patron patronWithMaxHolds = regularPatronWithHolds(5)
            AvailableBook book = circulatingAvailableBook()
        when:
            Either<Rejection, Allowance> result = regularPatronMaximumNumberOfHoldsPolicy.apply(patronWithMaxHolds, book)
        then:
            result.isLeft()
            result.getLeft().reason.reason.contains("Regular patron cannot hold more than")
    }

    def 'regularPatronMaximumNumberOfHoldsPolicy should allow when below maximum holds'() {
        given:
            Patron patronWithFewHolds = regularPatronWithHolds(3)
            AvailableBook book = circulatingAvailableBook()
        when:
            Either<Rejection, Allowance> result = regularPatronMaximumNumberOfHoldsPolicy.apply(patronWithFewHolds, book)
        then:
            result.isRight()
    }

    def 'overdueCheckoutsRejectionPolicy should reject when patron has overdue checkouts at same branch'() {
        given:
            AvailableBook book = circulatingAvailableBook()
            Set<BookId> overdueBooks = [anyBookId(), anyBookId()] as Set
            Patron patronWithOverdueCheckouts = regularPatronWithOverdueCheckouts(book.libraryBranch.libraryBranchId, overdueBooks)
        when:
            Either<Rejection, Allowance> result = overdueCheckoutsRejectionPolicy.apply(patronWithOverdueCheckouts, book)
        then:
            result.isLeft()
            result.getLeft().reason.reason.contains("Cannot place on hold when there are overdue checkouts")
    }

    def 'overdueCheckoutsRejectionPolicy should allow when patron has no overdue checkouts'() {
        given:
            Patron patronWithoutOverdueCheckouts = regularPatron()
            AvailableBook book = circulatingAvailableBook()
        when:
            Either<Rejection, Allowance> result = overdueCheckoutsRejectionPolicy.apply(patronWithoutOverdueCheckouts, book)
        then:
            result.isRight()
    }

    def 'allCurrentPolicies should return all policies'() {
        when:
            Set<PlacingOnHoldPolicy> policies = allCurrentPolicies()
        then:
            policies.size() == 4
            policies.contains(regularPatronCannotHoldRestrictedBooks)
            policies.contains(onlyResearcherPatronsCanPlaceOpenEndedHolds)
            policies.contains(regularPatronMaximumNumberOfHoldsPolicy)
            policies.contains(overdueCheckoutsRejectionPolicy)
    }

    def 'multiple policies should be evaluated correctly'() {
        given:
            Patron regularPatronWithMaxHolds = regularPatronWithHolds(5)
            AvailableBook restrictedBook = restrictedAvailableBook()
            Set<PlacingOnHoldPolicy> policies = [regularPatronCannotHoldRestrictedBooks, regularPatronMaximumNumberOfHoldsPolicy] as Set
        when:
            List<Either<Rejection, Allowance>> results = policies.collect { policy ->
                policy.apply(regularPatronWithMaxHolds, restrictedBook)
            }
        then:
            results.every { it.isLeft() }
            results.size() == 2
    }

    def 'policy combination should allow when all policies pass'() {
        given:
            Patron researcherPatronWithFewHolds = researcherPatronWithHolds(2)
            AvailableBook circulatingBook = circulatingAvailableBook()
            Set<PlacingOnHoldPolicy> policies = [regularPatronCannotHoldRestrictedBooks, regularPatronMaximumNumberOfHoldsPolicy] as Set
        when:
            List<Either<Rejection, Allowance>> results = policies.collect { policy ->
                policy.apply(researcherPatronWithFewHolds, circulatingBook)
            }
        then:
            results.every { it.isRight() }
            results.size() == 2
    }
}
