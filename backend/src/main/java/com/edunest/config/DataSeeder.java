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
}
