package task.service.managers.task;

public class TaskValidationException extends Exception {
    public TaskValidationException(final String message) {
        super(message);
    }

    public TaskValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaskValidationException(final Throwable cause) {
        super(cause);
    }
}
