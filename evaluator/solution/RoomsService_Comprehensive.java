/**
 * PLEASE DO NOT FORGET TO FILL IN THE FOLLOWING FIELDS:
 * Name:  
 * ID Number: 
 * Lab Number: 
 * System Number: 
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
final class Building {
  private final String name;

  private Building(String name) {
    this.name = name;
  }

  public static final Building LTC = new Building("LTC");
  public static final Building NAB = new Building("NAB");
  public static final Building FD1 = new Building("FD1");
  public static final Building FD2 = new Building("FD2");
  public static final Building FD3 = new Building("FD3");

  public String getName() { return name; }

  public String toString() { return name; }
}

final class ErrorCode {
  private final String name;

  private ErrorCode(String name) { this.name = name; }

  public static final ErrorCode OK = new ErrorCode("OK");
  public static final ErrorCode ROOM_NULL = new ErrorCode("ROOM_NULL");
  public static final ErrorCode INVALID_ROOM_NUMBER = new ErrorCode("INVALID_ROOM_NUMBER");
  public static final ErrorCode INVALID_CAPACITY = new ErrorCode("INVALID_CAPACITY");
  public static final ErrorCode DUPLICATE_ROOM = new ErrorCode("DUPLICATE_ROOM");
  public static final ErrorCode ROOM_NOT_FOUND = new ErrorCode("ROOM_NOT_FOUND");
  public static final ErrorCode INVALID_HOUR = new ErrorCode("INVALID_HOUR");
  public static final ErrorCode ALREADY_BOOKED = new ErrorCode("ALREADY_BOOKED");
  public static final ErrorCode INSUFFICIENT_CAPACITY = new ErrorCode("INSUFFICIENT_CAPACITY");
  public static final ErrorCode PROJECTOR_NOT_AVAILABLE = new ErrorCode("PROJECTOR_NOT_AVAILABLE");
  public static final ErrorCode INTERNET_NOT_AVAILABLE = new ErrorCode("INTERNET_NOT_AVAILABLE");
  public static final ErrorCode NOT_BOOKED = new ErrorCode("NOT_BOOKED");

  public String toString() { return name; }
}

public class RoomsService {

  public static class Room implements Comparable<Room> {
    private final Building building; // {LTC, NAB, FD1, FD2, FD3}
    private final String roomNumber; // e.g., 5101, 6101, 1101, 2101, 3101, etc.
    private int capacity;            // {50, 100, 150, 200, 250, 300, 350, 400}
    private boolean projectorAvailable; // true if projector is available, false otherwise
    private boolean internetAvailable; // true if internet is available, false otherwise

    public Room(Building building, String roomNumber, int capacity, boolean projectorAvailable, boolean internetAvailable) {
      this.building = building;
      this.roomNumber = roomNumber;
      this.capacity = capacity;
      this.projectorAvailable = projectorAvailable;
      this.internetAvailable = internetAvailable;
    }

    public Building getBuilding() { return building; }
    public String getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
    public boolean isProjectorAvailable() { return projectorAvailable; }
    public boolean isInternetAvailable() { return internetAvailable; }

    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setProjectorAvailable(boolean projectorAvailable) { this.projectorAvailable = projectorAvailable; }
    public void setInternetAvailable(boolean internetAvailable) { this.internetAvailable = internetAvailable; }

    /*
     * Student Task 1: Implement the compareTo method for comparing rooms.
     * First, the comparison is done by capacity (ascending order).
     * If the capacities are equal, the comparison is done by room number (lexicographical order).
     * Make sure to handle null objects.
     * Expected Time for completion: 5 minutes
     * [6 marks]
     */
    public int compareTo(Room other) {
      if (other == null) return 1;
      int cap = Integer.compare(this.capacity, other.capacity); // first compare capacity
      if (cap != 0) return cap;
      return this.roomNumber.compareTo(other.roomNumber); // if capacity is equal, compare room numbers
    }

    /*
     * Student Task 2: Implement the equals method for comparing rooms.
     * The rooms are equal if they have the same building and room number.
     * Make sure to handle null objects and objects of other types.
     * Expected Time for completion: 5 minutes
     * [6 marks]
     */
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Room room = (Room) o;
      return building == room.building && roomNumber.equals(room.roomNumber);
    }

    public int hashCode() {
      return java.util.Objects.hash(building, roomNumber);
    }

    public String toString() {
      return "Room{" +
        "building=" + building +
        ", roomNumber='" + roomNumber + '\'' +
        ", capacity=" + capacity +
        ", projectorAvailable=" + projectorAvailable +
        ", internetAvailable=" + internetAvailable +
        '}';
    }

    public static final class Comparators {
      private Comparators() {}

      /**
       * Student Task 3: Implement this Comparator using a lambda expression.
       * First, compare by building name lexicographically. 
       * Then, if the building names are equal, compare by room number lexicographically.
       * Note: Please do not use the anonymous inner class syntax for this. 
       * Strictly, use a lambda expression.
       * Make sure to handle null objects.
       * Expected Time for completion: 5 minutes
       * [6 marks]
       */
      public static final java.util.Comparator<Room> BY_BUILDING_THEN_ROOM = (a, b) -> {
        if (a == b) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        int byBuilding = a.getBuilding().getName().compareTo(b.getBuilding().getName());
        if (byBuilding != 0) return byBuilding;
        return a.getRoomNumber().compareTo(b.getRoomNumber());
      };
    }
  }

  private final List<Room> rooms = new ArrayList<>();
  
  /** 
   * Booking calendar: This is a map which contains the booking calendar for each room 
   * i.e. for each room, it contains the hours that are booked for that room.
   * key = room identity string (building + roomNumber), value = set of booked hours
  */
  private final Map<String, Set<Integer>> bookingsByRoomKey = new HashMap<>();

  // Functional interface used for filtering rooms based on certain conditions
  interface RoomPredicate {
    boolean test(Room r);
  }

  public List<Room> getRooms() { return rooms; }

  // This is a helper method to generate a unique key for each room
  // i.e. building + roomNumber
  private static String getRoomKey(Building b, String roomNumber) {
    return b + "#" + roomNumber;
  }

  /**
   * Student Task 4: Add room to the list of rooms
   * If the room is null, return ErrorCode.ROOM_NULL.
   * Use isValidRoomNumberForBuilding and isValidCapacity methods (already implemented) to validate the room.
   * If the building/roomNumber format is invalid, return ErrorCode.INVALID_ROOM_NUMBER.
   * If the capacity is invalid (not in {50, 100, 150, 200, 250, 300, 350, 400}), return ErrorCode.INVALID_CAPACITY.
   * If the room already exists, return ErrorCode.DUPLICATE_ROOM.
   * If all validations pass, add the room to the list of rooms, initialize its bookingsByRoomKey entry, and return ErrorCode.OK;
   * Expected Time for completion: 10 minutes
   * [10 marks]
   */
  public ErrorCode addRoom(Room room) {
    if (room == null) {
      return ErrorCode.ROOM_NULL;
    }
    Building b = room.getBuilding();
    String rn = room.getRoomNumber();
    if (!isValidRoomNumberForBuilding(b, rn)) {
      return ErrorCode.INVALID_ROOM_NUMBER;
    }
    if (!isValidCapacity(room.getCapacity())) {
      return ErrorCode.INVALID_CAPACITY;
    }
    // uniqueness
    for (Room r : rooms) {
      if (r.getBuilding() == b && r.getRoomNumber().equals(rn)) {
        return ErrorCode.DUPLICATE_ROOM;
      }
    }
    rooms.add(room);
    // init booking map entry
    bookingsByRoomKey.putIfAbsent(getRoomKey(b, rn), new HashSet<>());
    return ErrorCode.OK;
  }

  /**
   * Student Task 5: Remove room
   * If the room is found, remove it from the list of rooms and the bookingsByRoomKey map, and return ErrorCode.OK.
   * If the room is not found, return ErrorCode.ROOM_NOT_FOUND.
   * Use the getRoomKey method (already implemented) to get the key for the room.
   * Expected Time for completion: 7 minutes
   * [8 marks]
   */
  public ErrorCode removeRoom(Building building, String roomNumber) {
    for (int i = 0; i < rooms.size(); i++) {
      Room r = rooms.get(i);
      if (r.getBuilding() == building && r.getRoomNumber().equals(roomNumber)) {
        rooms.remove(i);
        bookingsByRoomKey.remove(getRoomKey(building, roomNumber));
        return ErrorCode.OK;
      }
    }
    return ErrorCode.ROOM_NOT_FOUND;
  }

  /**
   * Student Task 6: Get room
   * If the room is found (matching the given building and room number), returns the room.
   * If the room is not found, returns null.
   * Expected Time for completion: 5 minutes
   * [5 marks]
   */
  public Room getRoom(Building building, String roomNumber) {
    for (Room r : rooms) {
      if (r.getBuilding() == building && r.getRoomNumber().equals(roomNumber)) {
        return r;
      }
    }
    return null;
  }

  /**
   * Student Task 7: Filter rooms
   * Implement the RoomPredicate interface (strictly using a lambda expression). This predicate should check all provided conditions (capacity, building, projector, internet) for a given room and return true if all conditions are satisfied. 
   * The predicate should return true for a room if all the below conditions are satisfied:
   * - Capacity of the room is greater than or equal to minCapacity
   * - Building of the room is equal to the given building
   * - Projector is available in the room (if projectorRequired is true)
   * - Internet is available in the room (if internetRequired is true)
   * Rest of the implementation is already provided.
   * Expected Time for completion: 7 minutes
   * [8 marks]
   */
  public List<Room> filterRooms(Integer minCapacity, Building building, Boolean projectorRequired, Boolean internetRequired) {
    RoomPredicate predicate = r ->
      (minCapacity == null || r.getCapacity() >= minCapacity) &&
      (building == null || r.getBuilding() == building) &&
      (projectorRequired == null || !projectorRequired || r.isProjectorAvailable()) &&
      (internetRequired == null || !internetRequired || r.isInternetAvailable());

    List<Room> out = new ArrayList<>();
    for (Room r : rooms) {
      if (predicate.test(r)) out.add(r);
    }
    return out;
  }

  /**
   * Student Task 8: Book room for an hour
   * If the hour is invalid (not in {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}), return ErrorCode.INVALID_HOUR.
   * If the room does not exist, return ErrorCode.ROOM_NOT_FOUND.
   * If the room's capacity is less than minRequiredCapacity, return ErrorCode.INSUFFICIENT_CAPACITY.
   * If the projector is required and not available, return ErrorCode.PROJECTOR_NOT_AVAILABLE.
   * If the internet is required and not available, return ErrorCode.INTERNET_NOT_AVAILABLE.
   * If the hour is already booked, return ErrorCode.ALREADY_BOOKED.
   * Else, update the bookingsByRoomKey entry for the room and return ErrorCode.OK;
   * Expected Time for completion: 10 minutes
   * [10 marks]
   */
  public ErrorCode bookRoom(Building building, String roomNumber, int hour, int minRequiredCapacity, boolean requireProjector, boolean requireInternet) {
    if (hour < 1 || hour > 10) {
      return ErrorCode.INVALID_HOUR;
    }
    Room room = getRoom(building, roomNumber);
    if (room == null) {
      return ErrorCode.ROOM_NOT_FOUND;
    }
    if (room.getCapacity() < minRequiredCapacity) {
      return ErrorCode.INSUFFICIENT_CAPACITY;
    }
    if (requireProjector && !room.isProjectorAvailable()) {
      return ErrorCode.PROJECTOR_NOT_AVAILABLE;
    }
    if (requireInternet && !room.isInternetAvailable()) {
      return ErrorCode.INTERNET_NOT_AVAILABLE;
    }
    String k = getRoomKey(building, roomNumber);
    Set<Integer> booked = bookingsByRoomKey.get(k);
    if (booked == null) {
      booked = new HashSet<>();
      bookingsByRoomKey.put(k, booked);
    }
    // Alternative solution for fetching the booked set:
    // Set<Integer> booked = bookingsByRoomKey.computeIfAbsent(k, kk -> new HashSet<>());
    if (booked.contains(hour)) {
      return ErrorCode.ALREADY_BOOKED;
    }
    booked.add(hour);
    return ErrorCode.OK;
  }

  /**
   * Student Task 9: Check availability
   * If the hour is invalid (not in {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}), return ErrorCode.INVALID_HOUR.
   * If the room does not exist in the list of rooms, return ErrorCode.ROOM_NOT_FOUND.
   * If the hour is currently unbooked, return ErrorCode.OK.
   * If the hour is currently booked, return ErrorCode.ALREADY_BOOKED.
   * Expected Time for completion: 7 minutes
   * [8 marks]
   */
  public ErrorCode isAvailable(Building building, String roomNumber, int hour) {
    if (hour < 1 || hour > 10) return ErrorCode.INVALID_HOUR;
    Room room = getRoom(building, roomNumber);
    if (room == null) return ErrorCode.ROOM_NOT_FOUND;
    String k = getRoomKey(building, roomNumber);
    Set<Integer> booked = bookingsByRoomKey.get(k);
    if (booked == null) return ErrorCode.OK; // room exists and no bookings yet
    return booked.contains(hour) ? ErrorCode.ALREADY_BOOKED : ErrorCode.OK;
  }

  /**
   * Student Task 10: Available rooms by hour with optional filters
   * If the hour is invalid (not in {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}), return an empty list.
   * Filter rooms using the filterRooms method based on the input filters (minCapacity, building, projectorRequired, internetRequired).
   * then for each room in the filtered list, check if it is available at the given hour using the isAvailable method.
   * Return the list of rooms that are available at the given hour.
   * Expected Time for completion: 7 minutes
   * [8 marks]
   */
  public List<Room> getAvailableRoomsByHour(Building building, int hour, Integer minCapacity, Boolean projectorRequired, Boolean internetRequired) {
    if (hour < 1 || hour > 10) return new ArrayList<>();
    
    List<Room> filtered = filterRooms(minCapacity, building, projectorRequired, internetRequired);
    List<Room> available = new ArrayList<>();
    
    for (Room room : filtered) {
      if (isAvailable(room.getBuilding(), room.getRoomNumber(), hour) == ErrorCode.OK) {
        available.add(room);
      }
    }
    
    return available;
  }

  // ===== Helper validation functions =====

  boolean isValidCapacity(int capacity) {
    if (capacity < 50 || capacity > 400) return false;
    return capacity % 50 == 0;
  }

  boolean isValidRoomNumberForBuilding(Building b, String roomNumber) {
    if (roomNumber == null || roomNumber.length() != 4) return false;
    String prefix = roomNumber.substring(0, 2);
    if (b == Building.LTC) {
      return "51".equals(prefix);
    } else if (b == Building.NAB) {
      return "61".equals(prefix);
    } else if (b == Building.FD1) {
      return "11".equals(prefix);
    } else if (b == Building.FD2) {
      return "21".equals(prefix);
    } else if (b == Building.FD3) {
      return "31".equals(prefix);
    } else {
      return false;
    }
  }

  // ===== Comprehensive Driver class for testing =====
  public static void main(String[] args) {
    RoomsService service = new RoomsService();

    System.out.println("=== COMPREHENSIVE TESTING SUITE ===");
    System.out.println();

    // ===== TASK 4: addRoom - Comprehensive Testing =====
    System.out.println("=== TASK 4: addRoom Testing ===");
    
    // Test 1: Valid rooms from all buildings
    System.out.println("Test 1: Adding valid rooms from all buildings");
    System.out.println("LTC-5101: " + service.addRoom(new Room(Building.LTC, "5101", 100, true, true)));
    System.out.println("NAB-6101: " + service.addRoom(new Room(Building.NAB, "6101", 200, false, true)));
    System.out.println("FD1-1101: " + service.addRoom(new Room(Building.FD1, "1101", 150, true, false)));
    System.out.println("FD2-2101: " + service.addRoom(new Room(Building.FD2, "2101", 250, true, true)));
    System.out.println("FD3-3101: " + service.addRoom(new Room(Building.FD3, "3101", 300, false, false)));
    
    // Test 2: All valid capacities
    System.out.println("\nTest 2: Testing all valid capacities");
    System.out.println("Capacity 50: " + service.addRoom(new Room(Building.LTC, "5102", 50, true, true)));
    System.out.println("Capacity 350: " + service.addRoom(new Room(Building.LTC, "5103", 350, true, true)));
    System.out.println("Capacity 400: " + service.addRoom(new Room(Building.LTC, "5104", 400, true, true)));
    
    // Test 3: Duplicate room testing
    System.out.println("\nTest 3: Duplicate room testing");
    System.out.println("Duplicate LTC-5101: " + service.addRoom(new Room(Building.LTC, "5101", 100, true, true)));
    System.out.println("Duplicate NAB-6101: " + service.addRoom(new Room(Building.NAB, "6101", 200, false, true)));
    
    // Test 4: Invalid room numbers
    System.out.println("\nTest 4: Invalid room number testing");
    System.out.println("LTC with wrong prefix: " + service.addRoom(new Room(Building.LTC, "4101", 100, true, true)));
    System.out.println("NAB with wrong prefix: " + service.addRoom(new Room(Building.NAB, "7101", 100, true, true)));
    System.out.println("Short room number: " + service.addRoom(new Room(Building.LTC, "511", 100, true, true)));
    System.out.println("Long room number: " + service.addRoom(new Room(Building.LTC, "51011", 100, true, true)));
    
    // Test 5: Invalid capacities
    System.out.println("\nTest 5: Invalid capacity testing");
    System.out.println("Capacity 25: " + service.addRoom(new Room(Building.LTC, "5105", 25, true, true)));
    System.out.println("Capacity 75: " + service.addRoom(new Room(Building.LTC, "5106", 75, true, true)));
    System.out.println("Capacity 450: " + service.addRoom(new Room(Building.LTC, "5107", 450, true, true)));
    System.out.println("Capacity 125: " + service.addRoom(new Room(Building.LTC, "5108", 125, true, true)));
    
    System.out.println("Total rooms after addRoom tests: " + service.getRooms().size());
    System.out.println();

    // ===== TASK 1: compareTo - Comprehensive Testing =====
    System.out.println("=== TASK 1: compareTo Testing ===");
    
    // Test 1: Capacity-based sorting
    System.out.println("Test 1: Capacity-based sorting");
    List<Room> all = new ArrayList<>(service.getRooms());
    Collections.sort(all);
    System.out.println("Rooms sorted by capacity then room number:");
    for (Room room : all) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (capacity: " + room.getCapacity() + ")");
    }
    
    // Test 2: Same capacity, different room numbers
    System.out.println("\nTest 2: Same capacity, different room numbers");
    Room room1 = new Room(Building.LTC, "5109", 100, true, true);
    Room room2 = new Room(Building.LTC, "5110", 100, true, true);
    System.out.println("5109 vs 5110: " + room1.compareTo(room2));
    System.out.println("5110 vs 5109: " + room2.compareTo(room1));
    System.out.println("5109 vs 5109: " + room1.compareTo(room1));
    
    System.out.println();

    // ===== TASK 3: BY_BUILDING_THEN_ROOM Comparator - Comprehensive Testing =====
    System.out.println("=== TASK 3: BY_BUILDING_THEN_ROOM Comparator Testing ===");
    
    // Test 1: Building-based sorting
    System.out.println("Test 1: Building-based sorting");
    all.sort(Room.Comparators.BY_BUILDING_THEN_ROOM);
    System.out.println("Rooms sorted by building then room number:");
    for (Room room : all) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber());
    }
    
    // Test 2: Same building, different room numbers
    System.out.println("\nTest 2: Same building, different room numbers");
    List<Room> ltcRooms = new ArrayList<>();
    for (Room room : all) {
      if (room.getBuilding() == Building.LTC) {
        ltcRooms.add(room);
      }
    }
    ltcRooms.sort(Room.Comparators.BY_BUILDING_THEN_ROOM);
    System.out.println("LTC rooms sorted by room number:");
    for (Room room : ltcRooms) {
      System.out.println("  " + room.getRoomNumber());
    }
    
    System.out.println();

    // ===== TASK 2: equals - Comprehensive Testing =====
    System.out.println("=== TASK 2: equals Testing ===");
    
    // Test 1: Same room instances
    Room rA = service.getRoom(Building.LTC, "5101");
    Room rB = service.getRoom(Building.LTC, "5101");
    System.out.println("Same room from service: " + (rA != null && rA.equals(rB)));
    
    // Test 2: Different room instances, same building and room number
    Room rC = new Room(Building.LTC, "5101", 100, true, true);
    System.out.println("Different instances, same building+room: " + (rA != null && rA.equals(rC)));
    
    // Test 3: Different building, same room number
    Room rD = new Room(Building.NAB, "5101", 100, true, true);
    System.out.println("Different building, same room number: " + (rA != null && rA.equals(rD)));
    
    // Test 4: Same building, different room number
    Room rE = new Room(Building.LTC, "5102", 100, true, true);
    System.out.println("Same building, different room number: " + (rA != null && rA.equals(rE)));
    
    // Test 5: Different capacity, same building and room number
    Room rF = new Room(Building.LTC, "5101", 200, true, true);
    System.out.println("Different capacity, same building+room: " + (rA != null && rA.equals(rF)));
    
    System.out.println();

    // ===== TASK 6: getRoom - Comprehensive Testing =====
    System.out.println("=== TASK 6: getRoom Testing ===");
    
    // Test 1: Existing rooms
    System.out.println("Test 1: Getting existing rooms");
    System.out.println("LTC-5101: " + service.getRoom(Building.LTC, "5101"));
    System.out.println("NAB-6101: " + service.getRoom(Building.NAB, "6101"));
    System.out.println("FD1-1101: " + service.getRoom(Building.FD1, "1101"));
    
    // Test 2: Non-existing rooms
    System.out.println("\nTest 2: Getting non-existing rooms");
    System.out.println("LTC-9999: " + service.getRoom(Building.LTC, "9999"));
    System.out.println("NAB-9999: " + service.getRoom(Building.NAB, "9999"));
    System.out.println("FD1-9999: " + service.getRoom(Building.FD1, "9999"));
    
    System.out.println();

    // ===== TASK 7: filterRooms - Comprehensive Testing =====
    System.out.println("=== TASK 7: filterRooms Testing ===");
    
    // Test 1: Filter by capacity
    System.out.println("Test 1: Filter by minimum capacity");
    List<Room> capacity150 = service.filterRooms(150, null, null, null);
    System.out.println("Rooms with capacity >= 150: " + capacity150.size());
    for (Room room : capacity150) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (capacity: " + room.getCapacity() + ")");
    }
    
    // Test 2: Filter by building
    System.out.println("\nTest 2: Filter by building");
    List<Room> ltcRoomsFiltered = service.filterRooms(null, Building.LTC, null, null);
    System.out.println("LTC rooms: " + ltcRoomsFiltered.size());
    for (Room room : ltcRoomsFiltered) {
      System.out.println("  " + room.getRoomNumber());
    }
    
    // Test 3: Filter by projector requirement
    System.out.println("\nTest 3: Filter by projector requirement");
    List<Room> projectorRooms = service.filterRooms(null, null, true, null);
    System.out.println("Rooms with projector: " + projectorRooms.size());
    for (Room room : projectorRooms) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (projector: " + room.isProjectorAvailable() + ")");
    }
    
    // Test 4: Filter by internet requirement
    System.out.println("\nTest 4: Filter by internet requirement");
    List<Room> internetRooms = service.filterRooms(null, null, null, true);
    System.out.println("Rooms with internet: " + internetRooms.size());
    for (Room room : internetRooms) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (internet: " + room.isInternetAvailable() + ")");
    }
    
    // Test 5: Combined filters
    System.out.println("\nTest 5: Combined filters");
    List<Room> combinedFilter = service.filterRooms(200, Building.LTC, true, true);
    System.out.println("LTC rooms with capacity >= 200, projector and internet: " + combinedFilter.size());
    for (Room room : combinedFilter) {
      System.out.println("  " + room.getRoomNumber() + " (capacity: " + room.getCapacity() + ")");
    }
    
    System.out.println();

    // ===== TASK 8: bookRoom - Comprehensive Testing =====
    System.out.println("=== TASK 8: bookRoom Testing ===");
    
    // Test 1: Valid bookings
    System.out.println("Test 1: Valid bookings");
    System.out.println("Book LTC-5101 hour 1: " + service.bookRoom(Building.LTC, "5101", 1, 80, true, true));
    System.out.println("Book NAB-6101 hour 2: " + service.bookRoom(Building.NAB, "6101", 2, 150, false, true));
    System.out.println("Book FD1-1101 hour 3: " + service.bookRoom(Building.FD1, "1101", 3, 100, true, false));
    
    // Test 2: Invalid hours
    System.out.println("\nTest 2: Invalid hours");
    System.out.println("Book hour 0: " + service.bookRoom(Building.LTC, "5101", 0, 80, true, true));
    System.out.println("Book hour 11: " + service.bookRoom(Building.LTC, "5101", 11, 80, true, true));
    System.out.println("Book hour -1: " + service.bookRoom(Building.LTC, "5101", -1, 80, true, true));
    
    // Test 3: Non-existing room
    System.out.println("\nTest 3: Non-existing room");
    System.out.println("Book non-existing room: " + service.bookRoom(Building.LTC, "9999", 1, 80, true, true));
    
    // Test 4: Insufficient capacity
    System.out.println("\nTest 4: Insufficient capacity");
    System.out.println("Book with capacity 300 (room has 100): " + service.bookRoom(Building.LTC, "5101", 4, 300, true, true));
    
    // Test 5: Projector not available
    System.out.println("\nTest 5: Projector not available");
    System.out.println("Book room without projector: " + service.bookRoom(Building.NAB, "6101", 4, 150, true, true));
    
    // Test 6: Internet not available
    System.out.println("\nTest 6: Internet not available");
    System.out.println("Book room without internet: " + service.bookRoom(Building.FD1, "1101", 4, 100, false, true));
    
    // Test 7: Already booked
    System.out.println("\nTest 7: Already booked");
    System.out.println("Book already booked hour: " + service.bookRoom(Building.LTC, "5101", 1, 80, true, true));
    
    System.out.println();

    // ===== TASK 9: isAvailable - Comprehensive Testing =====
    System.out.println("=== TASK 9: isAvailable Testing ===");
    
    // Test 1: Available hours
    System.out.println("Test 1: Available hours");
    System.out.println("LTC-5101 hour 4: " + service.isAvailable(Building.LTC, "5101", 4));
    System.out.println("LTC-5101 hour 5: " + service.isAvailable(Building.LTC, "5101", 5));
    System.out.println("NAB-6101 hour 1: " + service.isAvailable(Building.NAB, "6101", 1));
    
    // Test 2: Booked hours
    System.out.println("\nTest 2: Booked hours");
    System.out.println("LTC-5101 hour 1: " + service.isAvailable(Building.LTC, "5101", 1));
    System.out.println("NAB-6101 hour 2: " + service.isAvailable(Building.NAB, "6101", 2));
    System.out.println("FD1-1101 hour 3: " + service.isAvailable(Building.FD1, "1101", 3));
    
    // Test 3: Invalid hours
    System.out.println("\nTest 3: Invalid hours");
    System.out.println("Hour 0: " + service.isAvailable(Building.LTC, "5101", 0));
    System.out.println("Hour 11: " + service.isAvailable(Building.LTC, "5101", 11));
    System.out.println("Hour -1: " + service.isAvailable(Building.LTC, "5101", -1));
    
    // Test 4: Non-existing room
    System.out.println("\nTest 4: Non-existing room");
    System.out.println("Non-existing room: " + service.isAvailable(Building.LTC, "9999", 1));
    
    System.out.println();

    // ===== TASK 10: getAvailableRoomsByHour - Comprehensive Testing =====
    System.out.println("=== TASK 10: getAvailableRoomsByHour Testing ===");
    
    // Test 1: Available rooms at specific hour
    System.out.println("Test 1: Available rooms at hour 4");
    List<Room> availableAt4 = service.getAvailableRoomsByHour(null, 4, null, null, null);
    System.out.println("Available rooms at hour 4: " + availableAt4.size());
    for (Room room : availableAt4) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber());
    }
    
    // Test 2: Available rooms with capacity filter
    System.out.println("\nTest 2: Available rooms at hour 4 with capacity >= 200");
    List<Room> availableCapacity200 = service.getAvailableRoomsByHour(null, 4, 200, null, null);
    System.out.println("Available rooms at hour 4 with capacity >= 200: " + availableCapacity200.size());
    for (Room room : availableCapacity200) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (capacity: " + room.getCapacity() + ")");
    }
    
    // Test 3: Available rooms with building filter
    System.out.println("\nTest 3: Available LTC rooms at hour 4");
    List<Room> availableLTC = service.getAvailableRoomsByHour(Building.LTC, 4, null, null, null);
    System.out.println("Available LTC rooms at hour 4: " + availableLTC.size());
    for (Room room : availableLTC) {
      System.out.println("  " + room.getRoomNumber());
    }
    
    // Test 4: Available rooms with projector requirement
    System.out.println("\nTest 4: Available rooms at hour 4 with projector");
    List<Room> availableProjector = service.getAvailableRoomsByHour(null, 4, null, true, null);
    System.out.println("Available rooms at hour 4 with projector: " + availableProjector.size());
    for (Room room : availableProjector) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (projector: " + room.isProjectorAvailable() + ")");
    }
    
    // Test 5: Available rooms with internet requirement
    System.out.println("\nTest 5: Available rooms at hour 4 with internet");
    List<Room> availableInternet = service.getAvailableRoomsByHour(null, 4, null, null, true);
    System.out.println("Available rooms at hour 4 with internet: " + availableInternet.size());
    for (Room room : availableInternet) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (internet: " + room.isInternetAvailable() + ")");
    }
    
    // Test 6: Invalid hour
    System.out.println("\nTest 6: Invalid hour");
    List<Room> invalidHour = service.getAvailableRoomsByHour(null, 0, null, null, null);
    System.out.println("Available rooms at invalid hour 0: " + invalidHour.size());
    
    System.out.println();

    // ===== TASK 5: removeRoom - Comprehensive Testing =====
    System.out.println("=== TASK 5: removeRoom Testing ===");
    
    // Test 1: Remove existing rooms
    System.out.println("Test 1: Remove existing rooms");
    System.out.println("Remove FD1-1101: " + service.removeRoom(Building.FD1, "1101"));
    System.out.println("Remove FD2-2101: " + service.removeRoom(Building.FD2, "2101"));
    
    // Test 2: Remove non-existing rooms
    System.out.println("\nTest 2: Remove non-existing rooms");
    System.out.println("Remove non-existing room: " + service.removeRoom(Building.LTC, "9999"));
    System.out.println("Remove already removed room: " + service.removeRoom(Building.FD1, "1101"));
    
    // Test 3: Verify removal
    System.out.println("\nTest 3: Verify removal");
    System.out.println("FD1-1101 after removal: " + service.getRoom(Building.FD1, "1101"));
    System.out.println("FD2-2101 after removal: " + service.getRoom(Building.FD2, "2101"));
    
    // Test 4: Check booking calendar cleanup
    System.out.println("\nTest 4: Check booking calendar cleanup");
    System.out.println("FD1-1101 availability after removal: " + service.isAvailable(Building.FD1, "1101", 3));
    
    System.out.println();

    // ===== Final Summary =====
    System.out.println("=== FINAL SUMMARY ===");
    System.out.println("Total rooms remaining: " + service.getRooms().size());
    System.out.println("Remaining rooms:");
    for (Room room : service.getRooms()) {
      System.out.println("  " + room.getBuilding() + "-" + room.getRoomNumber() + " (capacity: " + room.getCapacity() + 
                        ", projector: " + room.isProjectorAvailable() + ", internet: " + room.isInternetAvailable() + ")");
    }
    
    System.out.println("\n=== COMPREHENSIVE TESTING COMPLETE ===");
  }
}
