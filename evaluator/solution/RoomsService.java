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
    private final Building building;
    private final String roomNumber;
    private int capacity;
    private boolean projectorAvailable;
    private boolean internetAvailable;

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

    public int compareTo(Room other) {
      if (other == null) return 1;
      int cap = Integer.compare(this.capacity, other.capacity);
      if (cap != 0) return cap;
      return this.roomNumber.compareTo(other.roomNumber);
    }

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
  private final Map<String, Set<Integer>> bookingsByRoomKey = new HashMap<>();

  interface RoomPredicate {
    boolean test(Room r);
  }

  public List<Room> getRooms() { return rooms; }

  private static String getRoomKey(Building b, String roomNumber) {
    return b + "#" + roomNumber;
  }

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
    for (Room r : rooms) {
      if (r.getBuilding() == b && r.getRoomNumber().equals(rn)) {
        return ErrorCode.DUPLICATE_ROOM;
      }
    }
    rooms.add(room);
    bookingsByRoomKey.putIfAbsent(getRoomKey(b, rn), new HashSet<>());
    return ErrorCode.OK;
  }

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

  public Room getRoom(Building building, String roomNumber) {
    for (Room r : rooms) {
      if (r.getBuilding() == building && r.getRoomNumber().equals(roomNumber)) {
        return r;
      }
    }
    return null;
  }

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
    if (booked.contains(hour)) {
      return ErrorCode.ALREADY_BOOKED;
    }
    booked.add(hour);
    return ErrorCode.OK;
  }

  public ErrorCode isAvailable(Building building, String roomNumber, int hour) {
    if (hour < 1 || hour > 10) return ErrorCode.INVALID_HOUR;
    Room room = getRoom(building, roomNumber);
    if (room == null) return ErrorCode.ROOM_NOT_FOUND;
    String k = getRoomKey(building, roomNumber);
    Set<Integer> booked = bookingsByRoomKey.get(k);
    if (booked == null) return ErrorCode.OK;
    return booked.contains(hour) ? ErrorCode.ALREADY_BOOKED : ErrorCode.OK;
  }

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

  // ===== RUBRIC-BASED TESTING =====
  public static void main(String[] args) {
    RoomsService service = new RoomsService();

    System.out.println("=== RUBRIC-BASED TESTING ===");
    System.out.println();

    // TASK 1: compareTo
    System.out.println("=== TASK 1: compareTo ===");
    
    // Test Case 1.1: Different capacities
    System.out.println("TEST_CASE:1.1");
    Room room1_1 = new Room(Building.LTC, "5101", 100, true, true);
    Room room1_2 = new Room(Building.LTC, "5102", 200, true, true);
    System.out.println("Input: room1(capacity=100) vs room2(capacity=200)");
    System.out.println("Expected: negative (room1 < room2)");
    System.out.println("Actual: " + room1_1.compareTo(room1_2));
    System.out.println("Result: " + (room1_1.compareTo(room1_2) < 0 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 1.2: Same capacity, different room numbers
    System.out.println("TEST_CASE:1.2");
    Room room1_3 = new Room(Building.LTC, "5101", 100, true, true);
    Room room1_4 = new Room(Building.LTC, "5109", 100, true, true);
    System.out.println("Input: room1(5101) vs room2(5109), same capacity");
    System.out.println("Expected: negative (5101 < 5109)");
    System.out.println("Actual: " + room1_3.compareTo(room1_4));
    System.out.println("Result: " + (room1_3.compareTo(room1_4) < 0 ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 2: equals
    System.out.println("=== TASK 2: equals ===");
    
    // Test Case 2.1: Same building, different room number
    System.out.println("TEST_CASE:2.1");
    Room room2_1 = new Room(Building.LTC, "5101", 100, true, true);
    Room room2_2 = new Room(Building.LTC, "5102", 100, true, true);
    System.out.println("Input: room1(LTC-5101) vs room2(LTC-5102)");
    System.out.println("Expected: false");
    System.out.println("Actual: " + room2_1.equals(room2_2));
    System.out.println("Result: " + (!room2_1.equals(room2_2) ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 2.2: Different building, same room number
    System.out.println("TEST_CASE:2.2");
    Room room2_3 = new Room(Building.LTC, "5101", 100, true, true);
    Room room2_4 = new Room(Building.NAB, "5101", 100, true, true);
    System.out.println("Input: room1(LTC-5101) vs room2(NAB-5101)");
    System.out.println("Expected: false");
    System.out.println("Actual: " + room2_3.equals(room2_4));
    System.out.println("Result: " + (!room2_3.equals(room2_4) ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 2.3: Different building and room number
    System.out.println("TEST_CASE:2.3");
    Room room2_5 = new Room(Building.LTC, "5101", 100, true, true);
    Room room2_6 = new Room(Building.NAB, "6101", 200, true, true);
    System.out.println("Input: room1(LTC-5101) vs room2(NAB-6101)");
    System.out.println("Expected: false");
    System.out.println("Actual: " + room2_5.equals(room2_6));
    System.out.println("Result: " + (!room2_5.equals(room2_6) ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 2.4: Same building and room number
    System.out.println("TEST_CASE:2.4");
    Room room2_7 = new Room(Building.LTC, "5101", 100, true, true);
    Room room2_8 = new Room(Building.LTC, "5101", 200, true, true);
    System.out.println("Input: room1(LTC-5101) vs room2(LTC-5101)");
    System.out.println("Expected: true");
    System.out.println("Actual: " + room2_7.equals(room2_8));
    System.out.println("Result: " + (room2_7.equals(room2_8) ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 3: BY_BUILDING_THEN_ROOM
    System.out.println("=== TASK 3: BY_BUILDING_THEN_ROOM ===");
    
    // Test Case 3.1: Different buildings
    System.out.println("TEST_CASE:3.1");
    Room room3_1 = new Room(Building.FD1, "1101", 100, true, true);
    Room room3_2 = new Room(Building.LTC, "5101", 100, true, true);
    System.out.println("Input: room1(FD1-1101) vs room2(LTC-5101)");
    System.out.println("Expected: negative (FD1 < LTC)");
    System.out.println("Actual: " + Room.Comparators.BY_BUILDING_THEN_ROOM.compare(room3_1, room3_2));
    System.out.println("Result: " + (Room.Comparators.BY_BUILDING_THEN_ROOM.compare(room3_1, room3_2) < 0 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 3.2: Same building, different room numbers
    System.out.println("TEST_CASE:3.2");
    Room room3_3 = new Room(Building.LTC, "5101", 100, true, true);
    Room room3_4 = new Room(Building.LTC, "5109", 100, true, true);
    System.out.println("Input: room1(LTC-5101) vs room2(LTC-5109)");
    System.out.println("Expected: negative (5101 < 5109)");
    System.out.println("Actual: " + Room.Comparators.BY_BUILDING_THEN_ROOM.compare(room3_3, room3_4));
    System.out.println("Result: " + (Room.Comparators.BY_BUILDING_THEN_ROOM.compare(room3_3, room3_4) < 0 ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 4: addRoom
    System.out.println("=== TASK 4: addRoom ===");
    
    // Test Case 4.1: Invalid room number
    System.out.println("TEST_CASE:4.1");
    System.out.println("Input: LTC-4101 (invalid prefix)");
    System.out.println("Expected: INVALID_ROOM_NUMBER");
    System.out.println("Actual: " + service.addRoom(new Room(Building.LTC, "4101", 100, true, true)));
    System.out.println("Result: " + (service.addRoom(new Room(Building.LTC, "4101", 100, true, true)) == ErrorCode.INVALID_ROOM_NUMBER ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 4.2: Invalid capacity
    System.out.println("TEST_CASE:4.2");
    System.out.println("Input: capacity=75 (not multiple of 50)");
    System.out.println("Expected: INVALID_CAPACITY");
    System.out.println("Actual: " + service.addRoom(new Room(Building.LTC, "5101", 75, true, true)));
    System.out.println("Result: " + (service.addRoom(new Room(Building.LTC, "5101", 75, true, true)) == ErrorCode.INVALID_CAPACITY ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 4.3: Duplicate room
    System.out.println("TEST_CASE:4.3");
    service.addRoom(new Room(Building.LTC, "5101", 100, true, true));
    System.out.println("Input: Add LTC-5101 again");
    System.out.println("Expected: DUPLICATE_ROOM");
    System.out.println("Actual: " + service.addRoom(new Room(Building.LTC, "5101", 100, true, true)));
    System.out.println("Result: " + (service.addRoom(new Room(Building.LTC, "5101", 100, true, true)) == ErrorCode.DUPLICATE_ROOM ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 4.4: Add room to list
    System.out.println("TEST_CASE:4.4");
    System.out.println("Input: Add NAB-6101");
    System.out.println("Expected: OK, room added to list");
    System.out.println("Actual: " + service.addRoom(new Room(Building.NAB, "6101", 200, true, true)));
    System.out.println("Rooms in list: " + service.getRooms().size());
    System.out.println("Result: " + (service.getRooms().size() == 2 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 4.5: Initialize bookingsByRoomKey
    System.out.println("TEST_CASE:4.5");
    service.addRoom(new Room(Building.FD1, "1101", 150, true, true));
    System.out.println("Input: Add FD1-1101");
    System.out.println("Expected: OK, bookingsByRoomKey initialized");
    System.out.println("Actual: " + service.addRoom(new Room(Building.FD1, "1101", 150, true, true)));
    System.out.println("Result: " + (service.isAvailable(Building.FD1, "1101", 1) == ErrorCode.OK ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 4.6: Return OK
    System.out.println("TEST_CASE:4.6");
    System.out.println("Input: Add FD2-2101");
    System.out.println("Expected: OK");
    ErrorCode result4_6 = service.addRoom(new Room(Building.FD2, "2101", 250, true, true));
    System.out.println("Actual: " + result4_6);
    System.out.println("Result: " + (result4_6 == ErrorCode.OK ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 5: removeRoom
    System.out.println("=== TASK 5: removeRoom ===");
    
    // Test Case 5.1: Remove from list
    System.out.println("TEST_CASE:5.1");
    int sizeBefore = service.getRooms().size();
    System.out.println("Input: Remove FD1-1101");
    System.out.println("Expected: OK, room removed from list");
    System.out.println("Actual: " + service.removeRoom(Building.FD1, "1101"));
    System.out.println("Rooms before: " + sizeBefore + ", after: " + service.getRooms().size());
    System.out.println("Result: " + (service.getRooms().size() == sizeBefore - 1 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 5.2: Remove from bookingsByRoomKey
    System.out.println("TEST_CASE:5.2");
    System.out.println("Input: Remove FD2-2101");
    System.out.println("Expected: OK, bookingsByRoomKey entry removed");
    System.out.println("Actual: " + service.removeRoom(Building.FD2, "2101"));
    System.out.println("Result: " + (service.isAvailable(Building.FD2, "2101", 1) == ErrorCode.ROOM_NOT_FOUND ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 5.3: Return OK
    System.out.println("TEST_CASE:5.3");
    System.out.println("Input: Remove NAB-6101");
    System.out.println("Expected: OK");
    ErrorCode result5_3 = service.removeRoom(Building.NAB, "6101");
    System.out.println("Actual: " + result5_3);
    System.out.println("Result: " + (result5_3 == ErrorCode.OK ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 5.4: Return ROOM_NOT_FOUND
    System.out.println("TEST_CASE:5.4");
    System.out.println("Input: Remove non-existing room");
    System.out.println("Expected: ROOM_NOT_FOUND");
    System.out.println("Actual: " + service.removeRoom(Building.LTC, "9999"));
    System.out.println("Result: " + (service.removeRoom(Building.LTC, "9999") == ErrorCode.ROOM_NOT_FOUND ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 6: getRoom
    System.out.println("=== TASK 6: getRoom ===");
    
    // Test Case 6.1: Return room
    System.out.println("TEST_CASE:6.1");
    System.out.println("Input: Get LTC-5101");
    System.out.println("Expected: Room object");
    System.out.println("Actual: " + service.getRoom(Building.LTC, "5101"));
    System.out.println("Result: " + (service.getRoom(Building.LTC, "5101") != null ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 6.2: Return null
    System.out.println("TEST_CASE:6.2");
    System.out.println("Input: Get non-existing room");
    System.out.println("Expected: null");
    System.out.println("Actual: " + service.getRoom(Building.LTC, "9999"));
    System.out.println("Result: " + (service.getRoom(Building.LTC, "9999") == null ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 7: filterRooms
    System.out.println("=== TASK 7: filterRooms ===");
    
    // Setup for filter tests
    service.addRoom(new Room(Building.LTC, "5102", 100, false, true));
    service.addRoom(new Room(Building.LTC, "5103", 200, true, false));
    service.addRoom(new Room(Building.LTC, "5104", 150, true, true));
    
    // Test Case 7.1: Filter by capacity
    System.out.println("TEST_CASE:7.1");
    System.out.println("Input: minCapacity=150");
    System.out.println("Expected: Rooms with capacity >= 150");
    List<Room> filtered7_1 = service.filterRooms(150, null, null, null);
    System.out.println("Actual: " + filtered7_1.size() + " rooms");
    System.out.println("Result: " + (filtered7_1.size() >= 2 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 7.2: Filter by building
    System.out.println("TEST_CASE:7.2");
    System.out.println("Input: building=LTC");
    System.out.println("Expected: Only LTC rooms");
    List<Room> filtered7_2 = service.filterRooms(null, Building.LTC, null, null);
    System.out.println("Actual: " + filtered7_2.size() + " LTC rooms");
    System.out.println("Result: " + (filtered7_2.size() >= 4 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 7.3: Filter by projector
    System.out.println("TEST_CASE:7.3");
    System.out.println("Input: projectorRequired=true");
    System.out.println("Expected: Rooms with projector");
    List<Room> filtered7_3 = service.filterRooms(null, null, true, null);
    System.out.println("Actual: " + filtered7_3.size() + " rooms with projector");
    System.out.println("Result: " + (filtered7_3.size() >= 3 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 7.4: Filter by internet
    System.out.println("TEST_CASE:7.4");
    System.out.println("Input: internetRequired=true");
    System.out.println("Expected: Rooms with internet");
    List<Room> filtered7_4 = service.filterRooms(null, null, null, true);
    System.out.println("Actual: " + filtered7_4.size() + " rooms with internet");
    System.out.println("Result: " + (filtered7_4.size() >= 3 ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 8: bookRoom
    System.out.println("=== TASK 8: bookRoom ===");
    
    // Test Case 8.1: Invalid hour
    System.out.println("TEST_CASE:8.1");
    System.out.println("Input: hour=0 (invalid)");
    System.out.println("Expected: INVALID_HOUR");
    System.out.println("Actual: " + service.bookRoom(Building.LTC, "5101", 0, 80, true, true));
    System.out.println("Result: " + (service.bookRoom(Building.LTC, "5101", 0, 80, true, true) == ErrorCode.INVALID_HOUR ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.2: Room not found
    System.out.println("TEST_CASE:8.2");
    System.out.println("Input: Non-existing room");
    System.out.println("Expected: ROOM_NOT_FOUND");
    System.out.println("Actual: " + service.bookRoom(Building.LTC, "9999", 1, 80, true, true));
    System.out.println("Result: " + (service.bookRoom(Building.LTC, "9999", 1, 80, true, true) == ErrorCode.ROOM_NOT_FOUND ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.3: Insufficient capacity
    System.out.println("TEST_CASE:8.3");
    System.out.println("Input: minRequiredCapacity=300, room capacity=100");
    System.out.println("Expected: INSUFFICIENT_CAPACITY");
    System.out.println("Actual: " + service.bookRoom(Building.LTC, "5101", 1, 300, true, true));
    System.out.println("Result: " + (service.bookRoom(Building.LTC, "5101", 1, 300, true, true) == ErrorCode.INSUFFICIENT_CAPACITY ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.4: Projector not available
    System.out.println("TEST_CASE:8.4");
    System.out.println("Input: requireProjector=true, room has no projector");
    System.out.println("Expected: PROJECTOR_NOT_AVAILABLE");
    System.out.println("Actual: " + service.bookRoom(Building.LTC, "5102", 1, 80, true, true));
    System.out.println("Result: " + (service.bookRoom(Building.LTC, "5102", 1, 80, true, true) == ErrorCode.PROJECTOR_NOT_AVAILABLE ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.5: Internet not available
    System.out.println("TEST_CASE:8.5");
    System.out.println("Input: requireInternet=true, room has no internet");
    System.out.println("Expected: INTERNET_NOT_AVAILABLE");
    System.out.println("Actual: " + service.bookRoom(Building.LTC, "5103", 1, 150, true, true));
    System.out.println("Result: " + (service.bookRoom(Building.LTC, "5103", 1, 150, true, true) == ErrorCode.INTERNET_NOT_AVAILABLE ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.6: Already booked
    System.out.println("TEST_CASE:8.6");
    service.bookRoom(Building.LTC, "5101", 2, 80, true, true);
    System.out.println("Input: Book already booked hour");
    System.out.println("Expected: ALREADY_BOOKED");
    ErrorCode result8_6 = service.bookRoom(Building.LTC, "5101", 2, 80, true, true);
    System.out.println("Actual: " + result8_6);
    System.out.println("Result: " + (result8_6 == ErrorCode.ALREADY_BOOKED ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.7: Add to bookingsByRoomKey
    System.out.println("TEST_CASE:8.7");
    System.out.println("Input: Book hour 3");
    System.out.println("Expected: OK, hour added to bookingsByRoomKey");
    ErrorCode result8_7 = service.bookRoom(Building.LTC, "5101", 3, 80, true, true);
    System.out.println("Actual: " + result8_7);
    System.out.println("Result: " + (service.isAvailable(Building.LTC, "5101", 3) == ErrorCode.ALREADY_BOOKED ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 8.8: Return OK
    System.out.println("TEST_CASE:8.8");
    System.out.println("Input: Book hour 4");
    System.out.println("Expected: OK");
    ErrorCode result8_8 = service.bookRoom(Building.LTC, "5101", 4, 80, true, true);
    System.out.println("Actual: " + result8_8);
    System.out.println("Result: " + (result8_8 == ErrorCode.OK ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 9: isAvailable
    System.out.println("=== TASK 9: isAvailable ===");
    
    // Test Case 9.1: Invalid hour
    System.out.println("TEST_CASE:9.1");
    System.out.println("Input: hour=11 (invalid)");
    System.out.println("Expected: INVALID_HOUR");
    System.out.println("Actual: " + service.isAvailable(Building.LTC, "5101", 11));
    System.out.println("Result: " + (service.isAvailable(Building.LTC, "5101", 11) == ErrorCode.INVALID_HOUR ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 9.2: Room not found
    System.out.println("TEST_CASE:9.2");
    System.out.println("Input: Non-existing room");
    System.out.println("Expected: ROOM_NOT_FOUND");
    System.out.println("Actual: " + service.isAvailable(Building.LTC, "9999", 1));
    System.out.println("Result: " + (service.isAvailable(Building.LTC, "9999", 1) == ErrorCode.ROOM_NOT_FOUND ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 9.3: Room not booked
    System.out.println("TEST_CASE:9.3");
    System.out.println("Input: Check hour 5 (not booked)");
    System.out.println("Expected: OK");
    ErrorCode result9_3 = service.isAvailable(Building.LTC, "5101", 5);
    System.out.println("Actual: " + result9_3);
    System.out.println("Result: " + (result9_3 == ErrorCode.OK ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 9.4: Room booked
    System.out.println("TEST_CASE:9.4");
    System.out.println("Input: Check hour 2 (booked)");
    System.out.println("Expected: ALREADY_BOOKED");
    ErrorCode result9_4 = service.isAvailable(Building.LTC, "5101", 2);
    System.out.println("Actual: " + result9_4);
    System.out.println("Result: " + (result9_4 == ErrorCode.ALREADY_BOOKED ? "PASS" : "FAIL"));
    System.out.println();

    // TASK 10: getAvailableRoomsByHour
    System.out.println("=== TASK 10: getAvailableRoomsByHour ===");
    
    // Test Case 10.1: Invalid hour
    System.out.println("TEST_CASE:10.1");
    System.out.println("Input: hour=0 (invalid)");
    System.out.println("Expected: Empty list");
    List<Room> available10_1 = service.getAvailableRoomsByHour(null, 0, null, null, null);
    System.out.println("Actual: " + available10_1.size() + " rooms");
    System.out.println("Result: " + (available10_1.size() == 0 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 10.2: Filter rooms
    System.out.println("TEST_CASE:10.2");
    System.out.println("Input: hour=5, minCapacity=150");
    System.out.println("Expected: Rooms with capacity >= 150");
    List<Room> available10_2 = service.getAvailableRoomsByHour(null, 5, 150, null, null);
    System.out.println("Actual: " + available10_2.size() + " rooms");
    System.out.println("Result: " + (available10_2.size() >= 1 ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test Case 10.3: Available rooms
    System.out.println("TEST_CASE:10.3");
    System.out.println("Input: hour=6");
    System.out.println("Expected: Available rooms at hour 6");
    List<Room> available10_3 = service.getAvailableRoomsByHour(null, 6, null, null, null);
    System.out.println("Actual: " + available10_3.size() + " rooms");
    System.out.println("Result: " + (available10_3.size() >= 4 ? "PASS" : "FAIL"));
    System.out.println();

    System.out.println("=== RUBRIC-BASED TESTING COMPLETE ===");
  }
}
