Задача 4

Потребно е да се направи Chat Room апликација, каде повеќе клиенти се поврзуваат на еден сервер преку TCP/IP конекција, и комуницираат меѓусебно.

Серверот, при секое поврзување од клиент креира посебна нишка, која се справува со пораките добиени од соодветниот клиент и препраќање кон бараниот примач. Сите отворени сокети серверот ги чува во HashMap, и кога клиентот ја затвора конекцијата, се остранува и сокетот.

Секој клиент при креирање добива уникатен идентификувачки број, ID, кој се проследува преку конструктор. При иницијализација на клиентот, инцијално параќа почетна порака до серверот со тоа што го проследува соодветниот ID. Клиентот може да испрати порака до друг клиент преку сервверот, со тоа што пораката што ја праќа до серверот е во следен формат:

MESSAGE:RECEIVER_ID

Пример: Клиент со ID 1 праќа порака "Hello from client 1" до клиент со ID 2. Пораката пратена до серверот треба да биде како во прилог:

"Hello from client 1:2"

Серверот, при добивање на порака од клиент, го извлекува ID на примачот од добиената порака, и ја препраќа само пораката до одредениот клиент ( На соодветниот сокет)

Клиентот ја затвара конекцијата до серверот со праќање на пораката "END"

Почетниот код е даден во прилог