import db.Database;
import db.Entity;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;
import todo.service.StepService;
import todo.service.TaskService;
import todo.validator.StepValidator;
import todo.validator.TaskValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws InvalidEntityException {
        String firstInput = scanner.nextLine();
        if(!firstInput.equals("exit")) {
            String input = "";
            Database.registerValidator(Task.Task_ENTITY_CODE, new TaskValidator());
            Database.registerValidator(Step.Step_ENTITY_CODE, new StepValidator());
            while (!input.equals("exit")) {
                input = scanner.nextLine();
                if(input.equals("add task"))
                    addTask();
                if(input.equals("add step"))
                    addStep();
                if(input.equals("delete"))
                    delete();
                if(input.equals("update task"))
                    updateTask();
                if(input.equals("update step"))
                    updateStep();
                if(input.equals("get task-by-id"))
                    getTaskByID();
                if(input.equals("get all-tasks"))
                    getAllTasks();
                if(input.equals("get incomplete-tasks"))
                    getIncompleteTasks();
            }
            scanner.close();
        }
    }

    public static void addTask() {
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            System.out.print("Due date: ");
            Date dueDate = stringToDate(scanner.nextLine());

            Task task = new Task(title, description, dueDate);
            TaskValidator taskValidator = new TaskValidator();
            taskValidator.validate(task);
            Database.add(task);

            System.out.println("Task saved successfully.");
            System.out.println("ID: " + task.id);
        } catch (InvalidEntityException e) {
            System.out.println("Cannot save task.");
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
        System.out.println("Cannot save task.");
            System.out.println("Error: Something is invalid.");
        }
    }

    public static void addStep() {
        try {
            System.out.print("TaskID: ");
            int taskID = Integer.parseInt(scanner.nextLine());
            System.out.print("Title: ");
            String title = scanner.nextLine();

            int stepID = StepService.saveStep(taskID, title);
            Step step = (Step) Database.get(stepID);
            StepValidator stepValidator = new StepValidator();
            stepValidator.validate(step);

            System.out.println("Step saved successfully.");
            System.out.println("ID: " + stepID);
            System.out.println("Creation Date: " + step.creationDate);
        } catch (InvalidEntityException e) {
            System.out.println("Cannot save step.");
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Cannot save step.");
            System.out.println("Error: Invalid format.");
        } catch (EntityNotFoundException e) {
            System.out.println("Cannot save step.");
            System.out.println("Error: Cannot find task with ID=" + e.getMessage().split("=")[1]);
        }
    }

    public static Date stringToDate(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty!");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format! Expected format: yyyy-MM-dd");
        }
    }

    public static void delete() {
        try {
            System.out.print("ID: ");
            int ID = scanner.nextInt();
            Entity e = Database.get(ID);
            if (e instanceof Task) {
                ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
                for (Entity s : allSteps) {
                    if (((Step) s).taskRef == ID) {
                        Database.delete(s.id);
                    }
                }
            }
            Database.delete(ID);
            System.out.println("Entity with ID=" + ID + " successfully deleted.");
        } catch (EntityNotFoundException e) {
            System.out.println("Cannot delete entity with ID=" + e.getMessage().split("=")[1] + ".");
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Cannot delete entity.");
            System.out.println("Error: Invalid format.");
        }
    }

    public static void updateTask() {
        try {
            System.out.println("ID: ");
            int ID = Integer.parseInt(scanner.nextLine());
            System.out.println("Field: ");
            String field = scanner.nextLine();
            System.out.print("New Value: ");
            String newValue = scanner.nextLine();
            int valid = 1;

            Task preTask = (Task) Database.get(ID);
            Task newTask = (Task) preTask.copy();

            if(field.equals("title")) {
                newTask.title = newValue;
                Database.update(newTask);
                System.out.println("Successfully updated the task.");
                System.out.println("Field: title");
                System.out.println("Old Value: " + preTask.title);
                System.out.println("New Value: " + newTask.title);
                System.out.println("Modification Date: " + newTask.lastModificationDate);

            }
            else if(field.equals("description")) {
                newTask.description = newValue;
                Database.update(newTask);
                System.out.println("Successfully updated the task.");
                System.out.println("Field: description");
                System.out.println("Old Value: " + preTask.description);
                System.out.println("New Value: " + newTask.description);
                System.out.println("Modification Date: " + newTask.lastModificationDate);
            }
            else if(field.equals("dueDate")) {
                newTask.dueDate = stringToDate(newValue);
                Database.update(newTask);
                System.out.println("Successfully updated the task.");
                System.out.println("Field: dueDate");
                System.out.println("Old Value: " + preTask.dueDate);
                System.out.println("New Value: " + newTask.dueDate);
                System.out.println("Modification Date: " + newTask.lastModificationDate);
            }
            else if(field.equals("status")) {
                if(newValue.equals("completed")) {
                    TaskService.setAsCompleted(ID);
                    ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
                    for (Entity s : allSteps) {
                        if (((Step) s).taskRef == ID) {
                            StepService.setAsCompleted(s.id);
                        }
                    }
                }
                else if(newValue.equals("inProgress")) {
                    TaskService.setAsInProgress(ID);
                }
                else {
                    System.out.println("invalid status input!(please try again)");
                    valid = 0;
                }

                if(valid == 1) {
                    Task task = (Task) Database.get(ID);
                    System.out.println("Successfully updated the task.");
                    System.out.println("Field: status");
                    System.out.println("Old Value: " + preTask.status);
                    System.out.println("New Value: " + task.status);
                    System.out.println("Modification Date: " + task.lastModificationDate);
                }
            }
            else {
                System.out.println("invalid input!(please try again)");
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Cannot update task with ID=" + e.getMessage().split("=")[1] + ".");
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidEntityException e) {
            System.out.println("Cannot update task.");
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Cannot update task.");
            System.out.println("Error: something is invalid.");
        }
    }

    public static void updateStep() {
        try{
            System.out.println("ID: ");
            int ID = Integer.parseInt(scanner.nextLine());
            System.out.println("Field: ");
            String field = scanner.nextLine();
            System.out.print("New Value: ");
            String newValue = scanner.nextLine();
            int valid = 1;

            Step preStep = (Step) Database.get(ID);
            Step newStep = (Step) preStep.copy();

            if(field.equals("title")) {
                newStep.title = newValue;
                Database.update(newStep);
                System.out.println("Successfully updated the step.");
                System.out.println("Field: title");
                System.out.println("Old Value: " + preStep.title);
                System.out.println("New Value: " + newStep.title);
                System.out.println("Modification Date: " + newStep.lastModificationDate);
            }
            else if(field.equals("status")) {
                if(newValue.equals("completed")) {
                    StepService.setAsCompleted(ID);
                    int Steps = 0;
                    int completedSteps = 0;
                    Task task = (Task) Database.get(newStep.taskRef);
                    ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
                    for (Entity s : allSteps) {
                        if (((Step) s).taskRef == task.id) {
                            Steps++;
                            if(((Step) s).status == Step.Status.Completed)
                                completedSteps++;
                        }
                    }
                    if(Steps != 0 && Steps == completedSteps) {
                        TaskService.setAsCompleted(task.id);
                    }
                    if(Steps > completedSteps) {
                        TaskService.setAsInProgress(task.id);
                    }
                }
                else {
                    System.out.println("invalid status input!(please try again)");
                    valid = 0;
                }

                if(valid == 1) {
                    Step step = (Step) Database.get(ID);
                    System.out.println("Successfully updated the step.");
                    System.out.println("Field: status");
                    System.out.println("Old Value: " + preStep.status);
                    System.out.println("New Value: " + step.status);
                    System.out.println("Modification Date: " + step.lastModificationDate);
                }
            }
            else {
                System.out.println("invalid input!(please try again)");
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Cannot update step with ID=" + e.getMessage().split("=")[1] + ".");
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidEntityException e) {
            System.out.println("Cannot update step.");
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Cannot update step.");
            System.out.println("Error: something is invalid.");
        }
    }

    public static void getAllTasks() {
        ArrayList<Entity> allTasks = Database.getAll(Task.Task_ENTITY_CODE);
        allTasks.sort(Comparator.comparing(task -> ((Task) task).dueDate));
        for (Entity e : allTasks) {
            System.out.println("ID: " + e.id);
            System.out.println("Title: " + ((Task) e).title);
            System.out.println("Due Date: " + ((Task) e).dueDate);
            System.out.println("Status: " + ((Task) e).status);
            ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
            System.out.println("Steps: ");
            for (Entity s : allSteps) {
                if (((Step) s).taskRef == e.id) {
                    System.out.println("    + " + ((Step) s).title + ":");
                    System.out.println("      ID: " + s.id);
                    System.out.println("      Status: " + ((Step) s).status);
                }
            }
            System.out.println();
        }
    }

    public static void getTaskByID() {
        System.out.println("ID: ");
        int ID = Integer.parseInt(scanner.nextLine());
        ArrayList<Entity> allTasks = Database.getAll(Task.Task_ENTITY_CODE);
        int foundTask = 0;
        for (Entity e : allTasks) {
            if(e.id == ID) {
                foundTask = 1;
                System.out.println("ID: " + e.id);
                System.out.println("Title: " + ((Task) e).title);
                System.out.println("Due Date: " + ((Task) e).dueDate);
                System.out.println("Status: " + ((Task) e).status);
                ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
                int foundStep = 0;
                for (Entity s : allSteps) {
                    if (((Step) s).taskRef == e.id) {
                        if(foundStep == 0)
                            System.out.println("Steps: ");
                        System.out.println("    + " + ((Step) s).title + ":");
                        System.out.println("      ID: " + s.id);
                        System.out.println("      Status: " + ((Step) s).status);
                        foundStep++;
                    }
                }
                if(foundStep == 0)
                    System.out.println("This task has no steps yet!");
            }
        }
        if(foundTask == 0)
            System.out.println("Cannot find task with ID=" + ID + ".");
    }

    public static void getIncompleteTasks() {
        ArrayList<Entity> allTasks = Database.getAll(Task.Task_ENTITY_CODE);
        for (Entity e : allTasks) {
            if (((Task) e).status != Task.Status.Completed) {
                System.out.println("ID: " + e.id);
                System.out.println("Title: " + ((Task) e).title);
                System.out.println("Due Date: " + ((Task) e).dueDate);
                System.out.println("Status: " + ((Task) e).status);
                ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
                System.out.println("Steps: ");
                for (Entity s : allSteps) {
                    if (((Step) s).taskRef == e.id) {
                        System.out.println("    + " + ((Step) s).title + ":");
                        System.out.println("      ID: " + s.id);
                        System.out.println("      Status: " + ((Step) s).status);
                    }
                }
                System.out.println();
            }
        }
    }
}
