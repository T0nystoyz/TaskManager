package taskManagers;

import managers.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected void setManager() {
        taskManager = new InMemoryTaskManager();
    }
}