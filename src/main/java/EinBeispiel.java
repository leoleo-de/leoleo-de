import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EinBeispiel {

    static int wordCount = 0;

    public static void main(String[] args)  {
        /* Alles auf einmal */
        System.out.println("Anzahl Wörter kompakt:" + allFilesInFolder(new File(".")).filter(f -> f.getPath().endsWith(".java"))
                .map(File::toPath).map(EinBeispiel::allLinesInFile)
                .flatMap(List::stream)
                .flatMap(l -> Stream.of(l.split(" ")))
                .filter(w -> !w.trim().isEmpty())
                .count());

        /* Wir gehen die Schritte einzeln durch */

        // 1. Alle Dateien als Stream sammeln und filtern auf Endung
        Stream<File> allFiles = allFilesInFolder(new File(".")).filter(f -> f.getPath().endsWith(".java"));
        // 2. jedes File mappen auf den Pfad, den wir gleich als Input brauchen
        Stream<Path> allPaths = allFiles.map(File::toPath);
        // 3. aus jedem File die Zeilen lesen
        Stream<List<String>> linesLists = allPaths.map(EinBeispiel::allLinesInFile);
        // 4. Die Zeilen aus allen Listen in einen Stream aus Zeilen packen
        Stream<String> allLines = linesLists.flatMap(List::stream);
        // 5. Die Wörter aus den Zeilen in einen Stream packen
        Stream<String> allWords = allLines.flatMap(line -> Stream.of(line.split(" "))).map(w-> {EinBeispiel.wordCount++; return w.toLowerCase();}); // Im Seiteneffekt zählen wir hier schon mal mit
        // 6. Die Wörter ausfilter, die nur aus Whitespace bestehen
        Stream<String> nonEmptyWords = allWords.filter(w -> !w.trim().isEmpty());
        // 7a. Zuletzt zählen
        // 7b. Auch zählen, aber auch noch schnell mal untereinander ausgeben zur Kontrolle
        System.out.println("-----------------------------");
        System.out.println("Anzahl Woerter :" + nonEmptyWords.peek(System.out::println).count());
        System.out.println("Seiteneffekt wordCount = " + EinBeispiel.wordCount); // Erst hier wäre der Zähler aus dem Seiteneffekt gefüllt nach der terminal operation

    }

    /**
     * Hilfsmethode, die den Code zum rekursiven Sammeln aller Files in einem Verzeichnis kapselt. <br>
     * Schamlos geklaut von http://web.mit.edu/6.031/www/fa17/classes/26-map-filter-reduce/
     *
     * @param folder
     * @return Stream<File>
     */
    static Stream<File> allFilesInFolder(File folder) {
        Stream<File> descendants = Arrays.stream(folder.listFiles())
                .filter(File::isDirectory)
                .flatMap(EinBeispiel::allFilesInFolder);
        return Stream.concat(descendants,
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

}
