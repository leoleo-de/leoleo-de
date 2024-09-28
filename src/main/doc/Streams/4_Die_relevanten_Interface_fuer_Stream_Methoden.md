# Die relevanten funktionalen Interfaces für Stream-Methoden
In den Methoden in der Klasse Stream kommen einige wichtige funktionale Interfaces vor, die wir uns hier anschauen wollen.
Einen schänen Überblick (in Englisch allerdings) findet man auch bei Bealdung z.B.: https://www.baeldung.com/java-8-functional-interfaces 

Vorasugeschickt sei ein Zitat aus der Javadoc des JDK zum package _java.util.function_:
```qute
Functional interfaces provide target types for lambda expressions and method references. 
Each functional interface has a single abstract method, called the functional method for that functional interface, 
to which the lambda expression's parameter and return types are matched or adapted. Functional interfaces can provide 
a target type in multiple contexts, such as assignment context, method invocation, or cast context.
```

## Function apply
Um lambda-Expressions zu konstruieren wurden mit Java 8 die FuntcionalInterfaces (siehe die Annotation @FunctionalInterface)
eingeführt, die es ermöglichen die Charakteristiken von Funktionen zu beschreiben, die in lambda-Expressions verwendet werden 
können.  

Die zentrale Methode ist:  
```java
R apply(T t);
```
Argument vom Typ T rein und vom Typ R raus - sehr grundlegend.

Verwendung z.B. in map(..)
```
<R> Stream<R> map(Function<? super T, ? extends R> mapper);
```
Geeignet ist jede Methode dieser Form, z.B. in unserem Beispiel

```java
    Stream<List<String>> linesLists = allPaths.map(EinBeispiel::allLinesInFile);
```

Man könnte auch explizit schreiben 
```java
    Function<Path, List<String>> f = EinBeispiel::allLinesInFile;
    Stream<List<String>> linesLists = allPaths.map(f);
```

aber auch member-Methoden auf Klassen wie  

```java
map(String::toLowerCase);
```
(weil implizit ist das erste Argument dieser Methoden immer das eigene Objekt)
Man könnte auch hier explizit schreiben:
```java
    Function<String, String> f = String::toLowerCase;
    map(f);
```

Eine lambda-Expression kann ein FunctionalInterface implementieren - insbesondere mit apply Charakteristik, Beispiel:
```java
    Function<String, String> repeat = s -> s+s;    
```

## Function identity
Der einfache und manchmal praktische Fall, dass man genau das Input-Element wieder als Output haben möchte.
Es gilt quasi
```java  
    Function identity = t -> t;
```

Das ist an manchen Stellen praktisch, um z.B. eine Default-Transformation anzubieten.


## Function compose and andThen
### compose
Das Interface _Function_ hat noch zwei weitere Methoden
```java
default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }
```

Hier klaue ich Erklärung und Beispiel von https://www.baeldung.com/java-8-functional-interfaces :
```
The Function interface also has a default compose method that allows us to combine several functions into one and execute them sequentially:

    Function<Integer, String> intToString = Object::toString;
    Function<String, String> quote = s -> "'" + s + "'";
    Function<Integer, String> quoteIntToString = quote.compose(intToString);
    assertEquals("'5'", quoteIntToString.apply(5));
    
The quoteIntToString function is a combination of the quote function applied to a result of the intToString function.     
```
und außerdem 
### andThen
```java
default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
```

Auch damit kann man wieder Funktionen kombinieren, mit andThen-Logik.
Dagegen war die Logik bei compose eine before-Logik.



## BiFunction


## Consumer


## BiConsumer 


## Predicate


## BiPredicate


## Supplier


## UnaryOperator


## BinaryOperator
