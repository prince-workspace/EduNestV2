package com.edunest.config;

import com.edunest.entity.*;
import com.edunest.enums.ChallengeType;
import com.edunest.enums.QuestionType;
import com.edunest.enums.QuizType;
import com.edunest.enums.Role;
import com.edunest.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final ChallengeRepository challengeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      CourseRepository courseRepository,
                      TopicRepository topicRepository,
                      LessonRepository lessonRepository,
                      QuestionRepository questionRepository,
                      QuizRepository quizRepository,
                      ChallengeRepository challengeRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.challengeRepository = challengeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User instructor;
        if (userRepository.count() == 0) {
            User student = new User();
            student.setName("Aarav Sharma");
            student.setEmail("student@edunest.com");
            student.setPassword(passwordEncoder.encode("password123"));
            student.setRole(Role.STUDENT);
            student.setLearningGoal("Master Java Programming & Object-Oriented Design");
            student.setActive(true);
            userRepository.save(student);

            instructor = new User();
            instructor.setName("Prof. Vikram Verma");
            instructor.setEmail("instructor@edunest.com");
            instructor.setPassword(passwordEncoder.encode("password123"));
            instructor.setRole(Role.INSTRUCTOR);
            instructor.setLearningGoal("Teach Java & Data Structures");
            instructor.setActive(true);
            instructor = userRepository.save(instructor);

            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@edunest.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);

            System.out.println("[EduNest] Seeded default accounts: student@edunest.com, instructor@edunest.com, admin@edunest.com");
        } else {
            instructor = userRepository.findByEmail("instructor@edunest.com")
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        if (courseRepository.count() == 0) {
            seedJavaCourse(instructor);
        }
        seedAdditionalCoursesAndQuestions(instructor);
        updateCodingChallenges();
    }

    private void seedJavaCourse(User instructor) {
        Course javaCourse = new Course();
        javaCourse.setTitle("Java Programming - From Fundamentals to OOP");
        javaCourse.setDescription("Master Core Java with adaptive diagnostics, topic-level mastery tracking, spaced revision, and practical coding challenges.");
        javaCourse.setThumbnailUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600");
        javaCourse.setInstructor(instructor);
        javaCourse.setPublished(true);
        javaCourse = courseRepository.save(javaCourse);

        List<Question> diagnosticQuestions = new ArrayList<>();

        // Topic 1: Variables & Data Types
        Topic t1 = createTopic(javaCourse, 1, "Variables & Data Types",
                "Primitive types, reference variables, type casting, and operators.");
        createLesson(t1, 1, "Primitive Data Types & Variables", 12,
                "Variables store data in memory with a strict data type.;Java has 8 primitive types: byte, short, int, long, float, double, char, boolean.;Use 'final' keyword to declare constants.",
                """
                <h3>Understanding Variables in Java</h3>
                <p>Java is a <strong>statically-typed</strong> language, which means every variable must be declared with a data type before it is used.</p>
                <pre><code>int age = 21;
double gpa = 8.95;
char grade = 'A';
boolean isEnrolled = true;</code></pre>
                <h4>Type Casting</h4>
                <p>Widening conversion happens automatically (<code>int -&gt; double</code>), while narrowing conversion requires an explicit cast:</p>
                <pre><code>double price = 99.95;
int roundedPrice = (int) price; // 99</code></pre>
                """);
        diagnosticQuestions.add(createQuestion(t1, QuestionType.DIAGNOSTIC,
                "Which of the following is the correct way to declare a constant integer in Java?",
                "final int MAX = 100;", "const int MAX = 100;", "let int MAX = 100;", "static MAX = 100;",
                "A", "In Java, the 'final' keyword prevents a variable from being reassigned."));
        createQuestion(t1, QuestionType.PRACTICE,
                "What is the result of (int) 7.9 in Java?",
                "8", "7", "7.9", "Compilation Error",
                "B", "Casting a double to int truncates the decimal portion towards zero, yielding 7.");
        createQuestion(t1, QuestionType.QUIZ,
                "How many primitive data types exist in Java?",
                "6", "7", "8", "9",
                "C", "Java has 8 primitive types: byte, short, int, long, float, double, char, and boolean.");

        // Topic 2: Loops & Control Flow
        Topic t2 = createTopic(javaCourse, 2, "Loops & Control Flow",
                "Conditional branching (if/switch) and iteration (for, while, do-while).");
        createLesson(t2, 1, "Mastering For, While & Enhanced For-Each Loops", 15,
                "Use 'for' loops when iteration count is known.;Use 'while' loops when looping depends on a dynamic condition.;'break' exits the loop immediately; 'continue' skips to the next iteration.",
                """
                <h3>Iteration in Java</h3>
                <p>Loops allow you to execute a block of code repeatedly while a condition remains true.</p>
                <pre><code>// Standard For Loop
for (int i = 1; i &lt;= 5; i++) {
    System.out.println("Iteration: " + i);
}

// Enhanced For-Each Loop
int[] scores = {90, 85, 78};
for (int score : scores) {
    System.out.println(score);
}</code></pre>
                """);
        diagnosticQuestions.add(createQuestion(t2, QuestionType.DIAGNOSTIC,
                "Which loop in Java is guaranteed to execute its body at least once?",
                "for loop", "while loop", "do-while loop", "for-each loop",
                "C", "A do-while loop evaluates its condition at the bottom of the loop after executing the body."));
        createQuestion(t2, QuestionType.PRACTICE,
                "What does the 'continue' statement do inside a loop?",
                "Terminates the entire program", "Exits the loop completely", "Skips the remaining code in the current iteration and moves to the next iteration", "Restarts the loop from index 0",
                "C", "'continue' skips the rest of the current loop body and jumps to the next iteration check.");
        createQuestion(t2, QuestionType.QUIZ,
                "How many times will `for(int i = 0; i < 4; i++)` execute?",
                "3 times", "4 times", "5 times", "Infinite times",
                "B", "i takes values 0, 1, 2, and 3 - executing 4 times in total.");

        // Topic 3: Arrays
        Topic t3 = createTopic(javaCourse, 3, "Arrays & Memory Layout",
                "1D and 2D arrays, indexing, bounds checking, and java.util.Arrays utilities.");
        createLesson(t3, 1, "Working with 1D & 2D Arrays", 15,
                "Arrays are fixed-size contiguous memory containers.;Array indices are 0-based (0 to length - 1).;Accessing an invalid index throws ArrayIndexOutOfBoundsException.",
                """
                <h3>Single &amp; Multi-Dimensional Arrays</h3>
                <p>An array holds a fixed number of values of a single type. Its length is established when the array is created.</p>
                <pre><code>int[] marks = new int[5];
marks[0] = 95;

int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6}
};
System.out.println("Element at (1,2): " + matrix[1][2]); // 6</code></pre>
                """);
        diagnosticQuestions.add(createQuestion(t3, QuestionType.DIAGNOSTIC,
                "Given `int[] arr = {10, 20, 30};`, what happens when you access `arr[3]`?",
                "Returns 0", "Returns 30", "Throws ArrayIndexOutOfBoundsException", "Returns null",
                "C", "Valid indices for a 3-element array are 0, 1, and 2. Index 3 is out of bounds."));
        createQuestion(t3, QuestionType.PRACTICE,
                "How do you obtain the number of elements in a Java array named `nums`?",
                "nums.length()", "nums.length", "nums.size()", "nums.count",
                "B", "In Java, arrays have a final property `.length` (not a method call).");
        createQuestion(t3, QuestionType.QUIZ,
                "What is the default value of elements in a newly allocated `int[] data = new int[4];`?",
                "null", "0", "-1", "Garbage value",
                "B", "Numeric primitive array elements in Java are automatically initialized to 0.");

        // Topic 4: OOP - Classes & Encapsulation
        Topic t4 = createTopic(javaCourse, 4, "OOP - Classes & Encapsulation",
                "Blueprints, objects, constructors, access modifiers, and data hiding.");
        createLesson(t4, 1, "Classes, Objects & Constructors", 18,
                "A class is a blueprint; an object is a runtime instance allocated on the heap.;Constructors initialize object state and share the class name.;Encapsulation hides private fields behind public getters and setters.",
                """
                <h3>Object-Oriented Programming Core</h3>
                <p>Encapsulation bundles data (fields) and behavior (methods) together while restricting direct outside access to internal state.</p>
                <pre><code>public class Student {
    private String name;
    private double masteryScore;

    public Student(String name, double masteryScore) {
        this.name = name;
        this.masteryScore = masteryScore;
    }

    public double getMasteryScore() {
        return masteryScore;
    }
}</code></pre>
                """);
        diagnosticQuestions.add(createQuestion(t4, QuestionType.DIAGNOSTIC,
                "Which access modifier restricts a field so it is accessible ONLY within the same class?",
                "public", "protected", "private", "default (package-private)",
                "C", "The 'private' modifier enforces encapsulation by hiding fields from outside classes."));
        createQuestion(t4, QuestionType.PRACTICE,
                "What is true about a class constructor in Java?",
                "It must have a return type of void", "It must have the exact same name as the class", "It cannot accept parameters", "It is called using the delete keyword",
                "B", "A constructor has the exact same name as its class and has no return type.");
        createQuestion(t4, QuestionType.QUIZ,
                "What does the `this` keyword refer to inside an instance method?",
                "The parent class", "The current object instance", "The static class loader", "The main method",
                "B", "`this` holds a reference to the current object instance on which the method was invoked.");

        // Topic 5: Inheritance & Polymorphism
        Topic t5 = createTopic(javaCourse, 5, "Inheritance & Polymorphism",
                "Code reuse with 'extends', super keyword, method overriding, and dynamic dispatch.");
        createLesson(t5, 1, "Extending Classes & Method Overriding", 20,
                "Use 'extends' to inherit non-private fields and methods from a superclass.;Use 'super(...)' to invoke the parent class constructor.;Method overriding (@Override) enables runtime polymorphism.",
                """
                <h3>Inheritance Hierarchy in Java</h3>
                <p>Inheritance establishes an <strong>IS-A</strong> relationship between a parent class (superclass) and child class (subclass).</p>
                <pre><code>class Vehicle {
    public void start() {
        System.out.println("Vehicle starting...");
    }
}

class ElectricCar extends Vehicle {
    @Override
    public void start() {
        System.out.println("Silent battery ignition...");
    }
}</code></pre>
                """);
        diagnosticQuestions.add(createQuestion(t5, QuestionType.DIAGNOSTIC,
                "Which keyword is used by a subclass to invoke its parent class constructor in Java?",
                "this()", "parent()", "super()", "extends()",
                "C", "`super(...)` must be the first statement in a subclass constructor to call the superclass constructor."));
        createQuestion(t5, QuestionType.PRACTICE,
                "When a subclass provides a specific implementation of a method already defined in its superclass, what is it called?",
                "Method Overloading", "Method Overriding", "Encapsulation", "Static Binding",
                "B", "Method overriding allows a subclass to redefine an inherited method with the same signature.");
        createQuestion(t5, QuestionType.QUIZ,
                "Does Java support multiple inheritance of classes (e.g., `class C extends A, B`)?",
                "Yes, for all classes", "No, a class can extend only one superclass (though it can implement multiple interfaces)", "Only for abstract classes", "Only inside the same package",
                "B", "Java avoids the diamond problem by allowing single class inheritance and multiple interface implementation.");

        // Topic 6: Exception Handling & Collections
        Topic t6 = createTopic(javaCourse, 6, "Exception Handling & Collections",
                "try-catch-finally blocks, checked vs unchecked exceptions, ArrayList, and HashMap.");
        createLesson(t6, 1, "Robust Error Handling & Dynamic Collections", 20,
                "Use try-catch-finally to handle runtime anomalies gracefully.;ArrayList provides a resizable dynamic array.;HashMap stores key-value pairs with O(1) average lookup time.",
                """
                <h3>Exception Handling &amp; Java Collections Framework</h3>
                <pre><code>import java.util.*;

List&lt;String&gt; topics = new ArrayList&lt;&gt;();
topics.add("Inheritance");

Map&lt;String, Integer&gt; mastery = new HashMap&lt;&gt;();
mastery.put("Variables", 90);
mastery.put("Inheritance", 28);</code></pre>
                """);
        diagnosticQuestions.add(createQuestion(t6, QuestionType.DIAGNOSTIC,
                "Which block in Java exception handling executes regardless of whether an exception was thrown or caught?",
                "catch", "throw", "finally", "throws",
                "C", "The `finally` block always executes (unless the JVM exits), making it ideal for resource cleanup."));
        createQuestion(t6, QuestionType.PRACTICE,
                "Which Collection class stores unique elements as key-value pairs?",
                "ArrayList", "HashMap", "LinkedList", "HashSet",
                "B", "`HashMap<K, V>` maps unique keys to values with constant-time average performance.");
        createQuestion(t6, QuestionType.QUIZ,
                "Unlike a standard Java array (`int[]`), what is a key advantage of `ArrayList`?",
                "It can grow and shrink dynamically at runtime", "It uses zero memory", "It only stores boolean values", "It does not use 0-based indexing",
                "A", "`ArrayList` automatically resizes its internal backing array as elements are added.");

        // Create the Course Diagnostic Quiz
        Quiz diagQuiz = new Quiz();
        diagQuiz.setCourse(javaCourse);
        diagQuiz.setTitle("Java Programming - Diagnostic Assessment");
        diagQuiz.setQuizType(QuizType.DIAGNOSTIC);
        diagQuiz.setTimeLimitMinutes(15);
        diagQuiz.setQuestions(diagnosticQuestions);
        quizRepository.save(diagQuiz);

        // Seed Practical Challenges for each of the 6 Topics (Unlocked when Topic Mastery >= 90%)
        createChallenge(t1, "Constant Tax & Price Calculator",
                "Declare a constant `final double TAX_RATE = 0.18;` and compute the final integer price using explicit `(int)` type casting.",
                "public class PriceCalculator {\n    public static void main(String[] args) {\n        // Declare final double TAX_RATE and cast result to (int)\n    }\n}",
                "final;double;(int)", ChallengeType.CODING, "EASY", 50);

        createChallenge(t2, "Prime Number Filter Loop",
                "Write a `for` loop that iterates through numbers and uses `continue` or `break` to filter values.",
                "public class PrimeFilter {\n    public static void main(String[] args) {\n        // Implement a for loop using break or continue\n    }\n}",
                "for;if", ChallengeType.CODING, "EASY", 50);

        createChallenge(t3, "2D Matrix Diagonal Sum",
                "Given a 2D integer array `int[][] matrix`, compute the sum of its primary diagonal using `.length`.",
                "public class MatrixDiagonal {\n    public static int diagonalSum(int[][] matrix) {\n        // Iterate using matrix.length\n        return 0;\n    }\n}",
                "int[][];length;return", ChallengeType.CODING, "MEDIUM", 75);

        createChallenge(t4, "Encapsulated BankAccount Class",
                "Create a class with `private double balance`, a constructor using `this.balance`, and a `public` getter method.",
                "public class BankAccount {\n    // Add private balance field, constructor, and public getter\n}",
                "private;this.;public", ChallengeType.CODING, "MEDIUM", 75);

        createChallenge(t5, "Polymorphic Payment Gateway",
                "Create a subclass `UpiPayment extends Payment` that overrides `@Override public void process()` and calls `super`.",
                "class Payment {\n    public void process() {}\n}\n\n// Create UpiPayment subclass below\n",
                "extends;@Override;process", ChallengeType.MINI_PROJECT, "HARD", 100);

        createChallenge(t6, "Student Mastery HashMap Tracker",
                "Use a `HashMap<String, Integer>` inside a `try` / `catch` block to store and retrieve topic mastery scores safely.",
                "import java.util.*;\n\npublic class MasteryTracker {\n    public static void main(String[] args) {\n        // Use HashMap, put(), and try-catch\n    }\n}",
                "HashMap;put;try;catch", ChallengeType.MINI_PROJECT, "HARD", 100);

        System.out.println("[EduNest] Seeded flagship 'Java Programming' course with 6 topics, lessons, 18 questions, and 6 practical challenges.");
    }

    private void seedAdditionalCoursesAndQuestions(User instructor) {
        // Expand question bank on Java topics if total questions < 50
        if (questionRepository.count() < 50) {
            List<Topic> javaTopics = topicRepository.findAll();
            for (Topic t : javaTopics) {
                if (t.getTitle().contains("Variables")) {
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which Java primitive type occupies 64 bits of storage in memory?",
                            "int", "short", "long", "byte",
                            "C", "In Java, long is a 64-bit two's complement integer, whereas int is 32-bit.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "What is the maximum value of a Java `byte` data type?",
                            "127", "128", "255", "256",
                            "A", "The byte data type is an 8-bit signed two's complement integer ranging from -128 to 127.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which type casting is performed automatically by Java without an explicit cast operator?",
                            "Narrowing (double to int)", "Widening (int to double)", "Explicit string parsing", "Boolean to int",
                            "B", "Widening conversion happens automatically when moving to a larger data type.");
                    createQuestion(t, QuestionType.QUIZ,
                            "What is the output of `System.out.println(10 / 4)` in Java?",
                            "2.5", "2", "3", "Error",
                            "B", "Integer division truncates the decimal portion, resulting in 2.");
                } else if (t.getTitle().contains("Loops")) {
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which loop construct is best suited when iterating across elements of an iterable collection?",
                            "Classic for(;;)", "while loop", "Enhanced for-each loop", "do-while loop",
                            "C", "The enhanced for-each loop cleanly iterates over each element without manual index tracking.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which loop checks its boolean condition AFTER executing its loop body once?",
                            "for loop", "while loop", "do-while loop", "nested for loop",
                            "C", "The do-while loop always executes the body at least once because the test happens at the exit.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "What happens when you execute `for(;;)` in Java without a loop condition?",
                            "Compile error", "It creates an infinite loop", "It executes zero times", "It throws a RuntimeException",
                            "B", "An omitted condition in a for loop header evaluates to true, producing an infinite loop.");
                    createQuestion(t, QuestionType.QUIZ,
                            "What keyword skips only the current iteration of a loop without exiting?",
                            "pass", "skip", "continue", "break",
                            "C", "`continue` skips the rest of the current iteration and evaluates the next loop condition.");
                } else if (t.getTitle().contains("Arrays")) {
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which exception is thrown if you access `arr[arr.length]`?",
                            "NullPointerException", "ArrayIndexOutOfBoundsException", "IllegalArgumentException", "IndexError",
                            "B", "Array indices are 0 to length - 1. Accessing length throws ArrayIndexOutOfBoundsException.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "What does `Arrays.sort(arr)` do when given a primitive integer array?",
                            "Sorts the array in ascending order in-place", "Returns a new sorted copy", "Throws an exception", "Sorts in descending order",
                            "A", "java.util.Arrays.sort modifies the array directly in ascending numerical order.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "How do you initialize a 2D array with 3 rows and 4 columns in Java?",
                            "int[][] m = new int[3][4];", "int[][] m = new int[4][3];", "int m = new int[3, 4];", "matrix[3][4] int;",
                            "A", "The first bracket specifies the row count (3) and the second specifies columns (4).");
                } else if (t.getTitle().contains("OOP")) {
                    createQuestion(t, QuestionType.PRACTICE,
                            "Can an abstract class in Java have concrete methods with method bodies?",
                            "No, only abstract methods", "Yes, it can contain both abstract and concrete methods", "Only static methods", "Only private methods",
                            "B", "An abstract class can declare both fully implemented concrete methods and abstract method signatures.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which keyword prevents a class from being subclassed in Java?",
                            "abstract", "static", "final", "sealed",
                            "C", "Declaring a class `final` prevents any other class from extending it.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "What is the default access level of class members when no modifier is specified?",
                            "private", "public", "protected", "Package-private (default)",
                            "D", "Members without an access modifier are visible only within classes in the same package.");
                } else if (t.getTitle().contains("Inheritance")) {
                    createQuestion(t, QuestionType.PRACTICE,
                            "What does the `@Override` annotation indicate to the Java compiler?",
                            "The method must be static", "The method overrides a superclass declaration and causes a compiler error if signatures mismatch", "The method runs faster", "The method is deprecated",
                            "B", "`@Override` instructs the compiler to verify that the method actually overrides an inherited method.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which class sits at the very root of the entire Java class hierarchy?",
                            "java.lang.System", "java.lang.Object", "java.lang.Class", "java.lang.Base",
                            "B", "Every class in Java is a direct or indirect descendant of java.lang.Object.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "Can a subclass access `private` fields of its superclass directly?",
                            "Yes, always", "No, private fields are not directly accessible in subclasses", "Only if in the same package", "Only using super.",
                            "B", "Private members can only be accessed through public or protected getter/setter methods.");
                } else if (t.getTitle().contains("Exception")) {
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which of the following is an unchecked (Runtime) exception in Java?",
                            "IOException", "SQLException", "NullPointerException", "ClassNotFoundException",
                            "C", "Subclasses of RuntimeException (like NullPointerException) are unchecked in Java.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "What happens if both `try` and `finally` blocks have return statements?",
                            "Compiler error", "The return value from the finally block overrides the try block", "The try block return value is returned", "An exception is thrown",
                            "B", "Code in the finally block always executes and its return statement overrides any prior return.");
                    createQuestion(t, QuestionType.PRACTICE,
                            "Which collection does NOT allow duplicate elements?",
                            "ArrayList", "LinkedList", "HashSet", "Vector",
                            "C", "A Set (such as HashSet) guarantees unique elements and discards duplicate insertions.");
                }
            }
        }

        // Seed Python Course if not present
        boolean hasPython = courseRepository.findAll().stream()
                .anyMatch(c -> c.getTitle().toLowerCase().contains("python"));
        if (!hasPython) {
            Course pythonCourse = new Course();
            pythonCourse.setTitle("Python & Data Science Foundations");
            pythonCourse.setDescription("Master Python basics, data structures, NumPy arrays, and Pandas analysis with guided challenges.");
            pythonCourse.setThumbnailUrl("https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600");
            pythonCourse.setInstructor(instructor);
            pythonCourse.setPublished(true);
            pythonCourse = courseRepository.save(pythonCourse);

            Topic pyT1 = createTopic(pythonCourse, 1, "Python Syntax & Built-in Types", "Variables, dynamic typing, strings, and numeric operations.");
            createLesson(pyT1, 1, "Variables, F-Strings & Dynamic Types", 15,
                    "Python is dynamically typed.;F-strings allow concise string interpolation.;Indentation defines block scope.",
                    "<h3>Python Syntax Basics</h3><p>Python variables do not require explicit type declarations.</p><pre><code>name = 'EduNest'\ncount = 42\nprint(f'{name} has {count} drills')</code></pre>");
            createQuestion(pyT1, QuestionType.DIAGNOSTIC,
                    "Which built-in Python function returns the type of a variable?",
                    "typeof()", "type()", "varType()", "inspect()",
                    "B", "The `type()` built-in function returns the type object of any Python value.");
            createQuestion(pyT1, QuestionType.PRACTICE,
                    "How are code blocks defined in Python?",
                    "Curly braces {}", "Keywords begin/end", "Consistent whitespace indentation", "Semicolons",
                    "C", "Python relies strictly on indentation to demarcate code blocks.");
            createQuestion(pyT1, QuestionType.QUIZ,
                    "What is the output of `bool('')` in Python?",
                    "True", "False", "None", "SyntaxError",
                    "B", "An empty string evaluates to `False` in a boolean context.");

            Topic pyT2 = createTopic(pythonCourse, 2, "Lists, Dictionaries & Comprehensions", "Sequence manipulation and key-value mapping.");
            createLesson(pyT2, 1, "Python Collections & List Comprehensions", 18,
                    "Lists are ordered and mutable.;Dicts provide fast O(1) key lookups.;List comprehensions offer concise iteration.",
                    "<h3>Comprehensions in Python</h3><p>Create lists succinctly using comprehensions:</p><pre><code>squares = [x**2 for x in range(5)]\nprint(squares) # [0, 1, 4, 9, 16]</code></pre>");
            createQuestion(pyT2, QuestionType.PRACTICE,
                    "Which method removes and returns the last element of a Python list?",
                    "remove()", "pop()", "discard()", "delete()",
                    "B", "`list.pop()` removes and returns the last item by default.");
            createQuestion(pyT2, QuestionType.QUIZ,
                    "What data structure in Python does `{ 'a': 1, 'b': 2 }` represent?",
                    "List", "Tuple", "Dictionary", "Set",
                    "C", "Curly braces with key-value pairs define a dictionary (`dict`).");
        }

        // Seed Web Dev Course if not present
        boolean hasWeb = courseRepository.findAll().stream()
                .anyMatch(c -> c.getTitle().toLowerCase().contains("web"));
        if (!hasWeb) {
            Course webCourse = new Course();
            webCourse.setTitle("Full-Stack Web Development");
            webCourse.setDescription("Learn modern responsive HTML5, modern CSS layouts, and asynchronous JavaScript APIs.");
            webCourse.setThumbnailUrl("https://images.unsplash.com/photo-1547658719-da2b51169166?w=600");
            webCourse.setInstructor(instructor);
            webCourse.setPublished(true);
            webCourse = courseRepository.save(webCourse);

            Topic webT1 = createTopic(webCourse, 1, "Modern Semantic HTML & Accessibility", "Clean document outlines, forms, and ARIA roles.");
            createLesson(webT1, 1, "Semantic Elements & Accessible Documents", 14,
                    "Use main, nav, section, and article for structure.;Label every interactive input.;Prefer semantic tags over generic div tags.",
                    "<h3>Semantic HTML5</h3><p>Semantic markup makes websites accessible and SEO-friendly.</p><pre><code>&lt;header&gt;\n  &lt;nav&gt;...&lt;/nav&gt;\n&lt;/header&gt;</code></pre>");
            createQuestion(webT1, QuestionType.PRACTICE,
                    "Which HTML5 tag is most appropriate for a standalone, self-contained blog post?",
                    "&lt;div&gt;", "&lt;section&gt;", "&lt;article&gt;", "&lt;aside&gt;",
                    "C", "`&lt;article&gt;` represents a complete, self-contained composition.");
            createQuestion(webT1, QuestionType.QUIZ,
                    "What attribute connects an HTML `<label>` to its corresponding `<input>` element?",
                    "name", "for", "rel", "id",
                    "B", "The `<label for='id'>` matches the input element's `id` attribute.");
        }
    }

    private Topic createTopic(Course course, int orderIndex, String title, String description) {
        Topic topic = new Topic();
        topic.setCourse(course);
        topic.setOrderIndex(orderIndex);
        topic.setTitle(title);
        topic.setDescription(description);
        return topicRepository.save(topic);
    }

    private void createLesson(Topic topic, int orderIndex, String title, int estimatedMinutes,
                              String keyPoints, String content) {
        Lesson lesson = new Lesson();
        lesson.setTopic(topic);
        lesson.setOrderIndex(orderIndex);
        lesson.setTitle(title);
        lesson.setEstimatedMinutes(estimatedMinutes);
        lesson.setKeyPoints(keyPoints);
        lesson.setContent(content);
        lessonRepository.save(lesson);
    }

    private Question createQuestion(Topic topic, QuestionType type, String text,
                                    String a, String b, String c, String d,
                                    String correct, String explanation) {
        Question q = new Question();
        q.setTopic(topic);
        q.setQuestionType(type);
        q.setQuestionText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrectOption(correct);
        q.setExplanation(explanation);
        return questionRepository.save(q);
    }

    private void createChallenge(Topic topic, String title, String description,
                                 String starterCode, String expectedKeywords,
                                 ChallengeType type, String difficulty, int points) {
        Challenge c = new Challenge();
        c.setTopic(topic);
        c.setTitle(title);
        c.setDescription(description);
        c.setStarterCode(starterCode);
        c.setExpectedKeywords(expectedKeywords);
        c.setChallengeType(type);
        c.setDifficulty(difficulty);
        c.setPoints(points);
        c.setUnlockThreshold(90.0);
        challengeRepository.save(c);
    }

    private void updateCodingChallenges() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) return;
        Course javaCourse = courses.get(0);
        List<Topic> topics = topicRepository.findByCourseIdOrderByOrderIndexAsc(javaCourse.getId());
        if (topics.isEmpty()) return;

        // Challenge 1: Tax-Inclusive Price & Cast Calculator
        if (topics.size() >= 1) {
            Topic t1 = topics.get(0);
            Challenge c1 = getOrCreateChallenge(t1, "Tax-Inclusive Price & Cast Calculator");
            c1.setDescription("In commercial billing systems, transactions must apply dynamic percentage-based sales tax and truncate to integer currency values.\n\n"
                    + "Given two space-separated integers on standard input:\n"
                    + "- `subtotal`: The base item cost before tax.\n"
                    + "- `taxRate`: The percentage sales tax rate (e.g. `18` for 18%).\n\n"
                    + "Compute and print the final integer price calculated with the formula:\n"
                    + "`finalPrice = subtotal + (subtotal * taxRate / 100)`");
            c1.setInputFormat("A single line containing two space-separated integers: subtotal taxRate");
            c1.setOutputFormat("Print a single integer: the final calculated price.");
            c1.setConstraints("1 <= subtotal <= 1,000,000\n0 <= taxRate <= 100");
            c1.setDifficulty("EASY");
            c1.setPoints(50);
            c1.setUnlockThreshold(90.0);
            c1.setExpectedKeywords("final;double;(int)");
            c1.setSampleTestCasesJson("[{\"input\":\"100 18\",\"expectedOutput\":\"118\",\"explanation\":\"100 + (100 * 18 / 100) = 118\"},{\"input\":\"250 12\",\"expectedOutput\":\"280\",\"explanation\":\"250 + (250 * 12 / 100) = 280\"}]");
            c1.setHiddenTestCasesJson("[{\"input\":\"50 0\",\"expectedOutput\":\"50\"},{\"input\":\"999 15\",\"expectedOutput\":\"1148\"},{\"input\":\"10000 28\",\"expectedOutput\":\"12800\"}]");
            c1.setStarterCode("""
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            int subtotal = sc.nextInt();
            int taxRate = sc.nextInt();
            // TODO: Compute subtotal + (subtotal * taxRate / 100)
            int total = subtotal + (subtotal * taxRate / 100);
            System.out.println(total);
        }
    }
}
""");
            c1.setStarterCodePython("""
import sys

def main():
    data = sys.stdin.read().split()
    if len(data) >= 2:
        subtotal = int(data[0])
        tax_rate = int(data[1])
        # TODO: Compute subtotal + (subtotal * tax_rate // 100)
        total = subtotal + (subtotal * tax_rate // 100)
        print(total)

if __name__ == '__main__':
    main()
""");
            c1.setStarterCodeJs("""
const fs = require('fs');

function main() {
    const input = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);
    if (input.length >= 2) {
        const subtotal = parseInt(input[0], 10);
        const taxRate = parseInt(input[1], 10);
        // TODO: Compute subtotal + (subtotal * taxRate / 100)
        const total = Math.floor(subtotal + (subtotal * taxRate / 100));
        console.log(total);
    }
}

main();
""");
            challengeRepository.save(c1);
        }

        // Challenge 2: Sum of Evens in Range
        if (topics.size() >= 2) {
            Topic t2 = topics.get(1);
            Challenge c2 = getOrCreateChallenge(t2, "Sum of Evens in Range");
            c2.setDescription("Write a program that takes a positive integer `N` and computes the sum of all positive even integers from `1` up to `N` (inclusive).\n\n"
                    + "For example, if `N = 6`, the even integers are `2, 4, 6` and their sum is `2 + 4 + 6 = 12`.\n"
                    + "If `N < 2`, print `0`.");
            c2.setInputFormat("A single line containing an integer N.");
            c2.setOutputFormat("Print a single integer: the sum of all even integers <= N.");
            c2.setConstraints("1 <= N <= 10,000");
            c2.setDifficulty("EASY");
            c2.setPoints(50);
            c2.setUnlockThreshold(90.0);
            c2.setExpectedKeywords("for;if");
            c2.setSampleTestCasesJson("[{\"input\":\"6\",\"expectedOutput\":\"12\",\"explanation\":\"2 + 4 + 6 = 12\"},{\"input\":\"10\",\"expectedOutput\":\"30\",\"explanation\":\"2 + 4 + 6 + 8 + 10 = 30\"}]");
            c2.setHiddenTestCasesJson("[{\"input\":\"1\",\"expectedOutput\":\"0\"},{\"input\":\"20\",\"expectedOutput\":\"110\"},{\"input\":\"100\",\"expectedOutput\":\"2550\"}]");
            c2.setStarterCode("""
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            int n = sc.nextInt();
            // TODO: Sum of even numbers from 2 to n
            int sum = 0;
            for (int i = 2; i <= n; i += 2) {
                sum += i;
            }
            System.out.println(sum);
        }
    }
}
""");
            c2.setStarterCodePython("""
import sys

def main():
    data = sys.stdin.read().split()
    if data:
        n = int(data[0])
        total = sum(i for i in range(2, n + 1, 2))
        print(total)

if __name__ == '__main__':
    main()
""");
            c2.setStarterCodeJs("""
const fs = require('fs');

function main() {
    const input = fs.readFileSync(0, 'utf-8').trim();
    if (input) {
        const n = parseInt(input, 10);
        let sum = 0;
        for (let i = 2; i <= n; i += 2) {
            sum += i;
        }
        console.log(sum);
    }
}

main();
""");
            challengeRepository.save(c2);
        }

        // Challenge 3: Array Peak Element Finder
        if (topics.size() >= 3) {
            Topic t3 = topics.get(2);
            Challenge c3 = getOrCreateChallenge(t3, "Array Peak Element Finder");
            c3.setDescription("Given an array of integers, identify and print the maximum value (peak element) present in the array.\n\n"
                    + "The input begins with an integer `K` denoting the total number of elements, followed by `K` space-separated integers.");
            c3.setInputFormat("The first integer K represents array length, followed by K space-separated integers.");
            c3.setOutputFormat("Print the maximum integer value in the array.");
            c3.setConstraints("1 <= K <= 1,000\n-10,000 <= element <= 10,000");
            c3.setDifficulty("MEDIUM");
            c3.setPoints(75);
            c3.setUnlockThreshold(90.0);
            c3.setExpectedKeywords("int[][];length;return");
            c3.setSampleTestCasesJson("[{\"input\":\"5 1 9 3 7 4\",\"expectedOutput\":\"9\",\"explanation\":\"Maximum among [1, 9, 3, 7, 4] is 9.\"},{\"input\":\"4 -5 -2 -10 -1\",\"expectedOutput\":\"-1\",\"explanation\":\"Maximum among [-5, -2, -10, -1] is -1.\"}]");
            c3.setHiddenTestCasesJson("[{\"input\":\"3 100 200 50\",\"expectedOutput\":\"200\"},{\"input\":\"1 42\",\"expectedOutput\":\"42\"},{\"input\":\"6 8 8 8 8 8 8\",\"expectedOutput\":\"8\"}]");
            c3.setStarterCode("""
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            int k = sc.nextInt();
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < k; i++) {
                int val = sc.nextInt();
                if (val > max) max = val;
            }
            System.out.println(max);
        }
    }
}
""");
            c3.setStarterCodePython("""
import sys

def main():
    data = sys.stdin.read().split()
    if data:
        k = int(data[0])
        nums = [int(x) for x in data[1:k+1]]
        print(max(nums))

if __name__ == '__main__':
    main()
""");
            c3.setStarterCodeJs("""
const fs = require('fs');

function main() {
    const tokens = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);
    if (tokens.length > 0) {
        const k = parseInt(tokens[0], 10);
        let maxVal = -Infinity;
        for (let i = 1; i <= k; i++) {
            const val = parseInt(tokens[i], 10);
            if (val > maxVal) maxVal = val;
        }
        console.log(maxVal);
    }
}

main();
""");
            challengeRepository.save(c3);
        }

        // Challenge 4: Bank Account Transaction Simulator
        if (topics.size() >= 4) {
            Topic t4 = topics.get(3);
            Challenge c4 = getOrCreateChallenge(t4, "Bank Account Transaction Simulator");
            c4.setDescription("Simulate an encapsulated bank account with balance validation.\n\n"
                    + "Input begins with two integers:\n"
                    + "- `initialBalance`: The starting balance.\n"
                    + "- `N`: The number of sequential transactions.\n\n"
                    + "Followed by `N` integers representing transaction amounts (positive for deposit, negative for withdrawal).\n\n"
                    + "Process each transaction in sequence. If any withdrawal causes the balance to drop below `0`, print `INVALID`.\n"
                    + "Otherwise, print the final integer balance.");
            c4.setInputFormat("initialBalance N followed by N integers.");
            c4.setOutputFormat("Final balance, or INVALID if balance drops below 0.");
            c4.setConstraints("0 <= initialBalance <= 100,000\n1 <= N <= 100");
            c4.setDifficulty("MEDIUM");
            c4.setPoints(75);
            c4.setUnlockThreshold(90.0);
            c4.setExpectedKeywords("private;this.;public");
            c4.setSampleTestCasesJson("[{\"input\":\"100 2 50 -30\",\"expectedOutput\":\"120\",\"explanation\":\"100 + 50 = 150, 150 - 30 = 120 >= 0\"},{\"input\":\"50 2 20 -100\",\"expectedOutput\":\"INVALID\",\"explanation\":\"50 + 20 = 70, 70 - 100 = -30 < 0 (Invalid)\"}]");
            c4.setHiddenTestCasesJson("[{\"input\":\"200 1 -50\",\"expectedOutput\":\"150\"},{\"input\":\"100 3 -50 -50 25\",\"expectedOutput\":\"25\"},{\"input\":\"10 1 -11\",\"expectedOutput\":\"INVALID\"}]");
            c4.setStarterCode("""
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            int balance = sc.nextInt();
            int n = sc.nextInt();
            boolean valid = true;
            for (int i = 0; i < n; i++) {
                int tx = sc.nextInt();
                balance += tx;
                if (balance < 0) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                System.out.println(balance);
            } else {
                System.out.println("INVALID");
            }
        }
    }
}
""");
            c4.setStarterCodePython("""
import sys

def main():
    data = sys.stdin.read().split()
    if len(data) >= 2:
        balance = int(data[0])
        n = int(data[1])
        for i in range(2, 2 + n):
            tx = int(data[i])
            balance += tx
            if balance < 0:
                print("INVALID")
                return
        print(balance)

if __name__ == '__main__':
    main()
""");
            c4.setStarterCodeJs("""
const fs = require('fs');

function main() {
    const tokens = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);
    if (tokens.length >= 2) {
        let balance = parseInt(tokens[0], 10);
        const n = parseInt(tokens[1], 10);
        for (let i = 2; i < 2 + n; i++) {
            balance += parseInt(tokens[i], 10);
            if (balance < 0) {
                console.log("INVALID");
                return;
            }
        }
        console.log(balance);
    }
}

main();
""");
            challengeRepository.save(c4);
        }

        // Challenge 5: Shape Area Calculator
        if (topics.size() >= 5) {
            Topic t5 = topics.get(4);
            Challenge c5 = getOrCreateChallenge(t5, "Shape Area Calculator");
            c5.setDescription("Implement an object-oriented polymorphic shape area calculator.\n\n"
                    + "Input starts with a shape identifier string, followed by dimensions:\n"
                    + "- `RECTANGLE width height` -> Print area `width * height`\n"
                    + "- `SQUARE side` -> Print area `side * side`\n"
                    + "- `CIRCLE radius` -> Print integer area `3 * radius * radius` (approximating pi=3)");
            c5.setInputFormat("Shape identifier (RECTANGLE, SQUARE, or CIRCLE) followed by integer dimensions.");
            c5.setOutputFormat("Print a single integer: the calculated area.");
            c5.setConstraints("All dimensions are positive integers between 1 and 1,000.");
            c5.setDifficulty("HARD");
            c5.setPoints(100);
            c5.setUnlockThreshold(90.0);
            c5.setExpectedKeywords("extends;@Override;process");
            c5.setSampleTestCasesJson("[{\"input\":\"RECTANGLE 4 5\",\"expectedOutput\":\"20\",\"explanation\":\"4 * 5 = 20\"},{\"input\":\"SQUARE 7\",\"expectedOutput\":\"49\",\"explanation\":\"7 * 7 = 49\"}]");
            c5.setHiddenTestCasesJson("[{\"input\":\"CIRCLE 5\",\"expectedOutput\":\"75\"},{\"input\":\"RECTANGLE 10 25\",\"expectedOutput\":\"250\"},{\"input\":\"SQUARE 12\",\"expectedOutput\":\"144\"}]");
            c5.setStarterCode("""
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNext()) {
            String shape = sc.next().toUpperCase();
            if (shape.equals("RECTANGLE")) {
                int w = sc.nextInt();
                int h = sc.nextInt();
                System.out.println(w * h);
            } else if (shape.equals("SQUARE")) {
                int s = sc.nextInt();
                System.out.println(s * s);
            } else if (shape.equals("CIRCLE")) {
                int r = sc.nextInt();
                System.out.println(3 * r * r);
            }
        }
    }
}
""");
            c5.setStarterCodePython("""
import sys

def main():
    tokens = sys.stdin.read().split()
    if tokens:
        shape = tokens[0].upper()
        if shape == "RECTANGLE":
            print(int(tokens[1]) * int(tokens[2]))
        elif shape == "SQUARE":
            print(int(tokens[1]) * int(tokens[1]))
        elif shape == "CIRCLE":
            r = int(tokens[1])
            print(3 * r * r)

if __name__ == '__main__':
    main()
""");
            c5.setStarterCodeJs("""
const fs = require('fs');

function main() {
    const tokens = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);
    if (tokens.length > 0) {
        const shape = tokens[0].toUpperCase();
        if (shape === 'RECTANGLE') {
            console.log(parseInt(tokens[1], 10) * parseInt(tokens[2], 10));
        } else if (shape === 'SQUARE') {
            const s = parseInt(tokens[1], 10);
            console.log(s * s);
        } else if (shape === 'CIRCLE') {
            const r = parseInt(tokens[1], 10);
            console.log(3 * r * r);
        }
    }
}

main();
""");
            challengeRepository.save(c5);
        }

        // Challenge 6: Distinct Word Counter
        if (topics.size() >= 6) {
            Topic t6 = topics.get(5);
            Challenge c6 = getOrCreateChallenge(t6, "Distinct Word Counter");
            c6.setDescription("Given a sequence of words from a document stream, count how many distinct unique words exist in the collection.\n\n"
                    + "The first input integer `N` denotes the count of words, followed by `N` space-separated words.");
            c6.setInputFormat("Integer N followed by N space-separated words.");
            c6.setOutputFormat("Print a single integer: the count of unique words.");
            c6.setConstraints("1 <= N <= 1,000, words consist of alphanumeric characters.");
            c6.setDifficulty("HARD");
            c6.setPoints(100);
            c6.setUnlockThreshold(90.0);
            c6.setExpectedKeywords("HashMap;put;try;catch");
            c6.setSampleTestCasesJson("[{\"input\":\"5 java python java cpp python\",\"expectedOutput\":\"3\",\"explanation\":\"Unique words: java, python, cpp -> 3\"},{\"input\":\"4 hello world hello hello\",\"expectedOutput\":\"2\",\"explanation\":\"Unique words: hello, world -> 2\"}]");
            c6.setHiddenTestCasesJson("[{\"input\":\"3 apple banana orange\",\"expectedOutput\":\"3\"},{\"input\":\"1 code\",\"expectedOutput\":\"1\"},{\"input\":\"6 a b a c b d\",\"expectedOutput\":\"4\"}]");
            c6.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            int n = sc.nextInt();
            Set<String> set = new HashSet<>();
            for (int i = 0; i < n; i++) {
                set.add(sc.next());
            }
            System.out.println(set.size());
        }
    }
}
""");
            c6.setStarterCodePython("""
import sys

def main():
    tokens = sys.stdin.read().split()
    if tokens:
        n = int(tokens[0])
        words = tokens[1:n+1]
        print(len(set(words)))

if __name__ == '__main__':
    main()
""");
            c6.setStarterCodeJs("""
const fs = require('fs');

function main() {
    const tokens = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/);
    if (tokens.length > 0) {
        const n = parseInt(tokens[0], 10);
        const set = new Set();
        for (let i = 1; i <= n && i < tokens.length; i++) {
            set.add(tokens[i]);
        }
        console.log(set.size);
    }
}

main();
""");
            challengeRepository.save(c6);
        }

        System.out.println("[EduNest] Seeded and updated 6 Real Coding Challenges with multi-language starter code and test cases.");
    }

    private Challenge getOrCreateChallenge(Topic topic, String title) {
        List<Challenge> list = challengeRepository.findByTopicId(topic.getId());
        if (!list.isEmpty()) {
            Challenge existing = list.get(0);
            existing.setTitle(title);
            return existing;
        }
        Challenge c = new Challenge();
        c.setTopic(topic);
        c.setTitle(title);
        c.setChallengeType(ChallengeType.CODING);
        return c;
    }
}
