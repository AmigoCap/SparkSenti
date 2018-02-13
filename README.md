# SparkSenti

Projet d'analyse de sentiments dans les tweet avec Spark / Hadoop

# Prérequis

Pour fonctionner, vous devez avoir à disposition Git, Java8 et sbt sur votre système d'exploitation.

### Windows :

Installer la ligne de commande Linux et se référer à la partie Linux.
[Installation](https://docs.microsoft.com/en-us/windows/wsl/install-win10)

### Linux :

**sbt**

Entrer depuis le terminal les commandes suivantes.
```
sudo apt-get install openjdk-8-jdk openjdk-8-jre
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt expect
```

Vous pouvez vous assurer que sbt est bien installé avec la commande :
```
sbt about
```
**Java8**

Vérifier que vous avez la version Java8 (1.8) à disposition :
```
java -version
```
Vous devait alors avoir :
```
java version "1.8"
```
Si votre version est supérieur, vous devez changer de version :  [Change the default Java (JDK)](https://stackoverflow.com/questions/21964709/how-to-set-or-change-the-default-java-jdk-version-on-os-x)
Si votre version est inférieur, rendez vous sur le site de [Java](https://www.java.com/fr/download/)

### MacOS :

**sbt**
```
brew install sbt@1
```
Vous pouvez vous assurer que sbt est bien installé avec la commande :
```
sbt about
```
**Java8**

Vérifier que vous avez la version Java8 (1.8) à disposition :
```
java -version
```
Vous devait alors avoir :
```
java version "1.8"
```
Si votre version est supérieur, vous devez changer de version :  [Change the default Java (JDK)](https://stackoverflow.com/questions/21964709/how-to-set-or-change-the-default-java-jdk-version-on-os-x)
Si votre version est inférieur, rendez vous sur le site de [Java](https://www.java.com/fr/download/)

# Installation du projet

Depuis votre terminal rendez vous dans le dossier où vous désirez stocker le projet et entrez la commande :
```
git clone https://github.com/AmigoCap/SparkSenti.git
```

Rentrez ensuite dans le dossier de l'algorithme d'analyse :
```
cd SparkSenti/Algo1/
```

# Utilisation

**Lancement de sbt**

Vous devez être dans le dossier `SparkSenti/Algo1/`.
Lancez alors sbt :
```
sbt
```
Sbt va alors télécharger les librairies nécessaires et compiler le code source.
Une fois le processus aboutit vous devez pouvoir lire :
```
sbt:SparkSenti>
```

**Initialiser le communication vers le datacenter**

Lancez la commande :
```
sbt:SparkSenti> init
```
Ce script permet de créer un fichier de configuration pour la connection ssh vers le serveur.
A partir de cette étape vous devez être sur le réseau local de l'Ecole Centrale ou utiliser un VPN.
Le script vous demande d'entrer les paramètres :
* user : votre nom d'utilisateur sur le datacenter Amigo
* host : l'adresse IP du datacenter.
* password : le mot de passe de votre compte sur le datacenter

Vous devez alors pouvoir lire :
```
[success] Total time: 278 s, completed 8 févr. 2018 17:57:12
sbt:SparkSenti>
```
**Envoie des scripts sur le datacenter**

Pour envoyer les scripts sur votre compte sur le datacenter, utilisez la commande :
```
sbt:SparkSenti> pushAll
```
Ce script compresse un dossier contenant les .jar de l'application et de toutes les dépendances, puis l'envoie sur votre serveur et le décompresse.

Pour envoyer sur le datacenter en HDFS le fichier comptenant les tweets à analyser utiliser la commande :
```
sbt:SparkSenti> put "nom_du_fichier.json"
```
Vous avez à dispoition pour tester le fichier `trump.json` dans le repertoir Algo1.

**Lancement de l'algorithme**

Pour lancer l'algorithme un job spark et visualiser les sentiments des tweets stockés dans votre fichier, lancez la commande:
```
sbt:SparkSenti> submit "nom_du_fichier.json"
```

où `nom_du_fichier` est un fichier qui a été `put` au préalable (stocké en HDFS sur le datacer).

**Visualisation des résultats**

Se connecter en SSH à votre compte :
```
ssh user@IP
```
Lire le fichier `defaultoutput.txt` à la racine :
```
cat defaultoutput.txt
```

**Remarque :**

Lorsque vous souhaitez mettre à jour uniquement le code de l'application sur le serveur, vous pouvez utiliser la commande push depuis sbt :
```
sbt:SparkSenti> push
```

La différence entre `push` et `pushAll` est que la première met à jour sur le serveur uniquement le code de l'application, la deuxième compresse le .jar de l'application ainsi que toutes ses dépendances avant de les lancer sur le serveur.
**Il est donc nécessaire de lancer `pushAll` après avoir ajouter une librairie dans le fichier `build.sbt` afin de mettre à jour l'ensemble des librairies sur le serveur**

# Utilisation de Zeppelin

**Prérequis :**

* Un serveur Zeppelin doit être installé sur le datacenter et être en cours d'execution.
* Vous être en possesion de [Firefox](https://www.mozilla.org/fr/firefox/new/)

**Ouverture d'un tunel ssh:**

Pour avoir accès au client du serveur Zeppelin tournant sur le data center, vous devez ouvrir un tunel SSH entre votre machine et le data center.
Pour cela, suivre ce [mode opératoir](https://arliguy.net/2013/06/18/proxy-socks-via-ssh-pour-firefox/)
Une fois, le tunnel SSH ouvert, accédez à l'adresse http://host:port où :
* host : l'ip du server local Zeppelin ouvert sur le datacenter
* port : le port du server local Zeppelin ouvert sur le datacenter
