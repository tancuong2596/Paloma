package cit.edu.paloma.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by charlie on 2/27/17.
 */

public class FirebaseUtils {
    public static DatabaseReference getRootRef() {
        return FirebaseDatabase.getInstance().getReference();
    }


}
