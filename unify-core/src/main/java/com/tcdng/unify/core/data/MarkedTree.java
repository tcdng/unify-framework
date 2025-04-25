/*
 * Copyright 2018-2025 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tcdng.unify.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * A general tree with marked nodes. Nodes are marked with unique long ID that
 * are used for quick referencing. Defaults at maximum add operation count above
 * to Long.MAX_VALUE.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class MarkedTree<T> {

    private Map<Long, Node<T>> nodes;

    private MarkedTreePolicy<T> treePolicy;

    private Node<T> root;

    private Node<T> parent;

    private Node<T> last;

    private long markCounter;

    public MarkedTree(T rootItem) {
        root = new Node<T>(rootItem, markCounter++);
        clear();
    }

    /**
     * Sets marked tree chain mode.
     * 
     * @param chain
     *            A true value points parent to rootPolicies node and descends to the last
     *            child and enables chain-only operations. A false value enable
     *            unchained operations only.
     */
    public void setChain(boolean chain) {
        if (chain) {
            parent = root;
            descendToLast();
        } else {
            parent = last = null;
        }
    }

    /**
     * Checks if marked tree is in chain mode.
     * 
     * @return a true value if in chain mode otherwise false.
     */
    public boolean isChain() {
        return parent != null;
    }

    /**
     * Sets marked tree tre policy.
     * 
     * @param treePolicy
     *            the policy to set
     */
    public void setTreePolicy(MarkedTreePolicy<T> treePolicy) {
        this.treePolicy = treePolicy;
    }

    /**
     * Gets tree policy attached to this marked tree.
     * 
     * @return the tree policy
     */
    public MarkedTreePolicy<T> getTreePolicy() {
        return treePolicy;
    }

    /**
     * Checks id tree policy attached to this marked tree.
     * 
     * @return a true value if attached otherwise false
     */
    public boolean isTreePolicy() {
        return treePolicy != null;
    }

    /**
     * Gets marked tree rootPolicies node.
     * 
     * @return the rootPolicies node
     */
    public Node<T> getRoot() {
        return root;
    }

    /**
     * Gets node at mark.
     * 
     * @param mark
     *            the mark to get node at
     * @return the node if found otherwise null
     */
    public Node<T> getNode(Long mark) {
        return nodes.get(mark);
    }

    /**
     * Gets the parent of a node at supplied mark.
     * 
     * @param mark
     *            the mark of node to get parent
     * @return the node if found otherwise null
     */
    public Node<T> getParentNode(Long mark) {
        Node<T> nw = nodes.get(mark);
        if (nw != null) {
            return getParent(nw);
        }

        return null;
    }

    /**
     * Gets the current parent node in chained mode.
     * 
     * @return the parent node
     */
    public Node<T> getChainParent() {
        return parent;
    }

    /**
     * Gets the current last node in chained mode.
     * 
     * @return the last node
     */
    public Node<T> getChainLast() {
        return last;
    }

    /**
     * A chained mode operation that ascends tree to last node of upper level.
     * 
     * @return a true value if a move was made to upper level, otherwise false
     * @throws UnifyException
     *             if marked tree is not in chained mode.
     */
    public boolean ascend() throws UnifyException {
        checkNotChain();

        if (parent.prev != null) {
            parent = getParent(parent);
            descendToLast();
            return true;
        }

        return false;
    }

    /**
     * A chained mode operation that descends tree, at current last node, to last
     * node of lower level.
     * 
     * @return a true value if a move was made to lower level, otherwise false
     * @throws UnifyException
     *             if marked tree is not in chained mode.
     */
    public boolean descend() throws UnifyException {
        checkNotChain();

        if (last != null) {
            parent = last;
            descendToLast();
            return true;
        }

        return false;
    }

    /**
     * Finds first node, starting from rootPolicies node, whose item is matched by matcher.
     * 
     * @param matcher
     *            the matcher
     * @return the matching node otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    public Node<T> findFirstNode(MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        return matchNode(root, matcher);
    }

    /**
     * Finds first node, starting from supplied mark, whose item is matched by
     * matcher.
     * 
     * @param startMark
     *            the start mark
     * @param matcher
     *            the matcher
     * @return the matching node otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    public Node<T> findFirstNode(Long startMark, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            return matchNode(trg, matcher);
        }

        return null;
    }

    /**
     * Finds all node, starting from rootPolicies node, whose items are matched by matcher.
     * 
     * @param matcher
     *            the matcher
     * @return list of matching nodes
     * @throws UnifyException
     *             if an error occurs
     */
    public List<Node<T>> findNodes(MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        return matchNodes(root, matcher);
    }

    /**
     * Finds all nodes, starting from supplied mark, whose items are matched by
     * matcher.
     * 
     * @param startMark
     *            the start mark
     * @param matcher
     *            the matcher
     * @return list of matching nodes
     * @throws UnifyException
     *             if an error occurs
     */
    public List<Node<T>> findNodes(Long startMark, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            return matchNodes(trg, matcher);
        }

        return Collections.emptyList();
    }

    /**
     * Get all immediate child nodes for node at supplied parent mark.
     * 
     * @param parentMark
     *            the parent mark
     * @return list of matching child nodes
     * @throws UnifyException
     *             if an error occurs
     */
    public List<Node<T>> getChildNodes(Long parentMark) throws UnifyException {
        Node<T> trg = nodes.get(parentMark);
        if (trg != null) {
            return trg.getChildNodeList();
        }

        return Collections.emptyList();
    }

    /**
     * Get all immediate child nodes for node at supplied parent mark that are
     * matched by supplied matcher.
     * 
     * @param parentMark
     *            the parent mark
     * @param matcher
     *            the matcher
     * @return list of matching child nodes
     * @throws UnifyException
     *             if an error occurs
     */
    public List<Node<T>> getChildNodes(Long parentMark, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        Node<T> trg = nodes.get(parentMark);
        if (trg != null) {
            return trg.getChildNodeList(matcher);
        }

        return Collections.emptyList();
    }

    /**
     * Get first immediate child node for node at supplied parent mark that is
     * matched by supplied matcher.
     * 
     * @param parentMark
     *            the parent mark
     * @param matcher
     *            the matcher
     * @return the matching child node otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    public Node<T> getFirstChildNode(Long parentMark, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        Node<T> trg = nodes.get(parentMark);
        if (trg != null) {
            return trg.getFirstChildNode(matcher);
        }

        return null;
    }

    /**
     * Get all immediate child node items for node at supplied parent mark.
     * 
     * @param parentMark
     *            the parent mark
     * @return list of matching child node items
     * @throws UnifyException
     *             if an error occurs
     */
    public List<T> getChildItems(Long parentMark) throws UnifyException {
        Node<T> trg = nodes.get(parentMark);
        if (trg != null) {
            return trg.getChildItemList();
        }

        return Collections.emptyList();
    }

    /**
     * Get all immediate child node items for node at supplied parent mark that are
     * matched by supplied matcher..
     * 
     * @param parentMark
     *            the parent mark
     * @param matcher
     *            the matcher
     * @return list of matching child nodes
     * @throws UnifyException
     *             if an error occurs
     */
    public List<T> getChildItems(Long parentMark, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        Node<T> trg = nodes.get(parentMark);
        if (trg != null) {
            return trg.getChildItemList(matcher);
        }

        return Collections.emptyList();
    }

    /**
     * A chained mode operation that adds item to marked tree.
     * 
     * @param item
     *            the item to add
     * @return the added item mark
     * @throws UnifyException
     *             if marked tree is not in chained mode. if default at maximum add
     *             operation
     */
    public Long add(T item) throws UnifyException {
        checkNotChain();
        checkDefault();

        Node<T> nw = createNode(item);
        if (last == null) {
            nw.prev = parent;
            last = parent.child = nw;

            if (treePolicy != null) {
                treePolicy.performOnAdd(parent.mark, nw.mark, parent.item, nw.item);
            }
        } else {
            nw.prev = last;
            last = last.next = nw;

            if (treePolicy != null) {
                Node<T> parentNode = getParent(nw);
                treePolicy.performOnAdd(parentNode.mark, nw.mark, parentNode.item, nw.item);
            }
        }

        return last.mark;
    }

    /**
     * An unchained mode operation that adds supplied item above item at specified
     * mark.
     * 
     * @param mark
     *            the mark of item at destination location
     * @param item
     *            the item to add
     * @return the added item mark if successfully added otherwise null
     * @throws UnifyException
     *             if marked tree is not in unchained mode. if default at maximum
     *             add operation
     */
    public Long addAbove(Long mark, T item) throws UnifyException {
        checkNotUnchain();
        checkDefault();

        Node<T> trg = nodes.get(mark);
        if (trg != null) {
            Node<T> nw = createNode(item);
            attachAbove(trg, nw);
            return nw.mark;
        }

        return null;
    }

    /**
     * An unchained mode operation that adds supplied item below item at specified
     * mark.
     * 
     * @param mark
     *            the mark of item at destination location
     * @param item
     *            the item to add
     * @return the added item mark if successfully added otherwise null
     * @throws UnifyException
     *             if marked tree is not in unchained mode. if default at maximum
     *             add operation
     */
    public Long addBelow(Long mark, T item) throws UnifyException {
        checkNotUnchain();
        checkDefault();

        Node<T> trg = nodes.get(mark);
        if (trg != null) {
            Node<T> nw = createNode(item);
            attachBelow(trg, nw);
            return nw.mark;
        }

        return null;
    }

    /**
     * An unchained mode operation that adds supplied item as a child to item at
     * specified mark. Item is added as last child.
     * 
     * @param destMark
     *            the mark of item at destination location
     * @param childItem
     *            the item to add
     * @return the added child item mark if successfully added otherwise null
     * @throws UnifyException
     *             if marked tree is not in unchained mode. if default at maximum
     *             add operation
     */
    public Long addChild(Long destMark, T childItem) throws UnifyException {
        checkNotUnchain();
        checkDefault();

        Node<T> dest = nodes.get(destMark);
        if (dest != null) {
            Node<T> nw = createNode(childItem);
            attachChild(dest, nw);
            return nw.mark;
        }

        return null;
    }

    /**
     * An unchained mode operation that moves a node, including all its children, at
     * source mark to location above node at destination mark.
     * 
     * @param destMark
     *            the destination mark
     * @param srcMark
     *            the source mark
     * @return a true value if movement was successful otherwise false
     * @throws UnifyException
     *             if marked tree is not in unchained mode.
     */
    public boolean moveAbove(Long destMark, Long srcMark) throws UnifyException {
        Node<T> dest = nodes.get(destMark);
        Node<T> src = nodes.get(srcMark);

        if (canMove(dest, src)) {
            // Detach source without removing marks
            detach(src, false);

            // Attach above
            attachAbove(dest, src);

            return true;
        }

        return false;
    }

    /**
     * An unchained mode operation that moves a node, including all its children, at
     * source mark to location below node at destination mark.
     * 
     * @param destMark
     *            the destination mark
     * @param srcMark
     *            the source mark
     * @return a true value if movement was successful otherwise false
     * @throws UnifyException
     *             if marked tree is not in unchained mode.
     */
    public boolean moveBelow(Long destMark, Long srcMark) throws UnifyException {
        Node<T> dest = nodes.get(destMark);
        Node<T> src = nodes.get(srcMark);

        if (canMove(dest, src)) {
            // Detach source without removing marks
            detach(src, false);

            // Attach below
            attachBelow(dest, src);

            return true;
        }

        return false;
    }

    /**
     * An unchained mode operation that moves a node, including all its children, at
     * source mark to node at destination mark as a child item.
     * 
     * @param destMark
     *            the destination mark
     * @param srcMark
     *            the source mark
     * @return a true value if movement was successful otherwise false
     * @throws UnifyException
     *             if marked tree is not in unchained mode.
     */
    public boolean moveAsChild(Long destMark, Long srcMark) throws UnifyException {
        Node<T> dest = nodes.get(destMark);
        Node<T> src = nodes.get(srcMark);

        if (canMove(dest, src)) {
            // Detach source without removing marks
            detach(src, false);

            // Add as child
            attachChild(dest, src);

            return true;
        }

        return false;
    }

    /**
     * Updates rootPolicies node and all descendants using supplied update policy.
     * 
     * @param updater
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(MarkedTreeItemUpdater<T> updater) throws UnifyException {
        updateAll(updater);
    }

    /**
     * Updates rootPolicies node and all descendants that are matched by supplied matcher
     * using supplied update policy.
     * 
     * @param matcher
     *            the matcher
     * @param updater
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(MarkedTreeItemMatcher<T> matcher, MarkedTreeItemUpdater<T> updater) throws UnifyException {
        if (matcher != null) {
            updateAll(matcher, updater);
        } else {
            updateAll(updater);
        }
    }

    /**
     * Updates node at supplied mark and all descendants using supplied update
     * policy.
     * 
     * @param startMark
     *            the start mark
     * @param updater
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(Long startMark, MarkedTreeItemUpdater<T> updater) throws UnifyException {
        updateNodes(startMark, null, updater);
    }

    /**
     * Updates node at supplied mark and all descendants that are matched by
     * supplied matcher using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param matcher
     *            the matcher
     * @param updater
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(Long startMark, MarkedTreeItemMatcher<T> matcher, MarkedTreeItemUpdater<T> updater)
            throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            if (matcher != null) {
                updateNode(trg, matcher, updater);
            } else {
                updateNode(trg, updater);
            }
        }
    }

    /**
     * Updates all parent nodes of node at supplied mark using supplied update
     * policy.
     * 
     * @param startMark
     *            the start mark
     * @param updater
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateParentNodes(Long startMark, MarkedTreeItemUpdater<T> updater) throws UnifyException {
        updateParentNodes(startMark, null, updater);
    }

    /**
     * Updates all parent nodes of node at supplied mark that are matched by
     * supplied matcher using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param matcher
     *            the matcher
     * @param updater
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateParentNodes(Long startMark, MarkedTreeItemMatcher<T> matcher, MarkedTreeItemUpdater<T> updater)
            throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            if (matcher != null) {
                updateParentNode(trg, matcher, updater);
            } else {
                updateParentNode(trg, updater);
            }
        }
    }

    /**
     * An unchained mode operation that removes node, including all its children, at
     * supplied mark.
     * 
     * @param mark
     *            the mark of node to remove
     * @return the node if successfully removed otherwise null
     * @throws UnifyException
     *             if marked tree is not in unchained mode.
     */
    public Node<T> remove(Long mark) throws UnifyException {
        checkNotUnchain();

        Node<T> trg = nodes.get(mark);
        if (trg != null) {
            detach(trg, true);
            return trg;
        }

        return null;
    }

    // RemoveChildPolicy
    /**
     * Clears tree of all items except rootPolicies and enters chained mode.
     */
    public void clear() {
        nodes = new HashMap<Long, Node<T>>();
        root.child = null;
        setChain(true);
    }

    /**
     * Gets the number of nodes in this tree.
     * 
     * @return the number of nodes
     */
    public int size() {
        return nodes.size() + 1;
    }

    private void checkNotChain() throws UnifyException {
        if (parent == null) {
            throw new UnifyException(UnifyCoreErrorConstants.MARKEDTREE_NOT_CHAIN);
        }
    }

    private void checkNotUnchain() throws UnifyException {
        if (parent != null) {
            throw new UnifyException(UnifyCoreErrorConstants.MARKEDTREE_IN_CHAIN);
        }
    }

    private void checkDefault() throws UnifyException {
        if (markCounter < 0) {
            throw new UnifyException(UnifyCoreErrorConstants.MARKEDTREE_DEFAULT);
        }
    }

    private void descendToLast() {
        last = getLast(parent.child);
    }

    private boolean isFirstChild(Node<T> node) {
        return node.prev.child == node;
    }

    private boolean canMove(Node<T> dest, Node<T> src) {
        if (dest == null || src == null || dest == src) {
            return false;
        }

        if (isAncestor(src, dest)) {
            // Can not move a source node if destination node is its descendant.
            return false;
        }

        return true;
    }

    private boolean isAncestor(Node<T> ancestor, Node<T> node) {
        Node<T> parent = node;
        while ((parent = getParent(parent)) != null) {
            if (ancestor == parent) {
                return true;
            }
        }

        return false;
    }

    private void attachChild(Node<T> dest, Node<T> child) {
        if (dest.child == null) {
            child.prev = dest;
            dest.child = child;

            if (treePolicy != null) {
                treePolicy.performOnAdd(dest.mark, child.mark, dest.item, child.item);
            }
        } else {
            if (treePolicy == null) {
                attachBelow(getLast(dest.child), child);
            } else {
                Node<T> dch = dest.child;
                Node<T> lch = null;
                boolean added = false;
                do {
                    int descision = treePolicy.addDecision(dch.item, child.item);
                    if (descision != 0) {
                        if (descision < 0) {
                            attachAbove(dch, child);
                        } else {
                            attachBelow(dch, child);
                        }

                        added = true;
                        break;
                    }

                    lch = dch;
                } while ((dch = dch.next) != null);

                if (!added) {
                    attachBelow(lch, child);
                }
            }
        }
    }

    private Node<T> getParent(Node<T> node) {
        while (node.prev != null && node.prev.child != node) { // While not first child
            node = node.prev;
        }

        return node.prev;
    }

    private Node<T> getLast(Node<T> last) {
        if (last != null) {
            while (last.next != null) {
                last = last.next;
            }
        }

        return last;
    }

    private Node<T> createNode(T item) {
        Node<T> nw = new Node<T>(item, markCounter++);
        nodes.put(nw.mark, nw);
        return nw;
    }

    private void attachAbove(Node<T> trg, Node<T> nw) {
        nw.prev = trg.prev;
        nw.next = trg;
        if (isFirstChild(trg)) {
            trg.prev.child = nw;
        } else {
            trg.prev.next = nw;
        }
        trg.prev = nw;

        if (treePolicy != null) {
            Node<T> parentNode = getParent(nw);
            treePolicy.performOnAdd(parentNode.mark, nw.mark, parentNode.item, nw.item);
        }
    }

    private void attachBelow(Node<T> trg, Node<T> nw) {
        nw.prev = trg;
        nw.next = trg.next;
        if (trg.next != null) {
            trg.next.prev = nw;
        }
        trg.next = nw;

        if (treePolicy != null) {
            Node<T> parentNode = getParent(nw);
            treePolicy.performOnAdd(parentNode.mark, nw.mark, parentNode.item, nw.item);
        }
    }

    private void detach(Node<T> trg, boolean removeMark) {
        Node<T> parent = null;
        if (treePolicy != null) {
            parent = getParent(trg);
        }

        if (isFirstChild(trg)) {
            trg.prev.child = trg.next;
        } else {
            trg.prev.next = trg.next;
        }

        if (trg.next != null) {
            trg.next.prev = trg.prev;
        }

        trg.prev = trg.next = null;
        if (removeMark) {
            removeMarks(trg);
        }

        if (treePolicy != null && parent != null) {
            treePolicy.performOnRemove(parent.getMark(), trg.getMark(), parent.item, trg.item);
        }
    }

    private void removeMarks(Node<T> node) {
        nodes.remove(node.mark);
        Node<T> child = node.child;
        while (child != null) {
            removeMarks(child);
            child = child.next;
        }
    }

    private Node<T> matchNode(Node<T> trg, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        if (matcher.match(trg.item)) {
            return trg;
        }

        Node<T> ch = trg.child;
        while (ch != null) {
            Node<T> result = matchNode(ch, matcher);
            if (result != null) {
                return result;
            }
            ch = ch.next;
        }
        return null;
    }

    private List<Node<T>> matchNodes(Node<T> trg, MarkedTreeItemMatcher<T> matcher) throws UnifyException {
        List<Node<T>> list = matchNodes(trg, null, matcher);
        if (list != null) {
            return list;
        }

        return Collections.emptyList();
    }

    private List<Node<T>> matchNodes(Node<T> trg, List<Node<T>> matchList, MarkedTreeItemMatcher<T> matcher)
            throws UnifyException {
        if (matcher.match(trg.item)) {
            if (matchList == null) {
                matchList = new ArrayList<Node<T>>();
            }

            matchList.add(trg);
        }

        Node<T> ch = trg.child;
        while (ch != null) {
            matchList = matchNodes(ch, matchList, matcher);
            ch = ch.next;
        }

        return matchList;
    }

    private void updateAll(MarkedTreeItemUpdater<T> updater) {
        updater.update(root.item);
        for (Node<T> node : nodes.values()) {
            updater.update(node.item);
        }
    }

    private void updateAll(MarkedTreeItemMatcher<T> matcher, MarkedTreeItemUpdater<T> updater) {
        if (matcher.match(root.item)) {
            updater.update(root.item);
        }

        for (Node<T> node : nodes.values()) {
            if (matcher.match(node.item)) {
                updater.update(node.item);
            }
        }
    }

    private void updateNode(Node<T> trg, MarkedTreeItemUpdater<T> updater) {
        updater.update(trg.item);

        Node<T> ch = trg.child;
        while (ch != null) {
            updateNode(ch, updater);
            ch = ch.next;
        }
    }

    private void updateNode(Node<T> trg, MarkedTreeItemMatcher<T> matcher, MarkedTreeItemUpdater<T> updater) {
        if (matcher.match(trg.item)) {
            updater.update(trg.item);
        }

        Node<T> ch = trg.child;
        while (ch != null) {
            updateNode(ch, matcher, updater);
            ch = ch.next;
        }
    }

    private void updateParentNode(Node<T> trg, MarkedTreeItemUpdater<T> updater) {
        Node<T> parent = getParent(trg);
        if (parent != null) {
            updater.update(parent.item);
            updateParentNode(parent, updater);
        }
    }

    private void updateParentNode(Node<T> trg, MarkedTreeItemMatcher<T> matcher, MarkedTreeItemUpdater<T> updater) {
        Node<T> parent = getParent(trg);
        if (parent != null) {
            if (matcher.match(parent.item)) {
                updater.update(parent.item);
            }

            updateParentNode(parent, matcher, updater);
        }
    }

    public static class Node<T> {

        private Long mark;

        private T item;

        private Node<T> prev;

        private Node<T> next;

        private Node<T> child;

        private Node(T item, long mark) {
            this.item = item;
            this.mark = mark;
        }

        public Long getMark() {
            return mark;
        }

        public T getItem() {
            return item;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public Node<T> getChild() {
            return child;
        }

        public boolean isParent() {
            return child != null;
        }

        public List<Node<T>> getChildNodeList() {
            if (child != null) {
                List<Node<T>> list = new ArrayList<Node<T>>();
                Node<T> ch = child;
                do {
                    list.add(ch);
                } while ((ch = ch.next) != null);
                return list;
            }

            return Collections.emptyList();
        }

        public List<Node<T>> getChildNodeList(MarkedTreeItemMatcher<T> matcher) {
            if (child != null) {
                List<Node<T>> list = new ArrayList<Node<T>>();
                Node<T> ch = child;
                do {
                    if (matcher.match(ch.item)) {
                        list.add(ch);
                    }
                } while ((ch = ch.next) != null);
                return list;
            }

            return Collections.emptyList();
        }

        public Node<T> getFirstChildNode(MarkedTreeItemMatcher<T> matcher) {
            if (child != null) {
                Node<T> ch = child;
                do {
                    if (matcher.match(ch.item)) {
                        return ch;
                    }
                } while ((ch = ch.next) != null);
            }

            return null;
        }

        public List<T> getChildItemList() {
            if (child != null) {
                List<T> list = new ArrayList<T>();
                Node<T> ch = child;
                do {
                    list.add(ch.item);
                } while ((ch = ch.next) != null);
                return list;
            }

            return Collections.emptyList();
        }

        public List<T> getChildItemList(MarkedTreeItemMatcher<T> matcher) {
            if (child != null) {
                List<T> list = new ArrayList<T>();
                Node<T> ch = child;
                do {
                    if (matcher.match(ch.item)) {
                        list.add(ch.item);
                    }
                } while ((ch = ch.next) != null);
                return list;
            }

            return Collections.emptyList();
        }
    }

    public static interface MarkedTreeItemMatcher<T> {

        /**
         * Checks if supplied item matches conditions of this matcher
         * 
         * @param item
         *            the item to match
         * @return a true value if successfully matched otherwise false;
         */
        boolean match(T item);
    }

    public static interface MarkedTreeItemUpdater<T> {

        /**
         * Updates a tree item.
         * 
         * @param item
         *            the item to update
         */
        void update(T item);
    }

    public static interface MarkedTreePolicy<T> {

        /**
         * Used to determine how to add a child item to a specific node.
         * 
         * @param siblingItem
         *            the item at the target node
         * @param childItem
         *            the child item to add
         * @return Do not add if returned value equals 0, add above if returned value
         *         less than 0 and add below if returned value is greater than 0.
         */
        int addDecision(T siblingItem, T childItem);

        /**
         * Executes on add of child item.
         * 
         * @param parentMark
         *            the parent mark
         * @param childMark
         *            the child mark
         * @param targetParentItem
         *            the parent item
         * @param childItem
         *            the added child item
         */
        void performOnAdd(Long parentMark, Long childMark, T targetParentItem, T childItem);

        /**
         * Executes on remove of child item.
         * 
         * @param parentMark
         *            the parent mark
         * @param childMark
         *            the child mark
         * @param targetParentItem
         *            the parent item
         * @param childItem
         *            the removed child item
         */
        void performOnRemove(Long parentMark, Long childMark, T targetParentItem, T childItem);
    }
}
