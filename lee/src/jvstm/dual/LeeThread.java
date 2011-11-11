package jvstm.dual;
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

import jvstm.ReadWriteTransaction;
// import jvstm.CommitStats;
import jvstm.util.Cons;
// import jvstm.util.VMultiArray;

/**
 *
 * @author ansarim
 */
public class LeeThread extends Thread {
    
    public static boolean stop = false;
    boolean finished = false;

    LeeRouter lr;
    WorkQueue t;
    boolean done = true;
    
    LeeThread(LeeRouter lr) {
        this.lr = lr;
    }
    
    public void run() {
        while (!finished && !stop) {
            if(done) {
                t = lr.getNextTrack();
                done = false;
            }
            if(t==null) {
                finished = true;
                break;
            } else {
                //System.out.println("Laying track "+t.nn);
                lr.layNextTrack(t, this);
                done = true;
            }
        }
	// CommitStats.dumpToResults();
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