package persistence;

import java.io.*;
import java.util.Collection;

/**
 * Handles saving and loading of Books, Members, and Transactions using Serialization.
 * (Member 3 - File Handling and Serialization)
 */
public class PersistenceManager {

    public <T extends Serializable> void saveData(Collection<T> data, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
            System.out.println("Data saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving data to " + fileName);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> Collection<T> loadData(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Collection<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + fileName);
            return null;
        }
    }
}
