# ALIAS - Mines (Minesweeper JavaFX) 💣

Un projet de jeu de Démineur classique réalisé en **Java** avec la bibliothèque graphique **JavaFX**. Ce projet, initialement conçu en deuxième année, a été entièrement refactorisé pour adopter une architecture **MVC (Modèle-Vue-Contrôleur)** robuste, modulaire et évolutive.

---

##  Architecture du Projet

Le code est structuré selon le design pattern **MVC**, garantissant une séparation claire des responsabilités :

-   **Model (`com.example.promines.model`)** :
    -   `Board` : Gère la grille de jeu, le placement aléatoire des mines et les règles logiques (calcul des voisins, détection de victoire).
    -   `Cell` : Structure de données pure représentant l'état d'une case (bombe, révélée, drapeau).
    -   *Indépendant de l'interface graphique.*
-   **View (`com.example.promines.view`)** :
    -   `GameView` : Composant principal héritant de `GridPane` pour la disposition globale.
    -   `CellView` : Composant graphique personnalisé pour l'affichage dynamique des cases (images, styles CSS).
-   **Controller (`com.example.promines.controller`)** :
    -   `GameController` : Gère les interactions utilisateur (clics gauche/droit), le chronomètre et met à jour la vue en fonction de l'évolution du modèle.

---

## Fonctionnalités

-  **Mécaniques classiques** : Révélation de cases, pose de drapeaux (clic droit) et détection automatique des mines.
-  **Chronomètre de précision** : Affichage dynamique du temps écoulé au centième de seconde.
-  **Algorithme Flood-Fill** : Révélation récursive automatique des zones vides adjacentes.
-  **Système de réinitialisation** : Bouton permettant de relancer une nouvelle partie avec une grille fraîchement générée.
-  **Interface Responsive** : Layout flexible géré par des contraintes de lignes et de colonnes (`GridPane`).

---

##  Technologies & Outils

- **Langage** : Java 22
- **Interface Graphique** : JavaFX 22
- **Gestionnaire de projet** : Maven
- **Design Pattern** : MVC (Model-View-Controller)
- **Module System** : Utilisation de `module-info.java` pour une gestion moderne des dépendances.

---

##  Installation et Lancement

### Prérequis
- Java JDK 22 ou supérieur.
- Maven installé sur votre machine.

### Étapes
1. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/ton-pseudo/pro-mines.git
   cd pro-mines
   ```

2. **Compiler le projet** :
   ```bash
   mvn clean install
   ```

3. **Lancer l'application** :
   ```bash
   mvn javafx:run
   ```

---

## Aperçu visuel

Les ressources graphiques (bombes, chiffres, drapeaux) sont situées dans le dossier `src/main/resources/images`. L'interface utilise une palette de couleurs sombre et moderne.

---

*Développé avec passion dans le cadre d'un projet académique et amélioré pour illustrer les bonnes pratiques de programmation orientée objet.*
