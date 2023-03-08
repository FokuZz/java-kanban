package test.service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class HistoryManagerTest {
    CustomLinkedList<Task> history;
    ArrayList<Task> prioritizedHistory;
    static Epic epic1,epic2;
    static Subtask subtask1, subtask2, subtask3;
    @BeforeEach
    void createHistoryManager() {
        history = new CustomLinkedList<>();
        prioritizedHistory = new ArrayList<>();
    }

    @BeforeAll
    static void createTasks() {
        epic1 = new Epic("Сходить в магазин","Надо купить продуктов");
        subtask1 = new Subtask("Купить молоко", "Стоимость 80 рублей", StatusTask.DONE);
        subtask2 = new Subtask("Купить яица","Стоимость 100 рублей", StatusTask.DONE);
        epic2 = new Epic("Сделать омлет", "Надо приготовить омлет из продкутов" , LocalDateTime.of(2023,1,2,1,0),15);
        subtask3 = new Subtask("Купить продукты для омлета", "Надо 180р. чтобы сделать омлет");
    }
    // a. Пустая история задач.
    // b. Дублирование.

    // с. Удаление из истории: начало, середина, конец.
    //Насчёт пункта с я понял так, что это относится лишь к методу remove, ибо что на удаление проверять я не понимаю
    @Test
    void addEmptyHistory() {
        history.linkLast(epic1);
        history.linkLast(epic2);
        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic2,epic1)
        );
        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void addDuplication() {
        history.linkLast(epic1);
        history.linkLast(epic2);
        history.linkLast(epic2);
        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic2,epic1)
        );
        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void removeEmptyHistory() {
        history.remove(epic1);
        ArrayList<Task> historyExpected = new ArrayList<>();
        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void removeFirst() {
        history.linkLast(epic1);
        history.linkLast(subtask1);
        history.linkLast(subtask2);

        history.remove(epic1);
        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(subtask2,subtask1)
        );

        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void removeSecond() {
        history.linkLast(epic1);
        history.linkLast(subtask1);
        history.linkLast(subtask2);

        history.remove(subtask1);
        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(subtask2,epic1)
        );

        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void removeLast() {
        history.linkLast(epic1);
        history.linkLast(subtask1);
        history.linkLast(subtask2);

        history.remove(subtask2);
        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(subtask1,epic1)
        );

        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void getHistoryStandart() {
        history.linkLast(epic1);
        history.linkLast(subtask1);
        history.linkLast(subtask2);

        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(subtask2,subtask1,epic1)
        );

        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void getHistoryEmptyHistory() {
        ArrayList<Task> historyExpected = new ArrayList<>();

        Assertions.assertEquals(historyExpected, history.getTasks());
    }

    @Test
    void setHistoryStandart() {
        history.linkLast(epic1);
        history.linkLast(subtask1);
        history.linkLast(subtask2);

        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic2,subtask3)
        );



        CustomLinkedList<Task> testHistory = new CustomLinkedList<>();
        for(int i = historyExpected.size() - 1; i >= 0  ; i--){
            testHistory.linkLast(historyExpected.get(i));
        }

        history = testHistory;

        Assertions.assertEquals(historyExpected,history.getTasks());
    }

    @Test
    void setHistoryEmptyHistory() {

        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic2,subtask3)
        );



        CustomLinkedList<Task> testHistory = new CustomLinkedList<>();
        for(int i = historyExpected.size() - 1; i >= 0  ; i--){
            testHistory.linkLast(historyExpected.get(i));
        }

        history = testHistory;

        Assertions.assertEquals(historyExpected,history.getTasks());
    }

    @Test
    void getPrioritizedHistoryStandart() {
        history.linkLast(epic2);
        history.linkLast(epic1);

        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic2,epic1)
        );

        prioritizedHistory = history.getTasks();
        Collections.sort(prioritizedHistory,Task::compareTime);



        Assertions.assertEquals(historyExpected,prioritizedHistory);
    }

    @Test
    void getPrioritizedHistoryEmptyHistory() {
        ArrayList<Task> historyExpected = new ArrayList<>();

        Assertions.assertEquals(historyExpected, prioritizedHistory);
    }
}