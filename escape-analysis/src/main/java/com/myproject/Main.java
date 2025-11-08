package com.myproject;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.myproject.cfg.CFG;
import com.myproject.cfg.CFGBuilder;
import com.myproject.cfg.CFGNode;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        File file = new File("Test.java");
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            // 1. Find a method to analyze.
            // For now, let's just pick the first method we find in the file.
            MethodDeclaration method = cu.findAll(MethodDeclaration.class).stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No method found in file"));

            System.out.println("Analyzing method: " + method.getNameAsString());

            // 2. Build the CFG
            CFGBuilder builder = new CFGBuilder();
            CFG cfg = builder.build(method);

            // 3. Print the CFG to verify it
            System.out.println("\n--- CFG Structure ---");
            printCFG(cfg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A simple helper to print the graph structure.
     * It prints each node and its list of successors.
     */
    private static void printCFG(CFG cfg) {
        // We can just iterate over all nodes since our CFG class keeps track of them.
        // Sorting by line number (if available) makes the output easier to read.
        cfg.getAllNodes().stream()
                .sorted((n1, n2) -> {
                    int l1 = n1.getStmt().getBegin().map(p -> p.line).orElse(Integer.MAX_VALUE);
                    int l2 = n2.getStmt().getBegin().map(p -> p.line).orElse(Integer.MAX_VALUE);
                    return Integer.compare(l1, l2);
                })
                .forEach(node -> {
                    System.out.print(node + " -> [");
                    // Print successors
                    boolean first = true;
                    for (CFGNode succ : node.getSuccessors()) {
                        if (!first) System.out.print(", ");
                        System.out.print(succ);
                        first = false;
                    }
                    System.out.println("]");
                });
    }
}