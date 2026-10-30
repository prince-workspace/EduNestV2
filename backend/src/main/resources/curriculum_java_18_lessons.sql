USE edunest_db;

UPDATE lessons SET title = 'Java Architecture, JVM Bytecode & Your First Program', estimated_minutes = 15, key_points = 'Java achieves platform independence via bytecode executed on the JVM (Write Once, Run Anywhere).;The JDK contains the compiler (javac) and tools; the JRE provides the runtime; the JVM executes instructions.;Every Java application requires at least one class and an entry-point \'public static void main(String[] args)\' method.', content = '
<h3>1. The Java Platform &amp; Write Once, Run Anywhere (WORA)</h3>
<p>Traditional compiled languages like C and C++ compile source code directly into machine code specific to the host operating system and CPU architecture. In contrast, <strong>Java compiles source code into platform-independent intermediate instructions called bytecode</strong> (stored in <code>.class</code> files).</p>
<p>Any operating system with a compatible <strong>Java Virtual Machine (JVM)</strong> can execute this bytecode. This decoupling is the foundation of Java\'s famous motto: <em>"Write Once, Run Anywhere"</em>.</p>

<div class="lesson-callout">
  <strong>Key Architecture Tiers:</strong>
  <ul>
    <li><strong>JDK (Java Development Kit):</strong> Full toolkit for developers, including the compiler (<code>javac</code>), debugger, and utilities.</li>
    <li><strong>JRE (Java Runtime Environment):</strong> The minimum bundle required to run compiled Java programs (JVM + standard library classes).</li>
    <li><strong>JVM (Java Virtual Machine):</strong> The abstract computing machine that loads <code>.class</code> bytecode, verifies safety, and translates it into native CPU instructions via the Just-In-Time (JIT) compiler.</li>
  </ul>
</div>

<h3>2. Anatomy of a Standard Java Program</h3>
<p>In Java, all code must reside inside a class definition. The file name must match the public class name exactly (case-sensitive):</p>

<pre><code>// File: HelloWorld.java
public class HelloWorld {
    // The entry point where execution begins
    public static void main(String[] args) {
        System.out.println("Welcome to EduNest Core Java!");
    }
}</code></pre>

<h4>Dissecting the Entry Point:</h4>
<ul>
  <li><code>public</code>: Access modifier allowing the JVM to invoke the method from outside the class package.</li>
  <li><code>static</code>: Allows the JVM to invoke <code>main</code> without creating an instance of the class first.</li>
  <li><code>void</code>: The method performs actions and does not return a value.</li>
  <li><code>main</code>: The designated identifier recognized by the JVM launcher as the application starting point.</li>
  <li><code>String[] args</code>: Command-line arguments passed into the program as an array of strings.</li>
  <li><code>System.out.println()</code>: Prints text to the standard console followed by a newline character.</li>
</ul>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Format a Student Greeting</h4>
  <p><strong>Goal:</strong> Write a Java program that defines a class named <code>StudentBadge</code> and outputs the student\'s name, enrolled track, and current semester formatted on separate lines using escape sequences or multiple print calls.</p>
  <pre><code>public class StudentBadge {
    public static void main(String[] args) {
        String studentName = "Aarav Sharma";
        String track = "Full-Stack Java Development";
        int semester = 3;

        System.out.println("=============================");
        System.out.println("Student: " + studentName);
        System.out.println("Track:   " + track);
        System.out.println("Term:    Semester " + semester);
        System.out.println("=============================");
    }
}</code></pre>
</div>
' WHERE id = 1 AND topic_id = 1;

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 1, 2, 'Primitive Data Types, Memory Sizes & Variables', 18, 'Java is statically typed with 8 primitive types: byte, short, int, long, float, double, char, and boolean.;Primitives store raw values directly on the thread stack, whereas reference types store memory addresses on the heap.;Use \'final\' to declare constants that cannot be reassigned after initialization.', '
<h3>1. Statically-Typed Variables &amp; The 8 Primitives</h3>
<p>Java requires every variable to declare its type explicitly before assignment. Primitive types store their values directly in stack memory without object wrapper overhead.</p>

<table style="width:100%; border-collapse:collapse; margin:1rem 0; font-size:0.9rem;">
  <thead>
    <tr style="background:#f1f5f9; text-align:left;">
      <th style="padding:0.6rem; border:1px solid #e2e8f0;">Type</th>
      <th style="padding:0.6rem; border:1px solid #e2e8f0;">Bits</th>
      <th style="padding:0.6rem; border:1px solid #e2e8f0;">Range</th>
      <th style="padding:0.6rem; border:1px solid #e2e8f0;">Default Value</th>
      <th style="padding:0.6rem; border:1px solid #e2e8f0;">Example</th>
    </tr>
  </thead>
  <tbody>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>byte</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">8</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">-128 to 127</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>byte level = 3;</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>short</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">16</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">-32,768 to 32,767</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>short year = 2026;</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>int</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">32</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">-2^31 to 2^31 - 1 (~2.14B)</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>int score = 9450;</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>long</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">64</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">-2^63 to 2^63 - 1</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0L</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>long id = 9876543210L;</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>float</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">32</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">~6-7 decimal digits</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0.0f</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>float temp = 98.6f;</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>double</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">64</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">~15-16 decimal digits</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0.0d</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>double gpa = 3.92;</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>char</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">16</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">0 to 65,535 (Unicode)</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">\'\\u0000\'</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>char grade = \'A\';</code></td></tr>
    <tr><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>boolean</code></td><td style="padding:0.5rem; border:1px solid #e2e8f0;">1</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">true or false</td><td style="padding:0.5rem; border:1px solid #e2e8f0;">false</td><td style="padding:0.5rem; border:1px solid #e2e8f0;"><code>boolean active = true;</code></td></tr>
  </tbody>
</table>

<h3>2. Constants with the <code>final</code> Keyword</h3>
<p>When you want to prevent a variable from being modified after its initial value is assigned, prefix it with <code>final</code>. By convention, constant identifiers use uppercase with underscores:</p>

<pre><code>public class GeometryConstants {
    public static void main(String[] args) {
        final double PI = 3.141592653589793;
        final int MAX_ATTEMPTS = 3;

        // PI = 3.14; // Compilation error: cannot assign a value to final variable PI
        System.out.println("Configured Pi: " + PI);
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Financial Balance Tracker</h4>
  <p><strong>Goal:</strong> Declare variables tracking an initial account balance, an annual interest rate, and months invested. Calculate and print the simple interest accrued.</p>
  <pre><code>public class FinancialTracker {
    public static void main(String[] args) {
        double principal = 10000.0;
        float annualInterestRate = 0.065f; // 6.5%
        int months = 18;

        double timeInYears = months / 12.0;
        double interest = principal * annualInterestRate * timeInYears;
        double totalBalance = principal + interest;

        System.out.printf("Principal: $%.2f%n", principal);
        System.out.printf("Accrued Interest: $%.2f%n", interest);
        System.out.printf("Final Total: $%.2f%n", totalBalance);
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 1 AND order_index = 2);

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 1, 3, 'Type Casting, Arithmetic & Logical Operators', 16, 'Widening conversion (e.g. int to double) happens automatically without data loss.;Narrowing conversion (e.g. double to int) truncates decimal fractions and requires explicit (type) syntax.;Logical short-circuit operators (&&, ||) skip evaluating the right-hand operand when the outcome is already determined.', '
<h3>1. Type Casting Mechanisms</h3>
<p>Type conversion occurs when assigning a value of one type to a variable of another type.</p>

<h4>Widening Casting (Implicit)</h4>
<p>Happens automatically when converting a smaller datatype to a larger compatible type without precision loss:</p>
<pre><code>int smallNumber = 42;
double broadNumber = smallNumber; // Automatic widening: 42.0</code></pre>

<h4>Narrowing Casting (Explicit)</h4>
<p>Must be performed explicitly using parenthesis notation because data loss or truncation may occur:</p>
<pre><code>double productPrice = 99.85;
int truncatedDollarPrice = (int) productPrice; // Results in 99 (decimal truncated)</code></pre>

<div class="lesson-callout">
  <strong>Integer Division Pitfall:</strong> In Java, dividing two integers produces an integer result: <code>5 / 2</code> evaluates to <code>2</code>, not <code>2.5</code>. To retain precision, at least one operand must be a floating-point type: <code>5.0 / 2</code> or <code>(double) 5 / 2</code>.
</div>

<h3>2. Arithmetic, Unary &amp; Short-Circuit Operators</h3>
<pre><code>public class OperatorsDemo {
    public static void main(String[] args) {
        // Modulo operator (remainder)
        int remainder = 17 % 5; // 2

        // Prefix vs Postfix Increment
        int a = 5;
        int b = a++; // b = 5, then a becomes 6
        int c = ++a; // a becomes 7, then c = 7

        // Short-Circuit Evaluation: second condition is NOT checked if first is false
        String user = null;
        if (user != null &amp;&amp; user.length() &gt; 0) {
            System.out.println("Valid username");
        } else {
            System.out.println("Short-circuit prevented NullPointerException!");
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Unit Conversion Calculator</h4>
  <p><strong>Goal:</strong> Convert temperatures between Fahrenheit and Celsius using explicit casting and floating-point arithmetic.</p>
  <pre><code>public class TemperatureConverter {
    public static void main(String[] args) {
        double fahrenheit = 98.6;
        double celsius = (fahrenheit - 32.0) * (5.0 / 9.0);

        System.out.printf("%.1f°F is equal to %.2f°C%n", fahrenheit, celsius);
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 1 AND order_index = 3);

UPDATE lessons SET title = 'Conditional Branching & Modern Switch Expressions', estimated_minutes = 16, key_points = 'Use if-else-if ladders to evaluate sequential boolean conditions.;The ternary operator (condition ? exprA : exprB) provides concise conditional value assignment.;Modern Java switch expressions (arrow syntax ->) eliminate fall-through bugs and can return values directly.', content = '
<h3>1. Conditional Branching (if, else if, else)</h3>
<p>Conditional execution enables your program to choose different execution paths based on logical evaluations.</p>

<pre><code>public class GradeEvaluator {
    public static void main(String[] args) {
        int score = 87;
        char grade;

        if (score &gt;= 90) {
            grade = \'A\';
        } else if (score &gt;= 80) {
            grade = \'B\';
        } else if (score &gt;= 70) {
            grade = \'C\';
        } else if (score &gt;= 60) {
            grade = \'D\';
        } else {
            grade = \'F\';
        }

        System.out.println("Score: " + score + " =&gt; Grade: " + grade);

        // Ternary Operator for concise decision
        String status = (score &gt;= 60) ? "Passed" : "Needs Retake";
        System.out.println("Status: " + status);
    }
}</code></pre>

<h3>2. Modern Java Switch Expressions (Java 14+)</h3>
<p>Older <code>switch</code> statements required tedious <code>break;</code> calls on every case, making accidental fall-through bugs common. Modern Java introduces <strong>arrow syntax</strong> <code>case -&gt;</code> which does not fall through and can return values:</p>

<pre><code>public class HttpStatusDecoder {
    public static void main(String[] args) {
        int statusCode = 404;

        String description = switch (statusCode) {
            case 200 -&gt; "OK: Request succeeded";
            case 201 -&gt; "Created: Resource successfully generated";
            case 400 -&gt; "Bad Request: Malformed request payload";
            case 401 -&gt; "Unauthorized: Authentication required";
            case 403 -&gt; "Forbidden: Access denied";
            case 404 -&gt; "Not Found: Endpoint does not exist";
            case 500, 502, 503 -&gt; "Server Error: Upstream outage";
            default -&gt; "Unknown Status Code";
        };

        System.out.println("HTTP " + statusCode + " = " + description);
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> E-Commerce Shipping Tier Engine</h4>
  <p><strong>Goal:</strong> Implement a shipping fee calculator based on loyalty tier and order subtotal.</p>
  <pre><code>public class ShippingCalculator {
    public static void main(String[] args) {
        String tier = "PLATINUM";
        double orderTotal = 45.0;

        double shippingFee = switch (tier.toUpperCase()) {
            case "PLATINUM" -&gt; 0.0; // Always free
            case "GOLD" -&gt; (orderTotal &gt;= 50.0) ? 0.0 : 4.99;
            case "SILVER" -&gt; (orderTotal &gt;= 75.0) ? 0.0 : 7.99;
            default -&gt; 12.99; // Standard guest shipping
        };

        System.out.printf("Tier: %s | Order: $%.2f | Shipping: $%.2f%n", tier, orderTotal, shippingFee);
    }
}</code></pre>
</div>
' WHERE id = 2 AND topic_id = 2;

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 2, 2, 'Iteration with While, Do-While & For Loops', 18, 'For loops are optimal when the number of iterations is known before entering the loop.;While loops are optimal when iteration depends on a dynamic condition.;Do-while loops guarantee at least one execution because the termination condition is evaluated at the bottom.', '
<h3>1. Loop Varieties &amp; Usage Scenarios</h3>
<p>Iteration enables repeating blocks of logic until a termination invariant is satisfied.</p>

<h4>The Standard Counter-Controlled For Loop</h4>
<p>Composed of initialization, continuation condition, and update step:</p>
<pre><code>// Summing the first 10 integers
int sum = 0;
for (int i = 1; i &lt;= 10; i++) {
    sum += i;
}
System.out.println("Sum 1..10 = " + sum); // 55</code></pre>

<h4>The While Loop (Pre-Condition Evaluation)</h4>
<p>Evaluates its boolean expression before executing the body. If the condition is false initially, the body is never executed:</p>
<pre><code>int power = 1;
while (power &lt; 1000) {
    power *= 2;
}
System.out.println("First power of 2 &gt;= 1000 is " + power); // 1024</code></pre>

<h4>The Do-While Loop (Post-Condition Evaluation)</h4>
<p>Guaranteed to execute its body at least once, making it ideal for interactive menus, retry mechanisms, and user prompts:</p>
<pre><code>int attempts = 0;
boolean isConnected = false;

do {
    attempts++;
    System.out.println("Attempting server handshake... #" + attempts);
    if (attempts == 3) {
        isConnected = true;
    }
} while (!isConnected &amp;&amp; attempts &lt; 5);

System.out.println("Connected: " + isConnected);</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Compound Growth Simulation</h4>
  <p><strong>Goal:</strong> Calculate how many years it will take for an investment to double at a given annual interest rate using a while loop.</p>
  <pre><code>public class InvestmentGrowth {
    public static void main(String[] args) {
        double initialCapital = 5000.0;
        double targetCapital = initialCapital * 2.0;
        double balance = initialCapital;
        double interestRate = 0.08; // 8% annual
        int years = 0;

        while (balance &lt; targetCapital) {
            balance += balance * interestRate;
            years++;
        }

        System.out.printf("Initial: $%.2f | Doubled to: $%.2f in %d years.%n", initialCapital, balance, years);
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 2 AND order_index = 2);

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 2, 3, 'Loop Control (Break, Continue) & Nested Patterns', 20, '\'break\' immediately terminates the current loop; \'continue\' skips the rest of the current iteration.;Labelled statements allow breaking or continuing an outer loop from inside a nested loop.;Nested loops are commonly used for multidimensional grids and algorithm combinations.', '
<h3>1. Altering Iteration with Break &amp; Continue</h3>
<p>Inside any loop body, you can alter the execution sequence dynamically:</p>
<ul>
  <li><code>break</code>: Immediately halts loop execution and transfers control to the statement following the loop.</li>
  <li><code>continue</code>: Skips the remaining statements in the current iteration and jumps immediately to the loop\'s update/condition check.</li>
</ul>

<pre><code>public class LoopControlDemo {
    public static void main(String[] args) {
        // Find the first multiple of 7 and 11
        for (int i = 1; i &lt;= 500; i++) {
            if (i % 7 == 0 &amp;&amp; i % 11 == 0) {
                System.out.println("First common multiple: " + i);
                break; // Exit loop early
            }
        }

        // Print odd numbers only
        System.out.print("Odd numbers up to 10: ");
        for (int i = 1; i &lt;= 10; i++) {
            if (i % 2 == 0) {
                continue; // Skip even numbers
            }
            System.out.print(i + " ");
        }
        System.out.println();
    }
}</code></pre>

<h3>2. Labelled Loops in Nested Constructs</h3>
<p>When searching through nested grids, standard <code>break</code> only exits the innermost loop. Java allows <strong>labelled breaks</strong> to exit multiple enclosing loops simultaneously:</p>

<pre><code>public class LabelledLoopSearch {
    public static void main(String[] args) {
        int[][] matrix = {
            {3, 8, 12},
            {15, 99, 4},
            {7, 14, 21}
        };

        int target = 99;
        boolean found = false;

        searchGrid:
        for (int row = 0; row &lt; matrix.length; row++) {
            for (int col = 0; col &lt; matrix[row].length; col++) {
                if (matrix[row][col] == target) {
                    System.out.println("Found " + target + " at (" + row + ", " + col + ")");
                    found = true;
                    break searchGrid; // Exits BOTH loops cleanly
                }
            }
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Prime Number Sieve in a Range</h4>
  <p><strong>Goal:</strong> Write a nested loop that identifies and displays all prime numbers between 2 and 50 using an inner loop with early break.</p>
  <pre><code>public class PrimeFinder {
    public static void main(String[] args) {
        System.out.println("Primes between 2 and 50:");
        for (int n = 2; n &lt;= 50; n++) {
            boolean isPrime = true;
            for (int d = 2; d * d &lt;= n; d++) {
                if (n % d == 0) {
                    isPrime = false;
                    break; // Not prime, skip further division checks
                }
            }
            if (isPrime) {
                System.out.print(n + " ");
            }
        }
        System.out.println();
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 2 AND order_index = 3);

UPDATE lessons SET title = 'Single-Dimensional Arrays & Heap Allocation', estimated_minutes = 18, key_points = 'Arrays in Java are fixed-length objects allocated on the heap, with zero-based indexing.;Array references live on the stack and point to contiguous element allocations on the heap.;Accessing an invalid index throws ArrayIndexOutOfBoundsException at runtime.', content = '
<h3>1. Arrays as Heap Objects</h3>
<p>Unlike in C/C++ where arrays can be stack-allocated, <strong>all Java arrays are objects dynamically allocated on the heap</strong>. The variable itself is a reference that holds the memory address of the array object.</p>

<pre><code>public class ArrayFundamentals {
    public static void main(String[] args) {
        // Declaration and allocation
        int[] scores = new int[5]; // Allocated on heap, initialized to default 0s

        scores[0] = 95;
        scores[1] = 88;
        scores[2] = 72;
        scores[3] = 91;
        scores[4] = 84;

        // Array length is a final property, not a method
        System.out.println("Array capacity: " + scores.length);

        // Array Literal Initializer
        String[] subjects = {"Mathematics", "Physics", "Computer Science"};
        for (int i = 0; i &lt; subjects.length; i++) {
            System.out.println("Subject " + (i + 1) + ": " + subjects[i]);
        }
    }
}</code></pre>

<h3>2. The Enhanced For-Each Loop</h3>
<p>When you only need to inspect elements sequentially without modifying the underlying index, the enhanced <code>for-each</code> loop provides clean, bug-free traversal:</p>

<pre><code>int[] values = {12, 45, 67, 23, 89};
int max = values[0];

for (int v : values) {
    if (v &gt; max) {
        max = v;
    }
}
System.out.println("Maximum element: " + max); // 89</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Array Statistics &amp; Outlier Filter</h4>
  <p><strong>Goal:</strong> Calculate the average score and count how many students scored strictly above the class average.</p>
  <pre><code>public class ClassStatistics {
    public static void main(String[] args) {
        int[] grades = {78, 92, 85, 64, 89, 95, 73, 88};

        int total = 0;
        for (int g : grades) {
            total += g;
        }

        double average = (double) total / grades.length;
        int aboveAverageCount = 0;

        for (int g : grades) {
            if (g &gt; average) {
                aboveAverageCount++;
            }
        }

        System.out.printf("Average: %.2f | Above Average Students: %d%n", average, aboveAverageCount);
    }
}</code></pre>
</div>
' WHERE id = 3 AND topic_id = 3;

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 3, 2, 'Multidimensional & Ragged Arrays', 20, 'Java multidimensional arrays are \'arrays of arrays\' where each row is an independent heap object.;Ragged (jagged) arrays have rows with different column lengths, saving memory for non-rectangular data.;Traversing 2D arrays requires nested loops, where the outer loop iterates over rows and the inner over columns.', '
<h3>1. Arrays of Arrays Concept</h3>
<p>Java does not allocate contiguous 2D memory blocks. Instead, a 2D array <code>int[][] matrix</code> is an array whose elements are themselves references pointing to distinct 1D array objects.</p>

<pre><code>public class MatrixOperations {
    public static void main(String[] args) {
        // Standard 3x3 matrix
        int[][] grid = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // Accessing element at row 1, column 2
        System.out.println("Row 1, Col 2: " + grid[1][2]); // 6

        // Computing main diagonal sum
        int diagonalSum = 0;
        for (int i = 0; i &lt; grid.length; i++) {
            diagonalSum += grid[i][i];
        }
        System.out.println("Diagonal sum: " + diagonalSum); // 1 + 5 + 9 = 15
    }
}</code></pre>

<h3>2. Ragged (Jagged) Arrays</h3>
<p>Because each row is a separate object, each row can have a completely different length:</p>

<pre><code>public class RaggedArrayDemo {
    public static void main(String[] args) {
        // Allocate outer array with 3 rows
        int[][] salesByQuarter = new int[3][];

        // Assign different lengths to each row
        salesByQuarter[0] = new int[2]; // Department 1 has 2 branches
        salesByQuarter[1] = new int[4]; // Department 2 has 4 branches
        salesByQuarter[2] = new int[1]; // Department 3 has 1 branch

        salesByQuarter[1][3] = 45000;

        for (int r = 0; r &lt; salesByQuarter.length; r++) {
            System.out.println("Dept " + (r + 1) + " branch count: " + salesByQuarter[r].length);
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Matrix Transposition</h4>
  <p><strong>Goal:</strong> Transpose a 2x3 rectangular matrix into a 3x2 matrix by swapping rows and columns.</p>
  <pre><code>public class MatrixTranspose {
    public static void main(String[] args) {
        int[][] original = {
            {1, 2, 3},
            {4, 5, 6}
        };

        int rows = original.length;
        int cols = original[0].length;
        int[][] transposed = new int[cols][rows];

        for (int r = 0; r &lt; rows; r++) {
            for (int c = 0; c &lt; cols; c++) {
                transposed[c][r] = original[r][c];
            }
        }

        System.out.println("Transposed Matrix (3x2):");
        for (int[] row : transposed) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 3 AND order_index = 2);

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 3, 3, 'The java.util.Arrays Utility Toolkit', 16, 'Printing an array with System.out.println(arr) outputs its memory hashcode; use Arrays.toString() instead.;Arrays.sort() uses Dual-Pivot Quicksort / TimSort for high-performance sorting.;Arrays.binarySearch() runs in O(log n) time but strictly requires the array to be sorted beforehand.', '
<h3>1. Common Arrays Utilities in <code>java.util.Arrays</code></h3>
<p>The standard library provides static utility methods that save time and prevent manual boilerplate implementations.</p>

<pre><code>import java.util.Arrays;

public class ArraysUtilityDemo {
    public static void main(String[] args) {
        int[] numbers = {45, 12, 85, 32, 89, 3, 67};

        // 1. Correct String Representation
        System.out.println("Raw array print: " + numbers); // Prints hashcode: [I@...
        System.out.println("Arrays.toString: " + Arrays.toString(numbers));

        // 2. High-Performance Sorting
        Arrays.sort(numbers);
        System.out.println("Sorted: " + Arrays.toString(numbers));

        // 3. Binary Search (Requires sorted array!)
        int target = 32;
        int index = Arrays.binarySearch(numbers, target);
        System.out.println("Found " + target + " at sorted index: " + index);

        // 4. Copying and Resizing
        int[] expandedCopy = Arrays.copyOf(numbers, 10); // Extends capacity, pads with 0
        System.out.println("Copied with size 10: " + Arrays.toString(expandedCopy));

        // 5. Fill array with default value
        int[] flags = new int[5];
        Arrays.fill(flags, -1);
        System.out.println("Filled with -1: " + Arrays.toString(flags));
    }
}</code></pre>

<h3>2. Deep Comparison &amp; Deep Printing for 2D Arrays</h3>
<p>For multidimensional arrays, use <code>Arrays.deepToString()</code> and <code>Arrays.deepEquals()</code>:</p>
<pre><code>int[][] a = {{1, 2}, {3, 4}};
int[][] b = {{1, 2}, {3, 4}};

System.out.println(Arrays.deepToString(a)); // [[1, 2], [3, 4]]
System.out.println("Deep equals: " + Arrays.deepEquals(a, b)); // true</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Inventory Price Lookup</h4>
  <p><strong>Goal:</strong> Sort an array of stock prices and verify whether a target price is available using binary search.</p>
  <pre><code>import java.util.Arrays;

public class PriceLookup {
    public static void main(String[] args) {
        double[] prices = {49.99, 19.95, 99.00, 24.50, 79.99, 14.99};
        double searchPrice = 24.50;

        Arrays.sort(prices);
        int matchIdx = Arrays.binarySearch(prices, searchPrice);

        if (matchIdx &gt;= 0) {
            System.out.println("Item found in catalog at index " + matchIdx + " ($" + prices[matchIdx] + ")");
        } else {
            System.out.println("Price not found in current inventory.");
        }
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 3 AND order_index = 3);

UPDATE lessons SET title = 'Classes, Objects & Heap Memory Modeling', estimated_minutes = 20, key_points = 'A class is a structural template; an object is a concrete instance allocated in heap memory.;State is maintained in instance variables, while behavior is defined in instance methods.;Multiple reference variables can reference the exact same object in memory.', content = '
<h3>1. The Object-Oriented Paradigm in Java</h3>
<p>Object-Oriented Programming (OOP) models software systems as collections of cooperative objects that encapsulate data (state) and operations (behavior).</p>

<pre><code>public class BankAccount {
    // Fields (State)
    String accountNumber;
    String accountHolder;
    double balance;

    // Methods (Behavior)
    void deposit(double amount) {
        if (amount &gt; 0) {
            balance += amount;
            System.out.printf("Deposited $%.2f. New Balance: $%.2f%n", amount, balance);
        }
    }

    boolean withdraw(double amount) {
        if (amount &gt; 0 &amp;&amp; amount &lt;= balance) {
            balance -= amount;
            System.out.printf("Withdrew $%.2f. Remaining: $%.2f%n", amount, balance);
            return true;
        }
        System.out.println("Insufficient funds or invalid withdrawal amount.");
        return false;
    }
}</code></pre>

<h3>2. Object Instantiation and References</h3>
<p>When you call <code>new BankAccount()</code>, memory is reserved on the heap. Assigning one object variable to another copies the <em>reference</em>, not the underlying object:</p>

<pre><code>public class Main {
    public static void main(String[] args) {
        BankAccount acc1 = new BankAccount();
        acc1.accountHolder = "Aarav Sharma";
        acc1.balance = 500.0;

        // acc2 references the EXACT SAME heap object as acc1
        BankAccount acc2 = acc1;
        acc2.deposit(250.0);

        System.out.println("acc1 balance: " + acc1.balance); // 750.0!
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Library Book Entity</h4>
  <p><strong>Goal:</strong> Model a <code>Book</code> class with <code>title</code>, <code>author</code>, and <code>isIssued</code> fields, along with <code>borrowBook()</code> and <code>returnBook()</code> methods.</p>
  <pre><code>public class Book {
    String title;
    String author;
    boolean isIssued;

    void borrowBook() {
        if (!isIssued) {
            isIssued = true;
            System.out.println(title + " successfully checked out.");
        } else {
            System.out.println(title + " is already on loan.");
        }
    }

    void returnBook() {
        isIssued = false;
        System.out.println(title + " successfully returned.");
    }
}</code></pre>
</div>
' WHERE id = 4 AND topic_id = 4;

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 4, 2, 'Constructors, Overloading & The \'this\' Keyword', 18, 'Constructors initialize object state and share the exact name of the class with no return type.;Constructor overloading allows objects to be constructed with varying sets of initial parameters.;\'this(...)\' delegates to another constructor within the same class and must be the first line of code.', '
<h3>1. Constructor Essentials</h3>
<p>A constructor is a special block of code called when an instance is created via <code>new</code>. If you write no constructors, Java provides a default no-argument constructor. If you define <em>any</em> custom constructor, the compiler default is removed.</p>

<h3>2. Constructor Chaining with <code>this(...)</code></h3>
<p>To avoid duplicating initialization code across multiple overloaded constructors, use <code>this(...)</code> to delegate between them:</p>

<pre><code>public class UserAccount {
    private String username;
    private String email;
    private String role;
    private boolean isActive;

    // Full Parameterized Constructor
    public UserAccount(String username, String email, String role, boolean isActive) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    // Overloaded Constructor with default role and active status
    public UserAccount(String username, String email) {
        this(username, email, "STUDENT", true); // Calls the 4-arg constructor!
    }

    // Overloaded Guest Constructor
    public UserAccount() {
        this("Guest_" + System.currentTimeMillis(), "guest@edunest.com", "GUEST", false);
    }

    public void display() {
        System.out.println("User: " + username + " | Email: " + email + " | Role: " + role);
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Flexible Course Enrollment Record</h4>
  <p><strong>Goal:</strong> Implement a <code>CourseEnrollment</code> class with constructor overloading that handles both direct enrollment and enrollment with early-bird discount codes.</p>
  <pre><code>public class CourseEnrollment {
    String studentName;
    String courseCode;
    double feePaid;

    public CourseEnrollment(String studentName, String courseCode, double feePaid) {
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.feePaid = feePaid;
    }

    public CourseEnrollment(String studentName, String courseCode) {
        this(studentName, courseCode, 199.0); // Standard default course fee
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 4 AND order_index = 2);

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 4, 3, 'Access Modifiers, Encapsulation & Immutability', 20, 'Encapsulation hides sensitive internal fields by making them private and exposing controlled public getters and setters.;The four access levels: private, default (package-private), protected, and public.;Immutable classes have all final private fields, no mutator methods, and protect against state changes.', '
<h3>1. The 4 Access Levels in Java</h3>
<ul>
  <li><code>private</code>: Accessible only within the declaring class body.</li>
  <li><code>default</code> (no keyword): Accessible by any class within the same package.</li>
  <li><code>protected</code>: Accessible within the same package and by subclasses across packages.</li>
  <li><code>public</code>: Accessible from any class anywhere in the application.</li>
</ul>

<h3>2. Robust Encapsulation Pattern</h3>
<p>By declaring fields <code>private</code>, you enforce data invariants inside setter methods so invalid values cannot corrupt state:</p>

<pre><code>public class Employee {
    private String id;
    private String name;
    private double salary;

    public Employee(String id, String name, double salary) {
        this.id = id;
        this.name = name;
        setSalary(salary); // Use validation logic on construction
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary &lt; 0.0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
        this.salary = salary;
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Immutable Coordinate Entity</h4>
  <p><strong>Goal:</strong> Create a fully immutable <code>GeoPoint</code> class holding latitude and longitude with defensive validation.</p>
  <pre><code>public final class GeoPoint {
    private final double latitude;
    private final double longitude;

    public GeoPoint(double latitude, double longitude) {
        if (latitude &lt; -90.0 || latitude &gt; 90.0) {
            throw new IllegalArgumentException("Latitude out of range [-90, 90]");
        }
        if (longitude &lt; -180.0 || longitude &gt; 180.0) {
            throw new IllegalArgumentException("Longitude out of range [-180, 180]");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 4 AND order_index = 3);

UPDATE lessons SET title = 'Class Inheritance & Constructor Chaining with \'super\'', estimated_minutes = 20, key_points = 'Inheritance establishes an IS-A relationship using the \'extends\' keyword.;Java supports single-class inheritance (a class can only extend one direct superclass).;\'super(...)\' explicitly calls the parent class constructor and must be the first line in the subclass constructor.', content = '
<h3>1. The <code>extends</code> Keyword &amp; IS-A Relationship</h3>
<p>Inheritance enables a new class (subclass) to inherit state and behavior from an existing class (superclass), promoting code reuse and architectural hierarchies.</p>

<pre><code>public class Vehicle {
    protected String brand;
    protected int year;

    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }

    public void describe() {
        System.out.println(year + " " + brand);
    }
}

// Subclass inheriting from Vehicle
public class ElectricCar extends Vehicle {
    private int batteryCapacityKwh;

    public ElectricCar(String brand, int year, int batteryCapacityKwh) {
        super(brand, year); // Invokes the parent Vehicle constructor!
        this.batteryCapacityKwh = batteryCapacityKwh;
    }

    public void displaySpecs() {
        describe(); // Inherited method
        System.out.println("Battery: " + batteryCapacityKwh + " kWh");
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Staff &amp; Professor Hierarchy</h4>
  <p><strong>Goal:</strong> Model a base <code>UniversityMember</code> class and derive a <code>Professor</code> class with publications and department data.</p>
  <pre><code>public class UniversityMember {
    protected String memberId;
    protected String name;

    public UniversityMember(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }
}

public class Professor extends UniversityMember {
    private String department;

    public Professor(String memberId, String name, String department) {
        super(memberId, name);
        this.department = department;
    }
}</code></pre>
</div>
' WHERE id = 5 AND topic_id = 5;

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 5, 2, 'Method Overriding & Dynamic Runtime Polymorphism', 22, 'Method overriding replaces parent behavior with subclass implementation having the exact same signature.;The @Override annotation asks the compiler to verify signature compatibility.;Polymorphism allows a superclass reference variable to point to any subclass instance at runtime.', '
<h3>1. Overriding vs Overloading</h3>
<ul>
  <li><strong>Overloading (Compile-Time):</strong> Same method name with different parameter types/counts inside the same class.</li>
  <li><strong>Overriding (Runtime):</strong> Subclass provides a specific implementation of a method already declared in its superclass.</li>
</ul>

<h3>2. Dynamic Method Dispatch in Action</h3>
<p>When invoking an overridden method through a superclass reference, the JVM uses runtime dynamic dispatch to execute the subclass implementation:</p>

<pre><code>// Superclass
public class PaymentGateway {
    public void processPayment(double amount) {
        System.out.println("Processing generic payment of $" + amount);
    }
}

// Subclass 1
public class CreditCardGateway extends PaymentGateway {
    @Override
    public void processPayment(double amount) {
        System.out.println("Validating card with banking network... Authorized: $" + amount);
    }
}

// Subclass 2
public class CryptoGateway extends PaymentGateway {
    @Override
    public void processPayment(double amount) {
        System.out.println("Broadcasting transaction to blockchain... Confirmed: $" + amount);
    }
}

public class CheckoutApp {
    public static void main(String[] args) {
        // Polymorphic Array
        PaymentGateway[] gateways = {
            new CreditCardGateway(),
            new CryptoGateway()
        };

        for (PaymentGateway pg : gateways) {
            pg.processPayment(149.99); // Executes subclass implementation dynamically!
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Polymorphic Geometric Shapes</h4>
  <p><strong>Goal:</strong> Create a <code>Shape</code> class with an overridden <code>area()</code> method implemented in <code>Circle</code> and <code>Rectangle</code>.</p>
  <pre><code>public class Shape {
    public double area() { return 0.0; }
}

public class Circle extends Shape {
    private double radius;
    public Circle(double radius) { this.radius = radius; }
    @Override
    public double area() { return Math.PI * radius * radius; }
}

public class Rectangle extends Shape {
    private double w, h;
    public Rectangle(double w, double h) { this.w = w; this.h = h; }
    @Override
    public double area() { return w * h; }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 5 AND order_index = 2);

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 5, 3, 'Abstract Classes vs Interfaces & Contract Design', 22, 'Abstract classes cannot be instantiated and provide shared state and partial implementations.;Interfaces define pure behavioral contracts; a single class can implement multiple interfaces.;Java 8+ allows default and static methods inside interfaces to provide optional behavior.', '
<h3>1. Choosing Between Abstract Classes and Interfaces</h3>
<ul>
  <li>Use an <strong>Abstract Class</strong> when sharing common fields, state, or constructors across closely related classes in an inheritance hierarchy.</li>
  <li>Use an <strong>Interface</strong> when defining a capability or contract that unrelated classes can implement (e.g., <code>Comparable</code>, <code>AutoCloseable</code>).</li>
</ul>

<pre><code>// Interface contract
public interface Exportable {
    String exportAsJson();

    // Default method (Java 8+)
    default void logExport() {
        System.out.println("Export completed at: " + java.time.Instant.now());
    }
}

// Abstract base class
public abstract class Report {
    protected String title;

    public Report(String title) {
        this.title = title;
    }

    // Abstract method must be implemented by concrete subclasses
    public abstract void generate();
}

// Concrete class extending abstract class AND implementing interface
public class FinancialReport extends Report implements Exportable {
    public FinancialReport(String title) {
        super(title);
    }

    @Override
    public void generate() {
        System.out.println("Generating ledger data for: " + title);
    }

    @Override
    public String exportAsJson() {
        logExport();
        return "{\\"report\\": \\"" + title + "\\"}";
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Notification Dispatcher</h4>
  <p><strong>Goal:</strong> Design an interface <code>Notifier</code> with <code>send(String recipient, String message)</code> implemented by <code>EmailNotifier</code> and <code>SmsNotifier</code>.</p>
  <pre><code>public interface Notifier {
    void send(String recipient, String message);
}

public class EmailNotifier implements Notifier {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending SMTP email to " + recipient + ": " + message);
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 5 AND order_index = 3);

UPDATE lessons SET title = 'Exception Hierarchy & Try-Catch-Finally Mechanics', estimated_minutes = 20, key_points = 'Checked exceptions (compile-time) must be handled with try-catch or declared with throws.;Unchecked exceptions (RuntimeExceptions) represent programming errors like null references and array out of bounds.;The finally block executes unconditionally regardless of whether an exception occurred.', content = '
<h3>1. The Java Throwable Hierarchy</h3>
<ul>
  <li><code>Throwable</code>: The root class of all exceptions and errors.</li>
  <li><code>Error</code>: Unrecoverable system issues (e.g. <code>OutOfMemoryError</code>, <code>StackOverflowError</code>). Applications should not catch these.</li>
  <li><code>Exception</code>: Conditions that a reasonable application might want to catch.
    <ul>
      <li><strong>Checked Exceptions:</strong> Subclasses of <code>Exception</code> (excluding <code>RuntimeException</code>). Verified by compiler (e.g., <code>IOException</code>, <code>SQLException</code>).</li>
      <li><strong>Unchecked Exceptions:</strong> Subclasses of <code>RuntimeException</code> (e.g., <code>NullPointerException</code>, <code>IllegalArgumentException</code>).</li>
    </ul>
  </li>
</ul>

<h3>2. Try-Catch-Finally Execution Flow</h3>
<pre><code>public class ExceptionDemo {
    public static void main(String[] args) {
        try {
            int result = 10 / 0; // Throws ArithmeticException
            System.out.println("Result: " + result);
        } catch (ArithmeticException ex) {
            System.err.println("Caught math error: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Caught general error: " + ex.getMessage());
        } finally {
            // ALWAYS executes, even if an exception was thrown
            System.out.println("Cleanup executed successfully.");
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Safe String-to-Integer Parser</h4>
  <p><strong>Goal:</strong> Parse an array of string inputs into integers, skipping invalid entries gracefully without crashing.</p>
  <pre><code>public class SafeParser {
    public static void main(String[] args) {
        String[] rawInputs = {"10", "45", "INVALID", "89", "NULL"};

        for (String input : rawInputs) {
            try {
                int value = Integer.parseInt(input);
                System.out.println("Parsed successfully: " + value);
            } catch (NumberFormatException e) {
                System.out.println("Skipped invalid token: " + input);
            }
        }
    }
}</code></pre>
</div>
' WHERE id = 6 AND topic_id = 6;

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 6, 2, 'Custom Exceptions, Throws & Try-With-Resources', 20, 'Use \'throw\' to raise an exception manually; use \'throws\' in a method signature to declare exceptions.;Custom exceptions make error reporting descriptive and domain-specific.;Try-with-resources automatically closes AutoCloseable resources at the end of the statement.', '
<h3>1. Domain-Specific Custom Exceptions</h3>
<p>Create custom exception classes by extending <code>Exception</code> (for checked) or <code>RuntimeException</code> (for unchecked):</p>

<pre><code>public class InsufficientFundsException extends Exception {
    private double shortfall;

    public InsufficientFundsException(String message, double shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public double getShortfall() {
        return shortfall;
    }
}</code></pre>

<h3>2. Modern Try-with-Resources (Java 7+)</h3>
<p>Resources like database connections, files, and sockets implementing <code>AutoCloseable</code> are automatically closed upon leaving the <code>try (...)</code> block, eliminating resource leaks:</p>

<pre><code>import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

public class ResourceDemo {
    public static void main(String[] args) {
        String data = "First line\\nSecond line\\nThird line";

        // Resource declared inside try parenthesis is guaranteed to be closed!
        try (BufferedReader reader = new BufferedReader(new StringReader(data))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Line: " + line);
            }
        } catch (IOException e) {
            System.err.println("I/O failure: " + e.getMessage());
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Bank Account Withdrawal Guard</h4>
  <p><strong>Goal:</strong> Implement withdrawal logic that throws <code>InsufficientFundsException</code> when funds are lacking.</p>
  <pre><code>public class BankGuard {
    private double balance = 100.0;

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount &gt; balance) {
            throw new InsufficientFundsException("Insufficient funds in account", amount - balance);
        }
        balance -= amount;
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 6 AND order_index = 2);

INSERT INTO lessons (topic_id, order_index, title, estimated_minutes, key_points, content)
SELECT 6, 3, 'Core Java Collections Framework: ArrayList & HashMap', 24, 'ArrayList provides dynamic resizable arrays with fast index-based lookups O(1).;HashMap provides fast key-value associative lookups with average O(1) time complexity.;Generics (<T>, <K, V>) ensure compile-time type safety across all collections.', '
<h3>1. Limitations of Fixed Arrays &amp; The Collections Framework</h3>
<p>Traditional arrays cannot change their capacity after allocation. The <strong>Java Collections Framework (JCF)</strong> provides dynamic data structures that automatically resize as elements are added.</p>

<h3>2. The <code>ArrayList&lt;E&gt;</code> Resizable List</h3>
<pre><code>import java.util.ArrayList;
import java.util.List;

public class ArrayListDemo {
    public static void main(String[] args) {
        // Favor programming to interface: List on left, ArrayList on right
        List&lt;String&gt; enrolledStudents = new ArrayList&lt;&gt;();

        enrolledStudents.add("Aarav Sharma");
        enrolledStudents.add("Priya Patel");
        enrolledStudents.add("Vikram Verma");

        // O(1) Fast lookup by index
        System.out.println("First student: " + enrolledStudents.get(0));

        // Removal
        enrolledStudents.remove("Priya Patel");
        System.out.println("Total active: " + enrolledStudents.size());
    }
}</code></pre>

<h3>3. The <code>HashMap&lt;K, V&gt;</code> Key-Value Map</h3>
<p>Stores key-value associations with average constant time <code>O(1)</code> lookup:</p>

<pre><code>import java.util.HashMap;
import java.util.Map;

public class GradebookMap {
    public static void main(String[] args) {
        Map&lt;String, Integer&gt; scores = new HashMap&lt;&gt;();

        scores.put("Java Basics", 95);
        scores.put("OOP Design", 88);
        scores.put("Collections", 92);

        // Fast retrieval
        int javaScore = scores.getOrDefault("Java Basics", 0);
        System.out.println("Java Basics Score: " + javaScore);

        // Iterating through Map entries
        for (Map.Entry&lt;String, Integer&gt; entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " -&gt; " + entry.getValue() + "%");
        }
    }
}</code></pre>

<div class="lesson-exercise">
  <h4><span class="exercise-badge">Hands-On Exercise</span> Student Roster &amp; Lookup Directory</h4>
  <p><strong>Goal:</strong> Implement a student roster lookup using a <code>HashMap&lt;String, List&lt;Integer&gt;&gt;</code> to store student names and their quiz scores.</p>
  <pre><code>import java.util.*;

public class StudentRoster {
    public static void main(String[] args) {
        Map&lt;String, List&lt;Integer&gt;&gt; studentGrades = new HashMap&lt;&gt;();

        studentGrades.put("Aarav", Arrays.asList(90, 85, 92));
        studentGrades.put("Priya", Arrays.asList(88, 94, 96));

        for (String student : studentGrades.keySet()) {
            List&lt;Integer&gt; grades = studentGrades.get(student);
            double avg = grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            System.out.printf("Student: %s | Average: %.1f%n", student, avg);
        }
    }
}</code></pre>
</div>
'
WHERE NOT EXISTS (SELECT 1 FROM lessons WHERE topic_id = 6 AND order_index = 3);

