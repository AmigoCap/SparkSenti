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

## Configuration du Datacenter

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

## Utilisation en ligne de commande :

**Lancement de l'algorithme**

Pour envoyer sur le datacenter en HDFS le fichier comptenant les tweets à analyser utiliser la commande :
```
sbt:SparkSenti> put "nom_du_fichier.json"
```
Vous avez à dispoition pour tester le fichier `input_test.json` dans le repertoir Algo1.

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
Lire le fichier `defaultoutput.json` à la racine :
```
cat defaultoutput.json
```
**Remarque :**

Lorsque vous souhaitez mettre à jour uniquement le code de l'application sur le serveur, vous pouvez utiliser la commande push depuis sbt :
```
sbt:SparkSenti> push
```

La différence entre `push` et `pushAll` est que la première met à jour sur le serveur uniquement le code de l'application, la deuxième compresse le .jar de l'application ainsi que toutes ses dépendances avant de les lancer sur le serveur.
**Il est donc nécessaire de lancer `pushAll` après avoir ajouter une librairie dans le fichier `build.sbt` afin de mettre à jour l'ensemble des librairies sur le serveur**

## Utilisation clé en main (jupyter notebook)

Après avoir suivi le paragraphe, **Configuration du DataCenter**, suivez les étapes suivantes :

**Installation de Jupyter**

Se réferer au mode opératoir décrit sur le [site officiel](http://jupyter.readthedocs.io/en/latest/install.html#id4).

**Installation des librairies Python**

Pour fonctionner, le notebook Python nécessite Python3 et des librairies additionnels. Pour les installer, depuis le terminal lancés la commande :
```
pip3 install tweepy squarify geopy geojson folium json-lines matplotlib texttable jsonpickle certifi
```

**Clé API Twitter**

Pour récupérer des tweets automatiquement, vous devez renseigner votre clé pour l'API Twitter, et les placer dans un fichier. 
Ouvrez le fichier `API_key_example.txt` situé dans le dossier `SparkSenti`, renseignez vos clés personelles, et enregistrez en changeant le nom du fichier à `API_key.txt`

**Lancement de Jupyter**
Depuis le terminal, dans le dossier `SparkSenti`, lancez la commande :
```
jupyter notebook
```
Séléctionnez le fichier `Workflow.ipynb`.

**Configuration de la requête de tweets**

A FAIRE

**Visualisation**

Lancer les blocs un à un jusqu'à obtenir les résultats de l'analyse.
Vous remarquerez que les fichiers `input_tweet_XXX.json` et `output_tweet_XXX.json` ont été créé dans le dossier `tweets-database` contenant les resultats de l'analyse stocké au format `.json`.

**Dashboard**

Le dashboard utilise une extension de Jupyter, qu'il est nécessaire d'installer. Pour ce faire, reportez vous aux instructions sur [la page suivante](http://jupyter-dashboards-layout.readthedocs.io/en/latest/getting-started.html).

Une fois l'extension installée, ouvrez le fichier `Workflow_dashboard.ipynb`. Exécutez l'ensemble des blocs : cela devrait être rapide car les fonctions sont uniquement défninies, mais jamais appelées. Enfin, dans le menu sélectionnez `View`-> `Dahsboard Preview`. Vous devriez obtenir le dashboard pour sélectionner vos paramètres. Sélectionnez, attendez le chargement, puis cliquez sur les différents boutons pour afficher les visualisations. 

Notez qu'il est également possible de modifier ces paramètres, effectuer de nouveau la requête, puis actualiser les visualisations. 
