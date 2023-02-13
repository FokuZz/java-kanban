[33mcommit 02e1503f453b8be88588b625b6a1dd2779d69f46[m[33m ([m[1;36mHEAD -> [m[1;32mmain[m[33m)[m
Merge: 92b2ad1 7e5605f
Author: FokuZz <foxsus@yandex.ru>
Date:   Mon Feb 13 10:55:31 2023 +0300

    Merge branch 'main' of github.com:FokuZz/java-kanban

[33mcommit 92b2ad119e45987a333910b96a98a1cb5743afb5[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Mon Feb 13 10:54:45 2023 +0300

    fix: исправления в FileBackedTasksManager

[33mcommit 7e5605f23a310268830765eb3ebb226161a62783[m[33m ([m[1;31morigin/main[m[33m, [m[1;31morigin/HEAD[m[33m)[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sun Feb 12 04:53:26 2023 +0300

    fix: устранение ошибок, и доработка в комментариях

[33mcommit 54ea29a356969236e05f854c9e96acc94c6033c1[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sat Feb 11 17:34:32 2023 +0300

    feat: всё сделал для работы с файлом, остались ошибки которые нужно убрать

[33mcommit c9756d5f259c5c2d6455d8ca7773be27f02503a0[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Fri Feb 10 17:38:23 2023 +0300

    feat: новый менеджер для создания файлов в папке с проектом

[33mcommit 166674e0598d79d703e575c732c9939f0c243cdd[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sun Jan 22 23:15:14 2023 +0300

    style: Более корректное название переменных и приватности

[33mcommit bbe9f00c8bc38855c522a25646fc8a9d6732b42a[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sun Jan 22 21:27:53 2023 +0300

    feat: Создал свой CustomLinkedList, который имеет в себе HashMap и двухсвязный список из-за чего может сразу находить элементы для удаления. Добавил новые методы для работы с историей

[33mcommit 65c098af029bd4a597e9d7f89b6e43a19f3c8f00[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sat Jan 7 15:40:39 2023 +0300

    refractor : Перенёс менеджеры в пакет service, и поменял метод getHistory  в менджере истории

[33mcommit a3652a4644a310c43c5d1a3e3ed94a01991dff32[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Fri Jan 6 20:10:48 2023 +0300

    feat : Добавление 2-ух интерфейсов, HistoryManager, TaskManager. 1-го утилитарного класса Managers и изменение названия TaskManager>InMemoryTaskManager. Новый функционал который добавляет методы get для каждого из классов задач, и класс InMemoryHistoryManager который позволяет отслеживать 10 последних вызовов к классам задачи

[33mcommit 0f1f9bcc39faf55ea68c1885d0e3468c4b36d2d1[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sun Dec 25 00:28:05 2022 +0300

    style: добавил новые методы в инструкцию Readme

[33mcommit 799375e871959a9f0ba8c82a89d0a22db70f4027[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sun Dec 25 00:13:57 2022 +0300

    refractor: Переопределил методы setId,toString,hashCode в классе-родителе Task

[33mcommit fcfa55fe057636503a3d22928b62fb50526d10b6[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Sat Dec 24 14:34:21 2022 +0300

    refractor: deleteById разделил на 3 метода, удаления отдельных эпиков и сабтасков и общего удаления, и раскидал всё по пакетам

[33mcommit 81c919ea6a3c74f8bc2d056fc153e97c4d8fb634[m
Author: FokuZz <foxsus@yandex.ru>
Date:   Thu Dec 22 23:05:03 2022 +0300

    Трекер задач V0.1

[33mcommit 8711d67011f1958a1a5c2d375fa2acafb3c9189f[m
Author: Artem <56774094+FokuZz@users.noreply.github.com>
Date:   Sat Dec 17 15:00:44 2022 +0300

    Initial commit
