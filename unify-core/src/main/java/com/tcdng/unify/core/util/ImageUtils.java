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
package com.tcdng.unify.core.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

/**
 * Provides utility methods for Image processing.
 * 
 * @author Lateef Ojulari
 */
public class ImageUtils {

    private ImageUtils() {

    }

    /**
     * Converts image to JPEG image
     * 
     * @param image
     *            the image to convert
     * @return byte[] - the JPEG image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToJPEG(byte[] image) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "jpg", outstream);
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }
        return result;
    }

    /**
     * Converts image to PNG image
     * 
     * @param image
     *            the image to convert
     * @return byte[] - the PNG image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToPNG(byte[] image) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "png", outstream);
            bi = null;
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
            instream = null;
            outstream = null;
        }

        return result;
    }

    /**
     * Converts image to GIF image
     * 
     * @param image
     *            the image to convert
     * @return byte[] - the GIF image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToGIF(byte[] image) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "gif", outstream);
            bi = null;
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
            instream = null;
            outstream = null;
        }

        return result;
    }

    /**
     * Converts image to TIF image
     * 
     * @param image
     *            the image to convert
     * @return byte[] - the TIFF image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToTIF(byte[] image) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "tif", outstream);
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }

        return result;
    }

    /**
     * Converts image to JPEG image
     * 
     * @param image
     *            the image to convert
     * @param offset
     *            where to read from
     * @param length
     *            length to read
     * @return byte[] - the JPEG image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToJPEG(byte[] image, int offset, int length) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image, offset, length);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "jpg", outstream);
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }

        return result;
    }

    /**
     * Converts image to PNG image
     * 
     * @param image
     *            the image to convert
     * @param offset
     *            where to read from
     * @param length
     *            length to read
     * @return byte[] - the PNG image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToPNG(byte[] image, int offset, int length) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image, offset, length);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "png", outstream);
            bi = null;
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
            instream = null;
            outstream = null;
        }

        return result;
    }

    /**
     * Converts image to GIF image
     * 
     * @param image
     *            the image to convert
     * @param offset
     *            where to read from
     * @param length
     *            length to read
     * @return byte[] - the GIF image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToGIF(byte[] image, int offset, int length) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image, offset, length);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "gif", outstream);
            bi = null;
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
            instream = null;
            outstream = null;
        }

        return result;
    }

    /**
     * Converts image to TIF image
     * 
     * @param image
     *            the image to convert
     * @param offset
     *            where to read from
     * @param length
     *            length to read
     * @return byte[] - the TIFF image
     * @throws Exception
     *             - If an error exists
     */
    public static byte[] convertToTIF(byte[] image, int offset, int length) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        try {
            instream = new ByteArrayInputStream(image, offset, length);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            ImageIO.write(bi, "tif", outstream);
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }

        return result;
    }

    /**
     * Flips image horizontally and returns image in JPEG
     * 
     * @param image
     *            the image to flip
     * @return the flipped image
     * @throws Exception
     *             if an erro occurs
     */
    public static byte[] flipJPEGHorizontally(byte[] image) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        Graphics2D g = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            int w = bi.getWidth();
            int h = bi.getHeight();

            BufferedImage nbi = new BufferedImage(w, h, bi.getType());
            g = nbi.createGraphics();
            g.drawImage(bi, 0, 0, w, h, 0, h, w, 0, null);
            ImageIO.write(nbi, "jpg", outstream);
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (g != null) {
                try {
                    g.dispose();
                } catch (Exception e) {
                }
            }
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }

        return result;
    }

    /**
     * Flips image vertically and returns image in JPEG
     * 
     * @param image
     *            the image to flip
     * @return the flipped image
     * @throws Exception
     *             if an erro occurs
     */
    public static byte[] flipJPEGVertically(byte[] image, float quality) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        Graphics2D g = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            int w = bi.getWidth();
            int h = bi.getHeight();

            BufferedImage nbi = new BufferedImage(w, h, bi.getType());
            g = nbi.createGraphics();
            g.drawImage(bi, 0, 0, w, h, w, 0, 0, h, null);
            ImageIO.write(nbi, "jpg", outstream);
            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (g != null) {
                try {
                    g.dispose();
                } catch (Exception e) {
                }
            }
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }

        return result;
    }

    /**
     * Rotates image by angle and returns image in JPEG
     * 
     * @param image
     *            the image to rotate
     * @param angle
     *            the angle to rotate by in degrees
     * @return the rotated image
     * @throws Exception
     *             if an error occurs
     */
    public static byte[] rotateJPEG(byte[] image, double angle) throws Exception {
        ImageIO.setUseCache(false);
        byte[] result = image;
        ByteArrayInputStream instream = null;
        ByteArrayOutputStream outstream = null;
        Graphics2D g = null;
        try {
            instream = new ByteArrayInputStream(image);
            outstream = new ByteArrayOutputStream();
            BufferedImage bi = ImageIO.read(instream);
            int w = bi.getWidth();
            int h = bi.getHeight();

            BufferedImage nbi = new BufferedImage(w, h, bi.getType());
            g = nbi.createGraphics();
            g.rotate(Math.toRadians(angle), w / 2, h / 2);
            g.drawImage(bi, null, 0, 0);
            ImageIO.write(nbi, "jpg", outstream);

            result = outstream.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (g != null) {
                try {
                    g.dispose();
                } catch (Exception e) {
                }
            }
            IOUtils.close(instream);
            IOUtils.close(outstream);
        }
        return result;
    }
}
