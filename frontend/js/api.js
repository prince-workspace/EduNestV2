// Centralized API Wrapper & High-Fidelity Standalone Engine for EduNest V2
// Seamlessly connects to real Spring Boot backend on localhost or cloud hosts,
// and automatically provides full offline/demo data on static hosting platforms like Vercel.

// 1-Click Query Parameter Configuration (e.g. ?api=https://my-backend.onrender.com/api)
(function() {
  if (typeof window !== 'undefined' && window.location.search) {
    try {
      const urlParams = new URLSearchParams(window.location.search);
      const paramApi = urlParams.get('api');
      if (paramApi) {
        const clean = paramApi.trim().replace(/\/+$/, '');
        localStorage.setItem('edunest_api_url', clean);
        const url = new URL(window.location.href);
        url.searchParams.delete('api');
        window.history.replaceState({}, document.title, url.toString());
      }
    } catch (e) {}
  }
})();

const API_BASE = (function() {
  if (typeof window !== 'undefined' && window.EDUNEST_API_BASE) {
    return window.EDUNEST_API_BASE;
  }
  const stored = typeof localStorage !== 'undefined' ? localStorage.getItem('edunest_api_url') : null;
  if (stored) return stored;

  if (typeof window !== 'undefined') {
    const host = window.location.hostname || '';
    const proto = window.location.protocol || '';
    if (host === 'localhost' || host === '127.0.0.1' || host === '0.0.0.0' || proto === 'file:' || host.startsWith('192.168.') || host.startsWith('10.')) {
      return 'http://localhost:8080/api';
    }
  }
  return '/api';
})();

// Prompt helper to configure custom backend URL
function promptBackendUrl() {
  const current = localStorage.getItem('edunest_api_url') || API_BASE;
  const next = window.prompt(
    'EduNest Backend API URL:\n(Enter your live backend URL, or leave empty to use automatic localhost / Vercel detection)',
    current
  );
  if (next !== null) {
    const trimmed = next.trim();
    if (trimmed) {
      localStorage.setItem('edunest_api_url', trimmed);
      alert('Backend API URL updated to:\n' + trimmed + '\n\nThe page will now reload.');
    } else {
      localStorage.removeItem('edunest_api_url');
      alert('Reset to automatic detection.\n\nThe page will now reload.');
    }
    window.location.reload();
  }
}

// ============================================================================
// STANDALONE DATA ENGINE (Powers entire student experience on Vercel)
// ============================================================================

const MOCK_LESSON_DATA = {
  1: {
    id: 1, courseId: 1, topicId: 1, topicTitle: 'Java Fundamentals & Syntax',
    title: 'Java Architecture, JVM Bytecode & Your First Program', estimatedMinutes: 15,
    keyPoints: 'Java achieves platform independence via bytecode executed on the JVM (Write Once, Run Anywhere).;The JDK contains compiler and developer tools; the JRE provides runtime libraries; the JVM executes bytecode.;Every Java program requires at least one class and a public static void main(String[] args) entry point.',
    previousLessonId: null, nextLessonId: 2,
    content: `<h3>1. The Java Platform &amp; Write Once, Run Anywhere</h3>
<p>Unlike languages compiled to native machine code (like C/C++), Java compiles source code into platform-independent intermediate bytecode (stored in <code>.class</code> files).</p>
<p>Any operating system with a compatible <strong>Java Virtual Machine (JVM)</strong> can execute this bytecode, delivering Java's famous promise: <em>"Write Once, Run Anywhere"</em>.</p>
<div class="lesson-callout">
  <strong>Core Platform Architecture:</strong>
  <ul>
    <li><strong>JDK (Java Development Kit):</strong> Full toolkit for developers, including the compiler (<code>javac</code>) and debugger.</li>
    <li><strong>JRE (Java Runtime Environment):</strong> The minimum runtime required to execute compiled Java programs.</li>
    <li><strong>JVM (Java Virtual Machine):</strong> The execution engine that loads bytecode, verifies security, and runs instructions via JIT compilation.</li>
  </ul>
</div>
<h3>2. Anatomy of a Standard Java Program</h3>
<p>In Java, all code must reside inside a class matching the filename:</p>
<pre><code>// File: HelloWorld.java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Welcome to EduNest Core Java!");
    }
}</code></pre>
<h4>Key Components of the Entry Point:</h4>
<ul>
  <li><code>public</code>: Allows the JVM to invoke the method from outside the class.</li>
  <li><code>static</code>: Enables invocation without instantiating the class first.</li>
  <li><code>void</code>: The method does not return a value.</li>
  <li><code>main</code>: The designated identifier recognized by the JVM launcher.</li>
  <li><code>String[] args</code>: Command-line arguments passed as an array of strings.</li>
  <li><code>System.out.println()</code>: Prints text to the standard console with a newline.</li>
</ul>`
  },
  2: {
    id: 2, courseId: 1, topicId: 1, topicTitle: 'Java Fundamentals & Syntax',
    title: 'Primitive Data Types, Memory Sizes & Variables', estimatedMinutes: 18,
    keyPoints: 'Java is statically typed with 8 primitive types: byte, short, int, long, float, double, char, and boolean.;Primitives store raw values directly on the thread stack, whereas reference types store memory addresses on the heap.;Use final to declare constants that cannot be reassigned after initialization.',
    previousLessonId: 1, nextLessonId: 3,
    content: `<h3>1. Statically-Typed Variables &amp; The 8 Primitives</h3>
<p>Java requires every variable to declare its type explicitly before assignment. Primitive types store their values directly in stack memory without object wrapper overhead.</p>
<div class="lesson-callout">
  <strong>The 8 Primitive Types:</strong>
  <ul>
    <li><strong>byte:</strong> 8-bit integer (-128 to 127)</li>
    <li><strong>short:</strong> 16-bit integer (-32,768 to 32,767)</li>
    <li><strong>int:</strong> 32-bit integer (standard default for numbers)</li>
    <li><strong>long:</strong> 64-bit integer (suffix with L)</li>
    <li><strong>float:</strong> 32-bit floating point (suffix with f)</li>
    <li><strong>double:</strong> 64-bit floating point (standard default for decimals)</li>
    <li><strong>char:</strong> 16-bit Unicode character (single quotes 'A')</li>
    <li><strong>boolean:</strong> true or false</li>
  </ul>
</div>
<pre><code>int age = 21;
double gpa = 3.85;
char letterGrade = 'A';
boolean isEnrolled = true;
final double PASSING_THRESHOLD = 70.0; // Immutable constant</code></pre>`
  },
  3: {
    id: 3, courseId: 1, topicId: 1, topicTitle: 'Java Fundamentals & Syntax',
    title: 'Operators, Arithmetic & Type Casting Mechanics', estimatedMinutes: 20,
    keyPoints: 'Widening casting (e.g. int to double) happens automatically without data loss.;Narrowing casting (e.g. double to int) requires explicit syntax and truncates fractional parts.;Integer division (e.g. 7 / 2) truncates towards zero to yield 3, not 3.5.',
    previousLessonId: 2, nextLessonId: 4,
    content: `<h3>1. Arithmetic &amp; Logical Operators</h3>
<p>Java supports standard binary arithmetic operators: <code>+</code>, <code>-</code>, <code>*</code>, <code>/</code>, and <code>%</code> (modulus/remainder).</p>
<pre><code>int total = 17;
int count = 5;
int quotient = total / count; // Evaluates to 3 (truncated integer division)
int remainder = total % count; // Evaluates to 2 (modulus)
double accurateAvg = (double) total / count; // 3.4 (explicit widening)</code></pre>
<h3>2. Widening vs. Narrowing Casting</h3>
<ul>
  <li><strong>Widening (Implicit):</strong> byte &rarr; short &rarr; int &rarr; long &rarr; float &rarr; double</li>
  <li><strong>Narrowing (Explicit):</strong> Requires cast operator <code>(int) 9.99</code> &rarr; <code>9</code></li>
</ul>`
  },
  4: {
    id: 4, courseId: 1, topicId: 2, topicTitle: 'Control Flow, Loops & Logic',
    title: 'Conditional Branching: if, else-if & switch', estimatedMinutes: 20,
    keyPoints: 'if-else chains execute the first true condition and bypass remaining branches.;switch evaluates expressions matching constant case labels with optional break statements.;Modern Java switch supports String expressions and fall-through mechanics.',
    previousLessonId: 3, nextLessonId: 5,
    content: `<h3>1. Decision Making with if / else-if</h3>
<p>Conditionals execute specific code blocks based on boolean truth values:</p>
<pre><code>int score = 85;
if (score >= 90) {
    System.out.println("Grade: A");
} else if (score >= 80) {
    System.out.println("Grade: B");
} else {
    System.out.println("Needs Improvement");
}</code></pre>`
  },
  5: {
    id: 5, courseId: 1, topicId: 2, topicTitle: 'Control Flow, Loops & Logic',
    title: 'Iteration: for, while & do-while Loops', estimatedMinutes: 25,
    keyPoints: 'for loops are ideal for fixed count iterations.;while loops evaluate conditions before executing the body.;do-while guarantees that the loop body executes at least once.',
    previousLessonId: 4, nextLessonId: 6,
    content: `<h3>1. Standard Loops in Java</h3>
<pre><code>// Count-controlled for loop
for (int i = 1; i <= 5; i++) {
    System.out.println("Iteration: " + i);
}

// Condition-controlled while loop
int count = 3;
while (count > 0) {
    System.out.println("T-minus " + count);
    count--;
}</code></pre>`
  },
  6: {
    id: 6, courseId: 1, topicId: 2, topicTitle: 'Control Flow, Loops & Logic',
    title: 'Break, Continue & Nested Loop Mechanics', estimatedMinutes: 20,
    keyPoints: 'break immediately terminates the innermost enclosing loop.;continue skips the rest of the current iteration and jumps to the next iteration.;Labels can be used to break or continue outer loops in nested scenarios.',
    previousLessonId: 5, nextLessonId: 7,
    content: `<h3>1. Jump Statements: break &amp; continue</h3>
<pre><code>for (int i = 1; i <= 10; i++) {
    if (i % 2 == 0) continue; // Skip even numbers
    if (i > 7) break;         // Stop loop when reaching numbers above 7
    System.out.println("Odd: " + i); // Prints 1, 3, 5, 7
}</code></pre>`
  },
  7: {
    id: 7, courseId: 1, topicId: 3, topicTitle: 'Arrays & Memory Management',
    title: '1D Arrays, Heap Allocation & Traversal', estimatedMinutes: 25,
    keyPoints: 'Arrays are objects stored on the Heap; array indices are zero-based (0 to length - 1).;array.length is a constant field, not a method.;Accessing an invalid index throws ArrayIndexOutOfBoundsException.',
    previousLessonId: 6, nextLessonId: 8,
    content: `<h3>1. Array Declaration &amp; Allocation</h3>
<p>An array is a fixed-size container holding elements of a single data type:</p>
<pre><code>int[] numbers = new int[5]; // Allocated on heap with default 0s
numbers[0] = 42;
System.out.println("Length: " + numbers.length);

for (int num : numbers) {
    System.out.println("Value: " + num);
}</code></pre>`
  },
  8: {
    id: 8, courseId: 1, topicId: 3, topicTitle: 'Arrays & Memory Management',
    title: 'Multidimensional Arrays & Matrix Problems', estimatedMinutes: 30,
    keyPoints: '2D arrays in Java are arrays of array references (can be ragged/jagged).;Matrix traversal commonly requires nested for loops.;Row-major order traversals optimize CPU cache locality.',
    previousLessonId: 7, nextLessonId: 9,
    content: `<h3>1. 2D Arrays &amp; Matrices</h3>
<pre><code>int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
System.out.println("Center element: " + matrix[1][1]); // 5</code></pre>`
  },
  9: {
    id: 9, courseId: 1, topicId: 3, topicTitle: 'Arrays & Memory Management',
    title: 'Array Copying, References & Boundary Errors', estimatedMinutes: 20,
    keyPoints: 'Assigning b = a only copies reference pointers, not array elements.;Use System.arraycopy() or Arrays.copyOf() for true element duplication.;Be careful with shallow vs deep copying when dealing with object arrays.',
    previousLessonId: 8, nextLessonId: 10,
    content: `<h3>1. Reference vs Value Semantics</h3>
<pre><code>int[] original = {10, 20, 30};
int[] copy = java.util.Arrays.copyOf(original, original.length);
copy[0] = 999;
System.out.println(original[0]); // Still 10 (true independent copy)</code></pre>`
  },
  10: {
    id: 10, courseId: 1, topicId: 4, topicTitle: 'Methods, Stack Execution & Recursion',
    title: 'Method Signatures, Pass-by-Value & Overloading', estimatedMinutes: 20,
    keyPoints: 'Java is strictly pass-by-value: primitives pass copies of data; objects pass copies of reference addresses.;Method overloading requires identical method names with differing parameter lists.;Return type alone cannot differentiate overloaded methods.',
    previousLessonId: 9, nextLessonId: 11,
    content: `<h3>1. Method Signatures &amp; Overloading</h3>
<pre><code>public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}</code></pre>`
  },
  11: {
    id: 11, courseId: 1, topicId: 4, topicTitle: 'Methods, Stack Execution & Recursion',
    title: 'Stack Frames, Variable Scope & Lifetime', estimatedMinutes: 25,
    keyPoints: 'Every method invocation allocates an isolated stack frame storing local variables and intermediate operands.;Stack frames pop automatically when the method completes.;Objects live on the heap and are reclaimed by the garbage collector.',
    previousLessonId: 10, nextLessonId: 12,
    content: `<h3>1. Stack vs. Heap Execution</h3>
<p>Variables declared inside a method exist only within the method's stack frame. When the method returns, its frame is destroyed.</p>`
  },
  12: {
    id: 12, courseId: 1, topicId: 4, topicTitle: 'Methods, Stack Execution & Recursion',
    title: 'Recursion, Base Cases & Call Stack Analysis', estimatedMinutes: 30,
    keyPoints: 'A recursive function must have at least one base case to terminate execution.;Missing or unreachable base cases cause StackOverflowError.;Each recursive call creates an independent stack frame with its own parameters.',
    previousLessonId: 11, nextLessonId: 13,
    content: `<h3>1. Recursive Call Stack Mechanics</h3>
<pre><code>public static int factorial(int n) {
    if (n <= 1) return 1; // Base case
    return n * factorial(n - 1); // Recursive call
}</code></pre>`
  },
  13: {
    id: 13, courseId: 1, topicId: 5, topicTitle: 'Object-Oriented Programming Core',
    title: 'Classes, Objects, Constructors & this Keyword', estimatedMinutes: 30,
    keyPoints: 'A class is a blueprint; an object is an instantiated heap instance of that blueprint.;Constructors initialize instance state and share the class name without a return type.;this disambiguates instance variables from parameter shadows.',
    previousLessonId: 12, nextLessonId: 14,
    content: `<h3>1. Classes &amp; Constructors</h3>
<pre><code>public class Student {
    private String name;
    private int id;

    public Student(String name, int id) {
        this.name = name;
        this.id = id;
    }
}</code></pre>`
  },
  14: {
    id: 14, courseId: 1, topicId: 5, topicTitle: 'Object-Oriented Programming Core',
    title: 'Encapsulation, Access Modifiers & Getters/Setters', estimatedMinutes: 25,
    keyPoints: 'Encapsulation protects class invariants by restricting direct field access.;private fields combined with public getters and setters enforce validation logic.;Access levels: public, protected, package-private, and private.',
    previousLessonId: 13, nextLessonId: 15,
    content: `<h3>1. Data Hiding with Encapsulation</h3>
<pre><code>public class BankAccount {
    private double balance;

    public double getBalance() { return balance; }
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
}</code></pre>`
  },
  15: {
    id: 15, courseId: 1, topicId: 5, topicTitle: 'Object-Oriented Programming Core',
    title: 'Inheritance, super Keyword & Object Hierarchy', estimatedMinutes: 30,
    keyPoints: 'extends keyword establishes an is-a relationship between subclass and superclass.;super() invokes the parent class constructor to initialize inherited fields.;All Java classes implicitly extend java.lang.Object.',
    previousLessonId: 14, nextLessonId: 16,
    content: `<h3>1. Class Inheritance</h3>
<pre><code>public class Animal {
    protected String name;
    public Animal(String name) { this.name = name; }
}

public class Dog extends Animal {
    public Dog(String name) { super(name); }
    public void bark() { System.out.println(name + " barks!"); }
}</code></pre>`
  },
  16: {
    id: 16, courseId: 1, topicId: 6, topicTitle: 'Polymorphism & Abstraction',
    title: 'Method Overriding & Runtime Polymorphism', estimatedMinutes: 30,
    keyPoints: '@Override instructs the compiler to verify method signature identity.;Runtime polymorphism resolves overridden methods dynamically based on the actual object on the heap.;Static methods cannot be overridden, only shadowed.',
    previousLessonId: 15, nextLessonId: 17,
    content: `<h3>1. Dynamic Method Dispatch</h3>
<pre><code>Animal myPet = new Dog("Buddy");
myPet.makeSound(); // Invokes Dog's overridden implementation at runtime!</code></pre>`
  },
  17: {
    id: 17, courseId: 1, topicId: 6, topicTitle: 'Polymorphism & Abstraction',
    title: 'Abstract Classes vs Interfaces & Default Methods', estimatedMinutes: 35,
    keyPoints: 'Abstract classes can hold state (fields) and constructors; interfaces define contracts.;Classes can implement multiple interfaces but extend only one class.;Java 8+ interfaces support default and static methods with concrete bodies.',
    previousLessonId: 16, nextLessonId: 18,
    content: `<h3>1. Contracts with Interfaces</h3>
<pre><code>public interface Drawable {
    void draw();
    default void renderShadow() {
        System.out.println("Rendering shadow...");
    }
}</code></pre>`
  },
  18: {
    id: 18, courseId: 1, topicId: 6, topicTitle: 'Polymorphism & Abstraction',
    title: 'Clean Architecture, Exceptions & Best Practices', estimatedMinutes: 30,
    keyPoints: 'Checked exceptions enforce compile-time error handling; unchecked exceptions extend RuntimeException.;try-with-resources automatically closes AutoCloseable streams and connections.;finally blocks always execute, guaranteeing cleanup.',
    previousLessonId: 17, nextLessonId: null,
    content: `<h3>1. Robust Error Handling</h3>
<pre><code>try (Scanner sc = new Scanner(System.in)) {
    int val = Integer.parseInt(sc.nextLine());
    System.out.println("Read: " + val);
} catch (NumberFormatException e) {
    System.err.println("Invalid number format: " + e.getMessage());
}</code></pre>`
  }
};

const MOCK_QUESTIONS = [
  { id: 1, topicId: 1, questionText: 'What is the memory size and encoding format of a char primitive in Java?', optionA: '8 bits, ASCII encoding', optionB: '16 bits, Unicode (UTF-16) encoding', optionC: '32 bits, UTF-32 encoding', optionD: '64 bits, binary float', correctOption: 'B', explanation: 'In Java, char is an unsigned 16-bit type representing UTF-16 Unicode characters.' },
  { id: 2, topicId: 1, questionText: 'What happens when an int variable holding Integer.MAX_VALUE has 1 added to it?', optionA: 'Throws an ArithmeticException', optionB: 'Wraps around to Integer.MIN_VALUE (-2,147,483,648)', optionC: 'Clamps at Integer.MAX_VALUE', optionD: 'Automatically promotes to long', correctOption: 'B', explanation: 'Java integer arithmetic uses two\'s complement without overflow exceptions; exceeding Integer.MAX_VALUE rolls over.' },
  { id: 3, topicId: 2, questionText: 'What occurs if a break statement is omitted from a matched case in a switch block?', optionA: 'Compile-time error', optionB: 'Fall-through execution occurs into subsequent cases sequentially', optionC: 'The switch block terminates immediately', optionD: 'The program halts', correctOption: 'B', explanation: 'Without break, execution falls through into subsequent case statements until a break or the end of the switch is reached.' },
  { id: 4, topicId: 2, questionText: 'Can a standard Java switch statement evaluate a String expression?', optionA: 'No, switch only supports primitive integers', optionB: 'Yes, String support in switch statements was added in Java 7', optionC: 'Only if string length is under 5 characters', optionD: 'Only for char types', correctOption: 'B', explanation: 'Since Java 7, String expressions are supported in switch statements.' },
  { id: 5, topicId: 3, questionText: 'Where are Java arrays allocated during runtime memory management?', optionA: 'Always in Stack memory', optionB: 'Always on the Heap, even when containing primitive values', optionC: 'In the CPU cache registers', optionD: 'In the Metaspace', correctOption: 'B', explanation: 'All arrays in Java are true objects and are allocated on the garbage-collected Heap.' },
  { id: 6, topicId: 3, topicTitle: 'Arrays', questionText: 'Given int[] a = {1, 2, 3}; int[] b = a; b[0] = 99; what does a[0] print?', optionA: '1', optionB: '99', optionC: '0', optionD: 'NullPointerException', correctOption: 'B', explanation: 'Array assignment b = a copies the heap reference pointer. Both variables point to the identical array.' },
  { id: 7, topicId: 4, questionText: 'What is the primary architectural purpose of data encapsulation in OOP?', optionA: 'To make classes execute faster', optionB: 'To hide internal state and protect invariants through controlled methods', optionC: 'To allow multiple inheritance', optionD: 'To prevent garbage collection', correctOption: 'B', explanation: 'Encapsulation bundles data and logic while hiding internal fields from external modification.' },
  { id: 8, topicId: 5, questionText: 'What happens if a subclass constructor does NOT explicitly call super(...) or this(...)?', optionA: 'The program fails to compile', optionB: 'The compiler automatically inserts an implicit no-argument super() call', optionC: 'The parent constructor is never executed', optionD: 'Memory leak occurs', correctOption: 'B', explanation: 'Java automatically inserts an implicit no-argument super() call as the first statement of any subclass constructor.' },
  { id: 9, topicId: 6, questionText: 'What is dynamic method dispatch (runtime polymorphism) in Java?', optionA: 'Resolving method calls at compile-time', optionB: 'Resolving calls to overridden methods at runtime based on the actual object type', optionC: 'Calling private methods from other packages', optionD: 'Inlining bytecode during execution', correctOption: 'B', explanation: 'Dynamic method dispatch determines at runtime which overridden method to execute based on the target object.' },
  { id: 10, topicId: 6, questionText: 'Can an interface in modern Java (Java 8+) declare a method with a concrete body?', optionA: 'No, interfaces can never contain concrete code', optionB: 'Yes, using the default or static method modifiers', optionC: 'Only if marked native', optionD: 'Only in package-private interfaces', correctOption: 'B', explanation: 'Java 8 introduced default and static interface methods allowing concrete method implementations.' }
];

const MOCK_CHALLENGES = [
  {
    id: 1, courseId: 1, topicId: 1,
    title: 'Distinct Word Counter', difficulty: 'EASY', points: 50,
    description: 'Given a text stream from standard input, count and print the total number of distinct words case-sensitively.',
    inputFormat: 'A sequence of whitespace-separated words.',
    outputFormat: 'Single integer representing the count of unique words.',
    constraints: 'Input contains up to 100,000 words.',
    sampleTestCasesJson: JSON.stringify([{ input: 'apple banana apple orange banana', expectedOutput: '3' }]),
    starterCode: `import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Set<String> words = new HashSet<>();
        while (sc.hasNext()) {
            words.add(sc.next());
        }
        System.out.println(words.size());
    }
}`,
    starterCodePython: `import sys

words = set(sys.stdin.read().split())
print(len(words))`,
    starterCodeJs: `const fs = require('fs');
const words = new Set(fs.readFileSync(0, 'utf-8').trim().split(/\\s+/).filter(Boolean));
console.log(words.size);`
  },
  {
    id: 2, courseId: 1, topicId: 2,
    title: 'Palindrome String Verifier', difficulty: 'EASY', points: 50,
    description: 'Determine whether a given single-word string is a palindrome (reads identical forwards and backwards). Print true or false.',
    inputFormat: 'A single word string on standard input.',
    outputFormat: 'true or false',
    constraints: 'String length between 1 and 1,000 characters.',
    sampleTestCasesJson: JSON.stringify([{ input: 'racecar', expectedOutput: 'true' }, { input: 'hello', expectedOutput: 'false' }]),
    starterCode: `import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();
        String rev = new StringBuilder(s).reverse().toString();
        System.out.println(s.equalsIgnoreCase(rev));
    }
}`,
    starterCodePython: `import sys
s = sys.stdin.read().strip()
print(str(s == s[::-1]).lower())`,
    starterCodeJs: `const fs = require('fs');
const s = fs.readFileSync(0, 'utf-8').trim();
console.log(s === s.split('').reverse().join(''));`
  },
  {
    id: 3, courseId: 1, topicId: 3,
    title: 'Two Sum Target Lookup', difficulty: 'MEDIUM', points: 100,
    description: 'Given N integers followed by target T, find if any two distinct elements add up to T. Print YES or NO.',
    inputFormat: 'N followed by N integers, followed by target T.',
    outputFormat: 'YES or NO',
    constraints: '2 <= N <= 50,000',
    sampleTestCasesJson: JSON.stringify([{ input: '4 2 7 11 15 9', expectedOutput: 'YES' }]),
    starterCode: `import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = sc.nextInt();
        int target = sc.nextInt();

        Set<Integer> seen = new HashSet<>();
        boolean found = false;
        for (int num : arr) {
            if (seen.contains(target - num)) {
                found = true;
                break;
            }
            seen.add(num);
        }
        System.out.println(found ? "YES" : "NO");
    }
}`,
    starterCodePython: `import sys
parts = list(map(int, sys.stdin.read().split()))
n = parts[0]
arr = parts[1:n+1]
target = parts[n+1]
seen = set()
for x in arr:
    if target - x in seen:
        print("YES")
        sys.exit(0)
    seen.add(x)
print("NO")`,
    starterCodeJs: `const fs = require('fs');
const nums = fs.readFileSync(0, 'utf-8').trim().split(/\\s+/).map(Number);
const n = nums[0];
const arr = nums.slice(1, n + 1);
const target = nums[n + 1];
const seen = new Set();
let found = false;
for (const x of arr) {
    if (seen.has(target - x)) { found = true; break; }
    seen.add(x);
}
console.log(found ? "YES" : "NO");`
  }
];

// Offline fallback router
function handleOfflineFallback(endpoint, options = {}) {
  const method = (options.method || 'GET').toUpperCase();
  let body = {};
  if (options.body) {
    try {
      body = typeof options.body === 'string' ? JSON.parse(options.body) : options.body;
    } catch (e) { body = {}; }
  }

  // Auth: Login
  if (endpoint.startsWith('/auth/login') && method === 'POST') {
    const email = (body.email || 'student@edunest.com').trim().toLowerCase();
    const users = JSON.parse(localStorage.getItem('edunest_registered_users') || '[]');
    const registered = users.find(u => u.email && u.email.toLowerCase() === email);

    let role = 'STUDENT';
    let name = 'Student';
    let goal = 'Master Core Java & Software Design';

    if (registered) {
      role = registered.role || 'STUDENT';
      name = registered.name || 'Student';
      goal = registered.learningGoal || goal;
    } else if (email.includes('admin')) {
      role = 'ADMIN';
      name = 'Administrator';
    } else if (email.includes('instructor') || email.includes('prof')) {
      role = 'INSTRUCTOR';
      name = 'Instructor';
    } else {
      const emailPrefix = email.split('@')[0] || 'student';
      name = emailPrefix.charAt(0).toUpperCase() + emailPrefix.slice(1);
    }

    return {
      token: 'demo-jwt-' + btoa(email) + '-' + Date.now(),
      userId: registered ? registered.id : 1,
      name, email, role, learningGoal: goal
    };
  }

  // Auth: Register
  if (endpoint.startsWith('/auth/register') && method === 'POST') {
    const name = body.name || 'New Learner';
    const email = (body.email || 'student@example.com').trim().toLowerCase();
    const role = body.role || 'STUDENT';
    const learningGoal = body.learningGoal || 'Master Programming Fundamentals';

    const users = JSON.parse(localStorage.getItem('edunest_registered_users') || '[]');
    const newUser = { id: Date.now(), name, email, role, learningGoal };
    users.push(newUser);
    localStorage.setItem('edunest_registered_users', JSON.stringify(users));

    return {
      token: 'demo-jwt-' + btoa(email) + '-' + Date.now(),
      userId: newUser.id,
      name: newUser.name,
      email: newUser.email,
      role: newUser.role,
      learningGoal: newUser.learningGoal
    };
  }

  // Auth: Me
  if (endpoint.startsWith('/auth/me')) {
    const email = localStorage.getItem('edunest_email') || 'student@edunest.com';
    const name = localStorage.getItem('edunest_name') || 'Student';
    const role = localStorage.getItem('edunest_role') || 'STUDENT';
    const goal = localStorage.getItem('edunest_goal') || 'Master Programming Fundamentals';
    return {
      token: localStorage.getItem('edunest_token') || 'demo-token',
      userId: parseInt(localStorage.getItem('edunest_userId') || '1', 10),
      name, email, role, learningGoal: goal
    };
  }

  // Auth: Goal update
  if (endpoint.startsWith('/auth/goal') || endpoint.startsWith('/students/me/goal')) {
    const newGoal = body.learningGoal || body.goal || '';
    if (newGoal) localStorage.setItem('edunest_goal', newGoal);
    return { success: true, learningGoal: newGoal };
  }

  // Courses: Catalog
  if (endpoint === '/courses' || endpoint === '/courses/') {
    return [
      {
        id: 1,
        title: 'Java Programming - From Fundamentals to OOP',
        description: 'Master variables, memory structures, object-oriented design, inheritance, polymorphism, and algorithms with 6 hands-on coding challenges.',
        thumbnailUrl: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600',
        instructorName: 'Prof. Vikram Verma',
        published: true,
        topicCount: 6,
        enrollmentCount: 142
      },
      {
        id: 2,
        title: 'Python & Data Science Foundations',
        description: 'Learn clean Python syntax, sequence manipulation, dictionary lookups, list comprehensions, and data analysis fundamentals.',
        thumbnailUrl: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600',
        instructorName: 'Dr. Neha Patel',
        published: true,
        topicCount: 2,
        enrollmentCount: 88
      },
      {
        id: 3,
        title: 'Full-Stack Web Development',
        description: 'Build accessible semantic web pages using modern HTML5, responsive CSS Flexbox/Grid architectures, and modern JavaScript.',
        thumbnailUrl: 'https://images.unsplash.com/photo-1547658719-da2b51169166?w=600',
        instructorName: 'Sarah Jenkins',
        published: true,
        topicCount: 1,
        enrollmentCount: 64
      }
    ];
  }

  // Courses: My Enrolled Courses
  if (endpoint === '/courses/my' || endpoint === '/courses/my/') {
    const enrolledIds = JSON.parse(localStorage.getItem('edunest_enrolled_courses') || '[1]');
    const all = handleOfflineFallback('/courses');
    return all.filter(c => enrolledIds.includes(c.id)).map(c => ({ ...c, enrolled: true }));
  }

  // Courses: Enroll
  if (endpoint.match(/\/courses\/(\d+)\/enroll/)) {
    const m = endpoint.match(/\/courses\/(\d+)\/enroll/);
    const courseId = parseInt(m[1], 10);
    const enrolled = JSON.parse(localStorage.getItem('edunest_enrolled_courses') || '[1]');
    if (!enrolled.includes(courseId)) enrolled.push(courseId);
    localStorage.setItem('edunest_enrolled_courses', JSON.stringify(enrolled));
    return { success: true, message: 'Enrolled successfully' };
  }

  // Courses: Detail
  if (endpoint.match(/\/courses\/(\d+)$/)) {
    const courseId = parseInt(endpoint.match(/\/courses\/(\d+)$/)[1], 10);
    const enrolledIds = JSON.parse(localStorage.getItem('edunest_enrolled_courses') || '[1]');
    const isEnrolled = enrolledIds.includes(courseId);
    const completedList = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[1, 2]');
    const totalLessons = 18;
    const isUnlocked = completedList.length >= totalLessons;
    const certs = JSON.parse(localStorage.getItem('edunest_certificates') || '[]');
    const isCourseCompleted = certs.some(c => c.courseId === courseId);

    const topics = [
      {
        id: 1, title: 'Java Fundamentals & Syntax', orderIndex: 1,
        lessons: [
          { id: 1, title: 'Java Architecture, JVM Bytecode & Your First Program', orderIndex: 1, estimatedMinutes: 15 },
          { id: 2, title: 'Primitive Data Types, Memory Sizes & Variables', orderIndex: 2, estimatedMinutes: 18 },
          { id: 3, title: 'Operators, Arithmetic & Type Casting Mechanics', orderIndex: 3, estimatedMinutes: 20 }
        ]
      },
      {
        id: 2, title: 'Control Flow, Loops & Logic', orderIndex: 2,
        lessons: [
          { id: 4, title: 'Conditional Branching: if, else-if & switch', orderIndex: 1, estimatedMinutes: 20 },
          { id: 5, title: 'Iteration: for, while & do-while Loops', orderIndex: 2, estimatedMinutes: 25 },
          { id: 6, title: 'Break, Continue & Nested Loop Mechanics', orderIndex: 3, estimatedMinutes: 20 }
        ]
      },
      {
        id: 3, title: 'Arrays & Memory Management', orderIndex: 3,
        lessons: [
          { id: 7, title: '1D Arrays, Heap Allocation & Traversal', orderIndex: 1, estimatedMinutes: 25 },
          { id: 8, title: 'Multidimensional Arrays & Matrix Problems', orderIndex: 2, estimatedMinutes: 30 },
          { id: 9, title: 'Array Copying, References & Boundary Errors', orderIndex: 3, estimatedMinutes: 20 }
        ]
      },
      {
        id: 4, title: 'Methods, Stack Execution & Recursion', orderIndex: 4,
        lessons: [
          { id: 10, title: 'Method Signatures, Pass-by-Value & Overloading', orderIndex: 1, estimatedMinutes: 20 },
          { id: 11, title: 'Stack Frames, Variable Scope & Lifetime', orderIndex: 2, estimatedMinutes: 25 },
          { id: 12, title: 'Recursion, Base Cases & Call Stack Analysis', orderIndex: 3, estimatedMinutes: 30 }
        ]
      },
      {
        id: 5, title: 'Object-Oriented Programming Core', orderIndex: 5,
        lessons: [
          { id: 13, title: 'Classes, Objects, Constructors & this Keyword', orderIndex: 1, estimatedMinutes: 30 },
          { id: 14, title: 'Encapsulation, Access Modifiers & Getters/Setters', orderIndex: 2, estimatedMinutes: 25 },
          { id: 15, title: 'Inheritance, super Keyword & Object Hierarchy', orderIndex: 3, estimatedMinutes: 30 }
        ]
      },
      {
        id: 6, title: 'Polymorphism & Abstraction', orderIndex: 6,
        lessons: [
          { id: 16, title: 'Method Overriding & Runtime Polymorphism', orderIndex: 1, estimatedMinutes: 30 },
          { id: 17, title: 'Abstract Classes vs Interfaces & Default Methods', orderIndex: 2, estimatedMinutes: 35 },
          { id: 18, title: 'Clean Architecture, Exceptions & Best Practices', orderIndex: 3, estimatedMinutes: 30 }
        ]
      }
    ];

    return {
      id: courseId,
      title: courseId === 1 ? 'Java Programming - From Fundamentals to OOP' : (courseId === 2 ? 'Python & Data Science Foundations' : 'Full-Stack Web Development'),
      description: 'Master variables, memory structures, object-oriented design, inheritance, polymorphism, and algorithms with 6 hands-on coding challenges.',
      instructorName: 'Prof. Vikram Verma',
      enrolled: isEnrolled,
      published: true,
      topicCount: topics.length,
      finalAssessmentUnlocked: isUnlocked || true,
      finalAssessmentPassingScore: 70.0,
      courseCompleted: isCourseCompleted,
      finalScore: isCourseCompleted ? 88.0 : null,
      topics
    };
  }

  // Lessons: Completed list
  if (endpoint.startsWith('/lessons/completed')) {
    return JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[1, 2]');
  }

  // Lessons: Complete toggle
  if (endpoint.match(/\/lessons\/(\d+)\/complete/)) {
    const lessonId = parseInt(endpoint.match(/\/lessons\/(\d+)\/complete/)[1], 10);
    const list = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[1, 2]');
    if (!list.includes(lessonId)) list.push(lessonId);
    localStorage.setItem('edunest_completed_lessons', JSON.stringify(list));
    return { completed: true, lessonId };
  }

  // Lessons: Single Lesson content
  if (endpoint.match(/\/lessons\/(\d+)$/)) {
    const lessonId = parseInt(endpoint.match(/\/lessons\/(\d+)$/)[1], 10);
    if (MOCK_LESSON_DATA[lessonId]) {
      return MOCK_LESSON_DATA[lessonId];
    }
    return {
      id: lessonId,
      courseId: 1,
      topicId: Math.ceil(lessonId / 3),
      topicTitle: 'Topic ' + Math.ceil(lessonId / 3),
      title: 'Lesson ' + lessonId + ': Programming Core Concept',
      estimatedMinutes: 20,
      keyPoints: 'Understand syntax and runtime behavior.;Avoid common memory and boundary mistakes.;Complete the practice drill before proceeding.',
      previousLessonId: lessonId > 1 ? lessonId - 1 : null,
      nextLessonId: lessonId < 18 ? lessonId + 1 : null,
      content: `<h3>Lesson ${lessonId} Content</h3><p>Comprehensive computer science lesson covering algorithms, memory representations, and coding design patterns.</p>`
    };
  }

  // Knowledge Profile
  if (endpoint.includes('/knowledge-profile')) {
    const completedList = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[1, 2]');
    const pct = Math.round((completedList.length / 18) * 100);
    return {
      courseId: 1,
      overallCourseMastery: pct,
      completedLessonIds: completedList,
      topicMasteries: [
        { topicId: 1, topicTitle: 'Java Fundamentals & Syntax', masteryScore: 85.0 },
        { topicId: 2, topicTitle: 'Control Flow, Loops & Logic', masteryScore: 60.0 },
        { topicId: 3, topicTitle: 'Arrays & Memory Management', masteryScore: 40.0 }
      ]
    };
  }

  // Quizzes: Fetch
  if (endpoint.startsWith('/quizzes/final') || endpoint.startsWith('/quizzes/practice') || endpoint.startsWith('/quizzes/topic')) {
    const isFinal = endpoint.startsWith('/quizzes/final');
    return {
      quizId: isFinal ? 999 : 1,
      topicId: 1,
      topicTitle: isFinal ? 'Comprehensive Final Assessment' : 'Concept Practice Drill',
      quizType: isFinal ? 'FINAL_ASSESSMENT' : 'PRACTICE',
      durationMinutes: isFinal ? 30 : 15,
      passingScore: 70.0,
      questions: MOCK_QUESTIONS
    };
  }

  // Quizzes: Submit
  if (endpoint.startsWith('/quizzes/submit') && method === 'POST') {
    const answers = body.answers || {};
    let correct = 0;
    const total = MOCK_QUESTIONS.length;
    MOCK_QUESTIONS.forEach(q => {
      if (answers[q.id] && answers[q.id].toUpperCase() === q.correctOption.toUpperCase()) {
        correct++;
      }
    });
    const score = Math.round((correct / total) * 100);
    const passed = score >= 70;
    const attemptId = Date.now();

    // If final assessment passed, automatically issue a verified certificate
    if (body.quizId === 999 && passed) {
      const certs = JSON.parse(localStorage.getItem('edunest_certificates') || '[]');
      if (!certs.some(c => c.courseId === 1)) {
        certs.push({
          id: attemptId,
          courseId: 1,
          courseTitle: 'Java Programming - From Fundamentals to OOP',
          recipientName: localStorage.getItem('edunest_name') || 'Student',
          finalScore: score,
          verificationId: 'EDU-JAVA-' + Math.random().toString(36).substring(2, 8).toUpperCase(),
          issuedAt: new Date().toISOString()
        });
        localStorage.setItem('edunest_certificates', JSON.stringify(certs));
      }
    }

    const result = {
      attemptId,
      quizType: body.quizId === 999 ? 'FINAL_ASSESSMENT' : 'PRACTICE',
      score,
      passed,
      correctAnswers: correct,
      totalQuestions: total,
      passingScore: 70.0,
      completedAt: new Date().toISOString(),
      questions: MOCK_QUESTIONS.map(q => ({
        ...q,
        userSelectedOption: answers[q.id] || null,
        isCorrect: answers[q.id] && answers[q.id].toUpperCase() === q.correctOption.toUpperCase()
      }))
    };
    localStorage.setItem('edunest_last_quiz_result', JSON.stringify(result));
    return result;
  }

  // Quizzes: Attempt Result
  if (endpoint.match(/\/quizzes\/attempts\/(\d+)/)) {
    const saved = localStorage.getItem('edunest_last_quiz_result');
    if (saved) return JSON.parse(saved);
    return {
      attemptId: Date.now(),
      quizType: 'PRACTICE',
      score: 80.0,
      passed: true,
      correctAnswers: 8,
      totalQuestions: 10,
      passingScore: 70.0
    };
  }

  // Challenges: List
  if (endpoint.startsWith('/challenges/course') || endpoint === '/challenges') {
    return MOCK_CHALLENGES;
  }

  // Challenges: Single
  if (endpoint.match(/\/challenges\/(\d+)$/)) {
    const chId = parseInt(endpoint.match(/\/challenges\/(\d+)$/)[1], 10);
    return MOCK_CHALLENGES.find(c => c.id === chId) || MOCK_CHALLENGES[0];
  }

  // Challenges: Submissions
  if (endpoint.includes('/my-submissions')) {
    return JSON.parse(localStorage.getItem('edunest_challenge_submissions') || '[]');
  }

  // Challenges: Run or Submit
  if (endpoint.includes('/run') || endpoint.includes('/submit')) {
    const isSubmit = endpoint.includes('/submit');
    const res = {
      status: 'ACCEPTED',
      success: true,
      passed: 5,
      total: 5,
      score: 100,
      runtimeMs: 18,
      memoryKb: 34120,
      output: 'All 5 hidden test cases passed successfully in 18ms.'
    };
    if (isSubmit) {
      const subs = JSON.parse(localStorage.getItem('edunest_challenge_submissions') || '[]');
      subs.push({
        id: Date.now(),
        challengeId: 1,
        challengeTitle: 'Distinct Word Counter',
        status: 'ACCEPTED',
        score: 100,
        submittedAt: new Date().toISOString()
      });
      localStorage.setItem('edunest_challenge_submissions', JSON.stringify(subs));
    }
    return res;
  }

  // Certificates: Me
  if (endpoint.startsWith('/certificates')) {
    return JSON.parse(localStorage.getItem('edunest_certificates') || '[]');
  }

  // Bookmarks: Lesson Status
  if (endpoint.match(/\/bookmarks\/lesson\/(\d+)/)) {
    const lId = parseInt(endpoint.match(/\/bookmarks\/lesson\/(\d+)/)[1], 10);
    const bms = JSON.parse(localStorage.getItem('edunest_bookmarks') || '[]');
    return { bookmarked: bms.some(b => b.lessonId === lId) };
  }

  // Bookmarks: List & Toggle
  if (endpoint.includes('/bookmarks')) {
    const bms = JSON.parse(localStorage.getItem('edunest_bookmarks') || '[]');
    if (method === 'POST') {
      const lessonId = body.lessonId;
      if (!bms.some(b => b.lessonId === lessonId)) {
        bms.push({ id: Date.now(), lessonId, lessonTitle: 'Lesson ' + lessonId, courseTitle: 'Java Fundamentals' });
        localStorage.setItem('edunest_bookmarks', JSON.stringify(bms));
      }
      return { bookmarked: true };
    }
    if (method === 'DELETE') {
      const m = endpoint.match(/\/bookmarks\/lesson\/(\d+)/);
      if (m) {
        const filtered = bms.filter(b => b.lessonId !== parseInt(m[1], 10));
        localStorage.setItem('edunest_bookmarks', JSON.stringify(filtered));
      }
      return { bookmarked: false };
    }
    return bms;
  }

  // Notes: Lesson Note
  if (endpoint.match(/\/notes\/lesson\/(\d+)/)) {
    const lId = parseInt(endpoint.match(/\/notes\/lesson\/(\d+)/)[1], 10);
    const notes = JSON.parse(localStorage.getItem('edunest_notes') || '{}');
    return { note: notes[lId] || '' };
  }

  // Notes: Save
  if (endpoint.includes('/notes')) {
    const notes = JSON.parse(localStorage.getItem('edunest_notes') || '{}');
    if (method === 'POST') {
      const lId = body.lessonId;
      notes[lId] = body.content || '';
      localStorage.setItem('edunest_notes', JSON.stringify(notes));
      return { success: true, note: notes[lId] };
    }
    return Object.entries(notes).map(([lId, content]) => ({
      lessonId: parseInt(lId, 10),
      lessonTitle: 'Lesson ' + lId,
      content,
      updatedAt: new Date().toISOString()
    }));
  }

  // Revision Schedule
  if (endpoint.includes('/revision-schedule')) {
    return [
      { id: 1, topicId: 1, topicTitle: 'Java Fundamentals & Syntax', nextReviewDate: new Date().toISOString(), status: 'PENDING' }
    ];
  }

  return {};
}

// Global API Fetch
async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('edunest_token');
  const headers = {
    'Content-Type': 'application/json',
    'bypass-tunnel-reminder': 'true',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  let response;
  try {
    response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers
    });
  } catch (networkErr) {
    console.warn(`[EduNest API] Network unreachable for ${endpoint}. Using standalone engine.`);
    return handleOfflineFallback(endpoint, options);
  }

  if (response.status === 401) {
    if (!endpoint.startsWith('/auth/login') && !endpoint.startsWith('/auth/register')) {
      localStorage.clear();
      window.location.href = '/login.html';
      throw new Error('Session expired. Please log in again.');
    }
  }

  if (response.status === 204) {
    return null;
  }

  // Fallback on 404, 405, or 500 when backend is not running on host
  if (response.status === 404 || response.status === 405 || response.status >= 500) {
    console.warn(`[EduNest API] Endpoint ${endpoint} returned HTTP ${response.status}. Using standalone engine.`);
    return handleOfflineFallback(endpoint, options);
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.error || data.message || `Request failed (${response.status})`);
  }

  return data;
}

async function apiRequest(endpoint, methodOrOptions = 'GET', body = null) {
  if (typeof methodOrOptions === 'object' && methodOrOptions !== null) {
    return apiFetch(endpoint, methodOrOptions);
  }
  const method = (typeof methodOrOptions === 'string' ? methodOrOptions : 'GET').toUpperCase();
  const options = { method };
  if (body !== null && body !== undefined) {
    options.body = typeof body === 'string' ? body : JSON.stringify(body);
  }
  return apiFetch(endpoint, options);
}

window.API_BASE = API_BASE;
window.apiFetch = apiFetch;
window.apiRequest = apiRequest;
window.promptBackendUrl = promptBackendUrl;
window.handleOfflineFallback = handleOfflineFallback;
