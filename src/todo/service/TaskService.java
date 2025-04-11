package todo.service;

import db.Database;
import db.Entity;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

public class TaskService {
    public static void setAsCompleted(int taskId) throws InvalidEntityException {
        Entity e = Database.get(taskId);
        if(e ==  null)
            throw new InvalidEntityException("Invalid task!");

        Task t = (Task) e;
        t.status = Task.Status.Completed;
        Database.update(t);
    }

    public static void setAsInProgress(int taskId) throws InvalidEntityException {
        Entity e = Database.get(taskId);
        if(e ==  null)
            throw new InvalidEntityException("Invalid task!");

        Task t = (Task) e;
        t.status = Task.Status.InProgress;
        Database.update(t);
    }
}
