# Это репозиторий технического задания "Трекер задач"

---
## Класс родитель "Task"
Чтобы небыло необходимости создавать метод для каждого класса, мы создали абстрактный класс родитель, где в будущем
необходимые методы можно было бы переопределить, либо использовать интерфейс.
```
public abstract class Task {
    private String name;
    private Integer id;
    private String description;
    private String status = "NEW";


    Task(String name, String description){
        this.name = name;
        this.description = description;
        this.id = this.hashCode();
    }
```
Код показан без геттеров и сеттеров, а так по модификаторам доступа видно что код написан по основам ООП.

От него у нас будет создан классы наследователи __Epic__ и __Subtask__

---
## Менеджер трекера "TaskManager"
Он будет запускаться на старте программы и управлять всеми задачами.

Для управления задачами используются данные публичные методы:
```
getAllTasks()    // Выводит эпики и их сабтаски если у них один айди

deleteAllTasks()    // Удаляет все задачи

getById(int id)    // Выводит эпики и их сабтаски по заданному айди

createTask(Task task)    //  Общий метод для 2х массивов Epics и Subtasks

updateTask(Task task)    // Обновляет статус задачи

deleteById(int id)    // Удаляет епики и сабтаски по id

getAllSubtaskEpic(Epic epic)    //Выводит все сабтаски заданного епика
```
Спасибо за просмотр реадми
