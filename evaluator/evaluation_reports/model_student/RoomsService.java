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
     * [Total 6 marks]
     * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
     * 2 marks for correct comparison when the rooms have different capacities [Test Case 1.1]
     * 2 marks for correct comparison when the rooms have same capacities but different room numbers [Test Case 1.2]
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
     * [Total 6 marks]
     * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
     * 1 marks for correct comparison when both objects have same building but different room number [Test Case 2.1]
     * 1 marks for correct comparison when both objects have different building but same room number [Test Case 2.2]
     * 1 marks for correct comparison when both objects have different building and different room number [Test Case 2.3]
     * 1 mark for correct comparison when both objects have same building and same room number [Test Case 2.4]
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
       * [Total 6 marks]
       * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
       * 2 marks for correct comparison when the rooms have different building [Test Case 3.1]
       * 2 marks for correct comparison when the rooms have same building but different room number [Test Case 3.2]
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
   * [Total 10 marks]
   * 3 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 1 marks for correct validation of room number [Test Case 4.1]
   * 1 marks for correct validation of capacity [Test Case 4.2]
   * 1.5 marks for correct handling of duplicate room [Test Case 4.3]
   * 1 marks for correct addition of room to the list of rooms [Test Case 4.4]
   * 1.5 marks for correct initialization of bookingsByRoomKey entry [Test Case 4.5]
   * 1 mark for correct return of ErrorCode.OK [Test Case 4.6]
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
   * [Total 8 marks]
   * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 2 marks for correct removal of room from the list of rooms [Test Case 5.1]
   * 2 marks for correct removal of bookingsByRoomKey entry [Test Case 5.2]
   * 1 mark for correct return of ErrorCode.OK [Test Case 5.3]
   * 1 mark for correct return of ErrorCode.ROOM_NOT_FOUND [Test Case 5.4]
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
   * [Total 5 marks]
   * 1.5 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 2 marks for correct return of room [Test Case 6.1]
   * 1.5 mark for correct return of null [Test Case 6.2]
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
   * [Total 8 marks]
   * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 1 marks for correct filter of rooms based on capacity [Test Case 7.1]
   * 1 marks for correct filter of rooms based on building [Test Case 7.2]
   * 2 marks for correct filter of rooms based on projector [Test Case 7.3]
   * 2 marks for correct filter of rooms based on internet [Test Case 7.4]
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
   * [Total 10 marks]
   * 3 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 0.5 marks for correct validation of hour [Test Case 8.1]
   * 0.5 marks for correct validation of room [Test Case 8.2]
   * 0.5 marks for correct validation of capacity [Test Case 8.3]
   * 0.5 marks for correct validation of projector [Test Case 8.4]
   * 0.5 marks for correct validation of internet [Test Case 8.5]
   * 0.5 marks for correct return of ErrorCode.ALREADY_BOOKED [Test Case 8.6]
   * 3 mark for correct addition of hour to bookingsByRoomKey entry [Test Case 8.7]
   * 1 mark for correct return of ErrorCode.OK [Test Case 8.8]
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
   * [Total 8 marks]
   * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 1 marks for correct validation of hour [Test Case 9.1]
   * 1 marks for correct validation of room [Test Case 9.2]
   * 2 marks for correct return of ErrorCode.OK if the room is not booked [Test Case 9.3]
   * 2 marks for correct return of ErrorCode.ALREADY_BOOKED if the room is booked [Test Case 9.4]
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
   * [Total 8 marks]
   * 2 marks for successful compilation of this method (awarded only if the method is correctly implemented - logically)
   * 1 marks for correct return of empty list if the hour is invalid [Test Case 10.1]
   * 2 marks for correct filtering of rooms based on the given filters [Test Case 10.2]
   * 3 marks for correct return of available rooms [Test Case 10.3]
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

  // ===== Driver class for testing =====
  public static void main(String[] args) {
    RoomsService service = new RoomsService();

    // Add a few rooms (Task 4)
    System.out.println("-- Add rooms (Task 4) --");
    System.out.println(service.addRoom(new Room(Building.LTC, "5101", 100, true, true))); // OK
    System.out.println(service.addRoom(new Room(Building.NAB, "6101", 200, false, true))); // OK
    System.out.println(service.addRoom(new Room(Building.FD1, "1101", 150, true, false))); // OK
    System.out.println("Duplicate add (expect DUPLICATE_ROOM): " + service.addRoom(new Room(Building.LTC, "5101", 100, true, true)));

    // List and sort by natural order (first sort by capacity, then by room number) (Task 1)
    System.out.println("-- Natural sort (Task 1) --");
    List<Room> all = new ArrayList<>(service.getRooms());
    Collections.sort(all);
    System.out.println("Rooms by capacity then room number: " + all);

    // Sort by building then room (Task 3)
    System.out.println("-- Comparator sort (Task 3) --");
    all.sort(Room.Comparators.BY_BUILDING_THEN_ROOM);
    System.out.println("Rooms by building then room: " + all);

    // equals (Task 2): same identity should be equal
    System.out.println("-- Equals demo (same identity) --");
    Room rA = service.getRoom(Building.LTC, "5101");
    Room rB = new Room(Building.LTC, "5101", 100, true, true);
    System.out.println("rA.equals(rB): " + (rA != null && rA.equals(rB)));

    // getRoom (Task 6)
    System.out.println("-- getRoom --");
    System.out.println(service.getRoom(Building.NAB, "6101"));


    // filterRooms (Task 7)
    System.out.println("-- filterRooms (Task 6) --");
    System.out.println(service.filterRooms(150, null, true, null));

    // bookRoom (Task 8)
    System.out.println("-- bookRoom (Task 7) --");
    ErrorCode booked = service.bookRoom(Building.LTC, "5101", 2, 80, true, true);
    System.out.println("bookRoom result: " + booked);

    // isAvailable (Task 9)
    System.out.println("-- isAvailable (Task 8) --");
    System.out.println("LTC-5101 at hour 2 availability: " + service.isAvailable(Building.LTC, "5101", 2));

    // getAvailableRoomsByHour (Task 10)
    System.out.println("-- getAvailableRoomsByHour (Task 9) --");
    System.out.println(service.getAvailableRoomsByHour(null, 2, 100, null, null));

    // removeRoom (Task 5)
    System.out.println("-- removeRoom (Task 5) --");
    System.out.println(service.removeRoom(Building.FD1, "1101"));
  }
}
