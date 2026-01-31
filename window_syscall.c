#include <windows.h>
#include <string.h>
#include <stdio.h>

int main(void) {
    const char *message = "Hello, Windows API!\n";
    DWORD bytesWritten;

    BOOL ok = WriteFile(
        GetStdHandle(STD_OUTPUT_HANDLE), // stdout handle
        message,                         // buffer
        (DWORD)strlen(message),          // number of bytes to write
        &bytesWritten,                   // number of bytes written
        NULL                             // no overlapped structure
    );

    if (!ok) {
        DWORD err = GetLastError();
        fprintf(stderr, "WriteFile failed: %lu\n", err);
        return 1;
    }

    return 0;
}