package com.myproject;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File; // Import this
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        // Parse a real file
        File file = new File("Test.java");

        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            Optional<TypeDeclaration<?>> primaryTypeOpt = cu.getPrimaryType();

            if (primaryTypeOpt.isPresent()) {
                String className = primaryTypeOpt.get().getNameAsString();
                System.out.println("Successfully parsed class: " + className);
            } else {
                System.out.println("Parsed file, but no primary type was found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to parse file: " + e.getMessage());
        }
    }
}