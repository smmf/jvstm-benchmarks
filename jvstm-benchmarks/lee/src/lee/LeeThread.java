package lee;
/*
 * BSD License
 *
 * Copyright (c) 2007, The University of Manchester (UK)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     - Neither the name of the University of Manchester nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.concurrent.Callable;


/**
 *
 * @author ansarim
 */
public class LeeThread extends Thread {
    
    public static boolean stop = false;
    boolean finished = false;
    public boolean sampleNow = false;
    public boolean doneSample = true;
   // public static long totalTracks=0;
    public static long totalLaidTracks=0;
    public long myLaidTracks=0;
    private static Object lock = new Object();
    public String hardware = System.getenv("HOSTNAME");
    protected static ThreadLocal<ThreadState> _threadState = new ThreadLocal<ThreadState>() {
        protected synchronized ThreadState initialValue() {
            return new ThreadState();
        }
    };
    static ThreadLocal<Thread> _thread = new ThreadLocal<Thread>() {
        protected synchronized Thread initialValue() {
            return null;
        }
    };
    
    LeeRouter lt;
    WorkQueue t;
    boolean done = true;
    int[][][] tempg;
    
    LeeThread(LeeRouter lt) {
        this.lt = lt;
        tempg = new int[lt.GRID_SIZE][lt.GRID_SIZE][2]; // Lee 2D Grid copy
    }
    
    public void run() {
        while (!finished && !stop) {
            if(sampleNow) {
                collectMyStatistics();
                doneSample = true;
                sampleNow = false;
            }
            if(done) {
                t = lt.getNextTrack();
                done = false;
            }
            if(t==null) {
                finished = true;
//                 System.err.println("Finished");
                collectMyStatistics();
                collectStatistics(_threadState.get());
                break;
            } else {
                //System.out.println("Laying track "+t.nn);
                lt.layNextTrack(t, tempg, this);
// 		int localCounter = t.counter;
// 		if (localCounter % 100 == 0) {
// 		    long time = System.currentTimeMillis() - lt.initialTime;
// 		    System.out.println("Layed " + localCounter + " tracks in "
// 				       + time + " (" + (((double)localCounter*1000)/time) + " tracks/s)");
// 		}
                done = true;
                updateStatistics();
            }
        }
    }
    
    
    
    /**
     * Class that holds thread's actual state
     */
    public static class ThreadState {
        
        
        private long myLaidTracks = 0;        // number of laid tracks
        
        /**
         * Creates new ThreadState
         */
        public ThreadState() {
        }
        
        /**
         * Resets any metering information (commits/aborts, etc).
         */
        public void reset() {
            myLaidTracks = 0;            // total number of transactions
        }
        
        /**
         * used for debugging
         * @return string representation of thread state
         */
        public String toString() {
            return
                    "Thread" + hashCode() + "["+
                    "total: " +  myLaidTracks + "," +
                    "]";
        }
        
    }
    protected static void collectStatistics(ThreadState threadState) {
        // collect statistics
        synchronized (lock){
            totalLaidTracks+=threadState.myLaidTracks;
            threadState.reset();  // set up for next iteration
        }
    }
    
    public void updateStatistics(){
        _threadState.get().myLaidTracks++;
    }
    
    public void collectMyStatistics() {
        myLaidTracks=_threadState.get().myLaidTracks-myLaidTracks;
        
        
        
    }
    
    public void resetMyStatistics() {
        myLaidTracks=0;
    }

    // STATISTICAL DATA

    // number of read-only transactions executed
    protected int roTransactions = 0;
    // number of read-write transactions executed
    protected int rwTransactions = 0;
    // all restarts are counted
    protected int restarts = 0;
    // the first restart of a transaction counts as a conflict.  we only count conflicts once for
    // each transaction
    protected int conflicts = 0;
    
    // longest time to lay a track
    protected long longestTrackLayTime = 0;
    // shortest time to lay a track
    protected long shortestTrackLayTime = Long.MAX_VALUE;
    // total time spent laying tracks
    protected long totalWorkTime = 0;

}