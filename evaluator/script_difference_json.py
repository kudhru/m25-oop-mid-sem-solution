import json
import csv
import os
from typing import Dict, Any, List, Optional

# --- Configuration ---
OLD_DIR = "input_old"
NEW_DIR = "input_new"
OUTPUT_CSV = "marks_changed_report.csv"
REPORT_FILENAME = "evaluation_report.json"

# --- Helper Functions ---

def load_student_reports(base_dir: str) -> Dict[str, Dict[str, Any]]:
    """
    Loads all evaluation_report.json files from immediate subdirectories of base_dir.
    Returns a dictionary mapping student_id (folder name) to the report content.
    """
    reports = {}
    if not os.path.exists(base_dir):
        print(f"Error: Directory '{base_dir}' not found. Cannot proceed.")
        return reports

    for student_id in os.listdir(base_dir):
        student_path = os.path.join(base_dir, student_id)
        report_path = os.path.join(student_path, REPORT_FILENAME)

        if os.path.isdir(student_path) and os.path.exists(report_path):
            try:
                with open(report_path, 'r', encoding='utf-8') as f:
                    reports[student_id] = json.load(f)
            except json.JSONDecodeError:
                print(f"Warning: Could not decode JSON in {report_path}. Skipping.")
            except Exception as e:
                print(f"Warning: An error occurred reading {report_path}: {e}. Skipping.")
    return reports

# The old get_task_marks is no longer needed as we now access marks granularly.

def compare_reports(old_reports: Dict[str, Dict[str, Any]], new_reports: Dict[str, Dict[str, Any]]) -> List[List[str]]:
    """
    Compares marks between old and new reports for all students, including
    individual subtasks and compilation marks, and calculates the difference.
    Returns a list of rows for the CSV file.
    """
    # Header row: Added "Difference"
    diff_rows = [["Student Name", "ID Number", "Task Number", "Old Marks", "New Marks", "Difference"]]

    # Identify all unique student IDs present in either set of reports
    all_student_ids = set(old_reports.keys()) | set(new_reports.keys())
    
    for student_id in sorted(list(all_student_ids)):
        old_report = old_reports.get(student_id, {})
        new_report = new_reports.get(student_id, {})

        # Skip if report is missing in both versions
        if not old_report and not new_report:
            continue
        
        # Determine student info from the newer report or fallback to old
        source_report = new_report if new_report else old_report
        student_name = source_report.get('student_info', {}).get('name', student_id)
        id_number = source_report.get('student_info', {}).get('id_number', 'N/A')

        # Identify all unique tasks
        old_tasks = old_report.get('tasks', {})
        new_tasks = new_report.get('tasks', {})
        all_task_keys = set(old_tasks.keys()) | set(new_tasks.keys())

        # List to hold changes for the current student
        student_changes = []

        for task_key in sorted(list(all_task_keys)):
            old_task = old_tasks.get(task_key, {})
            new_task = new_tasks.get(task_key, {})
            
            # --- 1. Compare Compilation Marks ---
            old_comp_marks = old_task.get('compilation_marks_awarded', 0.0)
            new_comp_marks = new_task.get('compilation_marks_awarded', 0.0)
            
            if old_comp_marks != new_comp_marks:
                # Convert to float for calculation (needed if marks were retrieved as ints in JSON, though they are floats here)
                old_m = float(old_comp_marks)
                new_m = float(new_comp_marks)
                difference = new_m - old_m
                
                task_id_name = f"{task_key.replace('task_', '')} (compilation)"
                student_changes.append([
                    student_name,
                    id_number,
                    task_id_name,
                    f"{old_m:.1f}",
                    f"{new_m:.1f}",
                    f"{difference:+.1f}" # Use '+.1f' for sign display
                ])

            # --- 2. Compare Subtask Marks ---
            old_subtasks = old_task.get('subtasks', {})
            new_subtasks = new_task.get('subtasks', {})
            all_subtask_ids = set(old_subtasks.keys()) | set(new_subtasks.keys())
            
            for subtask_id in sorted(list(all_subtask_ids)):
                old_sub_marks = old_subtasks.get(subtask_id, {}).get('marks_awarded', 0.0)
                new_sub_marks = new_subtasks.get(subtask_id, {}).get('marks_awarded', 0.0)
                
                if old_sub_marks != new_sub_marks:
                    # Convert to float for calculation
                    old_m = float(old_sub_marks)
                    new_m = float(new_sub_marks)
                    difference = new_m - old_m
                    
                    student_changes.append([
                        student_name,
                        id_number,
                        subtask_id, # e.g., "1.1", "4.5"
                        f"{old_m:.1f}",
                        f"{new_m:.1f}",
                        f"{difference:+.1f}" # Use '+.1f' for sign display
                    ])

        # Append all granular changes for the current student
        diff_rows.extend(student_changes)

    return diff_rows

def main():
    """Main function to run the comparison and generate the CSV."""
    print(f"Loading reports from '{OLD_DIR}' and '{NEW_DIR}'...")
    old_reports = load_student_reports(OLD_DIR)
    new_reports = load_student_reports(NEW_DIR)

    if not old_reports and not new_reports:
        print("No reports found in either directory. Exiting.")
        return

    print(f"Found {len(old_reports)} old reports and {len(new_reports)} new reports.")

    # Get the rows of data for the CSV
    csv_rows = compare_reports(old_reports, new_reports)

    if len(csv_rows) <= 1:
        print("\nNo changes in marks detected across all students and tasks.")
        return

    # Write the CSV file
    try:
        with open(OUTPUT_CSV, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerows(csv_rows)
        print(f"\nSuccessfully generated change report: {OUTPUT_CSV}")
        print(f"Total granular changes detected: {len(csv_rows) - 1}")
    except Exception as e:
        print(f"Error writing CSV file: {e}")

if __name__ == "__main__":
    # Create mock directories and data for demonstration/testing if they don't exist
    if not os.path.exists(OLD_DIR) or not os.path.exists(NEW_DIR):
        print("Creating mock input structure for demonstration...")
        
        # Mock student data template reflecting subtasks and compilation marks
        mock_report_template = {
          "student_info": {
            "name": "STUDENT_NAME",
            "id_number": "ID_NUMBER"
          },
          "total_marks": 75.0,
          "max_marks": 75.0,
          "percentage": 100.0,
          "tasks": {
            "task_1": {
              "compilation_marks_awarded": 2.0, 
              "subtasks": {
                "1.1": {"marks_awarded": 2.0},
                "1.2": {"marks_awarded": 2.0}
              }
            },
            "task_2": {
              "compilation_marks_awarded": 2.0, 
              "subtasks": {
                "2.1": {"marks_awarded": 1.0},
                "2.2": {"marks_awarded": 1.0}
              }
            }
          }
        }

        def create_mock_report(path, name, id_num, t1_comp, t1_1, t2_comp, t2_2):
            report = json.loads(json.dumps(mock_report_template)) # Deep copy
            report['student_info']['name'] = name
            report['student_info']['id_number'] = id_num
            
            # Set Task 1 marks
            report['tasks']['task_1']['compilation_marks_awarded'] = t1_comp
            report['tasks']['task_1']['subtasks']['1.1']['marks_awarded'] = t1_1
            
            # Set Task 2 marks
            report['tasks']['task_2']['compilation_marks_awarded'] = t2_comp
            report['tasks']['task_2']['subtasks']['2.2']['marks_awarded'] = t2_2
            
            os.makedirs(path, exist_ok=True)
            with open(os.path.join(path, REPORT_FILENAME), 'w') as f:
                json.dump(report, f, indent=2)

        # --- OLD Reports ---
        os.makedirs(os.path.join(OLD_DIR, "A001"), exist_ok=True)
        # Old: Alice had no compilation, 1.1 passed, 2.2 failed
        create_mock_report(os.path.join(OLD_DIR, "A001"), "Alice A", "A001", 0.0, 2.0, 2.0, 0.0)
        os.makedirs(os.path.join(OLD_DIR, "B002"), exist_ok=True)
        # Old: Bob had compilation, 1.1 failed, 2.2 passed
        create_mock_report(os.path.join(OLD_DIR, "B002"), "Bob B", "B002", 2.0, 0.0, 0.0, 1.0) 

        # --- NEW Reports ---
        os.makedirs(os.path.join(NEW_DIR, "A001"), exist_ok=True)
        # New: Alice got compilation marks (0.0 -> 2.0), 2.2 fixed (0.0 -> 1.0)
        create_mock_report(os.path.join(NEW_DIR, "A001"), "Alice A", "A001", 2.0, 2.0, 2.0, 1.0) 
        os.makedirs(os.path.join(NEW_DIR, "B002"), exist_ok=True)
        # New: Bob lost compilation marks (2.0 -> 0.0), 1.1 fixed (0.0 -> 2.0)
        create_mock_report(os.path.join(NEW_DIR, "B002"), "Bob B", "B002", 0.0, 2.0, 0.0, 1.0) 
        
        print(f"Mock data created in '{OLD_DIR}' and '{NEW_DIR}'. Running comparison.")

    main()
