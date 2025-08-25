package io.pillopl.library.lending.patron.model

import io.pillopl.library.catalogue.BookId
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId
import spock.lang.Specification

import static io.pillopl.library.lending.patron.model.PatronFixture.anyBookId
import static io.pillopl.library.lending.patron.model.PatronFixture.anyBranch

class OverdueCheckoutsTest extends Specification {

    def 'should return zero count for non-existing library branch'() {
        given:
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(new HashMap<>())
            LibraryBranchId branchId = anyBranch()
        when:
            int count = overdueCheckouts.countAt(branchId)
        then:
            count == 0
    }

    def 'should return zero count for empty checkout map'() {
        given:
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(new HashMap<>())
            LibraryBranchId branchId = anyBranch()
        when:
            int count = overdueCheckouts.countAt(branchId)
        then:
            count == 0
    }

    def 'should return correct count for existing library branch'() {
        given:
            LibraryBranchId branchId = anyBranch()
            Set<BookId> books = [anyBookId(), anyBookId(), anyBookId()] as Set
            Map<LibraryBranchId, Set<BookId>> checkouts = [(branchId): books]
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(checkouts)
        when:
            int count = overdueCheckouts.countAt(branchId)
        then:
            count == 3
    }

    def 'should return zero count for library branch with empty book set'() {
        given:
            LibraryBranchId branchId = anyBranch()
            Map<LibraryBranchId, Set<BookId>> checkouts = [(branchId): new HashSet<>()]
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(checkouts)
        when:
            int count = overdueCheckouts.countAt(branchId)
        then:
            count == 0
    }

    def 'should handle multiple library branches correctly'() {
        given:
            LibraryBranchId branch1 = anyBranch()
            LibraryBranchId branch2 = anyBranch()
            LibraryBranchId branch3 = anyBranch()
            Set<BookId> books1 = [anyBookId(), anyBookId()] as Set
            Set<BookId> books2 = [anyBookId()] as Set
            Map<LibraryBranchId, Set<BookId>> checkouts = [
                (branch1): books1,
                (branch2): books2,
                (branch3): new HashSet<>()
            ]
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(checkouts)
        when:
            int count1 = overdueCheckouts.countAt(branch1)
            int count2 = overdueCheckouts.countAt(branch2)
            int count3 = overdueCheckouts.countAt(branch3)
            int countNonExisting = overdueCheckouts.countAt(anyBranch())
        then:
            count1 == 2
            count2 == 1
            count3 == 0
            countNonExisting == 0
    }

    def 'should handle maximum count scenario'() {
        given:
            LibraryBranchId branchId = anyBranch()
            Set<BookId> books = [anyBookId(), anyBookId()] as Set
            Map<LibraryBranchId, Set<BookId>> checkouts = [(branchId): books]
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(checkouts)
        when:
            int count = overdueCheckouts.countAt(branchId)
        then:
            count == OverdueCheckouts.MAX_COUNT_OF_OVERDUE_RESOURCES
    }

    def 'should handle count above maximum'() {
        given:
            LibraryBranchId branchId = anyBranch()
            Set<BookId> books = [anyBookId(), anyBookId(), anyBookId()] as Set
            Map<LibraryBranchId, Set<BookId>> checkouts = [(branchId): books]
            OverdueCheckouts overdueCheckouts = new OverdueCheckouts(checkouts)
        when:
            int count = overdueCheckouts.countAt(branchId)
        then:
            count > OverdueCheckouts.MAX_COUNT_OF_OVERDUE_RESOURCES
            count == 3
    }
}
