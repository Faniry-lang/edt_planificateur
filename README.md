Mini système de planification d'emploi du temps crée dans le contexte du projet de dévéloppement d'application web pour le lycée Nanisana

Structure du projet
    -planning: Dossier contenant toutes les classes java
        -entities: dossier sous jacent contenant les classes mappées à celle de la base, légèrement modifiées et généralement toutes les classes dont on aura besoin dans la résolution du problème.
        -solver: dossier sous jacent contenant les classes de OptaPlanner
            -constraints: ici on trouvera la classe pour définir les contraintes
            -entities: ici on trouvera les entités de planification et la solution
    App.java: classe main pour run le projet sur une demo (pas encore de test dans la base de donnée, ces données ont été générées par chatgpt et n'ont pas encore été vérifiée)

Pour compiler et lancer le projet: 
    mvn compile exec:java -Dexec.mainClass="com.edt.App"

Interprétation: 
    Le score indique la solution optimale trouvée, on devrait voir quelque chose comme 
    x hard ou x soft, x represente le nombre de contraintes violées ou de récompenses remplies (ici on n'a que des contraintes pour l'instant)

Les détails du projet sont dans context.txt
Il y a aussi un pdf servant de guide d'intro pour OptaPlanner généré avec Claude à partir de mes notes.