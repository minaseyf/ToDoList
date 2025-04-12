import db.Database;
import db.Entity;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;
import todo.service.StepService;
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
            while (!input.equals("exit")) {
                input = scanner.nextLine();
                if(input.equals("add task"))
                    addTask();
                if(input.equals("add step"))
                    addStep();
                if(input.equals("delete"))
                    delete();
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

    public static Date stringToDate(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty!");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // Ensures strict date parsing
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format! Expected format: yyyy-MM-dd");
        }
    }

    public static void addStep() {
        try {
            System.out.print("TaskID: ");
            int taskID = scanner.nextInt();
            System.out.print("Title: ");
            String title = scanner.nextLine();

            int stepID = StepService.saveStep(taskID, title);
            Step step = (Step) Database.get(stepID);
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

    public static void delete() {
        try {
            System.out.print("ID: ");
            int id = scanner.nextInt();
            Entity e = Database.get(id);
            if (e instanceof Task) {
                ArrayList<Entity> allSteps = Database.getAll(Step.Step_ENTITY_CODE);
                for (Entity s : allSteps) {
                    if (((Step) s).taskRef == id) {
                        Database.delete(s.id);
                    }
                }
            }
            Database.delete(id);
            System.out.println("Entity with ID=" + id + " successfully deleted.");
        } catch (EntityNotFoundException e) {
            System.out.println("Cannot delete entity with ID=" + e.getMessage().split("=")[1] + ".");
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Cannot delete entity.");
            System.out.println("Error: Invalid format.");
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
                if (((Step) s).taskRef == s.id) {
                    System.out.println("    + " + ((Step) s).title + ":");
                    System.out.println("      ID: " + s.id);
                    System.out.println("      Status: " + ((Step) s).status);
                }
            }
            System.out.println();
        }
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
                    if (((Step) s).taskRef == s.id) {
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
