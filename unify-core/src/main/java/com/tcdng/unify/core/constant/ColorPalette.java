/*
 * Copyright 2018-2023 The Code Department.
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

package com.tcdng.unify.core.constant;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Color palette.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ColorPalette {
    
    RED_SCALE,
    BLUE_SCALE,
    GREEN_SCALE,
    YELLOW_SCALE,
    ORANGE_SCALE,
    GRAY_SCALE,
    DEFAULT,
    CUSTOM;

    private static final Map<ColorPalette, List<Color>> palettes;

    static {
        Map<ColorPalette, List<Color>> map = new HashMap<ColorPalette, List<Color>>();
        map.put(ColorPalette.RED_SCALE,
                Collections.unmodifiableList(
                        Arrays.asList(Color.decode("#D04343"), Color.decode("#EA5D5D"), Color.decode("#FF7676"),
                                Color.decode("#FF9090"), Color.decode("#FFA9A9"), Color.decode("#FFC2C2"))));
        map.put(ColorPalette.BLUE_SCALE,
                Collections.unmodifiableList(
                        Arrays.asList(Color.decode("#4388D0"), Color.decode("#5DA2EA"), Color.decode("#76BBFF"),
                                Color.decode("#90D5FF"), Color.decode("#A9EEFF"), Color.decode("#C2FFFF"))));
        map.put(ColorPalette.GREEN_SCALE,
                Collections.unmodifiableList(
                        Arrays.asList(Color.decode("#43D088"), Color.decode("#5DEAA2"), Color.decode("#76FFBB"),
                                Color.decode("#90FFD5"), Color.decode("#A9FFEE"), Color.decode("#C2FFF8"))));
        map.put(ColorPalette.YELLOW_SCALE,
                Collections.unmodifiableList(
                        Arrays.asList(Color.decode("#D0C800"), Color.decode("#D9D333"), Color.decode("#E2DE66"),
                                Color.decode("#ECE999"), Color.decode("#F5F4CC"), Color.decode("#FAF9E5"))));
        map.put(ColorPalette.ORANGE_SCALE,
                Collections.unmodifiableList(
                        Arrays.asList(Color.decode("#D08843"), Color.decode("#EAA25D"), Color.decode("#FFBB76"),
                                Color.decode("#FFD590"), Color.decode("#FFEEA9"), Color.decode("#FFF8C2"))));
        map.put(ColorPalette.GRAY_SCALE,
                Collections.unmodifiableList(
                        Arrays.asList(Color.decode("#666666"), Color.decode("#808080"), Color.decode("#999999"),
                                Color.decode("#B3B3B3"), Color.decode("#CCCCCC"), Color.decode("#E5E5E5"))));
        palettes = Collections.unmodifiableMap(map);
    }

    public List<Color> pallete() {
        if (isCustom() || isDefault()) {
            return Collections.emptyList();
        }
        
        return palettes.get(this);
    }
    
    public Color getShade() {
        return getShade(0);
    }
    
    public Color getShade(int shadeIndex) {
        if (isCustom() || isDefault()) {
            return null;
        }
        
        if (shadeIndex >= 0 && shadeIndex <= 5) {
            return palettes.get(this).get(shadeIndex);
        }
        
        return null;
    }
    
    public boolean isCustom() {
        return CUSTOM.equals(this);
    }
    
    public boolean isDefault() {
        return DEFAULT.equals(this);
    }
}