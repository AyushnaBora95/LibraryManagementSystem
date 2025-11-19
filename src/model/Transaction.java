package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {

    private int id;
    private int bookId;
    private int memberId;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;

    public Transaction(int id, int bookId, int memberId,
                       LocalDate issueDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returned = false;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }

    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.returned = true;
    }

    
    public String toString() {
        return "TransactionID:" + id +
                " | BookID:" + bookId +
                " | MemberID:" + memberId +
                " | Issued:" + issueDate +
                " | Due:" + dueDate +
                " | Returned: " + (returned ? returnDate : "No");
    }
}
