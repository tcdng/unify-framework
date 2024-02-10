/*
 * Copyright 2018-2024 The Code Department.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.constant.PrintFormat;

/**
 * Provides utility methods for IO.
 * 
 * @author The Code Department
 */
public class IOUtils {

    private static final int BUFFER_SIZE = 1024 * 4;

    private static final File[] ZEROLEN_FILES = new File[0];

    private static boolean restrictedJARMode;
    
    private IOUtils() {

    }

    public static void enterRestrictedJARMode() {
        restrictedJARMode = true;
    }
    
    /**
     * Detects an opens an input stream for a streamable object.
     * 
     * @param streamable
     *                   the streamable object
     * @return opened inputstream on successful detection otherwise a null
     * @throws UnifyException
     *                        if an error occurs
     */
    public static InputStream detectAndOpenInputStream(Object streamable) throws UnifyException {
        try {
            if (streamable instanceof InputStream) {
                return (InputStream) streamable;
            } else if (streamable instanceof byte[]) {
                return new ByteArrayInputStream((byte[]) streamable);
            } else if (streamable instanceof File) {
                return new FileInputStream((File) streamable);
            }
        } catch (FileNotFoundException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM,
                    String.valueOf(streamable));
        }
        return null;
    }

    /**
     * Detects an opens a reader for a streamable object.
     * 
     * @param streamable
     *                   the streamable object
     * @return opened reader on successful detection otherwise a null
     * @throws UnifyException
     *                        if an error occurs
     */
    public static BufferedReader detectAndOpenBufferedReader(Object streamable) throws UnifyException {
        try {
            if (streamable instanceof BufferedReader) {
                return (BufferedReader) streamable;
            } else if (streamable instanceof Reader) {
                return new BufferedReader((Reader) streamable);
            } else if (streamable instanceof InputStream) {
                return new BufferedReader(new InputStreamReader((InputStream) streamable));
            } else if (streamable instanceof byte[]) {
                return new BufferedReader(new InputStreamReader(new ByteArrayInputStream((byte[]) streamable)));
            } else if (streamable instanceof File) {
                return new BufferedReader(new FileReader((File) streamable));
            }
        } catch (FileNotFoundException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM,
                    String.valueOf(streamable));
        }
        return null;
    }

    /**
     * Opens a file resource input stream. Runs in the following sequence until it
     * finds resource to open.
     * 
     * <pre>
     *     1. Check for file with supplied resourceName.
     *     2. If real path is supplied, check for file with name Application RealPath + resourceName
     *     3. Check for file with name System User Directory + resourceName
     *     4. Open class loader resource inputStream with resourceName
     * </pre>
     * 
     * @param resourceName
     *                     the resource name.
     * @param realPath
     *                     the optional real path. Can be null.
     * @return the file resource input stream
     * @throws UnifyException
     *                        if resource is not found. If an error occurs
     */
    public static InputStream openFileResourceInputStream(String resourceName, String realPath) throws UnifyException {
        try {
            if (!restrictedJARMode) {
                File file = IOUtils.fileInstance(resourceName, realPath);
                if (file.exists()) { 
                    return new FileInputStream(file);
                }
            }

            return IOUtils.openClassLoaderResourceInputStream(resourceName);
        } catch (FileNotFoundException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, resourceName);
        }
    }

    /**
     * Opens a resource input stream from the current class loader.
     * 
     * @param resourceName
     *                     the resource name
     * @return the opened input stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static InputStream openClassLoaderResourceInputStream(String resourceName) throws UnifyException {
        InputStream inputStream = IOUtils.class.getClassLoader()
                .getResourceAsStream(IOUtils.conformJarSeparator(resourceName));
        if (inputStream != null) {
            return inputStream;
        }
        throw new UnifyException(UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, resourceName);
    }

    /**
     * List resources in class loader directory.
     * 
     * @param path
     *             the path of directory to list
     * @return list of directory items
     * @throws UnifyException
     *                        if an error occurs
     */
    public static List<String> getResourceListFromClassLoaderDirectory(String path) throws UnifyException {
        List<String> list = new ArrayList<String>();
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.openClassLoaderResourceInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String resource;
            while ((resource = reader.readLine()) != null) {
                list.add(resource);
            }
        } catch (IOException ex) {
            throw new UnifyException(ex, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(inputStream);
        }
        return list;
    }

    /**
     * Opens a file input stream.
     * 
     * @param filename
     *                 the file name
     * @return the file input stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static InputStream openFileInputStream(String filename) throws UnifyException {
        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new UnifyException(UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, filename, e);
        }
    }

    /**
     * Opens a file input stream and skip by specified number of bytes..
     * 
     * @param filename
     *                 the file name
     * @param skip
     *                 the number of bytes to skip by
     * @return the file input stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static InputStream openFileInputStream(String filename, long skip) throws UnifyException {
        try {
            InputStream inputStream = IOUtils.openFileInputStream(filename);
            inputStream.skip(skip);
            return inputStream;
        } catch (IOException e) {
            throw new UnifyException(UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, filename, e);
        }
    }

    /**
     * Opens a file output stream. Truncates file if file already exists.
     * 
     * @param filename
     *                 the file name
     * @return the file output stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static OutputStream openFileOutputStream(String filename) throws UnifyException {
        try {
            return new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            throw new UnifyException(UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, filename, e);
        }
    }

    /**
     * Opens a file output stream.
     * 
     * @param filename
     *                 the file name
     * @param append
     *                 flag that indicates an append
     * @return the file output stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static OutputStream openFileOutputStream(String filename, boolean append) throws UnifyException {
        try {
            return new FileOutputStream(filename, append);
        } catch (FileNotFoundException e) {
            throw new UnifyException(UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, filename, e);
        }
    }

    /**
     * Checks if a resource file exists in system
     * 
     * @param filename
     *                 the file name
     * @return the file instance.
     */
    public static boolean isResourceFileInstance(String filename, String optionPath) {
        File file = IOUtils.fileInstance(filename, optionPath);
        if (!file.exists()) {
            return IOUtils.class.getClassLoader().getResource(filename) != null;
        }
        return true;
    }

    /**
     * Checks if a resource file exists in real path
     * 
     * @param filename
     *                 the file name
     * @return the file instance.
     */
    public static boolean isRealPathResource(String optionPath, String filename) {
        File file = IOUtils.fileInstance(filename, optionPath);
        return file.exists();
    }

    /**
     * Checks if a resource file exists in class loader
     * 
     * @param filename
     *                 the file name
     * @return the file instance.
     */
    public static boolean isClassLoaderResource(String filename) {
        return IOUtils.class.getClassLoader().getResource(filename) != null;
    }

    /**
     * Gets a file instance for existing file with filename.
     * 
     * @param filename
     *                 the file name
     * @return the file instance.
     */
    public static File fileInstance(String filename, String optionPath) {
        File file = new File(filename);
        if (!file.exists()) {
            if (StringUtils.isNotBlank(optionPath)) {
                file = new File(IOUtils.buildFilename(optionPath, filename));
            }
            if (!file.exists()) {
                file = new File(IOUtils.buildFilename(System.getProperty("user.dir"), filename));
            }
        }
        return file;
    }

    /**
     * Reads file resource into memory.
     * {@link #openFileResourceInputStream(String, String)}
     * 
     * @param resourceName
     *                     the resource name.
     * @return the file resource
     * @throws UnifyException
     *                        if an error occurs
     */
    public static byte[] readFileResourceInputStream(String resourceName) throws UnifyException {
        return IOUtils.readFileResourceInputStream(resourceName, null);
    }

    /**
     * Reads file resource into memory.
     * {@link #openFileResourceInputStream(String, String)}
     * 
     * @param resourceName
     *                     the resource name.
     * @param realPath
     *                     the optional real path. Can be null.
     * @return the file resource
     * @throws UnifyException
     *                        if an error occurs
     */
    public static byte[] readFileResourceInputStream(String resourceName, String realPath) throws UnifyException {
        return IOUtils.readAll(IOUtils.openFileResourceInputStream(resourceName, realPath));
    }

    public static Properties readPropertiesFromFileResource(String resourceName, String realPath)
            throws UnifyException {
        Properties properties = new Properties();
        IOUtils.readPropertiesFromFileResource(properties, resourceName, realPath);
        return properties;
    }

    public static Properties readPropertiesFromFileResources(List<String> resourceNames, String realPath)
            throws UnifyException {
        Properties properties = new Properties();
        for (String resourceName : resourceNames) {
            IOUtils.readPropertiesFromFileResource(properties, resourceName, realPath);
        }
        return properties;
    }

    private static void readPropertiesFromFileResource(Properties properties, String resourceName, String realPath)
            throws UnifyException {
        InputStream in = null;
        try {
            in = IOUtils.openFileResourceInputStream(resourceName, realPath);
            properties.load(in);
        } catch (IOException e) {
            throw new UnifyOperationException(e);
        } finally {
            IOUtils.close(in);
        }
    }

    /**
     * Reads data from input stream into supplied buffer. Reads at most the length
     * of the buffer.
     * 
     * @param buffer
     *                    the buffer to read into
     * @param inputStream
     *                    the input stream to read from
     * @return the number of bytes read
     * @throws UnifyException
     *                        if an error occurs
     */
    public static int read(byte[] buffer, InputStream inputStream) throws UnifyException {
        try {
            int read = 0;
            int index = 0;
            while ((index < buffer.length) && (read = inputStream.read(buffer, index, buffer.length - index)) >= 0) {
                index += read;
            }
            return index;
        } catch (IOException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        }
    }

    /**
     * Reads all data from file into a byte array.
     * 
     * @param file
     *             the file
     * @return byte[] the resulting byte array
     * @throws UnifyException
     *                        if an error occurs
     */
    public static byte[] readAll(File file) throws UnifyException {
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(fis);
        }

        return data;
    }

    /**
     * Reads all data from input stream into a byte array.
     * 
     * @param inputStream
     *                    the input stream to read from
     * @return byte[] the resulting byte array
     * @throws UnifyException
     *                        if an error occurs
     */
    public static byte[] readAll(InputStream inputStream) throws UnifyException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.writeAll(baos, inputStream);
        IOUtils.close(baos);
        return baos.toByteArray();
    }

    public static String readAll(Reader reader) throws UnifyException {
        return IOUtils.readAll(new BufferedReader(reader));
    }
    
    public static String readAll(BufferedReader reader) throws UnifyException {
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        }
    }
    
    public static List<String> readAllLines(BufferedReader reader) throws UnifyException {
        try {
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        }
    }

    /**
     * Writes all data from input stream to output stream. Closes input stream at
     * end of write.
     * 
     * @param outputStream
     *                     the output stream to write to
     * @param inputStream
     *                     the input stream to read from
     * @return the number of bytes written
     * @throws UnifyException
     *                        if an error occurs
     */
    public static long writeAll(OutputStream outputStream, InputStream inputStream) throws UnifyException {
        try {
            long totalRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, read);
                totalRead += read;
            }
            return totalRead;
        } catch (IOException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(inputStream);
        }
    }

    /**
     * Writes all supplied data to specified output stream.
     * 
     * @param outputStream
     *                     the output stream to write to
     * @param data
     *                     the data to write
     * @return the number of bytes written
     * @throws UnifyException
     *                        if an error occurs
     */
    public static long writeAll(OutputStream outputStream, byte[] data) throws UnifyException {
        try {
            outputStream.write(data);
            return data.length;
        } catch (IOException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        }
    }

    /**
     * Writes all input stream data into file.
     * 
     * @param filename
     *                    the file name
     * @param inputStream
     *                    the input stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeToFile(String filename, InputStream inputStream) throws UnifyException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            IOUtils.writeAll(outputStream, inputStream);
        } catch (FileNotFoundException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(outputStream);
        }
    }

    /**
     * Writes all input stream data into file.
     * 
     * @param file
     *                    the file
     * @param inputStream
     *                    the input stream
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeToFile(File file, InputStream inputStream) throws UnifyException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            IOUtils.writeAll(outputStream, inputStream);
        } catch (FileNotFoundException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(outputStream);
        }
    }

    /**
     * Writes all input stream data into file.
     * 
     * @param filename
     *                 the file name
     * @param data
     *                 the data to write
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeToFile(String filename, byte[] data) throws UnifyException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            outputStream.write(data);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(outputStream);
        }
    }

    /**
     * Writes all input stream data into file.
     * 
     * @param file
     *             the file
     * @param data
     *             the data to write
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeToFile(File file, byte[] data) throws UnifyException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(outputStream);
        }
    }

    /**
     * Streams a serializable object to bytes.
     * 
     * @param serializable
     *                     the serializable object
     * @return the streamed object
     * @throws UnifyException
     *                        if an error occurs
     */
    public static byte[] streamToBytes(Object serializable) throws UnifyException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(serializable);
            out.flush();
            out.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        }
    }

    /**
     * Reconstructs an object from a stream of bytes.
     * 
     * @param targetClass
     *                    the target type
     * @param bytes
     *                    the bytes to stream from
     * @return the constructed object
     * @throws UnifyException
     *                        if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T streamFromBytes(Class<T> targetClass, byte[] bytes) throws UnifyException {
        try {
            ObjectInputStream objectinputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            T object = (T) objectinputStream.readObject();
            objectinputStream.close();
            return object;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        }
    }

    /**
     * Closes a reader quietly.
     * 
     * @param reader
     *               the reader to close
     */
    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Closes an input stream quietly.
     * 
     * @param instream
     *                 the input stream to close
     */
    public static void close(InputStream instream) {
        if (instream != null) {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Closes a writer quietly.
     * 
     * @param writer
     *               the writer to close
     */
    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Closes an output stream quietly.
     * 
     * @param outstream
     *                  the output stream to close
     */
    public static void close(OutputStream outstream) {
        if (outstream != null) {
            try {
                outstream.flush();
                outstream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Ensures a directory exists. Checks if a directory exists. Creates one if
     * directory does not exist.
     * 
     * @param path
     *             the directory path
     * @return true value if directory exists now.
     */
    public static boolean ensureDirectoryExists(String path) {
        File file = new File(IOUtils.conform(System.getProperty("file.separator"), path));

        if (file.isFile()) {
            return false;
        }

        if (!file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

	public static void deleteDirectoryContents(String path) {
		File folder = new File(IOUtils.conform(System.getProperty("file.separator"), path));
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteFileOrDirectory(file);
				}
			}
		}
	}

	private static void deleteFileOrDirectory(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File _file : files) {
					deleteFileOrDirectory(_file);
				}
			}
		}
		
		file.delete();
	}

    /**
     * Builds a canonical file name.
     * 
     * @param path
     *                 the file path
     * @param filename
     *                 the file name
     * @return the proper file name
     */
    public static String buildFilename(String path, String filename) {
        String fileSeparator = System.getProperty("file.separator");
        path = IOUtils.conform(fileSeparator, path);
        filename = IOUtils.conform(fileSeparator, filename);
        if (path.endsWith(fileSeparator)) {
            if (filename.startsWith(fileSeparator)) {
                return path + filename.substring(fileSeparator.length());
            }
            return path + filename;
        }

        if (filename.startsWith(fileSeparator)) {
            return path + filename;
        }
        return path + fileSeparator + filename;
    }

    /**
     * Conforms supplied path to system format.
     * 
     * @param path
     *             the path to conform
     * @return the conformed path
     */
    public static String conformFilePath(String path) {
        String fileSeparator = System.getProperty("file.separator");
        path = IOUtils.conform(fileSeparator, path);
        if (!path.endsWith(fileSeparator)) {
            return path + fileSeparator;
        }

        return path;
    }

    /**
     * Conforms supplied path to system format.
     * 
     * @param path
     *             the path to conform
     * @return the conformed path
     */
    public static String conformAbsoluteFileName(String absoluteFilename) {
        String fileSeparator = System.getProperty("file.separator");
        return IOUtils.conform(fileSeparator, absoluteFilename);
    }

	/**
	 * Returns the actual file name.
	 * 
	 * @param absoluteFilename the absolute file name
	 * @return the actual file name
	 */
	public static String getActualFileName(String absoluteFilename) {
		return new File(IOUtils.conformAbsoluteFileName(absoluteFilename)).getName();
	}
    
    /**
     * Returns true if supplied file name is in file system.
     * 
     * @param absoluteFilename
     *                         the file name to test
     */
    public static boolean isFile(String absoluteFilename) {
        File file = new File(absoluteFilename);
        return file.isFile();
    }

    /**
     * Lists file names in supplied folder.
     * 
     * @param folder
     *               the folder to list
     * @return list of file names in folder
     */
    public static String[] listFolderFilenames(String folder) {
        String path = IOUtils.conformFilePath(folder);
        File dir = new File(path);
        if (dir.isDirectory()) {
            return dir.list();
        }

        return DataUtils.ZEROLEN_STRING_ARRAY;
    }

    /**
     * Lists file names in supplied folder using a filename filter.
     * 
     * @param folder
     *                       the folder to list
     * @param filenameFilter
     *                       the filename filter
     * @return list of file names in folder
     */
    public static String[] listFolderFilenames(String folder, FilenameFilter filenameFilter) {
        String path = IOUtils.conformFilePath(folder);
        File dir = new File(path);
        if (dir.isDirectory()) {
            return dir.list(filenameFilter);
        }

        return DataUtils.ZEROLEN_STRING_ARRAY;
    }

    /**
     * Lists files in supplied folder.
     * 
     * @param folder
     *               the folder to list
     * @return list of files in folder
     */
    public static File[] listFolderFiles(String folder) {
        String path = IOUtils.conformFilePath(folder);
        File dir = new File(path);
        if (dir.isDirectory()) {
            return dir.listFiles();
        }

        return ZEROLEN_FILES;
    }

    /**
     * Lists files in supplied folder using a filename filter.
     * 
     * @param folder
     *                       the folder to list
     * @param filenameFilter
     *                       the filename filter
     * @return list of files in folder
     */
    public static File[] listFolderFiles(String folder, FilenameFilter filenameFilter) {
        String path = IOUtils.conformFilePath(folder);
        File dir = new File(path);
        if (dir.isDirectory()) {
            return dir.listFiles(filenameFilter);
        }

        return ZEROLEN_FILES;
    }

    /**
     * Lists files in supplied folder using a file filter.
     * 
     * @param folder
     *                   the folder to list
     * @param fileFilter
     *                   the file filter
     * @return list of files in folder
     */
    public static File[] listFolderFiles(String folder, FileFilter fileFilter) {
        String path = IOUtils.conformFilePath(folder);
        File dir = new File(path);
        if (dir.isDirectory()) {
            return dir.listFiles(fileFilter);
        }

        return ZEROLEN_FILES;
    }

    /**
     * Reads a file resource, obtained from file system or class loader, as lines.
     * 
     * @param resourceName
     *                     the resource name
     * @param realPath
     *                     an optional real path
     * @return the lines read
     * @throws UnifyException
     *                        if an error occurs
     */
    public static List<String> readFileResourceLines(String resourceName, String realPath) throws UnifyException {
        List<String> lines = Collections.emptyList();
        InputStream in = null;
        try {
            in = IOUtils.openFileResourceInputStream(resourceName, realPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            lines = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, resourceName);
        } catch (IOException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
        } finally {
            IOUtils.close(in);
        }

        return lines;
    }

	/**
	 * Reads a file resource, obtained from file system or class loader, as lines
	 * into a single string.
	 * 
	 * @param resourceName the resource name
	 * @param realPath     an optional real path
	 * @return the lines read
	 * @throws UnifyException if an error occurs
	 */
	public static String readAllLines(String resourceName, String realPath) throws UnifyException {
		StringBuilder lines = new StringBuilder();
		InputStream in = null;
		try {
			in = IOUtils.openFileResourceInputStream(resourceName, realPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			String newline = System.lineSeparator();
			while ((line = br.readLine()) != null) {
				lines.append(line).append(newline);
			}
		} catch (FileNotFoundException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM, resourceName);
		} catch (IOException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
		} finally {
			IOUtils.close(in);
		}

		return lines.toString();
	}

    /**
     * Creats an in-memory text file.
     * 
     * @param lines
     *              the text file content
     * @return the created in file memory
     * @throws Exception
     *                   if an error occurs
     */
    public static byte[] createInMemoryTextFile(String... lines) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }
        bw.flush();
        bw.close();
        return baos.toByteArray();
    }

    /**
     * Gets a new instance of a custom file name filter.
     * 
     * @param prefixes
     *                   the file name prefixes
     * @param extensions
     *                   the file name extensions
     * @return the custom file name filter
     */
    public static CustomFilenameFilter getCustomFilenameFilter(String prefixes, String extensions) {
        return new CustomFilenameFilter(prefixes, extensions, "");
    }

    /**
     * Gets a new instance of a custom file name filter.
     * 
     * @param prefixes
     *                   the file name prefixes
     * @param extensions
     *                   the file name extensions
     * @param signatures
     *                   the body signatures
     * @return the custom file name filter
     */
    public static CustomFilenameFilter getCustomFilenameFilter(String prefixes, String extensions, String signatures) {
        return new CustomFilenameFilter(prefixes, extensions, signatures);
    }

	/**
	 * Posts an object to an end point using JSON.
	 * 
	 * @param responseClass the response class
	 * @param endpoint      the end point
	 * @param requestObject the object to post
	 * @return the response
	 * @throws UnifyException if an error occurs
	 */
	public static <T> T postObjectToEndpointUsingJson(Class<T> responseClass, String endpoint, Object requestObject)
			throws UnifyException {
		final String reqJSON = DataUtils.asJsonString(requestObject, PrintFormat.NONE);
		final String respJSON = IOUtils.postJsonToEndpoint(endpoint, reqJSON);
		return DataUtils.fromJsonString(responseClass, respJSON);
	}
    
	/**
	 * Posts JSON string to an end point.
	 * 
	 * @param endpoint the end point
	 * @param json     the json to post
	 * @return the response
	 * @throws UnifyException if an error occurs
	 */
	public static String postJsonToEndpoint(String endpoint, String json) throws UnifyException {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);

			try (OutputStream out = conn.getOutputStream()) {
				out.write(json.getBytes("utf-8"));
				out.flush();
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
			}
		} catch (Exception e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.IOUTIL_STREAM_RW_ERROR);
		}

		return response.toString();
	}
    
    public static class CustomFilenameFilter implements FilenameFilter {

        private static String SEPERATOR = ",";

        private String[] prefixList;

        private String[] extensionList;

        private String[] signatureList;

        private CustomFilenameFilter(String prefixes, String extensions, String signatures) {
            prefixList = StringUtils.split(prefixes.toLowerCase(), SEPERATOR);
            extensionList = StringUtils.split(extensions.toLowerCase(), SEPERATOR);
            signatureList = StringUtils.split(signatures.toLowerCase(), SEPERATOR);
        }

        @Override
        public boolean accept(File dir, String filename) {
            return accept(filename);
        }

        protected boolean accept(String filename) {
            String lowerFileName = filename.toLowerCase();
            boolean signaturePass = true;
            for (String signature : signatureList) {
                if ((signaturePass = (lowerFileName.indexOf(signature) >= 0))) {
                    break;
                }
            }
            if (signaturePass) {
                boolean prefixPass = true;
                for (String prefix : prefixList) {
                    if (prefixPass = lowerFileName.startsWith(prefix)) {
                        break;
                    }
                }
                if (prefixPass) {
                    boolean extensionPass = true;
                    for (String extension : extensionList) {
                        if (extensionPass = lowerFileName.endsWith(extension)) {
                            break;
                        }
                    }
                    return extensionPass;
                }
            }
            return false;
        }

    }

    private static String conformJarSeparator(String filename) {
        filename = IOUtils.conform("/", filename);
        if (filename.startsWith("/")) {
            return filename.substring("/".length());
        }
        return filename;
    }

    private static String conform(String separator, String name) {
        if (name == null) {
            return "";
        }

        if (separator.equals("\\")) {
            return name.replace('/', '\\');
        }
        return name.replace('\\', '/');
    }
}
