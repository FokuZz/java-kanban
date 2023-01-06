# Это репозиторий технического задания "Трекер задач"

---
## Класс родитель "Task"
Чтобы небыло необходимости создавать метод для каждого класса, мы создали абстрактный класс родитель, где в будущем
необходимые методы можно было бы переопределить, либо использовать интерфейс.
```
public abstract class model.Task {
    private String name;
    private Integer id;
    private String description;
    private String status = "NEW";


    model.Task(String name, String description){
        this.name = name;
        this.description = description;
        this.id = this.hashCode();
    }
```
Код показан без геттеров и сеттеров, а так по модификаторам доступа видно что код написан по основам ООП.

От него у нас будет создан классы наследователи __Epic__ и __Subtask__

---
## Менеджер трекера "InMemoryTaskManager"
Он будет запускаться на старте программы и управлять всеми задачами.

Для управления задачами используются данные публичные методы:
```
getAllTasks()    // Выводит эпики и их сабтаски если у них один айди

deleteAllTasks()    // Удаляет все задачи

getById(int id)    // Выводит эпики и их сабтаски по заданному айди

createTask(model.Task task)    //  Общий метод для 2х массивов Epics и Subtasks

updateTask(model.Task task)    // Обновляет статус задачи

deleteAllById(int id), deleteEpicById(int id), deleteSubtaskById(int id)   // Удаляет епики и сабтаски по id

getAllSubtaskEpic(model.Epic epic)    //Выводит все сабтаски заданного епика
```
## Менеджер истории "InMemoryHistoryManager"
Он будет запускаться на старте программы и управлять всеми задачами.

Для управления задачами используются данные публичные методы:
```
add(Task task)    // Добавляет в список просмотренный класс

getHistory()    // Возвращает список истории 

```
Спасибо за просмотр реадми
