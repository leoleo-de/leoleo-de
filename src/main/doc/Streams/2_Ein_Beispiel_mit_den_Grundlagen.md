## Eine Beispiel-Aufgabe

Wir wollen in einem Verzeichnis alle Wörter, die in allen Java-Files (auch in Unterverzeichnissen) vorkommen, finden 
und etwas mit ihnen tun (z.B. zählen im einfachsten Fall).
  

Dann könnten wir die Aufgabe in Teilaufgaben aufteilen wie:

1. Alle Filepfade finden, die in dem Verzeichnis oder in Unterverzeichnissen liegen
2. Die Files wegfiltern, die nicht auf ".java" enden.
3. Alle Zeilen aus jedem einzelnen File lesen     
4. Die Wörter in jeder Zeile zählen     

Wenn wir das klassisch mit for-Schleifen und Aufteilung in einzelne Methoden lösen, schreiben wir dafür eine ganze Menge 
Code nur um immer wieder in Schleifen über die Elemente zu gehen.
Hier wollen wir uns ansehen, wie das mit den Streams gelöst wird.  



