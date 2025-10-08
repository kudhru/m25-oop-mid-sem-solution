# CS F213 OOP Mid-Semester Exam 2025-2026 [BITS Pilani]
Max Marks: 75

Max Time: 90 minutes

Closed Book Test

## University Classroom Management and Booking

This programming task models classroom management and single‑day booking at a university. You are given a complete starter code with all classes and methods declared. Your job is to implement the methods marked as `Student Task` in code comments.

### Domain and constraints
- Buildings are fixed to the following strings: `LTC`, `NAB`, `FD1`, `FD2`, `FD3`.
- Room number format must match building‑specific prefixes (length must be exactly 4):
  - `LTC`: room numbers start with `51` (e.g., 5101, 5102, ...)
  - `NAB`: room numbers start with `61`
  - `FD1`: room numbers start with `11`
  - `FD2`: room numbers start with `21`
  - `FD3`: room numbers start with `31`
- Room capacity must be in {50, 100, 150, 200, 250, 300, 350, 400}.
- Each room records if a projector is available and if internet connectivity is available (true/false for each).
- Booking model: single day, hour slots are integers in {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}. A room can be booked for any hour that is not already booked.

### Operations to support
- Manage rooms: add, remove, get room, compare rooms, and filter rooms.
- Manage bookings: book a room for a requested hour, check availability, and list available rooms by hour with optional filters.
- Validations must be applied; on failure, methods return a specific error code (`ErrorCode`) instead of throwing exceptions or returning strings.

### Project layout (root‑level, no packages)
```
RoomsService.java         # Main file containing all the code and the main method
ProblemDescription.md     # You are here
```

### What is already implemented
- Building and ErrorCode classes.
- RoomsService.Room class with fields, constructor, getters/setters, and declared equals/hashCode/toString (students will complete equals as a task).
- Helper methods isValidCapacity, isValidRoomNumberForBuilding, and getRoomKey for validations and keys.
- Booking storage Map<String, Set<Integer>> bookingsByRoomKey for single-day hour bookings.
- A demo `main` method inside `RoomsService` that shows expected usage and calls each method to be implemented.

### Your tasks
Implement only those methods where we have mentioned `Student Task` in the comments. Do not change method signatures or add new fields. Return only the specified values; do not use exceptions or print from methods.

There are 10 `Student Task` methods in total, numbered for clarity.

1) RoomsService.Room.compareTo — Define the natural ordering of rooms by first comparing capacity in ascending order and, when capacities are equal, comparing room numbers lexicographically so that default sorting behaves predictably.

2) RoomsService.Room.equals — Determine room equality strictly by building and room number while handling nulls and non-room objects, so that logically identical rooms compare as equal.

3) RoomsService.Room.Comparators.BY_BUILDING_THEN_ROOM — Implement a lambda-based comparator that orders rooms by Building name lexicographically and, if building names match, then by room number lexicographically, so building-wise listings are consistent.

4) RoomsService.addRoom(Room room) — Validate the building/room number format (length 4 and correct prefix), ensure capacity is within the allowed multiples, check for uniqueness by (building, roomNumber), and add the room (with its booking record) when valid, returning an appropriate ErrorCode.

5) RoomsService.removeRoom(Building building, String roomNumber) — Remove the specified room and its booking calendar when present, or return a not-found ErrorCode when it does not exist.

6) RoomsService.getRoom(Building building, String roomNumber) — Look up and return the room instance that matches the given building and room number, or return null if no such room exists.

7) RoomsService.filterRooms(Integer minCapacity, Building building, Boolean projectorRequired, Boolean internetRequired) — Use a single lambda predicate (no Streams) to return a new list of rooms that satisfy all non‑null filters such as minimum capacity, selected building, and required features.

8) RoomsService.bookRoom(Building building, String roomNumber, int hour, int minRequiredCapacity, boolean requireProjector, boolean requireInternet) — Validate the hour, existence, capacity threshold, and required features and ensure the slot is not already booked; on success record the booking and return the precise ErrorCode for the outcome.

9) RoomsService.isAvailable(Building building, String roomNumber, int hour) — Report availability for a particular hour as an ErrorCode, returning OK when free, ALREADY_BOOKED when taken, and specific error codes for invalid inputs.

10) RoomsService.getAvailableRoomsByHour(Building building, int hour, Integer minCapacity, Boolean projectorRequired, Boolean internetRequired) — First filter rooms using the provided options and then include only those that are free at the given hour, returning a new list without modifying internal state.

### Constraints
- Use only standard Java (no external libraries)
- Do not add new fields or change method/class signatures
- Prefer `List`, `Set`, and `Map` with generics; do not use raw types

### How to run
From the project root:
```
javac RoomsService.java
java RoomsService
```

You will see methods annotated as `Student Task` left for you to implement. For each such method, follow the descriptions above and the inline comments in code, then run `RoomsService` to verify the behavior.


