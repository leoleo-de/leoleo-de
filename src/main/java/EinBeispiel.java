import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class EinBeispiel {

    static int wordCount = 0;

    public static void main(String[] args)  {
        /* Alles auf einmal */
        System.out.println("Anzahl Wörter kompakt: "
                + allFilesInFolder(new File(".")).filter(f -> f.getPath().endsWith(".java"))
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

        // 4. Die Zeilen aus allen Listen in einen Stream aus Zeilen flachklopfen
        Stream<String> allLines = linesLists.flatMap(List::stream);

        // 5. Die Zeilen in Arrays aus Strings (Wörtern) splitten
        Stream<String[]> allLinesAsStringArray = allLines.map(line -> line.split(" "));

        // 6. Die Wörter aus den StringArrays in einen Stream aus Strings flachlopfen und in lower case umwandeln
        Stream<String> allWords = allLinesAsStringArray.flatMap(Arrays::stream).map(String::toLowerCase);

        // 7. Die Wörter ausfiltern, die nur aus Whitespace bestehen
        Stream<String> nonEmptyWords = allWords.filter(w -> !w.trim().isEmpty());

        // 8. wir zählen im als SeiteEffekt in map() mal die Wörter in einer statischen Klassenvariable - während wir sonst nix machen, außer den Input wieder als Output rauszugeben
        nonEmptyWords = nonEmptyWords.map(w -> {EinBeispiel.wordCount++; return w;});
        System.out.println("Seiteneffekt wordCount (noch 0) = " + EinBeispiel.wordCount); // nur als Beispiel-Fallstrick: das wird hier noch 0 ergeben, weil die intermediate operation gar nicht ausgeführt wurde ...

        // 9. Zählen, aber auch noch schnell mal untereinander ausgeben zur Kontrolle
        System.out.println("-----------------------------");
        System.out.println("Anzahl Woerter :" + nonEmptyWords.peek(System.out::println).count());
        System.out.println("Seiteneffekt wordCount = " + EinBeispiel.wordCount); // Erst hier wäre der Zähler aus dem Seiteneffekt gefüllt nach der terminal operation

    }

    /**
     * Hilfsmethode, die den Code zum rekursiven Sammeln aller Files in einem Verzeichnis kapselt. <br>
     * Schamlos geklaut von <a href="http://web.mit.edu/6.031/www/fa17/classes/26-map-filter-reduce/">http://web.mit.edu</a>
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
     * @param path path to file
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
