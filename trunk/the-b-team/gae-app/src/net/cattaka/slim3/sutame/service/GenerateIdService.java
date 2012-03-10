package net.cattaka.slim3.sutame.service;

import java.util.Random;
import org.slim3.datastore.Datastore;
import com.google.appengine.api.datastore.DatastoreTimeoutException;


public class GenerateIdService {
    private static final int MAX_RETRY_COUNT = 100;
    
    private static Random random = new Random(System.currentTimeMillis());
    
    public static Long generateId(String uniqueIndexName) throws DatastoreTimeoutException {
        if (uniqueIndexName == null) {
            throw new NullPointerException("uniqueIndexName is null.");
        }
        Long id = null;
        for (int i=0;i<MAX_RETRY_COUNT;i++) {
            int newId = Math.abs(random.nextInt());
            String tmpId = String.format("%d", newId);
            if (Datastore.putUniqueValue(uniqueIndexName, tmpId)) {
                id = Long.valueOf(newId);
                break;
            }
        }
        if (id == null) {
            throw new DatastoreTimeoutException(String.format("Could not generate id(%s).", uniqueIndexName));
        }
        return id;
    }
    public static void deleteId(String uniqueIndexName, Long value) {
        String tmpId = String.format("%d", value);
        Datastore.deleteUniqueValue(uniqueIndexName, tmpId);
    }
}
