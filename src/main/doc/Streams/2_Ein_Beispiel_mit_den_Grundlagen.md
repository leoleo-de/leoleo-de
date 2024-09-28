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

Wenn wir das fertige Beispiel ansehen, könnte es so aussehen:
```java
   System.out.println("Anzahl Wörter: " 
            + allFilesInFolder(new File(".")).filter(f -> f.getPath().endsWith(".java"))
            .map(File::toPath).map(EinBeispiel::allLinesInFile)
            .flatMap(List::stream)
            .flatMap(l -> Stream.of(l.split(" ")))
            .filter(w -> !w.trim().isEmpty())
            .count());
```

Um es besser zu verstehen gehen wir es hier einzeln durch mit Kommentaren:
```java
    // 1. Alle Dateien als Stream sammeln und filtern auf Endung
    Stream<File> allFiles = allFilesInFolder(new File(".")).filter(f -> f.getPath().endsWith(".java"));

    // 2. jedes File mappen auf den Pfad, den wir gleich als Input brauchen
    Stream<Path> allPaths = allFiles.map(File::toPath);
    
    // 3. aus jedem File die Zeilen lesen
    Stream<List<String>> linesLists = allPaths.map(EinBeispiel::allLinesInFile);
    
    // 4. Die Zeilen aus allen Listen in einen Stream aus Zeilen packen
    Stream<String> allLines = linesLists.flatMap(List::stream);
    
    // 5. Die Wörter aus den Zeilen in einen Stream packen
    Stream<String> allWords = allLines.flatMap(line -> Stream.of(line.split(" "))).map(w-> {return w.toLowerCase();});
    
    // 6. Die Wörter ausfiltern, die nur aus Whitespace bestehen
    Stream<String> nonEmptyWords = allWords.filter(w -> !w.trim().isEmpty());
    
    // 7. wir zählen im als SeiteEffekt in map() mal die Wörter in einer statischen Klassenvariable - während wir sonst nix machen, außer den Input wieder als Output rauszugeben
    nonEmptyWords = nonEmptyWords.map(w -> {EinBeispiel.wordCount++; return w;});
    System.out.println("Seiteneffekt wordCount (noch 0) = " + EinBeispiel.wordCount); // nur als Beispiel-Fallstrick: das wird hier noch 0 ergeben, weil die intermediate operation gar nicht ausgeführt wurde ...
    
    // 8. Zählen, aber auch noch schnell mal untereinander ausgeben zur Kontrolle
    System.out.println("-----------------------------");
    System.out.println("Anzahl Woerter :" + nonEmptyWords.peek(System.out::println).count());
    System.out.println("Seiteneffekt wordCount = " + EinBeispiel.wordCount); // Erst hier wäre der Zähler aus dem Seiteneffekt gefüllt nach der terminal operation
```


Dabei haben wir jeweils zwei Hilfsmethoden verwendet:  
```java
    /**
     * Hilfsmethode, die den Code zum rekursiven Sammeln aller Files in einem Verzeichnis kapselt. <br>
     * Schamlos geklaut von http://web.mit.edu/6.031/www/fa17/classes/26-map-filter-reduce/
     *
     * @param folder
     * @return Stream<File>
     */
    static Stream<File> allFilesInFolder(File folder) {
        // rekursiv in die Unterverzeichnisse absteigen
        Stream<File> ausUnterverzeichnissen = Arrays.stream(folder.listFiles())
                .filter(File::isDirectory)
                .flatMap(EinBeispiel::allFilesInFolder);
        // noch die aus dem Verzeichnis selbst dazu
        return Stream.concat(ausUnterverzeichnis,
                Arrays.stream(folder.listFiles()).filter(File::isFile));
    }

    /**
     * Files.readAllLines() ausgelagert in diese Hilfsmethode, um die IOException abzuschütteln.
     * @param path
     * @return List<String>
     */
    static List<String> allLinesInFile(Path path){
        try {
            return Files.readAllLines(path);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
```



