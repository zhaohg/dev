package com.zhaohg.lambda.lambda2;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TestInterface {

    public static void main(String[] args) {
        TestDefaultInterface();
        TestLambda();
        TestPredicate();
        TestFunction();
        TestSupplier();
        TestConsumer();
        TestComparator();
        TestMap();

        PersonFactory<Person> personFactory = Person::new;
        Person person = personFactory.create("Peter", "Parker");

    }

    /**
     * 一、接口的默认方法
     * Java 8允许我们给接口添加一个非抽象的方法实现，
     * 只需要使用 default关键字即可，这个特征又叫做扩展方法
     */
    public static void TestDefaultInterface() {
        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {
                return sqrt(a * 100);
            }
        };
        formula.calculate(100);     // 100.0
        formula.sqrt(16);           // 4.0
    }

    /**
     * 二、Lambda 表达式
     */
    public static void TestLambda() {
        List<String> list = Arrays.asList("peter", "anna", "mike", "xenia");

        TestJDK7Sort1(list);
        TestJDK8Sort2(list);
    }

    public static void TestJDK7Sort1(List<String> list) {
        Comparator<String> mySort = new Comparator<String>() {
            @Override
            public int compare(String srt1, String str2) {
                return srt1.compareTo(str2);
            }
        };
        Collections.sort(list, mySort);
        System.out.println(list.toString());
    }


    public static void TestJDK8Sort2(List<String> list) {//Lambda 表达式

        Collections.sort(list, (str1, str2) -> str1.compareTo(str2));
        System.out.println(list.toString());

        Collections.sort(list, (str1, str2) -> {
            return str1.compareTo(str2);
        });
        System.out.println(list.toString());

        Collections.sort(list, (String a, String b) -> {
            return b.compareTo(a);
        });
        System.out.println(list.toString());

        //代码变得更段且更具有可读性，但是实际上还可以写得更短：
        Collections.sort(list, (String a, String b) -> b.compareTo(a));
        System.out.println(list.toString());

        //对于函数体只有一行代码的，你可以去掉大括号{}以及return关键字，但是你还可以写得更短点：
        Collections.sort(list, (a, b) -> b.compareTo(a));
        System.out.println(list.toString());
    }

    public static void TestOptional() {
        Optional<String> fullName = Optional.ofNullable(null);
        System.out.println("Full Name is set? " + fullName.isPresent());
        System.out.println("Full Name: " + fullName.orElseGet(() -> "[none]"));
        System.out.println(fullName.map(s -> "Hey " + s + "!").orElse("Hey Stranger!"));

        Optional<String> firstName = Optional.of("Tom");
        System.out.println("First Name is set? " + firstName.isPresent());
        System.out.println("First Name: " + firstName.orElseGet(() -> "[none]"));
        System.out.println(firstName.map(s -> "Hey " + s + "!").orElse("Hey Stranger!"));
        System.out.println();
    }

    public static void TestNashorn() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        System.out.println(engine.getClass().getName());
        //Run JS
        System.out.println("Result:" + engine.eval("function f() { return 1; }; f() + 1;"));
    }

    public static void TestParallel() {
        //JDK8增加了大量的新方法来对数组进行并行处理。可以说，最重要的是parallelSort()方法，
        // 因为它可以在多核机器上极大提高数组排序的速度。下面
        long[] arrayOfLong = new long[20000];
        Arrays.parallelSetAll(arrayOfLong, index -> ThreadLocalRandom.current().nextInt(1000000));
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();

        Arrays.parallelSort(arrayOfLong);
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();
    }

    public static void TestBase64() {
        final String text = "Base64 finally in Java 8!";
        final String encoded = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        System.out.println(encoded);
        final String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        System.out.println(decoded);
    }

    public static void TestPredicate() {
        Predicate<String> predicate = (s) -> s.length() > 0;
        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false
        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();
    }

    public static void TestFunction() {
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        backToString.apply("123");     // "123"
    }

    public static void TestSupplier() {
        Supplier<Person> personSupplier = Person::new;
        personSupplier.get();   // new Person
    }

    public static void TestConsumer() {
        Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
        greeter.accept(new Person("Luke", "Skywalker"));
    }

    public static void TestComparator() {
        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");
        comparator.compare(p1, p2);             // > 0
        comparator.reversed().compare(p1, p2);  // < 0
    }

    public static void TestStream() {
        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");
    }

    public static void TestFilter() {
        List<String> stringCollection = new ArrayList<>();
        stringCollection
                .stream()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);
        // "aaa2", "aaa1"
    }

    public static void TestSort() {
        List<String> stringCollection = new ArrayList<>();
        stringCollection
                .stream()
                .sorted()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);
        // "aaa1", "aaa2"
        System.out.println(stringCollection);
        // ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1
    }

    public static void TestMap() {
        List<String> stringCollection = new ArrayList<>();
        stringCollection
                .stream()
                .map(String::toUpperCase)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(System.out::println);
        // "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"
        //todo
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.putIfAbsent(i, "val" + i);
        }
        map.forEach((id, val) -> System.out.println(val));

        map.computeIfPresent(3, (num, val) -> val + num);
        map.get(3);             // val33
        map.computeIfPresent(9, (num, val) -> null);
        map.containsKey(9);     // false
        map.computeIfAbsent(23, num -> "val" + num);
        map.containsKey(23);    // true
        map.computeIfAbsent(3, num -> "bam");
        map.get(3);             // val33

        map.remove(3, "val3");
        map.get(3);             // val33
        map.remove(3, "val33");
        map.get(3);             // null

        map.getOrDefault(42, "not found");  // not found

        map.merge(9, "val9", (value, newValue) -> value.concat(newValue));
        map.get(9);             // val9
        map.merge(9, "concat", (value, newValue) -> value.concat(newValue));
        map.get(9);             // val9concat
    }

    public static void TestMatch() {
        List<String> stringCollection = new ArrayList<>();
        boolean anyStartsWithA =
                stringCollection
                        .stream()
                        .anyMatch((s) -> s.startsWith("a"));
        System.out.println(anyStartsWithA);      // true
        boolean allStartsWithA =
                stringCollection
                        .stream()
                        .allMatch((s) -> s.startsWith("a"));
        System.out.println(allStartsWithA);      // false
        boolean noneStartsWithZ =
                stringCollection
                        .stream()
                        .noneMatch((s) -> s.startsWith("z"));
        System.out.println(noneStartsWithZ);      // true
    }

    public static void TestCount() {
        List<String> stringCollection = new ArrayList<>();
        long startsWithB =
                stringCollection
                        .stream()
                        .filter((s) -> s.startsWith("b"))
                        .count();
        System.out.println(startsWithB);    // 3
    }

    public static void TestReduce() {
        List<String> stringCollection = new ArrayList<>();
        Optional<String> reduced =
                stringCollection
                        .stream()
                        .sorted()
                        .reduce((s1, s2) -> s1 + "#" + s2);
        reduced.ifPresent(System.out::println);
    }

    public static void TestStreams() {
        //创建一个没有重复元素的大表
        int max = 1000000;
        List<String> values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }
        //然后我们计算一下排序这个Stream要耗时多久，
        //串行排序：
        long t0 = System.nanoTime();
        long count1 = values.stream().sorted().count();
        System.out.println(count1);
        long t1 = System.nanoTime();
        long millis1 = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("sequential sort took: %d ms", millis1));

        //并行排序：
        long t2 = System.nanoTime();
        long count2 = values.parallelStream().sorted().count();
        System.out.println(count2);
        long t3 = System.nanoTime();
        long millis2 = TimeUnit.NANOSECONDS.toMillis(t3 - t2);
        System.out.println(String.format("parallel sort took: %d ms", millis2));
    }


    /**
     * 9 Date API
     */
    public static void TestDateApi() {
        //Clock 时钟
        final Clock clock = Clock.systemUTC();
        Instant instant = clock.instant();
        Date legacyDate = Date.from(instant);
        System.out.println(clock.instant());
        System.out.println(clock.millis());
        System.out.println(legacyDate);

        //Timezones 时区
        System.out.println(ZoneId.getAvailableZoneIds());
        // prints all available timezone ids
        ZoneId zone1 = ZoneId.of("Europe/Berlin");
        ZoneId zone2 = ZoneId.of("Brazil/East");
        System.out.println(zone1.getRules());
        System.out.println(zone2.getRules());

        final ZonedDateTime zonedDatetime = ZonedDateTime.now();
        final ZonedDateTime zonedDatetimeFromClock = ZonedDateTime.now(clock);
        final ZonedDateTime zonedDatetimeFromZone = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        System.out.println(zonedDatetime);
        System.out.println(zonedDatetimeFromClock);
        System.out.println(zonedDatetimeFromZone);

        //LocalTime 本地时间
        LocalTime now1 = LocalTime.now(zone1);
        LocalTime now2 = LocalTime.now(zone2);
        System.out.println(now1.isBefore(now2));  // false
        long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
        long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);
        System.out.println(hoursBetween);       // -3
        System.out.println(minutesBetween);     // -239
        final LocalTime time = LocalTime.now();
        final LocalTime timeFromClock = LocalTime.now(clock);
        System.out.println(time);
        System.out.println(timeFromClock);

        LocalTime late = LocalTime.of(23, 59, 59);
        System.out.println(late);       // 23:59:59
        DateTimeFormatter germanFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.GERMAN);
        LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
        System.out.println(leetTime);   // 13:37

        //LocalDate 本地日期
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
        LocalDate yesterday = tomorrow.minusDays(2);
        LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
        DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
        System.out.println(dayOfWeek);    // FRIDAY
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN);
        LocalDate xmas = LocalDate.parse("24.12.2014", formatter);
        System.out.println(xmas);   // 2014-12-24

        //LocalDateTime 本地日期时间
        LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);
        DayOfWeek dayWeek = sylvester.getDayOfWeek();
        System.out.println(dayWeek);      // WEDNESDAY
        Month month = sylvester.getMonth();
        System.out.println(month);          // DECEMBER
        long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
        System.out.println(minuteOfDay);    // 1439
        Instant instant1 = sylvester.atZone(ZoneId.systemDefault()).toInstant();
        Date legacyDate1 = Date.from(instant1);
        System.out.println(legacyDate1);     // Wed Dec 31 23:59:59 CET 2014
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
        LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", format);
        String string = formatter.format(parsed);
        System.out.println(string);     // Nov 03, 2014 - 07:13
        final LocalDateTime datetime = LocalDateTime.now();
        final LocalDateTime datetimeFromClock = LocalDateTime.now(clock);
        System.out.println(datetime);
        System.out.println(datetimeFromClock);

        final LocalDateTime from = LocalDateTime.of(2014, Month.APRIL, 16, 0, 0, 0);
        final LocalDateTime to = LocalDateTime.of(2015, Month.APRIL, 16, 23, 59, 59);
        final Duration duration = Duration.between(from, to);
        System.out.println("Duration in days: " + duration.toDays());
        System.out.println("Duration in hours: " + duration.toHours());

    }

    /**
     * 10 注解
     */
    public static void TestAnnotation() {
        //例 1: 使用包装类当容器来存多个注解（老方法）
//        @Hints({@Hint("hint1"), @Hint("hint2")})
//        class Person {}

        //例 2：使用多重注解（新方法）
        @Hint("hint1")
        @Hint("hint2")
        class Person {
        }

        //第二个例子里java编译器会隐性的帮你定义好@Hints注解，了解这一点有助于你用反射来获取这些信息：
        Hint hint = Person.class.getAnnotation(Hint.class);
        System.out.println(hint);                   // null
        Hints hints1 = Person.class.getAnnotation(Hints.class);
        System.out.println(hints1.value().length);  // 2
        Hint[] hints2 = Person.class.getAnnotationsByType(Hint.class);
        System.out.println(hints2.length);          // 2

        //即便我们没有在Person类上定义@Hints注解，我们还是可以通过 getAnnotation(Hints.class)
        //来获取 @Hints注解，更加方便的方法是使用 getAnnotationsByType 可以直接获取到所有的@Hint注解。
        //另外Java 8的注解还增加到两种新的target上了：
//        @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
//        @interface MyAnnotation {}
    }

}
