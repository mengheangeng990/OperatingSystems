import threading

# 1. Initialize Semaphores
# 'a' starts at 1 so Process 1 can run immediately
a = threading.Semaphore(1) 
# 'b' and 'c' start at 0 so they must wait for signals
b = threading.Semaphore(0)
c = threading.Semaphore(0)

# Shared counter to handle the two 'L's
l_count = 0

def process_1():
    # wait(a)
    a.acquire() 
    print("H", end="", flush=True)
    print("E", end="", flush=True)
    # signal(b) -> Moves control to Process 2
    b.release() 

def process_2():
    global l_count
    while True:
        # wait(b)
        b.acquire() 
        print("L", end="", flush=True)
        l_count += 1
        
        if l_count < 2:
            # signal(b) -> Signal itself to print the second 'L'
            b.release() 
        else:
            # signal(c) -> After two 'L's, signal Process 3
            c.release() 
            break

def process_3():
    # wait(c)
    c.acquire() 
    print("O", flush=True)
    # We do not signal 'a' again because it must print exactly once

# Create and start the threads
t1 = threading.Thread(target=process_1)
t2 = threading.Thread(target=process_2)
t3 = threading.Thread(target=process_3)

print("Output: ", end="")
t1.start()
t2.start()
t3.start()

# Wait for them to finish
t1.join()
t2.join()
t3.join()