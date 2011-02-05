/*
 * Copyright (c) Rustam Vishnyakov, 2008 (dyadix@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * $Id: FileLockManager.java,v 1.1 2008/08/29 18:13:23 dyadix Exp $
 */
package net.sf.timecult.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.HashMap;

/**
 * Manages file locks so that the same workspace can not be opened for write
 * multiple times. 
 */
public class FileLockManager {
    
    private static HashMap<String,LockEntry> lockMap = new HashMap<String,LockEntry>();
        
    public static synchronized boolean lock(String filePath) {
        boolean success = false;
        if(!isLocked(filePath) && !lockMap.containsKey(filePath)) {            
            try {
                LockEntry lockEntry = new LockEntry(filePath);
                if (lockEntry.lock()) {
                    lockMap.put(filePath, lockEntry);
                }
                success = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    
    public static synchronized boolean unlock(String filePath) {
        if(lockMap.containsKey(filePath)) {
            LockEntry lockEntry = lockMap.get(filePath);
            if(lockEntry.release()) {
                lockMap.remove(filePath);
                return true;
            }
        }
        return false;
    }
    
    
    public static void unlockAll() {
        for (LockEntry lockEntry : lockMap.values()) {
            unlock(lockEntry.getFilePath());
        }
    }
    
    
    public static boolean isLocked(String filePath) {
        boolean locked = false;
        File lockFile = getLockFile(filePath);
        if (lockFile.exists()) {
            FileReader fr = null;
            try {
                fr = new FileReader(lockFile);
                fr.read();
            }
            catch (Exception e) {
                locked = true;
            }
            finally {
                try {
                    fr.close();
                }
                catch (IOException e) {
                    // Surpress
                }
            }
        }
        return locked;
    }
    
    
    private static File getLockFile(String dataFilePath) {
        StringBuffer buf = new StringBuffer();
        int dotPos = dataFilePath.lastIndexOf(".");
        if (dotPos >= 0) {
            buf.append(dataFilePath.substring(0, dotPos));
        }
        else {
            buf.append(dataFilePath);
        }
        buf.append(".lock");
        return new File(buf.toString());
    }
    
    private static class LockEntry {
        private FileLock lock;
        private File lockFile;
        private String filePath;
        private FileOutputStream stream;
        
        public LockEntry(String filePath)
        {
            this.lockFile = FileLockManager.getLockFile(filePath);
            this.filePath = filePath;
        }
        
        public File getLockFile() {
            return this.lockFile;
        }
        
        public FileLock getLock() {
            return this.lock;
        }
        
        public boolean lock() {
            try {
                this.stream = new FileOutputStream(this.lockFile);
                this.stream.write(0);
                this.stream.getChannel();
                this.lock = this.stream.getChannel().lock();
                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        
        public boolean release() {
            try {
                this.lock.release();
                this.lock.channel().close();
                this.stream.close();
                if (this.lockFile.exists()) {
                    this.lockFile.delete();
                }
                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        
        public String getFilePath() {
            return this.filePath;
        }
    }
}
