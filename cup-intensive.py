import time

def cpu_intensive_task():
    while True:
        sum([i**2 for i in range(10000)])

if __name__ == "__main__":
    while True:
        cpu_intensive_task()
        time.sleep(1)
