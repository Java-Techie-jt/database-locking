

# Database Locking

This project demonstrates two types of database locking mechanisms — **Optimistic Locking** and **Pessimistic Locking** — with a focus on booking seats for movies.

## Insert Records to Seat Table

```sql
INSERT INTO seat (movieName, booked, version) VALUES 
('Inception', false, 0),
('Titanic', false, 0),
('Avengers: Endgame', false, 0),
('Interstellar', false, 0),
('The Dark Knight', false, 0);
```

---

## Optimistic Validation

### cURL Command

```bash
curl -X 'GET' \
  'http://localhost:9191/booking/optimistic/2' \
  -H 'accept: */*'
```

### Results

```plaintext
Thread-1 is attempting to book the seat optimistically...
Thread-1 fetched seat with version: 0
Thread-2 is attempting to book the seat optimistically...
Thread-2 fetched seat with version: 0
Thread-1 successfully booked the seat! and version is 1
Thread-2 failed: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [com.javatechie.entity.Seat#2]
```

### Step-by-Step Breakdown

1. **Thread-1 and Thread-2 Fetch the Same Seat (Version 0)**  
   Both threads start their tasks:
   - **Thread-1** fetches the `Seat` entity with `version: 0`.
   - Before Thread-1 completes its booking, **Thread-2** also fetches the same `Seat` entity with `version: 0`.  
   
   This is expected behavior because optimistic locking allows multiple threads to read the same entity simultaneously. At this point:
   - Both threads hold a copy of the entity with `version: 0`.

2. **Thread-1 Successfully Books the Seat**  
   - **Thread-1** proceeds to update the seat:
     - It checks the `version` of the entity in the database (`version: 0`) and finds it matches the version it fetched.
     - The update is successful, and the `version` of the entity in the database is incremented to `1`.
   - **Thread-1 logs**:
     ```plaintext
     Thread-1 successfully booked the seat! and version is 1
     ```

3. **Thread-2 Fails Due to Version Mismatch**  
   - After Thread-1 updates the seat, **Thread-2** attempts to update the same seat using its fetched copy (`version: 0`).
   - Before updating, the database checks if the `version` in Thread-2's copy (`version: 0`) matches the current version in the database (`version: 1`).
   - The versions do **not** match because Thread-1 has already updated the seat.
   - As a result, Hibernate throws an **OptimisticLockingFailureException**, indicating that another transaction modified the row since Thread-2 fetched it.
   - **Thread-2 logs**:
     ```plaintext
     Thread-2 failed: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [com.javatechie.entity.Seat#2]
     ```

---

## Pessimistic Locking in Action: Seat Booking Example

### cURL Command

```bash
curl -X 'GET' \
  'http://localhost:9191/booking/optimistic/2' \
  -H 'accept: */*'
```

### Scenario

Two threads (`Thread-1` and `Thread-2`) attempt to book the same seat (ID `2`) using **Pessimistic Locking**. This locking mechanism ensures that only one thread can access and modify the seat at a time.

---

### Results

```plaintext
Thread-1 is attempting to fetch the seat with a pessimistic lock...
Thread-2 is attempting to fetch the seat with a pessimistic lock...
Thread-1 acquired the lock for seat ID: 2
Thread-1 is booking the seat...
Thread-1 successfully booked the seat with ID: 2
Thread-2 acquired the lock for seat ID: 2
Thread-2 failed: Seat ID 2 is already booked!
Thread-2 failed: Seat already booked
```

### Execution Flow and Logs

1. **Thread-1 Attempts to Fetch the Seat**  
   - `Thread-1` starts first and acquires the lock for the seat (ID `2`).
   - Logs:
     ```plaintext
     Thread-1 is attempting to fetch the seat with a pessimistic lock...
     Thread-1 acquired the lock for seat ID: 2
     ```

2. **Thread-2 Attempts to Fetch the Seat**  
   - While `Thread-1` is holding the lock, `Thread-2` starts and waits for the lock to be released.
   - Logs:
     ```plaintext
     Thread-2 is attempting to fetch the seat with a pessimistic lock...
     ```

3. **Thread-1 Books the Seat**  
   - `Thread-1` successfully books the seat and releases the lock at the end of the transaction.
   - Logs:
     ```plaintext
     Thread-1 is booking the seat...
     Thread-1 successfully booked the seat with ID: 2
     ```

4. **Thread-2 Acquires the Lock and Fails**  
   - After the lock is released, `Thread-2` acquires the lock and checks the seat's status.
   - Since the seat is already booked, `Thread-2` fails with an exception.
   - Logs:
     ```plaintext
     Thread-2 acquired the lock for seat ID: 2
     Thread-2 failed: Seat ID 2 is already booked!
     Thread-2 failed: Seat already booked
     ```

---

### Key Points

- **Exclusive Access**:  
  Pessimistic Locking ensures that only one thread can modify the seat at a time by locking it in the database during the transaction.

- **Sequential Processing**:  
  Threads attempting to access the same entity are processed one after the other, avoiding conflicts and ensuring data consistency.

- **Failure Handling**:  
  If a thread finds the entity in an invalid state (e.g., already booked), it throws an exception to indicate the operation cannot proceed.

---

Feel free to contribute or raise issues regarding this implementation!
