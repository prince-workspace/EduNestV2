package com.edunest.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeExecutionService {

    private static final String PYTHON_EXECUTABLE;

    static {
        // Detect local Python executable path on Windows
        String candidate = "C:\\Users\\Prince Prajapati\\AppData\\Local\\Python\\bin\\python.exe";
        if (new File(candidate).exists()) {
            PYTHON_EXECUTABLE = candidate;
        } else {
            PYTHON_EXECUTABLE = "python";
        }
    }

    public static class TestCase {
        private String input;
        private String expectedOutput;
        private String explanation;

        public TestCase() {}

        public TestCase(String input, String expectedOutput) {
            this.input = input;
            this.expectedOutput = expectedOutput;
        }

        public TestCase(String input, String expectedOutput, String explanation) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.explanation = explanation;
        }

        public String getInput() {
            return input != null ? input : "";
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getExpectedOutput() {
            return expectedOutput != null ? expectedOutput : "";
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    public static class TestCaseResult {
        private int testCaseNumber;
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean passed;
        private String errorMessage;
        private long executionTimeMs;

        public TestCaseResult() {}

        public TestCaseResult(int testCaseNumber, String input, String expectedOutput,
                              String actualOutput, boolean passed, String errorMessage, long executionTimeMs) {
            this.testCaseNumber = testCaseNumber;
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.actualOutput = actualOutput;
            this.passed = passed;
            this.errorMessage = errorMessage;
            this.executionTimeMs = executionTimeMs;
        }

        public int getTestCaseNumber() {
            return testCaseNumber;
        }

        public String getInput() {
            return input;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public String getActualOutput() {
            return actualOutput;
        }

        public boolean isPassed() {
            return passed;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }
    }

    public static class ExecutionResult {
        private boolean compiled = true;
        private String compilationOutput = "";
        private boolean passed = false;
        private double score = 0.0;
        private int totalTestCases = 0;
        private int passedTestCases = 0;
        private List<TestCaseResult> testCaseResults = new ArrayList<>();
        private String feedback = "";
        private long totalExecutionTimeMs = 0;

        public ExecutionResult() {}

        public boolean isCompiled() {
            return compiled;
        }

        public void setCompiled(boolean compiled) {
            this.compiled = compiled;
        }

        public String getCompilationOutput() {
            return compilationOutput;
        }

        public void setCompilationOutput(String compilationOutput) {
            this.compilationOutput = compilationOutput;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getTotalTestCases() {
            return totalTestCases;
        }

        public void setTotalTestCases(int totalTestCases) {
            this.totalTestCases = totalTestCases;
        }

        public int getPassedTestCases() {
            return passedTestCases;
        }

        public void setPassedTestCases(int passedTestCases) {
            this.passedTestCases = passedTestCases;
        }

        public List<TestCaseResult> getTestCaseResults() {
            return testCaseResults;
        }

        public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
            this.testCaseResults = testCaseResults;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        public long getTotalExecutionTimeMs() {
            return totalExecutionTimeMs;
        }

        public void setTotalExecutionTimeMs(long totalExecutionTimeMs) {
            this.totalExecutionTimeMs = totalExecutionTimeMs;
        }
    }

    /**
     * Compiles and executes user source code against a set of test cases.
     */
    public ExecutionResult execute(String language, String sourceCode, List<TestCase> testCases) {
        String lang = (language != null ? language : "JAVA").toUpperCase();
        if (testCases == null) {
            testCases = Collections.emptyList();
        }

        switch (lang) {
            case "PYTHON":
            case "PY":
                return executePython(sourceCode, testCases);
            case "JAVASCRIPT":
            case "JS":
            case "NODE":
                return executeJavaScript(sourceCode, testCases);
            case "JAVA":
            default:
                return executeJava(sourceCode, testCases);
        }
    }

    private ExecutionResult executeJava(String sourceCode, List<TestCase> testCases) {
        ExecutionResult result = new ExecutionResult();
        result.setTotalTestCases(testCases.size());

        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            result.setCompiled(false);
            result.setCompilationOutput("Error: Source code cannot be empty.");
            result.setFeedback("Please write a solution before running or submitting.");
            return result;
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("edunest_java_exec_");

            // Extract or infer public class name
            String className = "Solution";
            Pattern pattern = Pattern.compile("public\\s+class\\s+([A-Za-z0-9_]+)");
            Matcher matcher = pattern.matcher(sourceCode);
            if (matcher.find()) {
                className = matcher.group(1);
            }

            File javaFile = new File(tempDir.toFile(), className + ".java");
            Files.writeString(javaFile.toPath(), sourceCode, StandardCharsets.UTF_8);

            // Step 1: Compile using javac
            ProcessBuilder javacBuilder = new ProcessBuilder("javac", "-encoding", "UTF-8", javaFile.getAbsolutePath());
            javacBuilder.directory(tempDir.toFile());
            Process javacProcess = javacBuilder.start();

            String compileErrors = readStream(javacProcess.getErrorStream());
            boolean finishedCompile = javacProcess.waitFor(10, TimeUnit.SECONDS);

            if (!finishedCompile || javacProcess.exitValue() != 0) {
                result.setCompiled(false);
                // Sanitize temp paths from compiler error message for a clean user view
                String cleanError = compileErrors.replace(tempDir.toFile().getAbsolutePath(), "")
                        .replace("\\" + className + ".java:", "Line ")
                        .trim();
                result.setCompilationOutput(cleanError.isEmpty() ? "Compilation failed with unknown error." : cleanError);
                result.setPassed(false);
                result.setScore(0.0);
                result.setFeedback("Compilation Error: Please fix syntax or type errors in your Java code.");
                return result;
            }

            result.setCompiled(true);
            result.setCompilationOutput("Compiled successfully without errors.");

            // Step 2: Run test cases
            int passedCount = 0;
            long totalTime = 0;

            for (int i = 0; i < testCases.size(); i++) {
                TestCase tc = testCases.get(i);
                long start = System.currentTimeMillis();

                ProcessBuilder runner = new ProcessBuilder("java", "-cp", tempDir.toFile().getAbsolutePath(), className);
                runner.directory(tempDir.toFile());
                Process runProcess = runner.start();

                // Pipe test input to stdin
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream(), StandardCharsets.UTF_8))) {
                    writer.write(tc.getInput());
                    writer.newLine();
                    writer.flush();
                } catch (IOException ignored) {}

                boolean finished = runProcess.waitFor(4, TimeUnit.SECONDS);
                long elapsed = System.currentTimeMillis() - start;
                totalTime += elapsed;

                if (!finished) {
                    runProcess.destroyForcibly();
                    result.getTestCaseResults().add(new TestCaseResult(
                            i + 1, tc.getInput(), tc.getExpectedOutput(), "", false, "Time Limit Exceeded (> 4000ms)", elapsed
                    ));
                    continue;
                }

                String stdout = readStream(runProcess.getInputStream());
                String stderr = readStream(runProcess.getErrorStream());

                if (runProcess.exitValue() != 0 && stdout.trim().isEmpty()) {
                    String cleanErr = stderr.replace(tempDir.toFile().getAbsolutePath(), "").trim();
                    result.getTestCaseResults().add(new TestCaseResult(
                            i + 1, tc.getInput(), tc.getExpectedOutput(), stdout.trim(), false, "Runtime Error: " + cleanErr, elapsed
                    ));
                    continue;
                }

                boolean passed = normalize(stdout).equals(normalize(tc.getExpectedOutput()));
                if (passed) {
                    passedCount++;
                }

                result.getTestCaseResults().add(new TestCaseResult(
                        i + 1, tc.getInput(), tc.getExpectedOutput(), stdout.trim(), passed, passed ? null : "Wrong output", elapsed
                ));
            }

            result.setPassedTestCases(passedCount);
            result.setTotalExecutionTimeMs(totalTime);
            double score = testCases.isEmpty() ? 100.0 : Math.round((passedCount * 1000.0) / testCases.size()) / 10.0;
            result.setScore(score);
            result.setPassed(passedCount == testCases.size() && !testCases.isEmpty());

            if (result.isPassed()) {
                result.setFeedback("All " + testCases.size() + " test cases passed! Solution accepted (100%).");
            } else {
                result.setFeedback("Passed " + passedCount + " of " + testCases.size() + " test cases (" + score + "%). Please verify edge cases.");
            }

        } catch (Exception e) {
            result.setCompiled(false);
            result.setCompilationOutput("Execution environment error: " + e.getMessage());
            result.setFeedback("Execution error: " + e.getMessage());
        } finally {
            cleanupTempDir(tempDir);
        }

        return result;
    }

    private ExecutionResult executePython(String sourceCode, List<TestCase> testCases) {
        ExecutionResult result = new ExecutionResult();
        result.setTotalTestCases(testCases.size());

        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            result.setCompiled(false);
            result.setCompilationOutput("Error: Python source code cannot be empty.");
            result.setFeedback("Please write a solution before running or submitting.");
            return result;
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("edunest_py_exec_");
            File pyFile = new File(tempDir.toFile(), "solution.py");
            Files.writeString(pyFile.toPath(), sourceCode, StandardCharsets.UTF_8);

            int passedCount = 0;
            long totalTime = 0;

            for (int i = 0; i < testCases.size(); i++) {
                TestCase tc = testCases.get(i);
                long start = System.currentTimeMillis();

                ProcessBuilder runner = new ProcessBuilder(PYTHON_EXECUTABLE, "-u", pyFile.getAbsolutePath());
                runner.directory(tempDir.toFile());
                Process process = runner.start();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                    writer.write(tc.getInput());
                    writer.newLine();
                    writer.flush();
                } catch (IOException ignored) {}

                boolean finished = process.waitFor(4, TimeUnit.SECONDS);
                long elapsed = System.currentTimeMillis() - start;
                totalTime += elapsed;

                if (!finished) {
                    process.destroyForcibly();
                    result.getTestCaseResults().add(new TestCaseResult(
                            i + 1, tc.getInput(), tc.getExpectedOutput(), "", false, "Time Limit Exceeded (> 4000ms)", elapsed
                    ));
                    continue;
                }

                String stdout = readStream(process.getInputStream());
                String stderr = readStream(process.getErrorStream());

                if (process.exitValue() != 0) {
                    String cleanErr = stderr.replace(tempDir.toFile().getAbsolutePath(), "").trim();
                    if (cleanErr.contains("SyntaxError")) {
                        result.setCompiled(false);
                        result.setCompilationOutput(cleanErr);
                    }
                    result.getTestCaseResults().add(new TestCaseResult(
                            i + 1, tc.getInput(), tc.getExpectedOutput(), stdout.trim(), false, "Runtime Error: " + cleanErr, elapsed
                    ));
                    continue;
                }

                boolean passed = normalize(stdout).equals(normalize(tc.getExpectedOutput()));
                if (passed) {
                    passedCount++;
                }

                result.getTestCaseResults().add(new TestCaseResult(
                        i + 1, tc.getInput(), tc.getExpectedOutput(), stdout.trim(), passed, passed ? null : "Wrong output", elapsed
                ));
            }

            result.setPassedTestCases(passedCount);
            result.setTotalExecutionTimeMs(totalTime);
            double score = testCases.isEmpty() ? 100.0 : Math.round((passedCount * 1000.0) / testCases.size()) / 10.0;
            result.setScore(score);
            result.setPassed(passedCount == testCases.size() && !testCases.isEmpty());

            if (result.isPassed()) {
                result.setFeedback("All " + testCases.size() + " test cases passed! Python solution accepted (100%).");
            } else {
                result.setFeedback("Passed " + passedCount + " of " + testCases.size() + " test cases (" + score + "%).");
            }

        } catch (Exception e) {
            result.setCompiled(false);
            result.setCompilationOutput("Execution error: " + e.getMessage());
            result.setFeedback("Execution error: " + e.getMessage());
        } finally {
            cleanupTempDir(tempDir);
        }

        return result;
    }

    private ExecutionResult executeJavaScript(String sourceCode, List<TestCase> testCases) {
        ExecutionResult result = new ExecutionResult();
        result.setTotalTestCases(testCases.size());

        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            result.setCompiled(false);
            result.setCompilationOutput("Error: JavaScript source code cannot be empty.");
            result.setFeedback("Please write a solution before running or submitting.");
            return result;
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("edunest_js_exec_");
            File jsFile = new File(tempDir.toFile(), "solution.js");
            Files.writeString(jsFile.toPath(), sourceCode, StandardCharsets.UTF_8);

            int passedCount = 0;
            long totalTime = 0;

            for (int i = 0; i < testCases.size(); i++) {
                TestCase tc = testCases.get(i);
                long start = System.currentTimeMillis();

                ProcessBuilder runner = new ProcessBuilder("node", jsFile.getAbsolutePath());
                runner.directory(tempDir.toFile());
                Process process = runner.start();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                    writer.write(tc.getInput());
                    writer.newLine();
                    writer.flush();
                } catch (IOException ignored) {}

                boolean finished = process.waitFor(4, TimeUnit.SECONDS);
                long elapsed = System.currentTimeMillis() - start;
                totalTime += elapsed;

                if (!finished) {
                    process.destroyForcibly();
                    result.getTestCaseResults().add(new TestCaseResult(
                            i + 1, tc.getInput(), tc.getExpectedOutput(), "", false, "Time Limit Exceeded (> 4000ms)", elapsed
                    ));
                    continue;
                }

                String stdout = readStream(process.getInputStream());
                String stderr = readStream(process.getErrorStream());

                if (process.exitValue() != 0) {
                    String cleanErr = stderr.replace(tempDir.toFile().getAbsolutePath(), "").trim();
                    if (cleanErr.contains("SyntaxError")) {
                        result.setCompiled(false);
                        result.setCompilationOutput(cleanErr);
                    }
                    result.getTestCaseResults().add(new TestCaseResult(
                            i + 1, tc.getInput(), tc.getExpectedOutput(), stdout.trim(), false, "Runtime Error: " + cleanErr, elapsed
                    ));
                    continue;
                }

                boolean passed = normalize(stdout).equals(normalize(tc.getExpectedOutput()));
                if (passed) {
                    passedCount++;
                }

                result.getTestCaseResults().add(new TestCaseResult(
                        i + 1, tc.getInput(), tc.getExpectedOutput(), stdout.trim(), passed, passed ? null : "Wrong output", elapsed
                ));
            }

            result.setPassedTestCases(passedCount);
            result.setTotalExecutionTimeMs(totalTime);
            double score = testCases.isEmpty() ? 100.0 : Math.round((passedCount * 1000.0) / testCases.size()) / 10.0;
            result.setScore(score);
            result.setPassed(passedCount == testCases.size() && !testCases.isEmpty());

            if (result.isPassed()) {
                result.setFeedback("All " + testCases.size() + " test cases passed! JavaScript solution accepted (100%).");
            } else {
                result.setFeedback("Passed " + passedCount + " of " + testCases.size() + " test cases (" + score + "%).");
            }

        } catch (Exception e) {
            result.setCompiled(false);
            result.setCompilationOutput("Execution error: " + e.getMessage());
            result.setFeedback("Execution error: " + e.getMessage());
        } finally {
            cleanupTempDir(tempDir);
        }

        return result;
    }

    private String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").replaceAll("[ \\t]+", " ");
    }

    private void cleanupTempDir(Path tempDir) {
        if (tempDir == null) return;
        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception ignored) {}
    }
}
