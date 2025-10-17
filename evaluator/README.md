# OOP Mid-Semester Exam Evaluator - Rubric-Based Version

An automated evaluation system for Java programming assignments with detailed rubric-based marking and comprehensive test coverage.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Directory Structure](#directory-structure)
- [Usage](#usage)
- [Program Flow](#program-flow)
- [Evaluation Report Structure](#evaluation-report-structure)
- [Rubric Details](#rubric-details)
- [Troubleshooting](#troubleshooting)
- [Examples](#examples)

## Overview

This evaluation system automatically grades Java programming assignments based on a detailed rubric. It evaluates student submissions for 10 different tasks, each with multiple subtasks and test cases. The system provides granular feedback on each subtask, making it easy to identify exactly where students lost marks.

## Features

- ✅ **Rubric-based evaluation** with detailed subtask marking
- ✅ **Automatic compilation and testing** of Java code
- ✅ **Batch processing** of multiple student submissions
- ✅ **Comprehensive test coverage** for all tasks
- ✅ **Detailed JSON reports** with subtask-level breakdown
- ✅ **Flexible folder structure** support (handles any folder names)
- ✅ **Multiple Java file support** (handles any Java file names)
- ✅ **Compilation marks** awarded only when all subtasks pass
- ✅ **Error handling** for compilation errors and runtime exceptions

## Prerequisites

- **Python 3.7+** (Python 3.8 or higher recommended)
- **Java JDK 8+** (Java 11 or higher recommended)
- **javac** and **java** commands available in PATH

### Checking Prerequisites

```bash
# Check Python version
python3 --version

# Check Java version
java -version
javac -version
```

## Installation

### 1. Clone or Download the Project

```bash
cd /path/to/your/project
```

### 2. Install Python Dependencies

No external Python packages are required! The script uses only Python standard library:
- `re` - Regular expressions
- `subprocess` - Running Java compilation and execution
- `json` - JSON report generation
- `os`, `shutil` - File operations
- `tempfile` - Temporary file management
- `pathlib` - Path handling

### 3. Verify Solution File

Ensure the rubric-based solution file exists:
```bash
ls solution/RoomsService_RubricBased.java
```

If not present, the script will fall back to `solution/RoomsService.java`.

### 4. Prepare Student Solutions

Organize student submissions in the `student_solutions/` directory:

```
student_solutions/
├── Student1_Name/
│   └── RoomsService.java
├── Student2_Name/
│   └── AnyFileName.java
└── 2023A7PS1234P/
    └── Solution.java
```

**Note**: 
- Folder names can be anything (student names, IDs, etc.)
- Java file names can be anything (RoomsService.java, Solution.java, etc.)
- Each student folder should contain exactly one Java file

## Directory Structure

```
oop-evaluation/
├── script_rubric.py              # Main evaluation script
├── solution/
│   ├── RoomsService.java         # Original solution (fallback)
│   ├── RoomsService_RubricBased.java  # Rubric-based solution (primary)
│   ├── RoomsService_Comprehensive.java  # Comprehensive testing version
│   └── RoomsService_Original.java  # Backup of original
├── student_solutions/            # Input: Student submissions
│   ├── Student1/
│   │   └── RoomsService.java
│   └── Student2/
│       └── Solution.java
├── evaluation_reports/           # Output: Evaluation results
│   ├── Student1/
│   │   ├── RoomsService.java
│   │   └── evaluation_report.json
│   ├── Student2/
│   │   ├── Solution.java
│   │   └── evaluation_report.json
│   └── batch_summary.json
└── README.md                     # This file
```

## Usage

### Basic Usage

Run the evaluation script:

```bash
python3 script_rubric.py
```

This will:
1. Scan all folders in `student_solutions/`
2. Find Java files in each folder
3. Evaluate each submission based on the rubric
4. Generate detailed reports in `evaluation_reports/`
5. Create a batch summary report

### Single Student Evaluation (Legacy Mode)

For testing individual students, you can modify the script or use the original `script.py`:

```bash
python3 script.py --single
```

### Output

After running the script, you'll see:

```
Starting batch rubric-based evaluation...
Found 6 student directories
Processing student: Student1
Starting rubric-based evaluation...
Evaluating submission for: John Doe
Evaluating Task 1...
Evaluating Task 2...
...
  Completed: 51.0/75.0 marks (68.0%)
...
============================================================
BATCH EVALUATION SUMMARY
============================================================
Total Students Processed: 6
Student1: 51.0/75.0 marks (68.0%)
Student2: 43.0/75.0 marks (57.33%)
...
Results saved in: evaluation_reports
============================================================
```

## Program Flow

### 1. Initialization
```
┌─────────────────────────────────────┐
│  BatchRubricEvaluator initialized   │
│  - student_solutions_dir             │
│  - solution_dir                      │
│  - output_dir                        │
└─────────────────────────────────────┘
```

### 2. Student Directory Scanning
```
┌─────────────────────────────────────┐
│  Scan student_solutions/ directory  │
│  - Find all subdirectories          │
│  - Filter out hidden folders        │
└─────────────────────────────────────┘
```

### 3. For Each Student
```
┌─────────────────────────────────────┐
│  1. Find Java file in student folder│
│  2. Extract student info (name, ID) │
│  3. Initialize RubricEvaluator      │
└─────────────────────────────────────┘
```

### 4. Task Evaluation Loop
```
For each task (1 to 10):
  ├─ Extract function implementation
  ├─ Create test file with student code
  ├─ Compile Java code
  ├─ Run rubric-based tests
  ├─ Parse test results
  ├─ Calculate marks for each subtask
  └─ Award compilation marks (if all pass)
```

### 5. Report Generation
```
┌─────────────────────────────────────┐
│  Generate evaluation_report.json    │
│  - Student info                     │
│  - Task-wise marks                  │
│  - Subtask breakdown                │
│  - Total marks and percentage       │
└─────────────────────────────────────┘
```

### 6. Batch Summary
```
┌─────────────────────────────────────┐
│  Generate batch_summary.json        │
│  - Overall statistics               │
│  - Per-student summary              │
│  - Average marks                    │
└─────────────────────────────────────┘
```

## Evaluation Report Structure

### Overall Report Structure

```json
{
  "student_info": {
    "name": "Student Name",
    "id_number": "2023A7PS1234P",
    "lab_number": "6016",
    "system_number": "23"
  },
  "total_marks": 51.0,
  "max_marks": 75.0,
  "percentage": 68.0,
  "tasks": {
    "task_1": { ... },
    "task_2": { ... },
    ...
    "task_10": { ... }
  }
}
```

### Task Structure

Each task contains the following fields:

```json
{
  "question_number": 1,
  "compile_error": false,
  "function_signature_changed": false,
  "not_implemented": false,
  "error": null,
  "subtasks": {
    "1.1": {
      "passed": true,
      "marks_awarded": 2,
      "maximum_marks": 2
    },
    "1.2": {
      "passed": false,
      "marks_awarded": 0,
      "maximum_marks": 2
    }
  },
  "compilation_marks_awarded": 2,
  "maximum_compilation_marks": 2,
  "total_marks_awarded": 6,
  "total_maximum_marks": 6
}
```

### Field Explanations

#### Top-Level Fields
- **student_info**: Student identification details
- **total_marks**: Total marks awarded across all tasks
- **max_marks**: Maximum possible marks (75)
- **percentage**: Percentage score (total_marks / max_marks × 100)
- **tasks**: Object containing all 10 tasks

#### Task-Level Fields
- **question_number**: Task number (1-10)
- **compile_error**: `true` if compilation failed
- **function_signature_changed**: `true` if function signature was modified (currently not implemented)
- **not_implemented**: `true` if function is empty or contains only comments
- **error**: Error message (if any)
- **subtasks**: Object containing all subtasks for this task
- **compilation_marks_awarded**: Marks for successful compilation (awarded only if all subtasks pass)
- **maximum_compilation_marks**: Maximum compilation marks for this task
- **total_marks_awarded**: Total marks for this task (subtasks + compilation)
- **total_maximum_marks**: Maximum marks for this task

#### Subtask-Level Fields
- **passed**: `true` if test case passed, `false` otherwise
- **marks_awarded**: Marks awarded for this subtask
- **maximum_marks**: Maximum marks for this subtask

### Example: Task 1 (compareTo)

```json
{
  "question_number": 1,
  "compile_error": false,
  "function_signature_changed": false,
  "not_implemented": false,
  "error": null,
  "subtasks": {
    "1.1": {
      "passed": true,
      "marks_awarded": 2,
      "maximum_marks": 2
    },
    "1.2": {
      "passed": true,
      "marks_awarded": 2,
      "maximum_marks": 2
    }
  },
  "compilation_marks_awarded": 2,
  "maximum_compilation_marks": 2,
  "total_marks_awarded": 6,
  "total_maximum_marks": 6
}
```

**Interpretation**:
- Student passed both test cases (1.1 and 1.2)
- Received 2 marks for each subtask (total 4 marks)
- Received 2 compilation marks (because all subtasks passed)
- Total marks: 6/6

### Example: Task with Compilation Error

```json
{
  "question_number": 2,
  "compile_error": true,
  "function_signature_changed": false,
  "not_implemented": false,
  "error": "Exception in thread \"main\" java.lang.NullPointerException...",
  "subtasks": {
    "2.1": {
      "passed": false,
      "marks_awarded": 0,
      "maximum_marks": 1
    },
    "2.2": {
      "passed": false,
      "marks_awarded": 0,
      "maximum_marks": 1
    },
    "2.3": {
      "passed": false,
      "marks_awarded": 0,
      "maximum_marks": 1
    },
    "2.4": {
      "passed": false,
      "marks_awarded": 0,
      "maximum_marks": 1
    }
  },
  "compilation_marks_awarded": 0,
  "maximum_compilation_marks": 2,
  "total_marks_awarded": 0,
  "total_maximum_marks": 6
}
```

**Interpretation**:
- Code had a runtime error (NullPointerException)
- All subtasks failed (0 marks)
- No compilation marks awarded
- Total marks: 0/6

## Rubric Details

### Task 1: compareTo (6 marks)
- **Compilation marks**: 2 (awarded only if all subtasks pass)
- **Test Case 1.1** (2 marks): Different capacities comparison
- **Test Case 1.2** (2 marks): Same capacity, different room numbers

### Task 2: equals (6 marks)
- **Compilation marks**: 2
- **Test Case 2.1** (1 mark): Same building, different room number
- **Test Case 2.2** (1 mark): Different building, same room number
- **Test Case 2.3** (1 mark): Different building and room number
- **Test Case 2.4** (1 mark): Same building and room number

### Task 3: BY_BUILDING_THEN_ROOM Comparator (6 marks)
- **Compilation marks**: 2
- **Test Case 3.1** (2 marks): Different buildings comparison
- **Test Case 3.2** (2 marks): Same building, different room numbers

### Task 4: addRoom (10 marks)
- **Compilation marks**: 3
- **Test Case 4.1** (1 mark): Invalid room number validation
- **Test Case 4.2** (1 mark): Invalid capacity validation
- **Test Case 4.3** (1.5 marks): Duplicate room handling
- **Test Case 4.4** (1 mark): Add room to list
- **Test Case 4.5** (1.5 marks): Initialize bookingsByRoomKey
- **Test Case 4.6** (1 mark): Return OK

### Task 5: removeRoom (8 marks)
- **Compilation marks**: 2
- **Test Case 5.1** (2 marks): Remove from list
- **Test Case 5.2** (2 marks): Remove from bookingsByRoomKey
- **Test Case 5.3** (1 mark): Return OK
- **Test Case 5.4** (1 mark): Return ROOM_NOT_FOUND

### Task 6: getRoom (5 marks)
- **Compilation marks**: 1.5
- **Test Case 6.1** (2 marks): Return room
- **Test Case 6.2** (1.5 marks): Return null

### Task 7: filterRooms (8 marks)
- **Compilation marks**: 2
- **Test Case 7.1** (1 mark): Filter by capacity
- **Test Case 7.2** (1 mark): Filter by building
- **Test Case 7.3** (2 marks): Filter by projector
- **Test Case 7.4** (2 marks): Filter by internet

### Task 8: bookRoom (10 marks)
- **Compilation marks**: 3
- **Test Case 8.1** (0.5 marks): Invalid hour validation
- **Test Case 8.2** (0.5 marks): Room not found
- **Test Case 8.3** (0.5 marks): Insufficient capacity
- **Test Case 8.4** (0.5 marks): Projector not available
- **Test Case 8.5** (0.5 marks): Internet not available
- **Test Case 8.6** (0.5 marks): Already booked
- **Test Case 8.7** (3 marks): Add to bookingsByRoomKey
- **Test Case 8.8** (1 mark): Return OK

### Task 9: isAvailable (8 marks)
- **Compilation marks**: 2
- **Test Case 9.1** (1 mark): Invalid hour validation
- **Test Case 9.2** (1 mark): Room not found
- **Test Case 9.3** (2 marks): Return OK if not booked
- **Test Case 9.4** (2 marks): Return ALREADY_BOOKED if booked

### Task 10: getAvailableRoomsByHour (8 marks)
- **Compilation marks**: 2
- **Test Case 10.1** (1 mark): Invalid hour returns empty list
- **Test Case 10.2** (2 marks): Filter rooms correctly
- **Test Case 10.3** (3 marks): Return available rooms

**Total Marks**: 75

## Troubleshooting

### Issue: "Error: Solution directory 'solution' not found"

**Solution**: Ensure you're running the script from the correct directory:
```bash
cd /path/to/oop-evaluation
python3 script_rubric.py
```

### Issue: "Error: Student solutions directory 'student_solutions' not found"

**Solution**: Create the student_solutions directory:
```bash
mkdir student_solutions
```

### Issue: "No Java file found for StudentName"

**Solution**: Ensure each student folder contains exactly one Java file:
```bash
ls student_solutions/StudentName/
```

### Issue: Compilation Errors

**Common causes**:
1. Student modified method signatures
2. Student added/removed fields
3. Student code has syntax errors

**Solution**: The script will mark these tasks with `compile_error: true` and award 0 marks.

### Issue: "Execution timeout"

**Solution**: The script has a 10-second timeout for compilation and execution. If a student's code runs indefinitely, it will timeout. Check the student's code for infinite loops.

### Issue: Java not found

**Solution**: Ensure Java is installed and in PATH:
```bash
# Check Java installation
java -version
javac -version

# If not installed, install Java
# On macOS:
brew install openjdk

# On Ubuntu/Debian:
sudo apt-get install openjdk-11-jdk
```

## Examples

### Example 1: Successful Evaluation

```json
{
  "student_info": {
    "name": "John Doe",
    "id_number": "2023A7PS1234P"
  },
  "total_marks": 68.0,
  "max_marks": 75.0,
  "percentage": 90.67,
  "tasks": {
    "task_1": {
      "total_marks_awarded": 6,
      "total_maximum_marks": 6
    },
    "task_2": {
      "total_marks_awarded": 6,
      "total_maximum_marks": 6
    }
    // ... other tasks
  }
}
```

### Example 2: Partial Marks

```json
{
  "task_4": {
    "question_number": 4,
    "compile_error": false,
    "subtasks": {
      "4.1": { "passed": true, "marks_awarded": 1 },
      "4.2": { "passed": true, "marks_awarded": 1 },
      "4.3": { "passed": false, "marks_awarded": 0 },
      "4.4": { "passed": true, "marks_awarded": 1 },
      "4.5": { "passed": true, "marks_awarded": 1.5 },
      "4.6": { "passed": true, "marks_awarded": 1 }
    },
    "compilation_marks_awarded": 0,
    "total_marks_awarded": 5.5,
    "total_maximum_marks": 10.0
  }
}
```

**Interpretation**: Student got 5.5/10 marks for Task 4. They passed 5 out of 6 subtasks, but failed subtask 4.3 (duplicate room handling). Since not all subtasks passed, no compilation marks were awarded.

### Example 3: Batch Summary

```json
{
  "total_students": 6,
  "students": {
    "Student1": {
      "name": "John Doe",
      "total_marks": 68.0,
      "max_marks": 75.0,
      "percentage": 90.67
    },
    "Student2": {
      "name": "Jane Smith",
      "total_marks": 51.0,
      "max_marks": 75.0,
      "percentage": 68.0
    }
  },
  "overall_stats": {
    "total_marks": 357.0,
    "max_marks": 450.0,
    "average_percentage": 79.33
  }
}
```

## Advanced Usage

### Customizing Rubric Marks

To modify the rubric, edit the `self.rubrics` dictionary in `script_rubric.py`:

```python
self.rubrics = {
    1: {
        "name": "compareTo",
        "compilation_marks": 2,
        "subtasks": {
            "1.1": {"marks": 2, "description": "Different capacities"},
            "1.2": {"marks": 2, "description": "Same capacity, different room numbers"}
        }
    },
    # ... other tasks
}
```

### Adding New Test Cases

To add new test cases, edit `solution/RoomsService_RubricBased.java`:

```java
// Add new test case
System.out.println("TEST_CASE:1.3");
// ... test code ...
System.out.println("Result: " + (condition ? "PASS" : "FAIL"));
```

Then update the rubric in `script_rubric.py`:

```python
"subtasks": {
    "1.1": {"marks": 2, "description": "Different capacities"},
    "1.2": {"marks": 2, "description": "Same capacity, different room numbers"},
    "1.3": {"marks": 2, "description": "New test case"}
}
```

## Support

For issues or questions:
1. Check the Troubleshooting section
2. Verify all prerequisites are met
3. Check the evaluation report JSON for detailed error messages
4. Review the student's Java code for syntax errors

## License

This evaluation system is provided for educational purposes.

---

**Last Updated**: 2025
**Version**: 2.0 (Rubric-Based)
