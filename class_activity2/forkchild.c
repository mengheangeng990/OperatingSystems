#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>

int main() {
    pid_t pid = fork();

    if (pid < 0) {
        perror("fork failed");
        return 1;
    }

    if (pid == 0) {
        // Child process runs ls
        execl("/bin/ls", "ls", NULL);
        perror("exec failed");
    } else {
        // Parent process
        printf("Parent: child created (PID = %d)\n", pid);
    }

    return 0;
}

