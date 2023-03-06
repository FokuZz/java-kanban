package test.service;

import service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager getTaskManager(){
        return new InMemoryTaskManager();
    }


}