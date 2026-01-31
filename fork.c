#include <windows.h>
#include <stdio.h>

/* ===============================
Structures & definitions
   =============================== */

typedef LONG NTSTATUS;

typedef struct _PROCESS_BASIC_INFORMATION
{
    PVOID Reserved1;
    PVOID PebBaseAddress;
    PVOID Reserved2[2];
    ULONG_PTR UniqueProcessId;
    ULONG_PTR InheritedFromUniqueProcessId;
} PROCESS_BASIC_INFORMATION;

/* ===============================
Kernel-level parent PID query
   =============================== */
DWORD GetParentPID(DWORD pid)
{
    HANDLE hProcess = OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION, FALSE, pid);
    if (!hProcess)
        return 0;

    PROCESS_BASIC_INFORMATION pbi;
    ULONG retLen = 0;

    typedef NTSTATUS(WINAPI * NtQueryInformationProcess_t)(
        HANDLE, ULONG, PVOID, ULONG, PULONG);

    NtQueryInformationProcess_t NtQueryInformationProcess =
        (NtQueryInformationProcess_t)GetProcAddress(
            GetModuleHandleW(L"ntdll.dll"),
            "NtQueryInformationProcess");

    if (!NtQueryInformationProcess)
    {
        CloseHandle(hProcess);
        return 0;
    }

    NTSTATUS status = NtQueryInformationProcess(
        hProcess,
        0, // ProcessBasicInformation
        &pbi,
        sizeof(pbi),
        &retLen);

    CloseHandle(hProcess);

    if (status != 0)
        return 0;

    return (DWORD)pbi.InheritedFromUniqueProcessId;
}

int main()
{
    /* ===============================
    1. START VS CODE
       =============================== */
    STARTUPINFOW si = {0};
    PROCESS_INFORMATION piVS = {0};
    si.cb = sizeof(si);

    wchar_t vscodePath[] =
        L"C:\\Users\\ASUS\\AppData\\Local\\Programs\\Microsoft VS Code\\Code.exe";

    if (!CreateProcessW(
            vscodePath,
            NULL,
            NULL, NULL,
            FALSE,
            0,
            NULL, NULL,
            &si,
            &piVS))
    {
        printf("Failed to start VS Code (error %lu)\n", GetLastError());
        return 1;
    }

    printf("VS Code launched\n");
    printf("VS Code PID: %lu\n\n", piVS.dwProcessId);

    Sleep(3000); // allow VS Code to initialize

    /* ===============================
    2. PREPARE PARENT ATTRIBUTE
       =============================== */
    SIZE_T size = 0;
    InitializeProcThreadAttributeList(NULL, 1, 0, &size);

    PPROC_THREAD_ATTRIBUTE_LIST attrList =
        (PPROC_THREAD_ATTRIBUTE_LIST)HeapAlloc(GetProcessHeap(), 0, size);

    InitializeProcThreadAttributeList(attrList, 1, 0, &size);

    UpdateProcThreadAttribute(
        attrList,
        0,
        PROC_THREAD_ATTRIBUTE_PARENT_PROCESS,
        &piVS.hProcess,
        sizeof(HANDLE),
        NULL,
        NULL);

    STARTUPINFOEXW siex = {0};
    siex.StartupInfo.cb = sizeof(STARTUPINFOEXW);
    siex.lpAttributeList = attrList;

    /* ===============================
    3. START NOTEPAD AS CHILD
       =============================== */
    PROCESS_INFORMATION piNote = {0};
    wchar_t notepadPath[] = L"C:\\Windows\\System32\\notepad.exe";

    if (!CreateProcessW(
            notepadPath,
            NULL,
            NULL, NULL,
            FALSE,
            EXTENDED_STARTUPINFO_PRESENT,
            NULL, NULL,
            &siex.StartupInfo,
            &piNote))
    {
        printf("Failed to start Notepad (error %lu)\n", GetLastError());
        return 1;
    }

    printf("Notepad launched\n");
    printf("Notepad PID: %lu\n", piNote.dwProcessId);

    Sleep(1000); // ensure process is fully registered

    /* ===============================
    4. VERIFY PARENT PID
       =============================== */
    DWORD actualParent = GetParentPID(piNote.dwProcessId);

    printf("\n=== VERIFICATION ===\n");
    printf("Expected Parent PID : %lu (VS Code)\n", piVS.dwProcessId);
    printf("Actual Parent PID   : %lu\n", actualParent);

    if (actualParent == piVS.dwProcessId)
        printf("RESULT: SUCCESS — Notepad is a CHILD of VS Code\n");
    else
        printf("RESULT: FAILED — Parent process is different\n");

    /* ===============================
    5. CLEANUP
       =============================== */
    DeleteProcThreadAttributeList(attrList);
    HeapFree(GetProcessHeap(), 0, attrList);

    CloseHandle(piNote.hThread);
    CloseHandle(piNote.hProcess);
    CloseHandle(piVS.hThread);
    CloseHandle(piVS.hProcess);

    printf("\nPress Enter to exit...\n");
    getchar();
    return 0;
}