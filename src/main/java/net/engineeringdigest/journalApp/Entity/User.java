package net.engineeringdigest.journalApp.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//POJO - plain old java object
@Document(collection="users")
@Data
public class User {

    @Id
    private ObjectId id;

    @Indexed(unique=true)
    @NonNull
    private String userName;

    @NonNull
    private String password;

    @DBRef //user reference rkhega of fields(documents ) in journal_entries collection
    private List<JournalEntry> journalEntries = new ArrayList<>();

    private List<String> roles;


}
