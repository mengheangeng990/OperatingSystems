import threading
import time
import random

# --- Shared Resources ---
BUFFER_SIZE = 100
buffer = []

# Semaphores
# empty_slots: starts at 100, decreases as we add particles
empty_slots = threading.Semaphore(BUFFER_SIZE) 
# filled_slots: starts at 0, increases as we add particles
filled_slots = threading.Semaphore(0)
# mutex: ensures only one machine touches the buffer at a time
mutex = threading.Lock()

def producer(id):
    while True:
        # 1. Produce pair (e.g., P0_a and P0_b)
        p1, p2 = f"P{id}_a", f"P{id}_b"
        time.sleep(random.uniform(0.5, 1.5)) 

        # Check if full before trying to enter
        if len(buffer) >= BUFFER_SIZE:
            print(f"--- ALERT: Buffer is FULL ({len(buffer)}/100). Producer {id} is waiting... ---")

        # 2. Wait for 2 empty spaces
        empty_slots.acquire()
        empty_slots.acquire()

        # 3. Critical Section: Place in buffer
        with mutex:
            buffer.append(p1)
            buffer.append(p2)
            print(f"PRODUCER {id}: Placed [{p1}, {p2}]. Buffer: {len(buffer)}")

        # 4. Signal that 2 items are ready for the consumer
        filled_slots.release()
        filled_slots.release()

def consumer():
    while True:
        # Check if empty before trying to fetch
        if len(buffer) == 0:
            print(f"--- ALERT: Buffer is EMPTY. Consumer machine is waiting... ---")

        # 1. Wait for at least 2 particles
        filled_slots.acquire()
        filled_slots.acquire()

        # 2. Critical Section: Fetch items
        with mutex:
            item1 = buffer.pop(0)
            item2 = buffer.pop(0)

            # --- MISMATCH CHECK ---
            # If the IDs don't match (e.g., P1 and P2 mixed), print an error
            id1 = item1.split('_')[0]
            id2 = item2.split('_')[0]
            
            if id1 != id2:
                print(f"!!! CRITICAL ERROR: MISMATCH DETECTED! {item1} mixed with {item2} !!!")
            else:
                print(f"CONSUMER: Successfully packaged [{item1}, {item2}]. Buffer: {len(buffer)}")

        # 3. Signal that 2 spaces are now free
        empty_slots.release()
        empty_slots.release()
        time.sleep(random.uniform(0.3, 0.8))

# --- Setup the Machines ---
print("Starting Schro Dinger's Factory...")
print("-" * 40)

# 1 Consumer (Packaging Machine)
threading.Thread(target=consumer, daemon=True).start()

# 5 Producers (Multiple production machines)
for i in range(5):
    threading.Thread(target=producer, args=(i,), daemon=True).start()

# Keep the script running
try:
    while True:
        time.sleep(1)
except KeyboardInterrupt:
    print("\nFactory shutting down.")