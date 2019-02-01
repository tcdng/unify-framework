/*
 * Copyright 2018-2019 The Code Department.
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
 * @author Lateef Ojulari
 * @since 1.0
 */
public class MarkedTree<T> {

    private Map<Long, Node<T>> nodes;

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
     *            A true value points parent to root node and descends to the last
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
     * Gets marked tree root node.
     * 
     * @return the root node
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
     * Finds first node, starting from root node, whose item is matched by matcher.
     * 
     * @param matcher
     *            the matcher
     * @return the matching node otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    public Node<T> findNode(Matcher<T> matcher) throws UnifyException {
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
    public Node<T> findNode(Long startMark, Matcher<T> matcher) throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            return matchNode(trg, matcher);
        }

        return null;
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

        if (last == null) {
            last = parent.child = addItem(item, parent);
        } else {
            last = last.next = addItem(item, last);
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
            Node<T> nw = addItem(item, trg.prev);
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
            Node<T> nw = addItem(item, trg);
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
        return addChild(destMark, childItem, null);
    }

    /**
     * An unchained mode operation that adds supplied item as a child to item at
     * specified mark. Item is added based on supplied policy.
     * 
     * @param destMark
     *            the mark of item at destination location
     * @param childItem
     *            the item to add
     * @param addChildPolicy
     *            the policy to use when adding child item. If not supplied or policy
     *            does not effect addition, item is added as last child.
     * 
     * @return the added child item mark if successfully added otherwise null
     * @throws UnifyException
     *             if marked tree is not in unchained mode. if default at maximum
     *             add operation
     */
    public Long addChild(Long destMark, T childItem, AddChildPolicy<T> addChildPolicy) throws UnifyException {
        checkNotUnchain();
        checkDefault();

        Node<T> dest = nodes.get(destMark);
        if (dest != null) {
            Node<T> nw = addItem(childItem, dest);
            attachChild(dest, nw, addChildPolicy);
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
        return moveAsChild(destMark, srcMark, null);
    }

    /**
     * An unchained mode operation that moves a node, including all its children, at
     * source mark to node at destination mark as a child item using policy.
     * 
     * @param destMark
     *            the destination mark
     * @param srcMark
     *            the source mark
     * @param addChildPolicy
     *            the policy to use when adding child item. If not supplied or policy
     *            does not effect addition, item is added as last child.
     * @return a true value if movement was successful otherwise false
     * @throws UnifyException
     *             if marked tree is not in unchained mode.
     */
    public boolean moveAsChild(Long destMark, Long srcMark, AddChildPolicy<T> addChildPolicy) throws UnifyException {
        Node<T> dest = nodes.get(destMark);
        Node<T> src = nodes.get(srcMark);

        if (canMove(dest, src)) {
            // Detach source without removing marks
            detach(src, false);

            // Add as child
            attachChild(dest, src, addChildPolicy);
            
            return true;
        }

        return false;
    }

    /**
     * Updates root node and all descendants using supplied update policy.
     * 
     * @param updateChildPolicy
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(UpdateChildPolicy<T> updateChildPolicy) throws UnifyException {
        updateAll(updateChildPolicy);
    }

    /**
     * Updates root node and all descendants that are matched by supplied matcher using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param matcher the matcher
     * @param updateChildPolicy
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(Matcher<T> matcher, UpdateChildPolicy<T> updateChildPolicy) throws UnifyException {
        if (matcher != null) {
            updateAll(matcher, updateChildPolicy);
        } else {
            updateAll(updateChildPolicy);
        }
    }

    /**
     * Updates node at supplied mark and all descendants using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param updateChildPolicy
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(Long startMark, UpdateChildPolicy<T> updateChildPolicy) throws UnifyException {
        updateNodes(startMark, null, updateChildPolicy);
    }

    /**
     * Updates node at supplied mark and all descendants that are matched by supplied matcher using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param matcher the matcher
     * @param updateChildPolicy
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateNodes(Long startMark, Matcher<T> matcher, UpdateChildPolicy<T> updateChildPolicy) throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            if (matcher != null) {
                updateNode(trg, matcher, updateChildPolicy);
            } else {
                updateNode(trg, updateChildPolicy);
            }
        }
    }

    /**
     * Updates all parent nodes of node at supplied mark using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param updateChildPolicy
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateParentNodes(Long startMark, UpdateChildPolicy<T> updateChildPolicy) throws UnifyException {
        updateParentNodes(startMark, null, updateChildPolicy);
    }

    /**
     * Updates all parent nodes of node at supplied mark that are matched by supplied matcher using supplied update policy.
     * 
     * @param startMark
     *            the start mark
     * @param matcher the matcher
     * @param updateChildPolicy
     *            the update policy
     * @throws UnifyException
     *             if an error occurs
     */
    public void updateParentNodes(Long startMark, Matcher<T> matcher, UpdateChildPolicy<T> updateChildPolicy) throws UnifyException {
        Node<T> trg = nodes.get(startMark);
        if (trg != null) {
            if (matcher != null) {
                updateParentNode(trg, matcher, updateChildPolicy);
            } else {
                updateParentNode(trg, updateChildPolicy);
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

    /**
     * Clears tree of all items except root and enters chained mode.
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

    private void attachChild(Node<T> dest, Node<T> child, AddChildPolicy<T> addChildPolicy) {
        if (dest.child == null) {
            child.prev = dest;
            dest.child = child;
        } else {
            if (addChildPolicy == null) {
                attachBelow(getLast(dest.child), child);
            } else {
                Node<T> dch = dest.child;
                Node<T> lch = null;
                boolean added = false;
                do {
                    int descision = addChildPolicy.addDecision(dch.item, child.item);
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

    private Node<T> addItem(T item, Node<T> prev) {
        Node<T> nw = new Node<T>(item, markCounter++, prev);
        nodes.put(nw.mark, nw);
        return nw;
    }

    private void attachAbove(Node<T> trg, Node<T> nw) {
        nw.next = trg;
        if (isFirstChild(trg)) {
            trg.prev.child = nw;
        } else {
            trg.prev.next = nw;
        }
        trg.prev = nw;
    }

    private void attachBelow(Node<T> trg, Node<T> nw) {
        nw.next = trg.next;
        if (trg.next != null) {
            trg.next.prev = nw;
        }
        trg.next = nw;
    }

    private void detach(Node<T> trg, boolean removeMark) {
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
    }

    private void removeMarks(Node<T> node) {
        nodes.remove(node.mark);
        Node<T> child = node.child;
        while (child != null) {
            removeMarks(child);
            child = child.next;
        }
    }

    private Node<T> matchNode(Node<T> trg, Matcher<T> matcher) throws UnifyException {
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

    private void updateAll(UpdateChildPolicy<T> updateChildPolicy) {
        updateChildPolicy.update(root.item);
        for (Node<T> node: nodes.values()) {
            updateChildPolicy.update(node.item);
        }
    }

    private void updateAll(Matcher<T> matcher, UpdateChildPolicy<T> updateChildPolicy) {
        if (matcher.match(root.item)) {
            updateChildPolicy.update(root.item);
        }
        
        for (Node<T> node: nodes.values()) {
            if (matcher.match(node.item)) {
                updateChildPolicy.update(node.item);
            }
        }
    }

    private void updateNode(Node<T> trg, UpdateChildPolicy<T> updateChildPolicy) {
        updateChildPolicy.update(trg.item);
        
        Node<T> ch = trg.child;
        while (ch != null) {
            updateNode(ch, updateChildPolicy);
            ch = ch.next;
        }
    }

    private void updateNode(Node<T> trg, Matcher<T> matcher, UpdateChildPolicy<T> updateChildPolicy) {
        if (matcher.match(trg.item)) {
            updateChildPolicy.update(trg.item);
        }
        
        Node<T> ch = trg.child;
        while (ch != null) {
            updateNode(ch, matcher, updateChildPolicy);
            ch = ch.next;
        }
    }

    private void updateParentNode(Node<T> trg, UpdateChildPolicy<T> updateChildPolicy) {
        Node<T> parent = getParent(trg);
        if (parent != null) {
            updateChildPolicy.update(parent.item);
            updateParentNode(parent, updateChildPolicy);
        }
    }

    private void updateParentNode(Node<T> trg, Matcher<T> matcher, UpdateChildPolicy<T> updateChildPolicy) {
        Node<T> parent = getParent(trg);
        if (parent != null) {
            if (matcher.match(parent.item)) {
                updateChildPolicy.update(parent.item);
            }

            updateParentNode(parent, matcher, updateChildPolicy);
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

        private Node(T item, long mark, Node<T> prev) {
            this.item = item;
            this.mark = mark;
            this.prev = prev;
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

        public List<T> getChildItemList(Matcher<T> matcher) {
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

    public static interface Matcher<T> {

        /**
         * Checks if supplied item matches conditions of this matcher
         * 
         * @param item
         *            the item to match
         * @return a true value if successfully matched otherwise false;
         */
        boolean match(T item);
    }

    public static interface AddChildPolicy<T> {

        /**
         * Used to determine how to add a child item to a specific node.
         * 
         * @param targetItem
         *            the item at the target node
         * @param childItem
         *            the child item to add
         * @return Do not add if returned value equals 0, add above if returned value
         *         less than 0 and add below if returned value is greater than 0.
         */
        int addDecision(T targetItem, T childItem);
    }

    public static interface UpdateChildPolicy<T> {

        /**
         * Update policy applied to child item.
         * 
         * @param childItem
         *            the child item to update
         */
        void update(T childItem);
    }
}
