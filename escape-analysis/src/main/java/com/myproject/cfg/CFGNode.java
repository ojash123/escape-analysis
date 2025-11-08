package com.myproject.cfg;

import com.github.javaparser.ast.stmt.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single node in the Control Flow Graph.
 * Each node corresponds to one statement in the Java source code.
 */
public class CFGNode {
    /** The JavaParser statement this node represents. Can be an EmptyStmt for entry/exit/join nodes. */
    private final Statement stmt;

    /** Nodes that can execute immediately after this node. */
    private final Set<CFGNode> successors = new HashSet<>();

    /** Nodes that can execute immediately before this node. */
    private final Set<CFGNode> predecessors = new HashSet<>();

    // Placeholder for Dataflow Facts (we'll add these later when we implement the analysis)
    // public DataflowFact inFact;
    // public DataflowFact outFact;

    // Add a field for a custom label
    private String label;

    public CFGNode(Statement stmt) {
        this(stmt, null);
    }

    public CFGNode(Statement stmt, String label) {
        this.stmt = stmt;
        this.label = label;
    }



    public Statement getStmt() {
        return stmt;
    }

    public Set<CFGNode> getSuccessors() {
        return successors;
    }

    public Set<CFGNode> getPredecessors() {
        return predecessors;
    }

    /**
     * Adds a directed edge from this node to the given successor node.
     * Automatically updates the predecessor list of the successor.
     */
    public void addSuccessor(CFGNode succ) {
        this.successors.add(succ);
        succ.predecessors.add(this);
    }

   
    @Override
    public String toString() {
        if (label != null) return "[" + label + "]";
        String lineNum = stmt.getBegin().map(p -> String.valueOf(p.line)).orElse("?");
        return "[" + lineNum + "] " + stmt.getClass().getSimpleName();
    }
}