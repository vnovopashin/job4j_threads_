package ru.job4j.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParallelStreamExample {
    public static void main(String[] args) {

        /*
          Обычный поток на примере перемножения чисел
          В данном примере все операции выполняются последовательно в главной нити Main.
          Чтобы создать из этого потока параллельный поток, используется метод parallelStream().
          Данный метод вернет параллельный поток, если это возможно.
          Если какие-то операции нельзя выполнить параллельно,
          то вернется обычный последовательный поток Stream.
          Данный метод определен в интерфейсе Collection.
         */
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> multiplication = list.stream()
                .reduce((left, right) -> left * right);
        System.out.println(multiplication.get());

        System.out.println();
        /*
        Перепишем пример на метод parallelStream():
        В строке ниже метод parallelStream() создает поток из элементов Integer,
        который может быть выполнен в многопоточной среде.
         */
        List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
        Stream<Integer> stream = list2.parallelStream();
        System.out.println(stream.isParallel());
        Optional<Integer> multiplication2 = stream.reduce((left, right) -> left * right);
        System.out.println(multiplication2.get());

        System.out.println();
        /*
        Вид потока можно изменить. Для этого существуют 2 метода, также определенных в интерфейсе BaseStream:
        - sequential() - если данный поток параллельный,
        то возвращает новый последовательный поток с теми же элементами.
        Если метод применяется к последовательному потоку,
        то возвращает тот же поток.
        - parallel() - если данный поток последовательный,
        то возвращает новый параллельный поток с теми же элементами.
        Если метод применяется к параллельному потоку, то возвращает тот же поток.
         */
        IntStream parallel = IntStream.range(1, 100).parallel();
        System.out.println(parallel.isParallel());
        IntStream sequential = parallel.sequential();
        System.out.println(sequential.isParallel());

        System.out.println();
        /*
        Нужно иметь в виду, что выполнение потока в многопоточной среде
        по умолчанию не гарантирует сохранения порядка следования элементов.
        В следующем примере в параллельном потоке применяется метод peek() для вывода элементов потока на печать.
         */
        List<Integer> list3 = Arrays.asList(1, 2, 3, 4, 5);
        list3.stream().parallel().peek(System.out::println).toList();

        System.out.println();
        /*
        Есть и другие методы с неустранимыми побочными эффектами. Например, метод forEach.
        При параллельной обработке потока он так же будет выдавать искаженный порядок элементов:
         */
        List<Integer> list4 = Arrays.asList(1, 2, 3, 4, 5);
        list4.stream().parallel().forEach(System.out::println);

        System.out.println();
        /*
        Для сохранения порядка следования элементов можно воспользоваться методов forEachOrdered()
         */
        List<Integer> list5 = Arrays.asList(1, 3, 4, 5, 2);
        list5.stream().parallel().forEachOrdered(System.out::println);
    }
}
