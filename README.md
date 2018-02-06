# SparkSenti

Projet d'analyse de sentiments dans les tweet avec Spark / Hadoop

# Installation

**Sur Windows, vous devez installer la ligne de commande linux**
Installer java 8 et sbt pour votre système d'exploitation:

**Linux**
```
sudo apt-get install openjdk-8-jdk openjre-8-jre
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt expect
```

**Mac**
```
brew install sbt@1
```

Dans le dossier Algo1, lancer: 
```
sbt
```

Il va télécharger les librairies nécessaires.
Une fois dans la console sbt, lancer:
```
init
```

Ce script créer un fichier de configuration pour la connection ssh avec le serveur.
Il vous demandera host, user et password pour vous connecter.

Une fois terminée, lancer, toujours depuis sbt:
```
pushAll
```

Ce script compresse un dossier contenant les .jar de l'application et de toutes les dépendances, puis l'envoie sur votre serveur et le décompresse.
Afin d'utiliser un fichier json pour faire l'analyse de sentiment, lancer, dans sbt:
```
put "nom_du_fichier"
```
Ou nom_du_fichier se situe à la racine de Algo1.

Enfin, pour soumettre un job spark, lancer la commande:
```
submit "nom_du_fichier"
```
où `nom_du_fichier` est in fichier qui a été `put` au préalable.

Lorsque vous mettez à jour le code de l'application, il faut mettre à jour la version sur le serveur. Pour cela, lancer:
```
push
```

La différence entre `push` et `pushAll` est que la première met à jour sur le serveur uniquement le code de l'application, la deuxième compresse le .jar de l'application ainsi que toutes ses dépendances avant de les lancer sur le serveur. 
**Il est donc nécessaire de lancer `pushAll` après avoir ajouter une librairie dans le fichier `build.sbt` afin de mettre à jour l'ensemble des librairies sur le serveur**
