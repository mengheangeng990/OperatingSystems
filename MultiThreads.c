#include <windows.h>
#include <stdio.h>
#include <stdlib.h>

#define NUM_THREADS 5

/* Thread result structure */
typedef struct {
    DWORD Upper;
    DWORD Result;
} ThreadData;

DWORD WINAPI Summation(LPVOID Param)
{
    ThreadData* data = (ThreadData*)Param;
    DWORD Upper = data->Upper;
    
    printf("Thread %lu: calculating sum from 1 to %lu...\n", GetCurrentThreadId(), Upper);
    
    DWORD localSum = 0;
    for (DWORD i = 1; i <= Upper; i++) {
        localSum += i;
        Sleep(100); // Simulate work
    }
    
    data->Result = localSum;
    printf("Thread %lu: completed. Sum = %lu\n", GetCurrentThreadId(), localSum);
    return 0;
}

int main(int argc, char *argv[])
{
    if (argc != 2) {
        printf("Usage: %s <number>\n", argv[0]);
        return 1;
    }

    DWORD Param = (DWORD)atoi(argv[1]);

    ThreadData threadData[NUM_THREADS];
    HANDLE threadHandles[NUM_THREADS];
    DWORD threadIds[NUM_THREADS];

    // Initialize thread data
    for (int i = 0; i < NUM_THREADS; i++) {
        threadData[i].Upper = Param; // All threads sum the same range
        threadData[i].Result = 0;
    }

    Sleep(5000); // Initial delay
    printf("Main: Creating %d threads...\n", NUM_THREADS);

    // Create threads
    for (int i = 0; i < NUM_THREADS; i++) {
        threadHandles[i] = CreateThread(
            NULL,
            0,
            Summation,
            &threadData[i],
            0,
            &threadIds[i]
        );

        if (threadHandles[i] == NULL) {
            fprintf(stderr, "Error creating thread %d: %lu\n", i, GetLastError());
            return 1;
        }
        printf("Main: Created thread ID: %lu\n", threadIds[i]);
    }

    printf("Main: Waiting for all threads to complete...\n");

    // Wait for all threads
    WaitForMultipleObjects(NUM_THREADS, threadHandles, TRUE, INFINITE);

    // Close handles and print results
    DWORD totalSum = 0;
    for (int i = 0; i < NUM_THREADS; i++) {
        CloseHandle(threadHandles[i]);
        totalSum += threadData[i].Result;
    }

    printf("\nMain: Individual sums:\n");
    for (int i = 0; i < NUM_THREADS; i++) {
        printf("  Thread %d: %lu\n", i + 1, threadData[i].Result);
    }
    printf("Main: Total of all thread results = %lu\n", totalSum);

    printf("Main: Press Enter to exit...\n");
    getchar();
    return 0;
}