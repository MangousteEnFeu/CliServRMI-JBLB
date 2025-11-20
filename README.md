# SCL-CH7-Sample



## Exemple d'utilisation du RMI

### RMI-Server

Exemple simple d'utilisation du RMI.

Il y a une interface `Hello` qui contient une méthode `sayHello` qui prend un `String` en paramètre et retourne un `String`.

Il y a une classe `HelloImpl` qui implémente l'interface `Hello`.

Il y a une classe `Main` qui crée une instance de `HelloImpl` et la lie à un registre RMI.

### RMI-Client

Il y a une classe `Main` qui se connecte au registre RMI et récupère l'instance de `Hello` pour appeler la méthode `sayHello`.

Il y a une copie de l'interface `Hello` dans le projet client pour éviter les problèmes de classpath. Cela illustre également le client qui ne serait pas sur la même machine que le serveur.

## Exemple de RMI + Protocole 

Test 
