# FileExecutor_ComputerNetworking

Proiect realizat in echipa, in limbajul de programare Java, pentru disciplina 
Retele de Calculatoare. Aplicatia este una client-server, cu interfata grafica
conceputa cu ajutorul WindowBuilder din eclipse (proiect Java SWT).

Cerinte:
Motor pentru executia de script-uri la distanta:
Clientii se conecteaza la server si publica o lista de script-uri identificate prin nume unic la nivelul server-ului;
Server-ul tine lista clientilor impreuna cu asocierea ce script pe care client se gaseste;
Pe durata unei sesiuni cu server-ul un client nu-si poate modifica lista de script-uri disponibile;
Un client poate publica pe server o comanda compusa identificata printr-un nume unic la nivelul server-ului, 
constand intr-o secventa de script-uri apelabile in conducta cu un fisier de intrare primit ca argument, 
urmand ca iesirea generata de primul script din secventa sa fie trimisa ca intrare pentru cel de-al doilea
si tot asa pana la ultimul script a carui iesire va constitui rezultatul executiei comenzii;
Clientii pot suprascrie comenzile deja publicate prin publicarea pe server a uneia cu acelasi nume;
Clientii pot solicita stergerea unei comenzi de pe server pe baza numelui acesteia;
Server-ul primeste fisiere pentru executia unei comenzi compuse identificata dupa numele fisierului de 
intrare care trebuie sa coincida cu cel al comenzii de executat;
Fisierul rezultat in urma executiei comenzii este trimis clientului care a initiat executia comenzii.
