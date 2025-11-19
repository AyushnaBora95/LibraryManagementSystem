// NOTE: default package (no "package" line) so Main.java can use it directly.
// Put this file in: service/LibraryService.java

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LibraryService implements Serializable {
	private static final long serialVersionUID=1L;

	private Map<Integer, Book> books=new HashMap<>();
	private Map<Integer, Member> members=new HashMap<>();
	private Map<Integer, Transaction> transactions=new HashMap<>();

	private int nextBookId=1;
	private int nextMemberId=1;
	private int nextTransactionId=1;

	private static final String DATA_DIR="data";
	private static final String BOOK_FILE=DATA_DIR+File.separator+"books.ser";
	private static final String MEMBER_FILE=DATA_DIR+File.separator+"members.ser";
	private static final String TRANS_FILE=DATA_DIR+File.separator+"transactions.ser";
	private static final String META_FILE=DATA_DIR+File.separator+"meta.ser";

	// autosave control
	private transient AtomicBoolean autosaveRunning=new AtomicBoolean(false);

	public LibraryService() {
		ensureDataDir();
		loadAll();
		// start autosave flag 
		autosaveRunning.set(true);
	}

	// ----------------- UI-facing methods-----------------

	// Add book 
	public synchronized void addBook(String title, String author) {
		int copies=1; 
		String isbn="N/A";
		Book b=new Book(nextBookId++, title, author, isbn, copies);
		books.put(b.getId(), b);
		saveBooks();
		saveMeta();
		System.out.println("Added book: "+b);
	}

	// Add member 
	public synchronized void addMember(String name) {
		String email="not-provided@example.com";
		Member m=new Member(nextMemberId++, name, email);
		members.put(m.getId(), m);
		saveMembers();
		saveMeta();
		System.out.println("Added member: "+m);
	}

	// Issue book by bookId & memberId 
	public synchronized String issueBook(int bookId, int memberId) {
		Book b=books.get(bookId);
		if(b==null) {
			String msg="Book not found (ID: "+bookId+")";
			System.out.println(msg);
			return msg;
		}
		Member m=members.get(memberId);
		if(m==null) {
			String msg="Member not found (ID: "+memberId+")";
			System.out.println(msg);
			return msg;
		}
		int copies=safeGetCopies(b);
		if(copies<=0) {
			String msg="No copies available for: "+b.getTitle();
			System.out.println(msg);
			return msg;
		}

		LocalDate issueDate=LocalDate.now();
		LocalDate dueDate=issueDate.plusDays(14); // default 14-day loan
		Transaction t=new Transaction(nextTransactionId++, bookId, memberId, issueDate, dueDate);
		transactions.put(t.getId(), t);

		// decrement copies (try borrow(), else set directly)
		try {
			b.getClass().getMethod("borrow").invoke(b);
		} catch(Exception ex) {
			try { b.setCopies(b.getCopies()-1); } catch(Exception ignored) {}
		}

		saveTransactions();
		saveBooks();
		saveMeta();

		String msg="Issued book. Transaction ID: "+t.getId()+". Due on: "+dueDate;
		System.out.println(msg);
		return msg;
	}

	public synchronized String returnBook(int bookId, int memberId) {
		Optional<Transaction> active=transactions.values().stream()
				.filter(t -> t.getBookId()==bookId&&t.getMemberId()==memberId&&!t.isReturned())
				.findFirst();

		if(!active.isPresent()) {
			String msg="No active transaction found for Book ID "+bookId+" and Member ID "+memberId;
			System.out.println(msg);
			return msg;
		}

		Transaction t=active.get();
		LocalDate returnDate=LocalDate.now();
		t.markReturned(returnDate);

		// increment copies
		Book b=books.get(bookId);
		if(b!=null) {
			try {
				b.getClass().getMethod("giveBack").invoke(b);
			} catch(Exception ex) {
				try { b.setCopies(b.getCopies()+1); } catch(Exception ignored) {}
			}
		}

		long lateDays=ChronoUnit.DAYS.between(t.getDueDate(), returnDate);
		double fine=lateDays>0? lateDays*5.0 : 0.0;

		saveTransactions();
		saveBooks();

		String msg=String.format("Book returned. Late days: %d. Fine: ₹%.2f", Math.max(0, lateDays), fine);
		System.out.println(msg);
		return msg;
	}

	public synchronized void showAllBooks() {
		if(books.isEmpty()) {
			System.out.println("[No books right now]");
			return;
		}
		books.values().stream()
				.sorted(Comparator.comparingInt(Book::getId))
				.forEach(System.out::println);
	}

	public synchronized void showAllMembers() {
		if(members.isEmpty()) {
			System.out.println("[No members right now]");
			return;
		}
		members.values().stream()
				.sorted(Comparator.comparingInt(Member::getId))
				.forEach(System.out::println);
	}

	public void autoSave() {
		saveAll();
	}

	public void shutdown() {
		autosaveRunning.set(false);
		saveAll();
	}

	// -------------------- persistence helpers --------------------

	private void ensureDataDir() {
		File dir=new File(DATA_DIR);
		if(!dir.exists()) dir.mkdirs();
	}

	@SuppressWarnings("unchecked")
	private void loadAll() {
		// books
		try (ObjectInputStream ois=new ObjectInputStream(new FileInputStream(BOOK_FILE))) {
			Object obj=ois.readObject();
			if(obj instanceof Map) books=(Map<Integer, Book>) obj;
		} catch(Exception ignored) { }

		// members
		try (ObjectInputStream ois=new ObjectInputStream(new FileInputStream(MEMBER_FILE))) {
			Object obj=ois.readObject();
			if(obj instanceof Map) members=(Map<Integer, Member>) obj;
		} catch(Exception ignored) { }

		// transactions
		try (ObjectInputStream ois=new ObjectInputStream(new FileInputStream(TRANS_FILE))) {
			Object obj=ois.readObject();
			if(obj instanceof Map) transactions=(Map<Integer, Transaction>) obj;
		} catch(Exception ignored) { }

		try (ObjectInputStream ois=new ObjectInputStream(new FileInputStream(META_FILE))) {
			nextBookId=ois.readInt();
			nextMemberId=ois.readInt();
			nextTransactionId=ois.readInt();
		} catch(Exception ignored) { }
	}
	private synchronized void saveBooks() {
		try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(BOOK_FILE))) {
			oos.writeObject(books);
		} catch(Exception e) {
			System.err.println("saveBooks error: "+e.getMessage());
		}
	}
	private synchronized void saveMembers() {
		try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(MEMBER_FILE))) {
			oos.writeObject(members);
		} catch(Exception e) {
			System.err.println("saveMembers error: "+e.getMessage());
		}
	}
	private synchronized void saveTransactions() {
		try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(TRANS_FILE))) {
			oos.writeObject(transactions);
		} catch(Exception e) {
			System.err.println("saveTransactions error: "+e.getMessage());
		}
	}
	private synchronized void saveMeta() {
		try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(META_FILE))) {
			oos.writeInt(nextBookId);
			oos.writeInt(nextMemberId);
			oos.writeInt(nextTransactionId);
		} catch(Exception e) {
			System.err.println("saveMeta error: "+e.getMessage());
		}
	}
	private synchronized void saveAll() {
		saveBooks();
		saveMembers();
		saveTransactions();
		saveMeta();
	}
	public synchronized void saveAllInternal() { saveAll(); }

	// -------------------- utilities --------------------
	private int safeGetCopies(Book b) {
		try { return b.getCopies(); } catch(Exception e) { return 0; }
	}
	// seed sample data
	public synchronized void seedSampleData() {
		if(!books.isEmpty()||!members.isEmpty()) return;
		addBook("Clean Code", "Robert C. Martin");
		addBook("Introduction to Algorithms", "Cormen et al.");
		addMember("Alice");
		addMember("Bob");
	}
}