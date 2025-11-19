public interface ILibrary {
    // UI Operations
    void addBook(String title,String author);
    void addMember(String name);
    String issueBook(int bookId,int memberId);
    String returnBook(int bookId,int memberId);
    void showAllBooks();
    void showAllMembers();
    // Background autosave support
    void autoSave();
    // Utility operations
    void seedSampleData();
    void shutdown();
}
