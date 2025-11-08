package com.myproject.cfg;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the Control Flow Graph for a single method.
 */
public class CFG {
    /** The unique entry point of the method's CFG. */
    private final CFGNode entry;

    /** The unique exit point of the method's CFG. */
    private final CFGNode exit;

    /** A collection of all nodes in this graph. */
    private final Set<CFGNode> allNodes = new HashSet<>();

    public CFG(CFGNode entry, CFGNode exit) {
        this.entry = entry;
        this.exit = exit;
        this.allNodes.add(entry);
        this.allNodes.add(exit);
    }

    public CFGNode getEntry() {
        return entry;
    }

    public CFGNode getExit() {
        return exit;
    }

    public Set<CFGNode> getAllNodes() {
        return allNodes;
    }

    /**
     * Helper method to register a new node with the graph.
     * Should be called whenever a new node is created by the builder.
     */
    public void addNode(CFGNode node) {
        this.allNodes.add(node);
    }
}