package io.pillopl.library.lending.book.new_model;

import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronId;
import io.pillopl.library.commons.aggregates.Version;
import java.time.Instant;

public interface BookState {
    // Core state transitions
    BookState placeOnHold(PatronId patronId, LibraryBranchId branchId, Instant holdTill);
    BookState checkout(PatronId patronId, LibraryBranchId branchId);
    BookState returnBook(LibraryBranchId branchId);
    BookState cancelHold();
    BookState expireHold();
    
    // Validation methods
    boolean canBeCheckedOut(PatronId patronId);
    boolean canBePutOnHold(PatronId patronId);
    boolean canBeReturned();
    
    // State information
    String getStateName();
    Version getVersion();
    LibraryBranchId getCurrentBranch();
    PatronId getCurrentPatron();
    
    // Default implementations for invalid state transitions
    default BookState invalidTransition(String message) {
        throw new IllegalStateException("Invalid state transition: " + message);
    }
}
