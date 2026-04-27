package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

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
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try {
            User user = userService.findByUserName(userName); //user find kiya
            journalEntry.setDate(LocalDateTime.now()); //journal entry mein time save kiya
            JournalEntry saved = journalEntryRepository.save(journalEntry); //journal_entries collection mein journal entry ko save kiya and then uss entry ko ek Naye variable mein store kiya which is of the type "Jounral entry"
            user.getJournalEntries().add(saved); //user entity ke journal entries ki field mein jo list hai, currently khali thi. usme journal entry saved ko add kiya
            userService.saveUser(user); //finally user ko save kiya in the "users" collection
        } catch (Exception e) {

            log.error("Exception ", e);
        }

    }

    //overloaded method especially made for PutMapping as there is no need of username
    @Transactional
    public void saveEntry(JournalEntry journalEntry) {
        try {

            journalEntryRepository.save(journalEntry);

        } catch (Exception e) {

            log.error("Exception ", e);
        }

    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName) { //passed id of journal entry and username as parameter
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName); //fetched the user object if present
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id)); //removed the reference of journal entry stored in the journal entries field list

            if (removed) {
                userService.saveUser(user); //deleted the journal entry from journey_entries collection
                journalEntryRepository.deleteById(id); // we first delete the reference stored in the list of journal entries in users, then in this step we delete the actual journal entry from journal_entries collection
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occured while trying to delete the journal entry");
        }
        return removed;

    }


}
