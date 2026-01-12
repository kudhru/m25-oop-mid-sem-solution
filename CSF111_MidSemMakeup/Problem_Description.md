# CS F213 OOP Mid-Semester Make-up 2025-2026 [BITS Pilani]

## Telephone Directory Management System 
You are required to design and implement a Telephone Directory Management System that stores contact information city-wise and provides various analytical and search functionalities. The system must demonstrate core Object-Oriented Programming (OOP) concepts, use Collections, Interfaces, Lambda expressions, Generics, and implement custom logic such as manual sorting and call analytics.

### Overview
A telecom service provider maintains a centralized telephone directory. This is used to organize subscriber information based on different cities. Each contact may have multiple phone numbers, reflecting modern usage patterns (personal, work, alternate numbers, etc.). The system stores contacts city-wise and keeps track of call activity for analytics. Admin users should be able to add, update, search, sort, and analyze the directory. Your task is to implement the TelephoneDirectorySystem class, a Contact class using OOP principles, and a ContactStatistics interface to retrieve analytics.

### Fields
1. private Map<String, List<Contact>> cityContacts
Maintains a mapping of city names to list of contacts in that city.

2. private Map<Contact, Integer> callRecords
Stores call count for each contact (for analytics).

### Class: Contact
Represents a subscriber in the directory.

### Interface : ContactStatistics
Provides an abstraction for analytics operations.

### Core Methods
1. addContact(String city, Contact contact)
    Adds a new contact to a city.
2. updatePhoneNumber(String contactID, String oldNum, String newNum)
    Updates ANY phone number belonging to a contact.
3. findMostActiveCaller()
    Returns the contact with the highest call count.
4. sortContactsByName(String city)
    Sorts contacts manually (no Collections.sort).
5. searchContactsByName(String partialName)
    Filters contacts across all cities.
6. getContactStatistics()
    Uses lambda expression to implement:
        getContactCountByCity(city)
        getCallsByContact(contact)
7. getAverageCallsPerContact()
    Computes:
        (total call count) / (number of contacts)
8. getMostFrequentCity()
    Finds city with maximum contacts.

### Your Tasks

Implement only those methods where we have mentioned TODO in the comments. Do not change method signatures or add new fields (except where specifically instructed by a new task). Follow the instructions in the code comments for each task. Write your own logic to arrive at the sample output as given in the SampleOutput.txt. Flexibility is given to be creative except violating the constraints given. For example, if a question indicates to use lambda, then please use a proper lambda expression in your custom logic. For most of the questions, you will need to adapt the return value of the methods and it is indicated in the code. (Do not adapt the return type.)

There are 14 Student Tasks in total: (numbered as Q1 to Q14.) 

**Q1) Code Fixes** — [10 Points] - Fix the field initialization, constructor logic, getter/setter methods, equals(), hashashCode(), toString() methods.
**Q2) addContact(String city, Contact contact)** - [05 Points] — Implement your custom logic to add a Contact to the telephone directory. Follow the hints given. 
**Q3) removeContact(String city, Contact contact)** - [05 Points] — Implement your custom logic to remove a Contact from the telephone directory.
**Q4) addPhoneToContact(String contactID, String phone)** - [02 Points] — Implement your custom logic to add a new phone number to an existing contact via the contactID. 
**Q5)updatePhone(String contactID, String oldNumber, String newNumber)** - [03 Points] — Implement your custom logic to update an existing contact number using the contactID.
**Q6)removePhoneFromContact(String contactID, String phone)** - [05 Points] — Implement your custom logic to delete a phone number for a contactID and remove the contact if it's last phone number.
**Q7)recordCall(Contact contact)** - [01 Point] — Implement your custom logic to increment the number of calls made.
**Q8)getContactsByCity(String city)** - [05 Points] — Implement your custom logic to a copy of contacts in a city sorted manually by name. Implement a bubble sort to achieve this.
**Q9)findMostActiveCaller()** - [02 Points] — Implement your custom logic to return the most active contact by checking the activity from callRecords Collection.
**Q10)getContactCountByCity(String city) and getCallsByContact(Contact contact)** - [02 Points] — Implement your custom logic to return a contact for a city. For the second method, write your code to return the number of calls made by a contact from the callRecords.
**Q11)getStatisticsProvider()** - [05 Points] — Implement your custom logic to provide an anonymous implementation of the methods getContactCountByCity(String city) and getCallsByContact(Contact contact).
**Q12)getAverageCallsPerContact()** - [08 Points] — Implement your custom logic using lambda to compute the average calls per contact.
**Q13)getMostFrequentCity()** - [05 Points] — Implement your custom logic to find the city with the highest number of contacts.
**Q14)searchContactsByName(String partial)** - [07 Points] — Implement your custom logic using lambda to search for a contact using the partial string.


### Constraints

- Use only standard Java (no external libraries)
- Do not add new fields or change method/class signatures unless the task specifically requires adding functionality that depends on it

### How to Run

From the project root:
```
javac TelephoneDirectorySystem.java
java TelephoneDirectorySystem
```
