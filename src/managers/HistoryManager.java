package managers;

import tasks.Task;
import utils.LimitedQueue;

public interface HistoryManager {

    void add(Task task);

    LimitedQueue<Task> getHistory();
}