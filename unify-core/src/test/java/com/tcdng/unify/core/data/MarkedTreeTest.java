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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.MarkedTree.AddChildPolicy;
import com.tcdng.unify.core.data.MarkedTree.Matcher;
import com.tcdng.unify.core.data.MarkedTree.Node;
import com.tcdng.unify.core.data.MarkedTree.UpdateChildPolicy;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Marked tree tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class MarkedTreeTest {

    @Test
    public void testCreateMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        assertTrue(mTree.isChain());
        assertNotNull(mTree.getChainParent());
        assertEquals("ROOT", mTree.getChainParent().getItem());
        assertNull(mTree.getChainLast());
        assertEquals(1, mTree.size());
    }

    @Test
    public void testGetMarkedTreeRoot() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Node<String> rootNode = mTree.getRoot();
        assertNotNull(rootNode);
        assertNull(rootNode.getPrev());
        assertNull(rootNode.getNext());
        assertNull(rootNode.getChild());
        assertEquals(Long.valueOf(0L), rootNode.getMark());
        assertEquals("ROOT", rootNode.getItem());
    }

    @Test
    public void testNewMarkedTreeToUnchainedNode() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.setChain(false);
        assertNull(mTree.getChainParent());
        assertNull(mTree.getChainLast());
    }

    @Test
    public void testGetTreeRootByMark() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Node<String> rootNode = mTree.getNode(Long.valueOf(0L));
        assertNull(rootNode);
    }

    @Test
    public void testAscendOnNewMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        assertFalse(mTree.ascend());
    }

    @Test
    public void testDescendOnNewMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        assertFalse(mTree.descend());
    }

    @Test
    public void testAddItemOnNewMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long mark1 = mTree.add("music");
        Long mark2 = mTree.add("video");
        assertEquals(Long.valueOf(1L), mark1);
        assertEquals(Long.valueOf(2L), mark2);
        assertEquals(3, mTree.size());
    }

    @Test
    public void testMarkedTreeBackToChainedNode() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.add("video");
        
        mTree.setChain(false);
        assertNull(mTree.getChainParent());
        assertNull(mTree.getChainLast());
                
        mTree.setChain(true);
        assertNotNull(mTree.getChainParent());
        assertEquals("ROOT", mTree.getChainParent().getItem());
        assertNotNull(mTree.getChainLast());
        assertEquals("video", mTree.getChainLast().getItem());
    }

    @Test(expected = UnifyException.class)
    public void testAddItemAboveOnNewMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.addAbove(0L, "music");
    }

    @Test(expected = UnifyException.class)
    public void testAddItemBelowOnNewMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.addBelow(0L, "music");
    }

    @Test
    public void testDeepAddItemOnNewMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");      
        // ROOT->music
        // ROOT->music->jazz
        // ROOT->music->blues
        // ROOT->music->blues->The Thrill is Gone
        // ROOT->music->blues->Mannish Boy
        // ROOT->music->rap
        // ROOT->videos
        // ROOT->videos->comedy
        // ROOT->videos->action
        mTree.add("music");
        assertNotNull(mTree.getChainLast());
        assertEquals("music", mTree.getChainLast().getItem());

        assertTrue(mTree.descend());
        assertNotNull(mTree.getChainParent());
        assertEquals("music", mTree.getChainParent().getItem());
        assertNull(mTree.getChainLast());
        
        mTree.add("jazz");
        mTree.add("blues");
        assertNotNull(mTree.getChainLast());
        assertEquals("blues", mTree.getChainLast().getItem());
        
        assertTrue(mTree.descend());
        assertNotNull(mTree.getChainParent());
        assertEquals("blues", mTree.getChainParent().getItem());
        assertNull(mTree.getChainLast());
        
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        assertNotNull(mTree.getChainLast());
        assertEquals("Mannish Boy", mTree.getChainLast().getItem());
        
        assertTrue(mTree.ascend());
        assertNotNull(mTree.getChainParent());
        assertEquals("music", mTree.getChainParent().getItem());
        assertNotNull(mTree.getChainLast());
        assertEquals("blues", mTree.getChainLast().getItem());

        mTree.add("rap");
        assertNotNull(mTree.getChainLast());
        assertEquals("rap", mTree.getChainLast().getItem());
        
        assertTrue(mTree.ascend());
        assertNotNull(mTree.getChainParent());
        assertEquals("ROOT", mTree.getChainParent().getItem());
        assertNotNull(mTree.getChainLast());
        assertEquals("music", mTree.getChainLast().getItem());

        mTree.add("video");
        
        assertTrue(mTree.descend());
        assertNotNull(mTree.getChainParent());
        assertEquals("video", mTree.getChainParent().getItem());
        assertNull(mTree.getChainLast());
        
        mTree.add("comedy");
        mTree.add("action");
        assertNotNull(mTree.getChainLast());
        assertEquals("action", mTree.getChainLast().getItem());

        assertTrue(mTree.ascend());
        assertNotNull(mTree.getChainParent());
        assertEquals("ROOT", mTree.getChainParent().getItem());
        assertNotNull(mTree.getChainLast());
        assertEquals("video", mTree.getChainLast().getItem());
       
        assertFalse(mTree.ascend());

        assertEquals(10, mTree.size());
    }

    @Test
    public void testGetTreeNodeByMarkForDeepAddItem() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        Long videoMark = mTree.add("video");
        mTree.descend();
        mTree.add("comedy");
        mTree.add("action");
        
        // Music
        assertEquals(Long.valueOf(1L), musicMark);
        Node<String> music = mTree.getNode(musicMark);
        assertNotNull(music);
        assertEquals("music", music.getItem());
        assertNotNull(music.getPrev());
        assertEquals("ROOT", music.getPrev().getItem());
        assertNotNull(music.getNext());
        assertEquals("video", music.getNext().getItem());
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("jazz", music.getChild().getNext().getPrev().getItem());
        assertEquals("blues", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("blues", music.getChild().getNext().getNext().getPrev().getItem());
        assertEquals("rap", music.getChild().getNext().getNext().getItem());
        assertNull(music.getChild().getNext().getNext().getNext());
        
        // Blues
        assertEquals(Long.valueOf(3L), bluesMark);
        Node<String> blues = mTree.getNode(bluesMark);
        assertNotNull(blues);
        assertEquals("blues", blues.getItem());
        assertNotNull(blues.getPrev());
        assertEquals("jazz", blues.getPrev().getItem());
        assertNotNull(blues.getNext());
        assertEquals("rap", blues.getNext().getItem());
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getPrev());
        assertEquals("blues", blues.getChild().getPrev().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
        assertNotNull(blues.getChild().getNext().getPrev());
        assertEquals("The Thrill is Gone", blues.getChild().getNext().getPrev().getItem());
        
        // Video
        assertEquals(Long.valueOf(7L), videoMark);
        Node<String> video = mTree.getNode(videoMark);
        assertNotNull(video);
        assertEquals("video", video.getItem());
        assertNotNull(video.getPrev());
        assertEquals("music", video.getPrev().getItem());
        assertNull(video.getNext());
        assertNotNull(video.getChild());
        assertEquals("comedy", video.getChild().getItem());
        assertNotNull(video.getChild().getPrev());
        assertEquals("video", video.getChild().getPrev().getItem());
        assertNotNull(video.getChild().getNext());
        assertEquals("action", video.getChild().getNext().getItem());
        assertNotNull(video.getChild().getNext().getPrev());
        assertEquals("comedy", video.getChild().getNext().getPrev().getItem());   
    }
    
    @Test
    public void testAddItemAbove() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        Long thrillMark = mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Add items above
        Long videoMark = mTree.addAbove(musicMark, "video");
        assertEquals(Long.valueOf(7L), videoMark);
        Long spoonfulMark = mTree.addAbove(mannishMark, "Spoonful");
        assertEquals(Long.valueOf(8L), spoonfulMark);
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("video", root.getChild().getItem());
        assertNotNull(root.getChild().getNext());
        assertEquals("music", root.getChild().getNext().getItem());
        assertNotNull(root.getChild().getNext().getPrev());
        assertEquals("video", root.getChild().getNext().getPrev().getItem());
        assertNull(root.getChild().getNext().getNext());
        
        Node<String> thrill =  mTree.getNode(thrillMark);
        assertNotNull(thrill.getNext());
        assertEquals("Spoonful", thrill.getNext().getItem());
        assertNotNull(thrill.getNext().getPrev());
        assertEquals("The Thrill is Gone", thrill.getNext().getPrev().getItem());
        
        assertNotNull(thrill.getNext().getNext());
        assertEquals("Mannish Boy", thrill.getNext().getNext().getItem());
        assertNotNull(thrill.getNext().getNext().getPrev());
        assertEquals("Spoonful", thrill.getNext().getNext().getPrev().getItem());
    }
    
    @Test
    public void testAddItemBelow() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        Long thrillMark = mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        Long rapMark = mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Add items below
        Long videoMark = mTree.addBelow(musicMark, "video");
        assertEquals(Long.valueOf(7L), videoMark);
        Long spoonfulMark = mTree.addBelow(thrillMark, "Spoonful");
        assertEquals(Long.valueOf(8L), spoonfulMark);
        Long hiphopMark = mTree.addBelow(rapMark, "hiphop");
        assertEquals(Long.valueOf(9L), hiphopMark);
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNotNull(root.getChild().getNext());
        assertEquals("video", root.getChild().getNext().getItem());
        assertNotNull(root.getChild().getNext().getPrev());
        assertEquals("music", root.getChild().getNext().getPrev().getItem());
        assertNull(root.getChild().getNext().getNext());
        
        Node<String> thrill =  mTree.getNode(thrillMark);
        assertNotNull(thrill.getNext());
        assertEquals("Spoonful", thrill.getNext().getItem());
        assertNotNull(thrill.getNext().getPrev());
        assertEquals("The Thrill is Gone", thrill.getNext().getPrev().getItem());
        
        assertNotNull(thrill.getNext().getNext());
        assertEquals("Mannish Boy", thrill.getNext().getNext().getItem());
        assertNotNull(thrill.getNext().getNext().getPrev());
        assertEquals("Spoonful", thrill.getNext().getNext().getPrev().getItem());
    }

    @Test
    public void testAddChild() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        Long jazzMark = mTree.add("jazz");
        mTree.add("blues");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Add child items
        mTree.addChild(musicMark, "rap");
        mTree.addChild(jazzMark, "So What");
        mTree.addChild(jazzMark, "Take Five");
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNull(root.getChild().getNext());
        
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("blues", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("rap", music.getChild().getNext().getNext().getItem());
        assertNull(music.getChild().getNext().getNext().getNext());
        
        Node<String> jazz =  mTree.getNode(jazzMark);
        assertNotNull(jazz.getChild());
        assertEquals("So What", jazz.getChild().getItem());
        assertNotNull(jazz.getChild().getNext());
        assertEquals("Take Five", jazz.getChild().getNext().getItem());
    }

    @Test
    public void testAddChildAboveWithPolicy() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        Long jazzMark = mTree.add("jazz");
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Add child items
        TestAddChildPolicy1 testAddChildPolicy1 = new TestAddChildPolicy1();
        mTree.addChild(musicMark, "blues", testAddChildPolicy1);
        mTree.addChild(musicMark, "hiphop", testAddChildPolicy1);
        mTree.addChild(musicMark, "zelt", testAddChildPolicy1);
        mTree.addChild(jazzMark, "Take Five", testAddChildPolicy1);
        mTree.addChild(jazzMark, "So What", testAddChildPolicy1);
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNull(root.getChild().getNext());
        
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("blues", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("hiphop", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("jazz", music.getChild().getNext().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext().getNext());
        assertEquals("rap", music.getChild().getNext().getNext().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext().getNext().getNext());
        assertEquals("zelt", music.getChild().getNext().getNext().getNext().getNext().getItem());
        
        Node<String> jazz =  mTree.getNode(jazzMark);
        assertNotNull(jazz.getChild());
        assertEquals("So What", jazz.getChild().getItem());
        assertNotNull(jazz.getChild().getNext());
        assertEquals("Take Five", jazz.getChild().getNext().getItem());
    }

    @Test
    public void testAddChildBelowWithPolicy() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        Long jazzMark = mTree.add("jazz");
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Add child items
        TestAddChildPolicy2 testAddChildPolicy2 = new TestAddChildPolicy2();
        mTree.addChild(musicMark, "blues", testAddChildPolicy2);
        mTree.addChild(musicMark, "hiphop", testAddChildPolicy2);
        mTree.addChild(musicMark, "zelt", testAddChildPolicy2);
        mTree.addChild(jazzMark, "Take Five", testAddChildPolicy2);
        mTree.addChild(jazzMark, "So What", testAddChildPolicy2);
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNull(root.getChild().getNext());
        
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("hiphop", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("blues", music.getChild().getNext().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext().getNext());
        assertEquals("rap", music.getChild().getNext().getNext().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext().getNext().getNext());
        assertEquals("zelt", music.getChild().getNext().getNext().getNext().getNext().getItem());
        
        Node<String> jazz =  mTree.getNode(jazzMark);
        assertNotNull(jazz.getChild());
        assertEquals("Take Five", jazz.getChild().getItem());
        assertNotNull(jazz.getChild().getNext());
        assertEquals("So What", jazz.getChild().getNext().getItem());
    }
    
    @Test
    public void testMoveAbove() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        Long jazzMark = mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        Long trillMark = mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item above
        assertTrue(mTree.moveAbove(jazzMark, bluesMark));
        assertTrue(mTree.moveAbove(trillMark, mannishMark));
        
        // Validate
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("blues", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("jazz", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("rap", music.getChild().getNext().getNext().getItem());
        assertNull(music.getChild().getNext().getNext().getNext());
        
        Node<String> blues =  mTree.getNode(bluesMark);
        assertNotNull(blues.getChild());
        assertEquals("Mannish Boy", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("The Thrill is Gone", blues.getChild().getNext().getItem());
    }
    
    @Test
    public void testMoveAboveAndAcrossLevel() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item above
        assertTrue(mTree.moveAbove(musicMark, bluesMark));
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("blues", root.getChild().getItem());
        assertNotNull(root.getChild().getNext());
        assertEquals("music", root.getChild().getNext().getItem());
        assertNull(root.getChild().getNext().getNext());
        
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("rap", music.getChild().getNext().getItem());
        assertNull(music.getChild().getNext().getNext());
        
        Node<String> blues =  mTree.getNode(bluesMark);
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
    }
    
    @Test
    public void testMoveAncestorAbove() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item above
        assertFalse(mTree.moveAbove(bluesMark, musicMark));
        assertFalse(mTree.moveAbove(mannishMark, bluesMark));
    }
    
    @Test
    public void testMoveAboveSame() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item above
        assertFalse(mTree.moveAbove(bluesMark, bluesMark));
        assertFalse(mTree.moveAbove(mannishMark, mannishMark));
    }
    
    
    @Test
    public void testMoveBelow() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        Long trillMark = mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        Long rapMark = mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item below
        assertTrue(mTree.moveBelow(rapMark, bluesMark));
        assertTrue(mTree.moveBelow(trillMark, mannishMark));
        
        // Validate
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("rap", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("blues", music.getChild().getNext().getNext().getItem());
        assertNull(music.getChild().getNext().getNext().getNext());
        
        Node<String> blues =  mTree.getNode(bluesMark);
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
    }
    
    @Test
    public void testMoveBelowAndAcrossLevel() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item below
        assertTrue(mTree.moveBelow(musicMark, bluesMark));
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNotNull(root.getChild().getNext());
        assertEquals("blues", root.getChild().getNext().getItem());
        assertNull(root.getChild().getNext().getNext());
        
        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("rap", music.getChild().getNext().getItem());
        assertNull(music.getChild().getNext().getNext());
        
        Node<String> blues =  mTree.getNode(bluesMark);
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
    }
    
    @Test
    public void testMoveAncestorBelow() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item below
        assertFalse(mTree.moveBelow(bluesMark, musicMark));
        assertFalse(mTree.moveBelow(mannishMark, bluesMark));
    }
    
    @Test
    public void testMoveBelowSame() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        Long mannishMark = mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item below
        assertFalse(mTree.moveBelow(bluesMark, bluesMark));
        assertFalse(mTree.moveBelow(mannishMark, mannishMark));
    }
    
    @Test
    public void testMoveAsChild() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        Long videoMark = mTree.add("video");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Move item below
        assertTrue(mTree.moveAsChild(videoMark, bluesMark));
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNotNull(root.getChild().getNext());
        assertEquals("video", root.getChild().getNext().getItem());
        assertNull(root.getChild().getNext().getNext());

        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("rap", music.getChild().getNext().getItem());
        assertNull(music.getChild().getNext().getNext());

        Node<String> video =  mTree.getNode(videoMark);
        assertNotNull(video);
        assertNotNull(video.getChild());
        assertEquals("blues", video.getChild().getItem());
        assertNull(video.getChild().getNext());
        
        Node<String> blues =  mTree.getNode(bluesMark);
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
    }

    @Test
    public void testFindNodeFromRootBlank() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        assertNull(mTree.findNode(new TestMatcher1("music")));
    }

    @Test
    public void testFindNodeFromRoot() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        mTree.add("video");
        
        Node<String> music = mTree.findNode(new TestMatcher1("music"));
        assertNotNull(music);
        assertEquals("music", music.getItem());
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("blues", music.getChild().getNext().getItem());
        assertNotNull(music.getChild().getNext().getNext());
        assertEquals("rap", music.getChild().getNext().getNext().getItem());
        assertNull(music.getChild().getNext().getNext().getNext());
        
        Node<String> video = mTree.findNode(new TestMatcher1("video"));
        assertNotNull(video);
        assertEquals("video", video.getItem());
        assertNull(video.getChild());
    }

    @Test
    public void testFindNodeDeepFromRoot() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        mTree.add("video");
        
        Node<String> blues = mTree.findNode(new TestMatcher1("blues"));
        assertNotNull(blues);
        assertEquals("blues", blues.getItem());
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
        assertNull(blues.getChild().getNext().getNext());

        Node<String> mannish = mTree.findNode(new TestMatcher1("Mannish Boy"));
        assertNotNull(mannish);
        assertEquals("Mannish Boy", mannish.getItem());
        assertNull(mannish.getChild());
    }

    @Test
    public void testFindNodeFromRootNoMatch() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        mTree.add("video");
        
        assertNull(mTree.findNode(new TestMatcher1("hiphop")));
    }

    @Test
    public void testFindNodeFromMark() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        Long videoMark = mTree.add("video");
        
        Node<String> blues = mTree.findNode(musicMark, new TestMatcher1("blues"));
        assertNotNull(blues);
        assertEquals("blues", blues.getItem());
        assertNotNull(blues.getChild());
        assertEquals("The Thrill is Gone", blues.getChild().getItem());
        assertNotNull(blues.getChild().getNext());
        assertEquals("Mannish Boy", blues.getChild().getNext().getItem());
        assertNull(blues.getChild().getNext().getNext());

        Node<String> mannish = mTree.findNode(bluesMark, new TestMatcher1("Mannish Boy"));
        assertNotNull(mannish);
        assertEquals("Mannish Boy", mannish.getItem());
        assertNull(mannish.getChild());
        
        // Match at start point
        Node<String> video = mTree.findNode(videoMark, new TestMatcher1("video"));
        assertNotNull(video);
        assertEquals("video", video.getItem());
    }

    @Test
    public void testFindNodeFromMarkNoMatch() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        mTree.add("video");
        
        // No existent mark
        assertNull(mTree.findNode(Long.valueOf(99L), new TestMatcher1("video")));
        
        // Not in branch
        assertNull(mTree.findNode(bluesMark, new TestMatcher1("music")));
        
        // Unmatched
        assertNull(mTree.findNode(musicMark, new TestMatcher1("hiphop")));
    }

    @Test
    public void testFindNodesFromRoot() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.descend();
        mTree.add("No Man's Land");
        mTree.add("The Beast");
        mTree.ascend();
        mTree.ascend();
        mTree.add("video");
        
        List<Node<String>> nodeList = mTree.findNodes(new TestMatcher2("The"));
        assertNotNull(nodeList);
        assertEquals(2, nodeList.size());
        assertNotNull(nodeList.get(0));
        assertEquals("The Thrill is Gone", nodeList.get(0).getItem());
        assertNotNull(nodeList.get(1));
        assertEquals("The Beast", nodeList.get(1).getItem());
    }

    @Test
    public void testFindNodesFromMark() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        mTree.add("music");
        mTree.descend();
        Long jazzMark = mTree.add("jazz");
        mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        Long rapMark = mTree.add("rap");
        mTree.descend();
        mTree.add("No Man's Land");
        mTree.add("The Beast");
        mTree.ascend();
        mTree.ascend();
        mTree.add("video");
        
        List<Node<String>> nodeList = mTree.findNodes(jazzMark, new TestMatcher2("No"));
        assertNotNull(nodeList);
        assertEquals(0, nodeList.size());
        
        nodeList = mTree.findNodes(rapMark, new TestMatcher2("No"));
        assertNotNull(nodeList);
        assertEquals(1, nodeList.size());
        assertNotNull(nodeList.get(0));
        assertEquals("No Man's Land", nodeList.get(0).getItem());
    }

    @Test
    public void testUpdateFromRootNewTree() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));

        // Select all
        mTree.updateNodes(new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertTrue(root.getItem().isSelected());
    }

    @Test
    public void testUpdateFromRoot() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));
        mTree.add(new TestFood("fruit"));
        mTree.descend();
        mTree.add(new TestFood("apple"));
        mTree.add(new TestFood("bananna"));
        mTree.add(new TestFood("apricot"));
        mTree.add(new TestFood("orange"));
        mTree.ascend();
        mTree.add(new TestFood("vegetable"));
        mTree.descend();
        mTree.add(new TestFood("tomato"));
        mTree.add(new TestFood("asparagus"));


        // Select all
        mTree.updateNodes(new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertTrue(root.getItem().isSelected());

        Node<TestFood> fruit = root.getChild();
        assertTrue(fruit.getItem().isSelected());
        assertTrue(fruit.getChild().getItem().isSelected());
        assertTrue(fruit.getChild().getNext().getItem().isSelected());
        assertTrue(fruit.getChild().getNext().getNext().getItem().isSelected());
        assertTrue(fruit.getChild().getNext().getNext().getNext().getItem().isSelected());

        Node<TestFood> vegetable = root.getChild().getNext();
        assertTrue(vegetable.getItem().isSelected());
        assertTrue(vegetable.getChild().getItem().isSelected());
        assertTrue(vegetable.getChild().getNext().getItem().isSelected());
    }

    @Test
    public void testUpdateFromRootWithMatch() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));
        mTree.add(new TestFood("fruit"));
        mTree.descend();
        mTree.add(new TestFood("apple"));
        mTree.add(new TestFood("bananna"));
        mTree.add(new TestFood("apricot"));
        mTree.add(new TestFood("orange"));
        mTree.ascend();
        mTree.add(new TestFood("vegetable"));
        mTree.descend();
        mTree.add(new TestFood("tomato"));
        mTree.add(new TestFood("asparagus"));


        // Select all with name starting with "a"
        mTree.updateNodes(new TestMatcher3("a"), new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertFalse(root.getItem().isSelected());

        Node<TestFood> fruit = root.getChild();
        assertFalse(fruit.getItem().isSelected());
        assertTrue(fruit.getChild().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getItem().isSelected());
        assertTrue(fruit.getChild().getNext().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getNext().getItem().isSelected());

        Node<TestFood> vegetable = root.getChild().getNext();
        assertFalse(vegetable.getItem().isSelected());
        assertFalse(vegetable.getChild().getItem().isSelected());
        assertTrue(vegetable.getChild().getNext().getItem().isSelected());
    }

    @Test
    public void testUpdateFromMark() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));
        mTree.add(new TestFood("fruit"));
        mTree.descend();
        mTree.add(new TestFood("apple"));
        mTree.add(new TestFood("bananna"));
        mTree.add(new TestFood("apricot"));
        Long orangeMark = mTree.add(new TestFood("orange"));
        mTree.ascend();
        Long vegMark = mTree.add(new TestFood("vegetable"));
        mTree.descend();
        mTree.add(new TestFood("tomato"));
        mTree.add(new TestFood("asparagus"));


        // Select all from specific nodes
        mTree.updateNodes(orangeMark, new TestUpdateChildPolicy1());
        mTree.updateNodes(vegMark, new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertFalse(root.getItem().isSelected());

        Node<TestFood> fruit = root.getChild();
        assertFalse(fruit.getItem().isSelected());
        assertFalse(fruit.getChild().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getItem().isSelected());
        assertTrue(fruit.getChild().getNext().getNext().getNext().getItem().isSelected());

        Node<TestFood> vegetable = root.getChild().getNext();
        assertTrue(vegetable.getItem().isSelected());
        assertTrue(vegetable.getChild().getItem().isSelected());
        assertTrue(vegetable.getChild().getNext().getItem().isSelected());
    }

    @Test
    public void testUpdateFromMarkWithMatch() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));
        Long fruitMark = mTree.add(new TestFood("fruit"));
        mTree.descend();
        mTree.add(new TestFood("apple"));
        mTree.add(new TestFood("bananna"));
        mTree.add(new TestFood("apricot"));
        mTree.add(new TestFood("orange"));
        mTree.ascend();
        Long vegMark = mTree.add(new TestFood("vegetable"));
        mTree.descend();
        mTree.add(new TestFood("tomato"));
        mTree.add(new TestFood("asparagus"));


        // Select all from specific nodes
        mTree.updateNodes(fruitMark, new TestMatcher3("a"), new TestUpdateChildPolicy1());
        mTree.updateNodes(vegMark, new TestMatcher3("t"), new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertFalse(root.getItem().isSelected());

        Node<TestFood> fruit = root.getChild();
        assertFalse(fruit.getItem().isSelected());
        assertTrue(fruit.getChild().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getItem().isSelected());
        assertTrue(fruit.getChild().getNext().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getNext().getItem().isSelected());

        Node<TestFood> vegetable = root.getChild().getNext();
        assertFalse(vegetable.getItem().isSelected());
        assertTrue(vegetable.getChild().getItem().isSelected());
        assertFalse(vegetable.getChild().getNext().getItem().isSelected());
    }

    @Test
    public void testUpdateParentsFromMark() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));
        mTree.add(new TestFood("fruit"));
        mTree.descend();
        mTree.add(new TestFood("apple"));
        mTree.add(new TestFood("bananna"));
        mTree.add(new TestFood("apricot"));
        mTree.add(new TestFood("orange"));
        mTree.ascend();
        mTree.add(new TestFood("vegetable"));
        mTree.descend();
        mTree.add(new TestFood("tomato"));
        Long aspMark =  mTree.add(new TestFood("asparagus"));


        // Select all parents from specific node
        mTree.updateParentNodes(aspMark, new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertTrue(root.getItem().isSelected());

        Node<TestFood> fruit = root.getChild();
        assertFalse(fruit.getItem().isSelected());
        assertFalse(fruit.getChild().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getNext().getItem().isSelected());

        Node<TestFood> vegetable = root.getChild().getNext();
        assertTrue(vegetable.getItem().isSelected());
        assertFalse(vegetable.getChild().getItem().isSelected());
        assertFalse(vegetable.getChild().getNext().getItem().isSelected());
    }

    @Test
    public void testUpdateParentsFromMarkWithMatch() throws Exception {
        MarkedTree<TestFood> mTree = new MarkedTree<TestFood>(new TestFood("food"));
        mTree.add(new TestFood("fruit"));
        mTree.descend();
        mTree.add(new TestFood("apple"));
        mTree.add(new TestFood("bananna"));
        mTree.add(new TestFood("apricot"));
        mTree.add(new TestFood("orange"));
        mTree.ascend();
        mTree.add(new TestFood("vegetable"));
        mTree.descend();
        mTree.add(new TestFood("tomato"));
        Long aspMark =  mTree.add(new TestFood("asparagus"));


        // Select all parents from specific node
        mTree.updateParentNodes(aspMark, new TestMatcher3("v"), new TestUpdateChildPolicy1());
        
        // Validate
        Node<TestFood> root = mTree.getRoot();
        assertFalse(root.getItem().isSelected());

        Node<TestFood> fruit = root.getChild();
        assertFalse(fruit.getItem().isSelected());
        assertFalse(fruit.getChild().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getItem().isSelected());
        assertFalse(fruit.getChild().getNext().getNext().getNext().getItem().isSelected());

        Node<TestFood> vegetable = root.getChild().getNext();
        assertTrue(vegetable.getItem().isSelected());
        assertFalse(vegetable.getChild().getItem().isSelected());
        assertFalse(vegetable.getChild().getNext().getItem().isSelected());
    }
    
    @Test
    public void testRemoveByUnknownMark() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");

        // Enter unchained mode
        mTree.setChain(false);
        
        // Remove
        Node<String> rem = mTree.remove(Long.valueOf(1L));
        assertNull(rem);
    }
    
    @Test
    public void testRemoveByMark() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark = mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        mTree.add("video");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Remove
        Node<String> rem = mTree.remove(bluesMark);
        assertNotNull(rem);
        assertEquals("blues", rem.getItem());
        assertNotNull(rem.getChild());
        assertEquals("The Thrill is Gone", rem.getChild().getItem());
        assertNotNull(rem.getChild().getNext());
        assertEquals("Mannish Boy", rem.getChild().getNext().getItem());
        
        assertNull(mTree.getNode(bluesMark));
        
        // Validate
        Node<String> root =  mTree.getRoot();
        assertNotNull(root.getChild());
        assertEquals("music", root.getChild().getItem());
        assertNotNull(root.getChild().getNext());
        assertEquals("video", root.getChild().getNext().getItem());
        assertNull(root.getChild().getNext().getNext());

        Node<String> music =  mTree.getNode(musicMark);
        assertNotNull(music.getChild());
        assertEquals("jazz", music.getChild().getItem());
        assertNotNull(music.getChild().getNext());
        assertEquals("rap", music.getChild().getNext().getItem());
        assertNull(music.getChild().getNext().getNext());
    }
    

    @Test
    public void testClearMarkedTree() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        Long jazzMark = mTree.add("jazz");
        mTree.add("blues");
        
        // Enter unchained mode
        mTree.setChain(false);

        // Add child items
        mTree.addChild(musicMark, "rap");
        mTree.addChild(jazzMark, "So What");
        mTree.addChild(jazzMark, "Take Five");
        
        // Clear
        mTree.clear();
        assertTrue(mTree.isChain());
        assertNotNull(mTree.getChainParent());
        assertEquals("ROOT", mTree.getChainParent().getItem());
        assertNull(mTree.getChainLast());
        assertEquals(1, mTree.size());
    }
    
    
    @Test
    public void testNodeGetChildItemList() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark =  mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        Long videoMark = mTree.add("video");
        
        Node<String> root = mTree.getRoot();
        List<String> rootList = root.getChildItemList();
        assertNotNull(rootList);
        assertEquals(2, rootList.size());
        assertEquals("music", rootList.get(0));
        assertEquals("video", rootList.get(1));
        
        Node<String> music = mTree.getNode(musicMark);
        List<String> musicList = music.getChildItemList();
        assertNotNull(musicList);
        assertEquals(3, musicList.size());
        assertEquals("jazz", musicList.get(0));
        assertEquals("blues", musicList.get(1));
        assertEquals("rap", musicList.get(2));
        
        Node<String> blues = mTree.getNode(bluesMark);
        List<String> bluesList = blues.getChildItemList();
        assertNotNull(bluesList);
        assertEquals(2, bluesList.size());
        assertEquals("The Thrill is Gone", bluesList.get(0));
        assertEquals("Mannish Boy", bluesList.get(1));
        
        Node<String> video = mTree.getNode(videoMark);
        List<String> videoList = video.getChildItemList();
        assertNotNull(videoList);
        assertEquals(0, videoList.size());
    }
    
    
    @Test
    public void testNodeGetChildItemListWithMatching() throws Exception {
        MarkedTree<String> mTree = new MarkedTree<String>("ROOT");
        Long musicMark = mTree.add("music");
        mTree.descend();
        mTree.add("jazz");
        Long bluesMark =  mTree.add("blues");
        mTree.descend();
        mTree.add("The Thrill is Gone");
        mTree.add("Mannish Boy");
        mTree.ascend();
        mTree.add("rap");
        mTree.ascend();
        mTree.add("video");
        
        Node<String> root = mTree.getRoot();
        List<String> rootList = root.getChildItemList(new TestMatcher1("video"));
        assertNotNull(rootList);
        assertEquals(1, rootList.size());
        assertEquals("video", rootList.get(0));
        
        Node<String> music = mTree.getNode(musicMark);
        List<String> musicList = music.getChildItemList(new TestMatcher1("blues"));
        assertNotNull(musicList);
        assertEquals(1, musicList.size());
        assertEquals("blues", musicList.get(0));
        
        Node<String> blues = mTree.getNode(bluesMark);
        List<String> bluesList = blues.getChildItemList(new TestMatcher1("White Skull"));
        assertNotNull(bluesList);
        assertEquals(0, bluesList.size());
    }
}

class TestMatcher1 implements Matcher<String> {

    private String matchStr;
    
    public TestMatcher1(String matchStr) {
        this.matchStr = matchStr;
    }
    
    @Override
    public boolean match(String item) {
        return matchStr.equals(item);
    }
    
}

class TestMatcher2 implements Matcher<String> {

    private String beginStr;
    
    public TestMatcher2(String beginStr) {
        this.beginStr = beginStr;
    }

    @Override
    public boolean match(String item) {
        return item.startsWith(beginStr);
    }
    
}

class TestAddChildPolicy1 implements AddChildPolicy<String> {

    @Override
    public int addDecision(String targetItem, String childItem) {
        if (!StringUtils.isBlank(targetItem) && !StringUtils.isBlank(childItem)) {
            if (childItem.compareTo(targetItem) < 0) {
                return -1;
            }
        }
        return 0;
    }  
}

class TestAddChildPolicy2 implements AddChildPolicy<String> {

    @Override
    public int addDecision(String targetItem, String childItem) {
        if (!StringUtils.isBlank(targetItem) && !StringUtils.isBlank(childItem)) {
            if (childItem.compareTo(targetItem) < 0) {
                return 1;
            }
        }
        return 0;
    }  
}

class TestMatcher3 implements Matcher<TestFood> {

    private String beginStr;
    
    public TestMatcher3(String beginStr) {
        this.beginStr = beginStr;
    }

    @Override
    public boolean match(TestFood item) {
        return item.getName().startsWith(beginStr);
    }
    
}

class TestUpdateChildPolicy1 implements UpdateChildPolicy<TestFood> {

    @Override
    public void update(TestFood childItem) {
        childItem.setSelected(true); 
    }
    
}

class TestFood {
    
    private String name;
    
    private boolean selected;

    public TestFood(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }
    
}


