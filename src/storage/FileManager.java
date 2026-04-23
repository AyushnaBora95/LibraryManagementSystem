import java.io.*;
import java.util.*;

public class FileManager {

    private static final String BOOKS_FILE = "data/books.ser";
    private static final String MEMBERS_FILE = "data/members.ser";
    private static final String TRANSACTIONS_FILE = "data/transactions.ser";

    // Ensure data directory exists
    private static void ensureDataDir() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
    }

    // Save helpers
    public static void saveBooks(Map<Integer, Book> books) {
        ensureDataDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BOOKS_FILE))) {
            out.writeObject(books);
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    public static Map<Integer, Book> loadBooks() {
        ensureDataDir();
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BOOKS_FILE))) {
            Object obj = in.readObject();
            return (Map<Integer, Book>) obj;
        } catch (Exception e) {
            System.err.println("Error loading books (returning empty): " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveMembers(Map<Integer, Member> members) {
        ensureDataDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(MEMBERS_FILE))) {
            out.writeObject(members);
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }

    public static Map<Integer, Member> loadMembers() {
        ensureDataDir();
        File f = new File(MEMBERS_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(MEMBERS_FILE))) {
            Object obj = in.readObject();
            return (Map<Integer, Member>) obj;
        } catch (Exception e) {
            System.err.println("Error loading members (returning empty): " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveTransactions(List<Transaction> transactions) {
        ensureDataDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            out.writeObject(transactions);
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    public static List<Transaction> loadTransactions() {
        ensureDataDir();
        File f = new File(TRANSACTIONS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(TRANSACTIONS_FILE))) {
            Object obj = in.readObject();
            return (List<Transaction>) obj;
        } catch (Exception e) {
            System.err.println("Error loading transactions (returning empty): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save everything
    public static void saveAll(Map<Integer, Book> books, Map<Integer, Member> members, List<Transaction> transactions) {
        saveBooks(books);
        saveMembers(members);
        saveTransactions(transactions);
    }

    // Load everything
    public static DataBundle loadAll() {
        Map<Integer, Book> books = loadBooks();
        Map<Integer, Member> members = loadMembers();
        List<Transaction> transactions = loadTransactions();
        return new DataBundle(books, members, transactions);
    }

    // small container
    public static class DataBundle {
        public final Map<Integer, Book> books;
        public final Map<Integer, Member> members;
        public final List<Transaction> transactions;

        public DataBundle(Map<Integer, Book> books, Map<Integer, Member> members, List<Transaction> transactions) {
            this.books = books;
            this.members = members;
            this.transactions = transactions;
        }
    }
}
