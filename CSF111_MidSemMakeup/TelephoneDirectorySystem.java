/**
 * PLEASE DO NOT FORGET TO FILL IN THE FOLLOWING FIELDS:
 * Name: [Your Name]
 * ID Number: [Your ID Number]
 * Lab Number: [Your Lab Number]
 * System Number: [Your System Number]
 */

import java.util.*;

interface ContactStatistics {
    int getContactCountByCity(String city);
    int getCallsByContact(Contact contact);
}

class Contact {
    // TODO: Q1 (A)
    //TODO: fix the initialization to run the code.
    // TODO: Adapt the initialization of the final variables to match the constructor code [01 Point]
    private final String contactID = null;
    private String name;
    private final List<String> phoneNumbers = null;

    public Contact(String contactID, String name, String... phones) {

        // TODO: Q1 (B) initialize fields, add provided phones  [02 Points]

    }

    // TODO: Q1  (C) Adapt the code of the following getter methods [01 Point]
    public String getContactID() { return null; /* TODO */ }
    public String getName() { return null; /* TODO */}
    public List<String> getPhoneNumbers() { return null; /* TODO */ }

    public void setName(String newName) { this.name = newName; }

    public boolean addPhone(String phone) {
        // TODO: Q1 (D)
        // TODO Add phone only if not duplicate [01 Point]
        return false;
    }

    public boolean removePhone(String phone) {
        // TODO: Q1 (E)
        // TODO Remove phone [01 Point]
        return false;
    }

    //TODO: Q1 (F) : equals & hashCode based on contactID [03 Points]
    @Override
    public boolean equals(Object o) {
        // TODO Q1 (F)
        return false;
    }

    @Override
    public int hashCode() {
        // TODO part of Q1 (F)
        return 0;
    }

    @Override
    public String toString() {
        // TODO Q1 (G) return readable string [01 Point]

        return "";
    }
}

public class TelephoneDirectorySystem implements ContactStatistics {

    // TODO  Q1 (H) initialize [01 Points]
    private Map<String, List<Contact>> cityContacts = null;
    private Map<Contact, Integer> callRecords = null;

    public TelephoneDirectorySystem() {
        // empty constructor
    }

    // TODO Q2. addContact(city, contact) [05 Points]
    // - create city list if absent
    // - add contact only if not present
    // Hint: Add contact to city; if contact exists (same ID) update it; avoid duplicates
    public void addContact(String city, Contact contact) {
        // TODO Write code for Q2.
    }

    // TODO Q3. removeContact(city, contact) [05 Points]
    // Remove contact from a city
    public boolean removeContact(String city, Contact contact) {
        // TODO remove and return true if removed
        // TODO Write code for Q3.
        // Adapt the return type if required.
        return false;
    }

    // TODO Q4. Add phone number to an existing contact (found by contactID) [02 Points]
    public boolean addPhoneToContact(String contactID, String phone) {
        // TODO Write code for Q4.
        // Adapt the return type if required.
        return false;
    }

    // TODO Q5. Update phone: replace oldNumber with newNumber for contactID [03 Points]
    public boolean updatePhone(String contactID, String oldNumber, String newNumber) {
        // TODO Write code for Q5.
        // Adapt the return type if required.
        return false;
    }

    // TODO Q6. Remove a phone; if it's last phone, remove the contact [05 Points]
    public boolean removePhoneFromContact(String contactID, String phone) {
        // TODO Write code for Q6.
        // Adapt the return type if required.
        return false;
    }

    // TODO Q7. Record a call (increment count) [01 Point]
    public void recordCall(Contact contact) {
        // TODO Write code for Q7.
    }

    // TODO Q8. Return a copy of contacts in a city sorted manually by name (bubble sort) [05 Points]
    public List<Contact> getContactsByCity(String city) {
        // TODO Write code for Q8.
        // Adapt the return type if required.
        return null;
    }

    // TODO Q9. Find most active caller (manual max) [02 Points]
    public Contact findMostActiveCaller() {
        // TODO Write code for Q9.
        // Adapt the return type if required.
        return null;
    }

    // TODO Q10. Implement ContactStatistics methods [02 Points]
    @Override
    public int getContactCountByCity(String city) {
        // TODO Write code for Q10.
        // Adapt the return type if required.
        return 0;
    }

    @Override
    public int getCallsByContact(Contact contact) {
        // TODO Write code for Q10.
        // Adapt the return type if required.
        return 0;
    }

    // TODO Q11. Provide an anonymous implementation (demonstrate abstraction) [05 Points]
    public ContactStatistics getStatisticsProvider() {
        // TODO Write code for Q11.
        // Adapt the return type if required.
        return null;
    }

    // TODO Q12. Average calls per contact (no Stream API) — use lambda  [08 Points]
    public double getAverageCallsPerContact() {
        // TODO Write code for Q12.
        // Adapt the return type if required.
        return 0;
    }

    // TODO Q13. Find most frequent city (manual) [05 Points]
    public String getMostFrequentCity() {
        // TODO Write code for Q13.
        // Adapt the return type if required.
        return null;
    }

    // TODO Q14. Search contacts across all cities using a predicate (lambda) [07 Points]
    public List<Contact> searchContactsByName(String partial) {
        // TODO Write code for Q14.
        // Adapt the return type if required.
        return null;

    }

    // Pretty-print directory
    public void printDirectory() {
        if (cityContacts.isEmpty()) {
            System.out.println("Directory is empty.");
            return;
        }
        System.out.println("----- TELEPHONE DIRECTORY -----");
        // print cities in sorted order (manual sort of keys)
        List<String> cities = new ArrayList<>(cityContacts.keySet());
        // manual sort cities
        for (int i = 0; i < cities.size() - 1; i++) {
            for (int j = 0; j < cities.size() - i - 1; j++) {
                if (cities.get(j).compareToIgnoreCase(cities.get(j + 1)) > 0) {
                    String tmp = cities.get(j);
                    cities.set(j, cities.get(j + 1));
                    cities.set(j + 1, tmp);
                }
            }
        }
        for (String city : cities) {
            System.out.println("City: " + city);
            List<Contact> list = getContactsByCity(city);
            for (Contact c : list) {
                System.out.println("  " + c);
            }
        }
        System.out.println("-------------------------------");
    }

    // MAIN — demonstration & quick tests
    // TODO Check the output with the provided sample output -- no changes to be done here
    public static void main(String[] args) {
        TelephoneDirectorySystem sys = new TelephoneDirectorySystem();

        Contact a = new Contact("C001", "Ram", "9123456789", "9123456788");
        Contact b = new Contact("C002", "Rakesh", "8765432111");
        Contact c = new Contact("C003", "John", "7778889990", "9998889991");

        sys.addContact("Delhi", a);
        sys.addContact("Delhi", b);
        sys.addContact("Mumbai", c);

        // record calls
        sys.recordCall(a); sys.recordCall(a); // Ram: 2
        sys.recordCall(b);                    // Rakesh: 1
        for (int i = 0; i < 5; i++) sys.recordCall(c); // John: 5

        // display
        sys.printDirectory();

        // stats
        System.out.println("Most active: " + sys.findMostActiveCaller());
        System.out.println("Average calls per contact: " + sys.getAverageCallsPerContact());
        System.out.println("Most frequent city: " + sys.getMostFrequentCity());

        // search
        System.out.println("Search 'oh': " + sys.searchContactsByName("oh"));

        // provider (anonymous class)
        ContactStatistics stats = sys.getStatisticsProvider();
        System.out.println("Contacts in Delhi: " + stats.getContactCountByCity("Delhi"));
        System.out.println("Calls for Ram: " + stats.getCallsByContact(a));
        System.out.println("Calls for Rakesh: " + stats.getCallsByContact(b));
        System.out.println("Calls for John: " + stats.getCallsByContact(c));

        // display
        System.out.println("Removing Rakesh");
        sys.removeContact("Delhi",b);
        System.out.println("Printing Updated Telephone Directory");
        sys.printDirectory();

        System.out.println("Removing a phone number of john");
        sys.removePhoneFromContact("C003","7778889990");
        System.out.println("Printing Updated Telephone Directory");
        sys.printDirectory();

        System.out.println("Removing the second phone number of john");
        sys.removePhoneFromContact("C003","9998889991");
        System.out.println("Printing Updated Telephone Directory");
        sys.printDirectory();
    }
}
