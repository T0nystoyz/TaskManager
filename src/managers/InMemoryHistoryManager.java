package managers;

import tasks.Task;
import utils.LimitedQueue;

public class InMemoryHistoryManager implements HistoryManager {

    public LimitedQueue<Task> historyList = new LimitedQueue<>(6); // выбираем любой размер листа

    @Override
    public void add(Task task) {
       historyList.add(task);
    }

    @Override
    public LimitedQueue<Task> getHistory() {
        return historyList;
    }
}