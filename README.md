# Это репозиторий технического задания "Трекер задач" (в разработке)

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
## Менеджеры "HttpTaskManager" -> "FileBackedTaskManager" -> "InMemoryHistoryManager"
Каждый из менеджеров наследует от своего предшественника, за счёт которого сохраняет данные
в указанном типе

У каждого типа 
```
getAllTasks()      // Выводит список всех задач, по группам из Epic и его Сабтаскам

getById(int id)     // Выводит Task по его айди

createTask(Task task)    // Добавляет в менеджер класс

updateTask(Task task)    // Обновляет Task если в списке есть то же имя и описание

deleteAllTasks()        // Удаляет все Task

deleteSubtaskById(int id),      // Удаляет Subtask
deleteEpicById(int epicId),     // Удаляет Epic с его Subtask
deleteEpicOrSubtask(int id)     // Удаляет наследуемые от Task обьекты

getAllSubtaskEpic(int groupId)  // Выводит все Subtask конкретного Epic

getTask(int id), getSubtask(int id), getEpic(int id) // Методы get для вывода

getHistory()    // Возвращает список истории 

removeHistory(int superId)  // Удаляет конкретный Task в истории

clearHistory()  // Очищает историю

getPrioritizedTasks()   // Получает список сортированый по времени, все элементы без времени уходят в конец списка

```
Спасибо за просмотр
