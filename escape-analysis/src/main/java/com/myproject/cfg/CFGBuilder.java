package com.myproject.cfg;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Visitor that traverses a MethodDeclaration to build its CFG.
 */
public class CFGBuilder extends VoidVisitorAdapter<CFGNode> {

    private CFG cfg;
    private CFGNode exitNode;

    /**
     * Main entry point to build a CFG for a method.
     */
    public CFG build(MethodDeclaration method) {
        CFGNode entryNode = new CFGNode(new EmptyStmt(), "Entry");
        this.exitNode = new CFGNode(new EmptyStmt(), "Exit");
        this.cfg = new CFG(entryNode, this.exitNode);

        // The method body is a BlockStmt. We visit it, passing the 'entry' node
        // as its predecessor.
        method.getBody().ifPresent(body -> {
            // We don't want to visit the BlockStmt itself as a node, just its contents.
            // So we manually trigger the visit for its statements.
            visitBlockContents(body, entryNode);
        });

        return this.cfg;
    }

    /**
     * Helper to visit the statements within a block.
     * Returns the last node of the block (the new "tail").
     */
    private CFGNode visitBlockContents(BlockStmt block, CFGNode predecessor) {
        CFGNode currentPredecessor = predecessor;
        for (Statement stmt : block.getStatements()) {

            if (stmt.isIfStmt() || stmt.isWhileStmt() || stmt.isForStmt() || stmt.isReturnStmt()) {
                currentPredecessor = buildNode(stmt, currentPredecessor);
            } else if (stmt.isBlockStmt()) {
                 // Nested block
                 currentPredecessor = visitBlockContents(stmt.asBlockStmt(), currentPredecessor);
            } else {
                // Simple statement (ExpressionStmt, AssertStmt, etc.)
                CFGNode node = new CFGNode(stmt);
                cfg.addNode(node);
                currentPredecessor.addSuccessor(node);
                currentPredecessor = node;
            }
        }
        return currentPredecessor;
    }

    /**
     * Dispatches to specific build methods based on statement type.
     */
    private CFGNode buildNode(Statement stmt, CFGNode predecessor) {
        if (stmt.isIfStmt()) return buildIf(stmt.asIfStmt(), predecessor);
        if (stmt.isReturnStmt()) return buildReturn(stmt.asReturnStmt(), predecessor);
        // Add While/For here later
        return predecessor; // Should not happen if all types are covered
    }

    private CFGNode buildIf(IfStmt stmt, CFGNode predecessor) {
        // 1. Create the IF node
        CFGNode ifNode = new CFGNode(stmt);
        cfg.addNode(ifNode);
        predecessor.addSuccessor(ifNode);

        // 2. Build THEN branch
        CFGNode thenTail = buildBranch(stmt.getThenStmt(), ifNode);

        // 3. Build ELSE branch (if it exists)
        CFGNode elseTail = null;
        if (stmt.getElseStmt().isPresent()) {
            elseTail = buildBranch(stmt.getElseStmt().get(), ifNode);
        } else {
            // If no else, the 'if' node itself can go directly to the join
            elseTail = ifNode;
        }

        // 4. Create JOIN node
        CFGNode joinNode = new CFGNode(new EmptyStmt(), "Join branch");
        cfg.addNode(joinNode);

        // 5. Wire branches to JOIN
        // We only link if the branch didn't already return/throw (tail wouldn't be null or similar check in full impl)
        // For simplicity, we assume it might fall through.
        if (thenTail != null) thenTail.addSuccessor(joinNode);
        if (elseTail != null) elseTail.addSuccessor(joinNode);

        return joinNode; // The join node is the new tail
    }

    private CFGNode buildBranch(Statement branchStmt, CFGNode predecessor) {
        if (branchStmt.isBlockStmt()) {
            return visitBlockContents(branchStmt.asBlockStmt(), predecessor);
        } else {
            // Single statement branch (e.g., if (c) x = 1;)
            return buildNode(branchStmt, predecessor);
        }
    }

    private CFGNode buildReturn(ReturnStmt stmt, CFGNode predecessor) {
        CFGNode returnNode = new CFGNode(stmt);
        cfg.addNode(returnNode);
        predecessor.addSuccessor(returnNode);
        // Link directly to the generic graph exit node
        returnNode.addSuccessor(cfg.getExit());
        // Returns don't have a "next" node in the regular flow, so we return null
        // to signal that this path has ended.
        return null;
    }

}