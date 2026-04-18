package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry, String userName){
        try{
            User user = userService.findByUserName(userName); //user find kiya
            journalEntry.setDate(LocalDateTime.now()); //journal entry mein time save kiya
            JournalEntry saved = journalEntryRepository.save(journalEntry); //journal_entries collection mein journal entry ko save kiya and then uss entry ko ek Naye variable mein store kiya which is of the type "Jounral entry"
            user.getJournalEntries().add(saved); //user entity ke journal entries ki field mein jo list hai, currently khali thi. usme journal entry saved ko add kiya
            userService.saveEntry(user); //finally user ko save kiya in the "users" collection
        }
        catch(Exception e){

            log.error("Exception ",e);
        }

    }
    //overloaded method especially made for PutMapping as there is no need of username
    public void saveEntry(JournalEntry journalEntry){
        try{

            journalEntryRepository.save(journalEntry);

        }
        catch(Exception e){

            log.error("Exception ",e);
        }

    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepository.findById(id);
    }

    public void deleteById(ObjectId id, String userName){ //passed id of journal entry and username as parameter
        User user = userService.findByUserName(userName); //fetched the user object if present
        user.getJournalEntries().removeIf(x -> x.getId().equals(id)); //removed the reference of journal entry stored in the journal entries field list
        userService.saveEntry(user); //deleted the journal entry from journey_entries collection
        journalEntryRepository.deleteById(id);
    }
}
