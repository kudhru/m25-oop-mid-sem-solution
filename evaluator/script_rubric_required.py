#!/usr/bin/env python3
"""
OOP Mid-Semester Exam Evaluator - Rubric-Based Version
Evaluates student submissions based on detailed rubric with subtasks
"""

import re
import subprocess
import json
import os
import shutil
from pathlib import Path
from typing import Dict, List, Tuple, Any, Optional
import tempfile


class RubricEvaluator:
    def __init__(self, student_file: str, solution_dir: str):
        self.student_file = student_file
        self.solution_dir = solution_dir
        # Use rubric-based testing version
        rubric_solution = os.path.join(solution_dir, "RoomsService_RubricBased.java")
        if os.path.exists(rubric_solution):
            self.solution_file = rubric_solution
        else:
            # Fall back to regular solution
            self.solution_file = os.path.join(solution_dir, "RoomsService.java")
        self.temp_dir = tempfile.mkdtemp()
        # Directory where per-student evaluation reports are stored (set by caller)
        # initialize as empty string so assignments later have consistent type
        self.output_dir: str = ""
        # Sanitized student name used to name saved temp test files
        self.student_name: str = ""

        # Define rubric structure with marks for each subtask
        self.rubrics = {
            1: {
                "name": "compareTo",
                "compilation_marks": 2,
                "subtasks": {
                    "1.1": {"marks": 2, "description": "Different capacities"},
                    "1.2": {
                        "marks": 2,
                        "description": "Same capacity, different room numbers",
                    },
                },
            },
            2: {
                "name": "equals",
                "compilation_marks": 2,
                "subtasks": {
                    "2.1": {
                        "marks": 1,
                        "description": "Same building, different room number",
                    },
                    "2.2": {
                        "marks": 1,
                        "description": "Different building, same room number",
                    },
                    "2.3": {
                        "marks": 1,
                        "description": "Different building and room number",
                    },
                    "2.4": {"marks": 1, "description": "Same building and room number"},
                },
            },
            3: {
                "name": "BY_BUILDING_THEN_ROOM",
                "compilation_marks": 2,
                "subtasks": {
                    "3.1": {"marks": 2, "description": "Different buildings"},
                    "3.2": {
                        "marks": 2,
                        "description": "Same building, different room numbers",
                    },
                },
            },
            4: {
                "name": "addRoom",
                "compilation_marks": 3,
                "subtasks": {
                    "4.1": {"marks": 1, "description": "Invalid room number"},
                    "4.2": {"marks": 1, "description": "Invalid capacity"},
                    "4.3": {"marks": 1.5, "description": "Duplicate room"},
                    "4.4": {"marks": 1, "description": "Add room to list"},
                    "4.5": {
                        "marks": 1.5,
                        "description": "Initialize bookingsByRoomKey",
                    },
                    "4.6": {"marks": 1, "description": "Return OK"},
                },
            },
            5: {
                "name": "removeRoom",
                "compilation_marks": 2,
                "subtasks": {
                    "5.1": {"marks": 2, "description": "Remove from list"},
                    "5.2": {"marks": 2, "description": "Remove from bookingsByRoomKey"},
                    "5.3": {"marks": 1, "description": "Return OK"},
                    "5.4": {"marks": 1, "description": "Return ROOM_NOT_FOUND"},
                },
            },
            6: {
                "name": "getRoom",
                "compilation_marks": 1.5,
                "subtasks": {
                    "6.1": {"marks": 2, "description": "Return room"},
                    "6.2": {"marks": 1.5, "description": "Return null"},
                },
            },
            7: {
                "name": "filterRooms",
                "compilation_marks": 2,
                "subtasks": {
                    "7.1": {"marks": 1, "description": "Filter by capacity"},
                    "7.2": {"marks": 1, "description": "Filter by building"},
                    "7.3": {"marks": 2, "description": "Filter by projector"},
                    "7.4": {"marks": 2, "description": "Filter by internet"},
                },
            },
            8: {
                "name": "bookRoom",
                "compilation_marks": 3,
                "subtasks": {
                    "8.1": {"marks": 0.5, "description": "Invalid hour"},
                    "8.2": {"marks": 0.5, "description": "Room not found"},
                    "8.3": {"marks": 0.5, "description": "Insufficient capacity"},
                    "8.4": {"marks": 0.5, "description": "Projector not available"},
                    "8.5": {"marks": 0.5, "description": "Internet not available"},
                    "8.6": {"marks": 0.5, "description": "Already booked"},
                    "8.7": {"marks": 3, "description": "Add to bookingsByRoomKey"},
                    "8.8": {"marks": 1, "description": "Return OK"},
                },
            },
            9: {
                "name": "isAvailable",
                "compilation_marks": 2,
                "subtasks": {
                    "9.1": {"marks": 1, "description": "Invalid hour"},
                    "9.2": {"marks": 1, "description": "Room not found"},
                    "9.3": {"marks": 2, "description": "Return OK if not booked"},
                    "9.4": {
                        "marks": 2,
                        "description": "Return ALREADY_BOOKED if booked",
                    },
                },
            },
            10: {
                "name": "getAvailableRoomsByHour",
                "compilation_marks": 2,
                "subtasks": {
                    "10.1": {
                        "marks": 1,
                        "description": "Invalid hour returns empty list",
                    },
                    "10.2": {"marks": 2, "description": "Filter rooms correctly"},
                    "10.3": {"marks": 3, "description": "Return available rooms"},
                },
            },
        }

    def read_file(self, filepath: str) -> str:
        """Read content from a file"""
        with open(filepath, "r", encoding="utf-8") as f:
            return f.read()

    def write_file(self, filepath: str, content: str):
        """Write content to a file"""
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)

    def extract_student_info(self, content: str) -> Dict[str, str]:
        """Extract student information from the file header"""
        info = {
            "name": "Not Provided",
            "id_number": "Not Provided",
            "lab_number": "Not Provided",
            "system_number": "Not Provided",
        }

        name_match = re.search(r"Name:\s*(.+)", content)
        id_match = re.search(r"ID Number:\s*(.+)", content)
        lab_match = re.search(r"Lab Number:\s*(.+)", content)
        system_match = re.search(r"System Number:\s*(.+)", content)

        if name_match:
            info["name"] = name_match.group(1).strip()
        if id_match:
            info["id_number"] = id_match.group(1).strip()
        if lab_match:
            info["lab_number"] = lab_match.group(1).strip()
        if system_match:
            info["system_number"] = system_match.group(1).strip()

        return info

    def extract_function(self, content: str, task_num: int) -> Tuple[str, bool]:
        """Extract a specific function from student code"""
        task = self.rubrics[task_num]
        name = task["name"]

        # Pattern to match the function
        if task_num == 3:
            pattern = r"public\s+static\s+final\s+java\.util\.Comparator<Room> BY_BUILDING_THEN_ROOM\s+=\s+([\s\S]*?\}\s*);"
        elif task_num == 7:
            pattern = r"RoomPredicate\s+predicate\s*=\s*(.*?);"
        else:
            # For regular methods
            if task_num == 1:
                pattern = r"public\s+int\s+compareTo\s*\(\s*Room\s+other\s*\)\s*\{(.*?)\n\s{4}\}"
            elif task_num == 2:
                pattern = r"public\s+boolean\s+equals\s*\(\s*Object\s+o\s*\)\s*\{(.*?)\n\s{4}\}"
            elif task_num == 4:
                pattern = r"public\s+ErrorCode\s+addRoom\s*\(\s*Room\s+room\s*\)\s*\{(.*?)\n\s{2}\}"
            elif task_num == 5:
                pattern = r"public\s+ErrorCode\s+removeRoom\s*\(\s*Building\s+building\s*,\s*String\s+roomNumber\s*\)\s*\{(.*?)\n\s{2}\}"
            elif task_num == 6:
                pattern = r"public\s+Room\s+getRoom\s*\(\s*Building\s+building\s*,\s*String\s+roomNumber\s*\)\s*\{(.*?)\n\s{2}\}"
            elif task_num == 8:
                pattern = (
                    r"public\s+ErrorCode\s+bookRoom\s*\([^)]+\)\s*\{(.*?)\n\s{2}\}"
                )
            elif task_num == 9:
                pattern = r"public\s+ErrorCode\s+isAvailable\s*\(\s*Building\s+building\s*,\s*String\s+roomNumber\s*,\s*int\s+hour\s*\)\s*\{(.*?)\n\s{2}\}"
            elif task_num == 10:
                pattern = r"public\s+List<Room>\s+getAvailableRoomsByHour\s*\([^)]+\)\s*\{(.*?)\n\s{2}\}"

        match = re.search(pattern, content, re.DOTALL)

        if not match:
            return "", True  # Not implemented

        extracted = match.group(1).strip()

        # Check if it's just a comment or placeholder
        if (
            not extracted
            or extracted == "/* Write your code here */"
            or len(extracted) < 5
        ):
            return "", True

        return extracted, False

    def create_test_file(self, task_num: int, student_impl: str) -> str:
        """Create a test file with student implementation injected"""
        solution_content = self.read_file(self.solution_file)
        task = self.rubrics[task_num]

        if task_num == 3:
            # Match everything from = up to the closing '};' of the comparator block (multi-line lambda)
            pattern = r"(public\s+static\s+final\s+java\.util\.Comparator<Room>\s+BY_BUILDING_THEN_ROOM\s*=\s*)([\s\S]*?\}\s*;)"
            replacement = r"\1 " + student_impl + ";"
        elif task_num == 7:
            pattern = r"(RoomPredicate\s+predicate\s*=\s*).*?;"
            replacement = r"\1" + student_impl + ";"
        else:
            if task_num == 1:
                pattern = r"(public\s+int\s+compareTo\s*\(\s*Room\s+other\s*\)\s*\{).*?(\n\s{4}\})"
            elif task_num == 2:
                pattern = r"(public\s+boolean\s+equals\s*\(\s*Object\s+o\s*\)\s*\{).*?(\n\s{4}\})"
            elif task_num == 4:
                pattern = r"(public\s+ErrorCode\s+addRoom\s*\(\s*Room\s+room\s*\)\s*\{).*?(\n\s{2}\})"
            elif task_num == 5:
                pattern = r"(public\s+ErrorCode\s+removeRoom\s*\(\s*Building\s+building\s*,\s*String\s+roomNumber\s*\)\s*\{).*?(\n\s{2}\})"
            elif task_num == 6:
                pattern = r"(public\s+Room\s+getRoom\s*\(\s*Building\s+building\s*,\s*String\s+roomNumber\s*\)\s*\{).*?(\n\s{2}\})"
            elif task_num == 8:
                pattern = (
                    r"(public\s+ErrorCode\s+bookRoom\s*\([^)]+\)\s*\{).*?(\n\s{2}\})"
                )
            elif task_num == 9:
                pattern = r"(public\s+ErrorCode\s+isAvailable\s*\(\s*Building\s+building\s*,\s*String\s+roomNumber\s*,\s*int\s+hour\s*\)\s*\{).*?(\n\s{2}\})"
            elif task_num == 10:
                pattern = r"(public\s+List<Room>\s+getAvailableRoomsByHour\s*\([^)]+\)\s*\{).*?(\n\s{2}\})"

            replacement = r"\1\n" + student_impl + r"\2"

        modified_content = re.sub(
            pattern, replacement, solution_content, flags=re.DOTALL
        )

        test_file = os.path.join(self.temp_dir, "RoomsService.java")
        self.write_file(test_file, modified_content)

        # Also save a copy of the generated test file into the evaluation reports folder
        # with a filename based on student name and task number (e.g. "StudentName_task1.java").
        try:
            if getattr(self, "output_dir", None) and getattr(
                self, "student_name", None
            ):
                safe_name = re.sub(r"[^A-Za-z0-9_.-]", "_", self.student_name)
                student_test_filename = f"{safe_name}_task{task_num}.java"
                student_test_path = os.path.join(self.output_dir, student_test_filename)
                # Write the modified content to the student's evaluation reports folder
                self.write_file(student_test_path, modified_content)
        except Exception:
            # Don't fail evaluation if saving temp files fails
            pass

        return test_file

    def compile_and_run(self, java_file: str) -> Tuple[bool, str, str]:
        """Compile and run a Java file, return success status and output"""
        try:
            java_dir = os.path.dirname(java_file)
            if not java_dir:
                java_dir = "."

            compile_result = subprocess.run(
                ["javac", java_file],
                capture_output=True,
                text=True,
                timeout=10,
                cwd=java_dir,
            )

            if compile_result.returncode != 0:
                return False, "", compile_result.stderr

            run_result = subprocess.run(
                ["java", "-cp", java_dir, "RoomsService"],
                capture_output=True,
                text=True,
                timeout=10,
                cwd=java_dir,
            )

            if run_result.returncode != 0:
                return False, run_result.stdout, run_result.stderr

            return True, run_result.stdout, run_result.stderr

        except subprocess.TimeoutExpired:
            return False, "", "Execution timeout"
        except Exception as e:
            return False, "", str(e)

    def parse_test_results(
        self, output: str, task_num: int
    ) -> Dict[str, Dict[str, Any]]:
        """Parse test results from output"""
        results = {}
        rubric = self.rubrics[task_num]

        for subtask_id in rubric["subtasks"].keys():
            # Look for the test case in output
            pattern = rf"TEST_CASE:{subtask_id}.*?Result:\s*(PASS|FAIL)"
            match = re.search(pattern, output, re.DOTALL)

            if match:
                passed = match.group(1) == "PASS"
                results[subtask_id] = {
                    "passed": passed,
                    "marks_awarded": (
                        rubric["subtasks"][subtask_id]["marks"] if passed else 0
                    ),
                    "maximum_marks": rubric["subtasks"][subtask_id]["marks"],
                }
            else:
                results[subtask_id] = {
                    "passed": False,
                    "marks_awarded": 0,
                    "maximum_marks": rubric["subtasks"][subtask_id]["marks"],
                }

        return results

    def evaluate_task(self, task_num: int, student_impl: str) -> Dict[str, Any]:
        """Evaluate a single task based on rubric"""
        # Initialize subtasks structure with all subtasks
        subtasks = {}
        for subtask_id in self.rubrics[task_num]["subtasks"]:
            subtasks[subtask_id] = {
                "passed": False,
                "marks_awarded": 0,
                "maximum_marks": self.rubrics[task_num]["subtasks"][subtask_id][
                    "marks"
                ],
            }

        result = {
            "question_number": task_num,
            "compile_error": False,
            "function_signature_changed": False,
            "not_implemented": False,
            "error": None,
            "subtasks": subtasks,
            "compilation_marks_awarded": 0,
            "maximum_compilation_marks": self.rubrics[task_num]["compilation_marks"],
            "total_marks_awarded": 0,
            "total_maximum_marks": sum(
                self.rubrics[task_num]["subtasks"][st]["marks"]
                for st in self.rubrics[task_num]["subtasks"]
            )
            + self.rubrics[task_num]["compilation_marks"],
        }

        if not student_impl:
            result["not_implemented"] = True
            return result

        # Create test file with student implementation
        test_file = self.create_test_file(task_num, student_impl)

        # Compile and run
        success, output, error = self.compile_and_run(test_file)

        if not success:
            result["compile_error"] = True
            result["error"] = error[:200]
            return result

        # Parse test results
        subtask_results = self.parse_test_results(output, task_num)
        result["subtasks"] = subtask_results

        # Calculate marks
        subtask_marks = sum(
            subtask_results[st]["marks_awarded"] for st in subtask_results
        )

        # Award compilation marks only if all subtasks passed
        all_passed = all(subtask_results[st]["passed"] for st in subtask_results)
        if all_passed:
            result["compilation_marks_awarded"] = self.rubrics[task_num][
                "compilation_marks"
            ]

        result["total_marks_awarded"] = (
            subtask_marks + result["compilation_marks_awarded"]
        )

        return result

    def evaluate(self) -> Dict[str, Any]:
        """Main evaluation function"""
        print("Starting rubric-based evaluation...")

        student_content = self.read_file(self.student_file)
        student_info = self.extract_student_info(student_content)
        # Store a student name to use for naming saved temp files. Prefer provided name,
        # otherwise fall back to id number.
        name_for_file = student_info.get("name") or ""
        if not name_for_file or name_for_file == "Not Provided":
            name_for_file = student_info.get("id_number", "")
        self.student_name = name_for_file.strip()
        print(f"Evaluating submission for: {student_info['name']}")

        results = {}
        total_marks = 0
        max_total_marks = 0

        for task_num in range(1, 11):
            print(f"Evaluating Task {task_num}...")
            student_impl, not_impl = self.extract_function(student_content, task_num)

            if not_impl:
                rubric = self.rubrics[task_num]
                # Initialize subtasks structure with all subtasks
                subtasks = {}
                for subtask_id in rubric["subtasks"]:
                    subtasks[subtask_id] = {
                        "passed": False,
                        "marks_awarded": 0,
                        "maximum_marks": rubric["subtasks"][subtask_id]["marks"],
                    }

                results[f"task_{task_num}"] = {
                    "question_number": task_num,
                    "compile_error": False,
                    "function_signature_changed": False,
                    "not_implemented": True,
                    "error": None,
                    "subtasks": subtasks,
                    "compilation_marks_awarded": 0,
                    "maximum_compilation_marks": rubric["compilation_marks"],
                    "total_marks_awarded": 0,
                    "total_maximum_marks": sum(
                        rubric["subtasks"][st]["marks"] for st in rubric["subtasks"]
                    )
                    + rubric["compilation_marks"],
                }
            else:
                task_result = self.evaluate_task(task_num, student_impl)
                results[f"task_{task_num}"] = task_result
                total_marks += task_result["total_marks_awarded"]

            max_total_marks += results[f"task_{task_num}"]["total_maximum_marks"]

        report = {
            "student_info": student_info,
            "total_marks": total_marks,
            "max_marks": max_total_marks,
            "percentage": (
                round((total_marks / max_total_marks) * 100, 2)
                if max_total_marks > 0
                else 0
            ),
            "tasks": results,
        }

        return report

    def cleanup(self):
        """Clean up temporary files"""
        try:
            shutil.rmtree(self.temp_dir)
        except:
            pass


class BatchRubricEvaluator:
    def __init__(self, student_solutions_dir: str, solution_dir: str, output_dir: str):
        self.student_solutions_dir = student_solutions_dir
        self.solution_dir = solution_dir
        self.output_dir = output_dir

    def get_student_directories(self) -> List[str]:
        """Get list of student directories"""
        student_dirs = []
        if os.path.exists(self.student_solutions_dir):
            for item in os.listdir(self.student_solutions_dir):
                item_path = os.path.join(self.student_solutions_dir, item)
                if os.path.isdir(item_path) and not item.startswith("."):
                    student_dirs.append(item)
        return student_dirs

    def find_student_java_file(self, student_dir: str) -> Optional[str]:
        """Find the Java file in student directory"""
        student_path = os.path.join(self.student_solutions_dir, student_dir)

        # Look for any .java file
        for file in os.listdir(student_path):
            if file.endswith(".java"):
                return os.path.join(student_path, file)

        return None

    def create_output_structure(self, student_id: str):
        """Create output directory structure for a student"""
        student_output_dir = os.path.join(self.output_dir, student_id)
        os.makedirs(student_output_dir, exist_ok=True)
        return student_output_dir

    def process_student(self, student_id: str) -> Optional[Dict[str, Any]]:
        """Process a single student's solution"""
        print(f"Processing student: {student_id}")

        student_file = self.find_student_java_file(student_id)
        if not student_file:
            print(f"  No Java file found for {student_id}")
            return None

        # Create output directory for this student before evaluation so temporary
        # test files can be saved there during evaluation.
        output_dir = self.create_output_structure(student_id)
        evaluator = RubricEvaluator(student_file, self.solution_dir)
        evaluator.output_dir = output_dir

        try:
            report = evaluator.evaluate()

            # Copy student's solution to output directory
            import shutil

            student_output_file = os.path.join(
                output_dir, os.path.basename(student_file)
            )
            shutil.copy2(student_file, student_output_file)

            # Save evaluation report
            report_file = os.path.join(output_dir, "evaluation_report.json")
            with open(report_file, "w", encoding="utf-8") as f:
                json.dump(report, f, indent=2)

            print(
                f"  Completed: {report['total_marks']}/{report['max_marks']} marks ({report['percentage']}%)"
            )
            return report

        except Exception as e:
            print(f"  Error processing {student_id}: {str(e)}")
            return None
        finally:
            evaluator.cleanup()

    def process_all_students(self) -> Dict[str, Any]:
        """Process all student solutions"""
        print("Starting batch rubric-based evaluation...")

        os.makedirs(self.output_dir, exist_ok=True)

        student_dirs = self.get_student_directories()

        if not student_dirs:
            print("No student directories found!")
            return {}

        print(f"Found {len(student_dirs)} student directories")

        results = {}
        for student_id in student_dirs:
            result = self.process_student(student_id)
            if result:
                results[student_id] = result

        summary = self.create_summary_report(results)
        summary_file = os.path.join(self.output_dir, "batch_summary.json")
        with open(summary_file, "w", encoding="utf-8") as f:
            json.dump(summary, f, indent=2)

        print(f"\nBatch evaluation complete! Processed {len(results)} students.")
        print(f"Results saved in: {self.output_dir}")

        return results

    def create_summary_report(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """Create a summary report of all evaluations"""
        summary = {"total_students": len(results), "students": {}}

        total_marks_all = 0
        max_marks_all = 0

        for student_id, report in results.items():
            student_summary = {
                "name": report.get("student_info", {}).get("name", "Unknown"),
                "id_number": report.get("student_info", {}).get(
                    "id_number", student_id
                ),
                "total_marks": report.get("total_marks", 0),
                "max_marks": report.get("max_marks", 0),
                "percentage": report.get("percentage", 0.0),
            }
            summary["students"][student_id] = student_summary

            total_marks_all += report.get("total_marks", 0)
            max_marks_all += report.get("max_marks", 0)

        summary["overall_stats"] = {
            "total_marks": total_marks_all,
            "max_marks": max_marks_all,
            "average_percentage": round(
                (total_marks_all / max_marks_all * 100) if max_marks_all > 0 else 0, 2
            ),
        }

        return summary


def main():
    """Main execution function"""
    import sys

    student_solutions_dir = "student_solutions"
    solution_dir = "solution"
    output_dir = "evaluation_reports"

    if not os.path.exists(solution_dir):
        print(f"Error: Solution directory '{solution_dir}' not found")
        return

    if not os.path.exists(student_solutions_dir):
        print(f"Error: Student solutions directory '{student_solutions_dir}' not found")
        return

    batch_evaluator = BatchRubricEvaluator(
        student_solutions_dir, solution_dir, output_dir
    )
    try:
        results = batch_evaluator.process_all_students()

        if results:
            print(f"\n{'='*60}")
            print("BATCH EVALUATION SUMMARY")
            print(f"{'='*60}")
            print(f"Total Students Processed: {len(results)}")

            for student_id, report in results.items():
                print(
                    f"{student_id}: {report['total_marks']}/{report['max_marks']} marks ({report['percentage']}%)"
                )

            print(f"\nResults saved in: {output_dir}")
            print(f"{'='*60}\n")

    except Exception as e:
        print(f"Error during batch evaluation: {str(e)}")
        import traceback

        traceback.print_exc()


if __name__ == "__main__":
    main()
