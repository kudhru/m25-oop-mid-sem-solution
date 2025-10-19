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

def get_task_marks(report: Dict[str, Any], task_key: str) -> Optional[float]:
    """Safely extracts the total_marks_awarded for a given task key."""
    try:
        # Use .get() to safely navigate the dictionary structure
        return report.get('tasks', {}).get(task_key, {}).get('total_marks_awarded')
    except AttributeError:
        # This handles cases where intermediate keys might not exist, though the
        # structure seems consistent based on the example.
        return None

def compare_reports(old_reports: Dict[str, Dict[str, Any]], new_reports: Dict[str, Dict[str, Any]]) -> List[List[str]]:
    """
    Compares marks between old and new reports for all students and tasks.
    Returns a list of rows for the CSV file.
    """
    # Header row
    diff_rows = [["Student Name", "ID Number", "Task Number", "Old Marks", "New Marks"]]

    # Identify all unique student IDs present in either set of reports
    all_student_ids = set(old_reports.keys()) | set(new_reports.keys())
    
    for student_id in sorted(list(all_student_ids)):
        old_report = old_reports.get(student_id)
        new_report = new_reports.get(student_id)

        # Skip students if report is missing in both versions, or if new report is missing
        if not new_report:
            if old_report:
                print(f"Note: Student {student_id} found in OLD but missing in NEW. Skipping comparison.")
            continue

        if not old_report:
            print(f"Note: Student {student_id} found in NEW but missing in OLD. Assuming all tasks are new/changed.")
            # For new students, we still want to establish their identity info
            student_name = new_report.get('student_info', {}).get('name', student_id)
            id_number = new_report.get('student_info', {}).get('id_number', 'N/A')
            
            # Treat all new tasks as having a change from 0 to new marks
            for task_key in sorted(new_report.get('tasks', {}).keys()):
                new_marks = get_task_marks(new_report, task_key)
                if new_marks is not None and new_marks > 0:
                    diff_rows.append([
                        student_name,
                        id_number,
                        task_key.replace('task_', ''),
                        "0.0",
                        f"{new_marks:.1f}"
                    ])
            continue


        # Extract common student info from the NEW report
        student_name = new_report.get('student_info', {}).get('name', student_id)
        id_number = new_report.get('student_info', {}).get('id_number', 'N/A')

        # Identify all unique tasks
        all_task_keys = set(old_report.get('tasks', {}).keys()) | set(new_report.get('tasks', {}).keys())

        # List to hold changes for the current student
        student_changes = []

        for task_key in sorted(list(all_task_keys)):
            old_marks = get_task_marks(old_report, task_key)
            new_marks = get_task_marks(new_report, task_key)

            # If both marks exist and they are different, or if one is missing (considered a change)
            if old_marks != new_marks:
                # Format marks for output, treating None as 0.0 for comparison
                old_m_out = f"{old_marks:.1f}" if old_marks is not None else "0.0"
                new_m_out = f"{new_marks:.1f}" if new_marks is not None else "0.0"

                student_changes.append([
                    student_name,
                    id_number,
                    task_key.replace('task_', ''),
                    old_m_out,
                    new_m_out
                ])

        # Append all changes for the current student
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
        print(f"Total tasks with marks changes: {len(csv_rows) - 1}")
    except Exception as e:
        print(f"Error writing CSV file: {e}")

if __name__ == "__main__":
    # Create mock directories and data for demonstration/testing if they don't exist
    if not os.path.exists(OLD_DIR) or not os.path.exists(NEW_DIR):
        print("Creating mock input structure for demonstration...")
        
        # Mock student data (based on the provided evaluation_report.json structure)
        mock_report_template = json.loads('''
        {
          "student_info": {
            "name": "STUDENT_NAME",
            "id_number": "ID_NUMBER"
          },
          "total_marks": 75.0,
          "max_marks": 75.0,
          "percentage": 100.0,
          "tasks": {
            "task_1": {"total_marks_awarded": 6.0, "question_number": 1},
            "task_2": {"total_marks_awarded": 4.0, "question_number": 2},
            "task_3": {"total_marks_awarded": 0.0, "question_number": 3},
            "task_4": {"total_marks_awarded": 10.0, "question_number": 4},
            "task_5": {"total_marks_awarded": 8.0, "question_number": 5}
          }
        }
        ''')

        def create_mock_report(path, name, id_num, t2_marks, t3_marks):
            report = mock_report_template.copy()
            report['student_info']['name'] = name
            report['student_info']['id_number'] = id_num
            report['tasks']['task_2']['total_marks_awarded'] = t2_marks
            report['tasks']['task_3']['total_marks_awarded'] = t3_marks
            os.makedirs(path, exist_ok=True)
            with open(os.path.join(path, REPORT_FILENAME), 'w') as f:
                json.dump(report, f, indent=2)

        # --- OLD Reports ---
        os.makedirs(os.path.join(OLD_DIR, "2023A"), exist_ok=True)
        create_mock_report(os.path.join(OLD_DIR, "2023A"), "Alice A", "2023A", 4.0, 0.0)
        os.makedirs(os.path.join(OLD_DIR, "2023B"), exist_ok=True)
        create_mock_report(os.path.join(OLD_DIR, "2023B"), "Bob B", "2023B", 4.0, 6.0)

        # --- NEW Reports ---
        os.makedirs(os.path.join(NEW_DIR, "2023A"), exist_ok=True)
        # Alice: Task 2 fixed (4.0 -> 6.0), Task 3 still 0.0 (No change)
        create_mock_report(os.path.join(NEW_DIR, "2023A"), "Alice A", "2023A", 6.0, 0.0) 
        os.makedirs(os.path.join(NEW_DIR, "2023B"), exist_ok=True)
        # Bob: Task 2 regressed (4.0 -> 0.0), Task 3 still 6.0 (No change)
        create_mock_report(os.path.join(NEW_DIR, "2023B"), "Bob B", "2023B", 0.0, 6.0)
        os.makedirs(os.path.join(NEW_DIR, "2023C"), exist_ok=True)
        # New Student Charlie: Task 3 got full marks (change from 0.0 -> 6.0)
        create_mock_report(os.path.join(NEW_DIR, "2023C"), "Charlie C", "2023C", 4.0, 6.0)
        
        print(f"Mock data created in '{OLD_DIR}' and '{NEW_DIR}'. Running comparison.")

    main()
