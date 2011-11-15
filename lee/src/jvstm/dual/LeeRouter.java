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


//Simple Lee's Routing Algorithm
//Author: IW
import java.io.*;
import java.util.*;
// import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jvstm.CommitException;
import jvstm.Transaction;
import jvstm.util.Cons;
import jvstm.util.NestedWorkUnit;
// import jvstm.CommitStats;
// import jvstm.util.VMultiArray;

public class LeeRouter {
    final static int cyan = 0x00FFFF;
    
    public static final long MAX_SAMPLE_THRESHOLD = 60000;
    
    final static int magenta = 0xFF00FF;
    
    final static int yellow = 0xFFFF00;
    
    final static int green = 0x00FF00;
    
    final static int red = 0xFF0000;
    
    final static int blue = 0x0000FF;
    
    final int GRID_SIZE;
    
    final static int EMPTY = 0;
    
    final static int TEMP_EMPTY = 10000;
    
    final static int OCC = 5120;
    
    final static int VIA = 6000;
    
    final static int BVIA = 6001;
    
    final static int TRACK = 8192;
    
    final static int GOAL = 1024;
    
    final static int MAX_WEIGHT = 1;
    
    final Grid grid;
    
    final Object gridLock = new Object();
    
    private static Document doc;
    
    static int netNo = 0;
    
//	private static int printDest = 0; // 1 for file, 0 for screen.
    
    // note these very useful arrays
    final static int dx[][] = { { -1, 1, 0, 0 }, { 0, 0, -1, 1 } };
    
    // to help look NSEW.
    final static int dy[][] = { { 0, 0, -1, 1 }, { -1, 1, 0, 0 } };
    
    static Viewer view;
    
    static int failures = 0;
    
    static int num_vias = 0;
    
    static int forced_vias = 0;
    
    static BufferedReader inputFile;
    
    static String input_line;
    
    static int linepos = 0;
    
    final Object queueLock = new Object();
    
    final WorkQueue work;
    
    final WorkQueue debugQueue;
    
    public static boolean TEST = true;
    
    public static boolean DEBUG = false;
    public static boolean VIEW = false;
    
    
    public LeeRouter(String file, boolean test, boolean debug, boolean rel) {
        TEST = test;
        DEBUG = debug;
        if (TEST) GRID_SIZE = 10;
        else GRID_SIZE = 600;
        if(DEBUG || VIEW) view = new Viewer(GRID_SIZE);
        if(DEBUG) System.out.println("Creating grid...");
        grid = new Grid(GRID_SIZE, GRID_SIZE, 2, rel); //the Lee 3D Grid;
        if(DEBUG) System.out.println("Done creating grid");
        work = new WorkQueue(); // empty
        if(DEBUG) System.out.println("Parsing data...");
        if (!TEST) parseDataFile(file);
        else fakeTestData(); //WARNING: Needs grid at least 10x10x2
        if(DEBUG) System.out.println("Done parsing data");
        if(DEBUG) System.out.println("Adding weights...");
        grid.addweights();
        if(DEBUG) System.out.println("Done adding weights");
        work.sort();
        if(DEBUG)
            debugQueue = new WorkQueue();
        else
            debugQueue = null;
        
    }
    
    public LeeRouter(String file) {
        this(file, false, false, false);
    }
    
    public LeeRouter(String file, boolean rel) {
        this(file, false, false, rel);
    }
    
    private void fakeTestData() {
        netNo++;
        grid.occupy(7, 3, 7, 3);grid.occupy(7, 7, 7, 7);
        work.next = work.enQueue(7, 3, 7, 7, netNo);
        
        netNo++;
        grid.occupy(3, 6, 3, 6);grid.occupy(8, 6, 8, 6);
        work.next = work.enQueue(3, 6, 8, 6, netNo);
        
        netNo++;
        grid.occupy(5, 3, 5, 3);grid.occupy(8, 5, 8, 5);
        work.next = work.enQueue(5, 3, 8, 5, netNo);
        
        netNo++;
        grid.occupy(8, 3, 8, 3);grid.occupy(2, 6, 2, 6);
        work.next = work.enQueue(8, 3, 2, 6, netNo);
        
        netNo++;
        grid.occupy(4, 3, 4, 3);grid.occupy(6, 7, 6, 7);
        work.next = work.enQueue(4, 3, 6, 7, netNo);
        
        netNo++;
        grid.occupy(3, 8, 3, 8);grid.occupy(8, 3, 8, 3);
        work.next = work.enQueue(3, 8, 8, 3, netNo);
    }
    
    private void parseDataFile(String fileName) {
        // Read very simple HDL file
        try {
            
            inputFile = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName)));
            int i = 0;
            while (true) {
                nextLine();
                char c = readChar();
                if (c == 'E')
                    break; // end of file
                if (c == 'C') // chip bounding box
                {
                    int x0 = readInt();
                    int y0 = readInt();
                    int x1 = readInt();
                    int y1 = readInt();
                    grid.occupy(x0, y0, x1, y1);
                }
                if (c == 'P') // pad
                {
                    int x0 = readInt();
                    int y0 = readInt();
                    grid.occupy(x0, y0, x0, y0);
                }
                if (c == 'J') // join connection pts
                {
                    i++;
                    int x0 = readInt();
                    int y0 = readInt();
                    int x1 = readInt();
                    int y1 = readInt();
                    netNo++;
                    work.next = work.enQueue(x0, y0, x1, y1, netNo);
                }
            }
        } catch (FileNotFoundException exception) {
            System.out.println("Cannot open file: " + fileName);
            System.exit(1);
        } catch (IOException exception) {
            System.out.println(exception);
            exception.printStackTrace();
        }
        
    }
    
    public WorkQueue getNextTrack() {
        synchronized(queueLock) {
            if(work.next != null) {
                return work.deQueue();
            }
        }
        return null;
    }
    
    public boolean layNextTrack(WorkQueue q, LeeThread lt) {
        // start transaction
        boolean done = false;
	long start = System.currentTimeMillis();
	done = connect(q.x1, q.y1, q.x2, q.y2, q.nn, grid, lt);
	long duration = System.currentTimeMillis() - start;
	lt.totalWorkTime+= duration;
	if (duration > lt.longestTrackLayTime) {
	    lt.longestTrackLayTime = duration;
	}
	if (duration < lt.shortestTrackLayTime) {
	    lt.shortestTrackLayTime = duration;
	}
	if(DEBUG && done) {
	    debugQueue.next = debugQueue.enQueue(q);
	}
        return done;
        // end transaction
    }
    
    private static void nextLine() throws IOException {
        input_line = inputFile.readLine();
        linepos = 0;
    }
    
    private static char readChar() {
        while ((input_line.charAt(linepos) == ' ')
        && (input_line.charAt(linepos) == '\t'))
            linepos++;
        char c = input_line.charAt(linepos);
        if (linepos < input_line.length() - 1)
            linepos++;
        return c;
    }
    
    private static int readInt() {
        while ((input_line.charAt(linepos) == ' ')
        || (input_line.charAt(linepos) == '\t'))
            linepos++;
        int fpos = linepos;
        while ((linepos < input_line.length())
        && (input_line.charAt(linepos) != ' ')
        && (input_line.charAt(linepos) != '\t'))
            linepos++;
        int n = Integer.parseInt(input_line.substring(fpos, linepos));
        return n;
    }
    
    public boolean ok(int x, int y) {
        // checks that point is actually within the bounds
        // of grid array
        return (x > 0 && x < GRID_SIZE - 1 && y > 0 && y < GRID_SIZE - 1);
    }
    
    // number of points to group in a block. The more, the larger the size of the nested
    // transaction, because all points within a block are expanded within the same nested
    // transaction.  The downside is that a large BLOCK_SIZE reduces parallellism.
    static final int BLOCK_SIZE = 10; //10;
    // minimum number of blocks required to start parallelizing
    static final int MIN_BLOCKS = 5; //4;
    static final int MIN_FRONT_SIZE = BLOCK_SIZE * MIN_BLOCKS;
    // how many contiguous blocks to skip when launching parallel tasks.  For each STRIDE, blocks
    // are expanded concurrently, So, if STRIDE is greater than the total number of blocks, blocks
    // are executed in sequence.
    static final int STRIDE = 4; //2;
    public boolean expandFromTo(int x, int y, int xGoal, int yGoal,
				int num, TempGrid tempg0, TempGrid tempg1, Grid grid) {
        // this method should use Lee's expansion algorithm from
        // coordinate (x,y) to (xGoal, yGoal) for the num iterations
        // it should return true if the goal is found and false if it is not
        // reached within the number of iterations allowed.
        
        // g[xGoal][yGoal][0] = EMPTY; // set goal as empty
        // g[xGoal][yGoal][1] = EMPTY; // set goal as empty
        Vector<Frontier> front = new Vector<Frontier>();
        Vector<Frontier> tmp_front = null; // = new Vector<Frontier>();
        tempg0.put(1, x, y); // set grid (x,y) as 1
        tempg1.put(1, x, y); // set grid (x,y) as 1

        front.addElement(new Frontier(x, y, 0));
        front.addElement(new Frontier(x, y, 1)); // we can start from either
        // side
        if(DEBUG) System.out.println("Expanding " + x + " " + y + " " + xGoal + " "
                + yGoal);

        boolean reached = false;
        while (!front.isEmpty()) {
            // tmp_front is used to merge the results
            tmp_front = new Vector();

            int frontSize = front.size();
            // System.out.println("\nExpand: " + frontSize);

            // don't parallelize under a minimum threshold
            if (frontSize >= MIN_FRONT_SIZE) {
                int nFullBlocks = frontSize / BLOCK_SIZE;
                int sizePartialBlock = frontSize % BLOCK_SIZE;
                Future<ExpansionResult>[] results = new Future[nFullBlocks + (sizePartialBlock == 0 ? 0 : 1)];

                for (int stride = 0; stride < STRIDE && stride < results.length && !reached; stride++) {
                    // parallelize within each stride
                    for (int block = stride; block < results.length; block += STRIDE) {
                        int minPos = BLOCK_SIZE * block;
                        // last block may be smaller
                        int maxPos = (block == results.length - 1) ? frontSize : BLOCK_SIZE * (block + 1);

                        // System.out.print("[" + block + "");
                        results[block] = threadPool.submit(new ExpansionTask(front, minPos, maxPos, tempg0, tempg1,
                                                                             xGoal, yGoal, this));

                    }
                    // join nested transactions.  This ensures that the nested txs in next stride will see the commits
                    // System.out.print(";");
                    // merge results in tmp_front
                    try {
                        int block = stride;
                        for (; block < results.length; block += STRIDE) {
                            // System.out.println("going to get() block " + block);
                            // System.out.print("" + block + "]");
                            ExpansionResult result = results[block].get(/*10, java.util.concurrent.TimeUnit.MILLISECONDS*/);
                            if (result.reached) {
                                // System.out.println("got goal on block " + block);
                                reached = true;
                                break;
                            }
                            // the goal was not reached. Add this expansion result to the new nodes to expand
                            tmp_front.addAll(result.places);
                        }
                        // smf: in this case, do I need to wait for others to finish?  This only happens
                        // when the previous cycle 'breaks', because some expansion reached the goal
                        // System.out.println("Waiting for others...");
                        for (int i = block + STRIDE; i < results.length; i += STRIDE) {
                            // System.out.print(" -" + i + "-");
                            results[i].get();
                        }
                        if (reached) return true;
                        // System.out.print("|");
                    // } catch (java.util.concurrent.TimeoutException te) {
                    //     System.out.println("timeout");
                    //     System.exit(-1);
                    } catch (InterruptedException ie) {
                        System.out.println("InterruptedException");
                        ie.printStackTrace();
                        System.exit(1);
                    } catch (ExecutionException ee) {
                        // System.out.println("ExecutionException");
                        // ee.printStackTrace();
                        throw (RuntimeException)ee.getCause();
                    }
                }
            } else {
                // System.out.println("Single: [0; " + frontSize + "[");
                ExpansionResult result = expand(front, 0, frontSize, tempg0, tempg1, xGoal, yGoal);
                if (result.reached) {
                    return true;
                    // reached = true;
                } else {
                    tmp_front = new Vector(result.places.size());
                    tmp_front.addAll(result.places);
                }
            }
            
            // // bail out if a path was found
            // if (reached) {
            //     return true;
            // }

            // otherwise reset front to be tmp_front and continue searching
            front = tmp_front;
        }
//		 view.pad(x,y,red);
//		 view.pad(xGoal,yGoal,red);
        return false;
    }
    
    // perform actual expansion of points in 'front' between [min;max[.  'front' is shared so cannot
    // be changed
    //
    // return a new vector with vector[0] = found? and the remaining positions containing
    private ExpansionResult expand(Vector<Frontier> front, int min, int max, TempGrid tempg0, TempGrid tempg1,
                                   int xGoal, int yGoal) {
        // System.out.println("Expansion for " + Thread.currentThread() + "; min=" + min + "; max=" + max + "; xGoal=" + xGoal + "; yGoal=" + yGoal);
        // boolean trace1 = false;
        // code to run in parallel
        ExpansionResult result = new ExpansionResult();
        boolean reached0 = false;
        boolean reached1 = false;

        // while (!front.isEmpty()) {
        for (int i = min; i < max; i++) {
            int weight, prev_val;
            Frontier f = (Frontier) front.elementAt(i);
            // System.out.println("Element [" + i + "]; (" + f.x + "," + f.y + "," + f.z + ")");
            // front.removeElementAt(0);
            // if (trace1)
            //     if(DEBUG)
            //         System.out.println("X " + f.x + " Y " + f.y + " Z " + f.z + " processing - val "
            //                            + getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y));

            weight = grid.getPoint(f.x,f.y + 1,f.z) + 1;
            prev_val = getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y + 1);
            boolean reached = (f.x == xGoal) && (f.y + 1 == yGoal);
            if ((prev_val > getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight)
                && (weight < OCC) || reached) {
                if (ok(f.x, f.y + 1)) {
                    getCorrectTempg(tempg0, tempg1, f.z).put(getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight,
                                                             f.x, f.y + 1); // looking north
                    if (!reached)
                        result.places.addElement(new Frontier(f.x, f.y + 1, f.z));
                }
            }
            weight = grid.getPoint(f.x + 1,f.y,f.z) + 1;
            prev_val = getCorrectTempg(tempg0, tempg1, f.z).get(f.x + 1, f.y);
            reached = (f.x + 1 == xGoal) && (f.y == yGoal);
            if ((prev_val > getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight)
                && (weight < OCC) || reached) {
                if (ok(f.x + 1, f.y)) {
                    getCorrectTempg(tempg0, tempg1, f.z).put(getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight,
                                                             f.x + 1, f.y); // looking east
                    if (!reached)
                        result.places.addElement(new Frontier(f.x + 1, f.y, f.z));
                }
            }
            weight = grid.getPoint(f.x,f.y - 1,f.z) + 1;
            prev_val = getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y - 1);
            reached = (f.x == xGoal) && (f.y - 1 == yGoal);
            if ((prev_val > getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight)
                && (weight < OCC) || reached) {
                if (ok(f.x, f.y - 1)) {
                    getCorrectTempg(tempg0, tempg1, f.z).put(getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight,
                                                             f.x, f.y - 1); // looking south
                    if (!reached)
                        result.places.addElement(new Frontier(f.x, f.y - 1, f.z));
                }
            }
            weight = grid.getPoint(f.x - 1,f.y,f.z) + 1;
            prev_val = getCorrectTempg(tempg0, tempg1, f.z).get(f.x - 1, f.y);
            reached = (f.x - 1 == xGoal) && (f.y == yGoal);
            if ((prev_val > getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight)
                && (weight < OCC) || reached) {
                if (ok(f.x - 1, f.y)) {
                    getCorrectTempg(tempg0, tempg1, f.z).put(getCorrectTempg(tempg0, tempg1, f.z).get(f.x, f.y) + weight,
                                                             f.x - 1, f.y); // looking west
                    if (!reached)
                        result.places.addElement(new Frontier(f.x - 1, f.y, f.z));
                }
            }
            if (f.z == 0) {
                weight = grid.getPoint(f.x,f.y,1) + 1;
                if ((getCorrectTempg(tempg0, tempg1, 1).get(f.x, f.y) > getCorrectTempg(tempg0, tempg1, 0).get(f.x, f.y))
                    && (weight < OCC)) {
                    getCorrectTempg(tempg0, tempg1, 1).put(getCorrectTempg(tempg0, tempg1, 0).get(f.x, f.y), f.x, f.y);
                    result.places.addElement(new Frontier(f.x, f.y, 1));
                }
            } else {
                weight = grid.getPoint(f.x,f.y,0) + 1;
                if ((getCorrectTempg(tempg0, tempg1, 0).get(f.x, f.y) > getCorrectTempg(tempg0, tempg1, 1).get(f.x, f.y))
                    && (weight < OCC)) {
                    getCorrectTempg(tempg0, tempg1, 0).put(getCorrectTempg(tempg0, tempg1, 1).get(f.x, f.y), f.x, f.y);
                    result.places.addElement(new Frontier(f.x, f.y, 0));
                }
            }

            // Random r = new java.util.Random((int)(Math.random()*1000000));
            // int s = r.nextInt(5);
            // System.out.println("" + Thread.currentThread() + " sleeping for " + s);
        
            // try{Thread.sleep(s * 1000);} catch (Exception e) { System.out.println("no problemo");e.printStackTrace(); }
            // must check if found goal, if so return TRUE
            reached0 = getCorrectTempg(tempg0, tempg1, 0).get(xGoal, yGoal) != TEMP_EMPTY;
            reached1 = getCorrectTempg(tempg0, tempg1, 1).get(xGoal, yGoal) != TEMP_EMPTY;
            if (reached0 && reached1) {
                // if (xGoal, yGoal) can be found in time
                result.reached = true;
                // return result;
                break;
            }
        }
        return result;
    }

    private boolean pathFromOtherSide(TempGrid g0, TempGrid g1, int X, int Y, int Z) {
        boolean ok;
        int Zo;
        Zo = 1 - Z; // other side
        int sqval = getCorrectTempg(g0, g1, Zo).get(X, Y);
        if ((sqval == VIA) || (sqval == BVIA))
            return false;
        ok = (getCorrectTempg(g0, g1, Zo).get(X, Y) <= getCorrectTempg(g0, g1, Z).get(X, Y));
        if (ok)
            ok = (getCorrectTempg(g0, g1, Zo).get(X - 1, Y) < sqval) || (getCorrectTempg(g0, g1, Zo).get(X + 1, Y) < sqval)
            || (getCorrectTempg(g0, g1, Zo).get(X, Y - 1) < sqval) || (getCorrectTempg(g0, g1, Zo).get(X, Y + 1) < sqval);
        return ok;
    }
    
    private int tlength(int x1, int y1, int x2, int y2) {
        int sq = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        return (int) Math.sqrt((double) sq);
    }
    
    private static int deviation(int x1, int y1, int x2, int y2) {
        int xdiff = x2 - x1;
        int ydiff = y2 - y1;
        if (xdiff < 0)
            xdiff = -xdiff;
        if (ydiff < 0)
            ydiff = -ydiff;
        if (xdiff < ydiff)
            return xdiff;
        else
            return ydiff;
    }
    
    public void backtrackFrom(int xGoal, int yGoal, int xStart,
			      int yStart, int trackNo, TempGrid tempg0, TempGrid tempg1, Grid grid) {
        // this method should backtrack from the goal position (xGoal, yGoal)
        // back to the starting position (xStart, yStart) filling in the
        // grid array g with the specified track number trackNo ( + TRACK).
        
        // ***
        // CurrentPos = Goal
        // Loop
        // Find dir to start back from current position
        // Loop
        // Keep going in current dir and Fill in track (update currentPos)
        // Until box number increases in this current dir
        // Until back at starting point
        // ***
//		int count = 100;
        if(DEBUG)System.out.println("Track " + trackNo + " backtrack " + "Length "
                + tlength(xStart, yStart, xGoal, yGoal));
//		boolean trace = false;
        int zGoal;
        int distsofar = 0;
        if (Math.abs(xGoal - xStart) > Math.abs(yGoal - yStart))
            zGoal = 0;
        else
            zGoal = 1;
        if (getCorrectTempg(tempg0, tempg1, zGoal).get(xGoal, yGoal) == TEMP_EMPTY) {
            if(DEBUG) System.out.println("Preferred Layer not reached " + zGoal);
            zGoal = 1 - zGoal;
        }
        int tempY = yGoal;
        int tempX = xGoal;
        int tempZ = zGoal;
        int lastdir = -10;
        while ((tempX != xStart) || (tempY != yStart)) { // PDL: until back
            
            // at starting point
            boolean advanced = false;
            int mind = 0; // minimum direction
            int dir = 0;  // direction to take
            int min_square = 100000;
            int d;  // aux direction
            for (d = 0; d < 4; d++) { // PDL: Find dir to start back from
                // current position
                if ((getCorrectTempg(tempg0, tempg1, tempZ).get(tempX + dx[tempZ][d], tempY + dy[tempZ][d]) < getCorrectTempg(tempg0, tempg1, tempZ).get(tempX, tempY))
		    && (getCorrectTempg(tempg0, tempg1, tempZ).get(tempX + dx[tempZ][d], tempY + dy[tempZ][d]) != TEMP_EMPTY)) {
                    if (getCorrectTempg(tempg0, tempg1, tempZ).get(tempX + dx[tempZ][d], tempY + dy[tempZ][d]) < min_square) {
                        min_square = getCorrectTempg(tempg0, tempg1, tempZ).get(tempX + dx[tempZ][d], tempY + dy[tempZ][d]);
                        mind = d;
                        dir = dx[tempZ][d] * 2 + dy[tempZ][d]; // hashed dir
                        if (lastdir < -2)
                            lastdir = dir;
                        advanced = true;
                    }
                }
            }
            if (advanced)
                distsofar++;
            if(DEBUG)
                System.out.println("Backtracking "+tempX+" "+tempY+" "+tempZ+
                        " "+getCorrectTempg(tempg0, tempg1, tempZ).get(tempX, tempY)+" "+advanced+" "+mind);
	    int tXYZ = grid.getPoint(tempX,tempY,tempZ);
            if (pathFromOtherSide(tempg0, tempg1, tempX, tempY, tempZ)
            && ((mind > 1)
            && // not preferred dir for this layer
                    (distsofar > 15)
                    && (tlength(tempX, tempY, xStart, yStart) > 15) ||
                    // (deviation(tempX,tempY,xStart,yStart) > 3) ||
                    (!advanced && ((tXYZ != VIA)
                    && (tXYZ != BVIA))))) {
                int tZ = 1 - tempZ; // 0 if 1, 1 if 0
                int viat;
                if (advanced)
                    viat = VIA;
                else
                    viat = BVIA; // BVIA is nowhere else to go
                // mark via
                getCorrectTempg(tempg0, tempg1, tempZ).put(viat, tempX, tempY);
                grid.setPoint(tempX,tempY,tempZ,viat);
                if(DEBUG)grid.setDebugPoint(tempX,tempY,tempZ,trackNo);
                tempZ = tZ;
                // and the other side
                getCorrectTempg(tempg0, tempg1, tempZ).put(viat, tempX, tempY);
                grid.setPoint(tempX,tempY,tempZ,viat);
                if(DEBUG)grid.setDebugPoint(tempX,tempY,tempZ,trackNo);
                num_vias++;
                if (!advanced)
                    forced_vias++;
                if (advanced)
                    if(DEBUG)
                        System.out.println("Via " + distsofar + " "
                                + tlength(tempX, tempY, xStart, yStart) + " "
                                + deviation(tempX, tempY, xStart, yStart));
                distsofar = 0;
            } else {
                if (tXYZ < OCC) {
                    // PDL: fill in track unless connection point
                    grid.setPoint(tempX,tempY,tempZ,TRACK);
                    if(DEBUG)grid.setDebugPoint(tempX,tempY,tempZ,trackNo);
                } else if (tXYZ == OCC) {
                    if(DEBUG)grid.setDebugPoint(tempX,tempY,tempZ,OCC);
                    if(DEBUG)grid.setDebugPoint(tempX,tempY,1-tempZ,OCC);
                }
                tempX = tempX + dx[tempZ][mind]; // PDL: updating current
                // position on x axis
                tempY = tempY + dy[tempZ][mind]; // PDL: updating current
                // position on y axis
            }
            lastdir = dir;
        }
        if(DEBUG) System.out.println("Track " + trackNo + " completed");
    }
    
    public boolean connect(int xs, int ys, int xg, int yg, int netNo, Grid grid, LeeThread lt) {
        // calls expandFrom and backtrackFrom to create connection
        // This is the only real change needed to make the program
        // transactional.
        // Instead of using the grid 'in place' to do the expansion, we take a
        // copy
        // but the backtrack writes to the original grid.
        // This is not a correctness issue. The transactions would still
        // complete eventually without it.
        // However the expansion writes are only temporary and do not logically
        // conflict.
        // There is a question as to whether a copy is really necessary as a
        // transaction will anyway create
        // its own copy. if we were then to distinguish between writes not to be
        // committed (expansion) and
        // those to be committed (backtrack), we would not need an explicit
        // copy.
        // Taking the copy is not really a computational(time) overhead because
        // it avoids the grid 'reset' phase
        // needed if we do the expansion in place.
	boolean found;
	boolean conflictNoted = false;
	while (true) {
            TempGrid tempg0 = new TempGrid(GRID_SIZE, GRID_SIZE);
            TempGrid tempg1 = new TempGrid(GRID_SIZE, GRID_SIZE);

	    found = false;
	    boolean committed = false;
	    try {
		// read-only tx on expansion
		Transaction.begin(false);
		lt.roTransactions++;
		// call the expansion method to return found/not found boolean
		found = expandFromTo(xs, ys, xg, yg, GRID_SIZE * 5, tempg0, tempg1, grid);
		if (found) {
		    // Transaction.commitAndBegin(false);
		    lt.rwTransactions++;
		    backtrackFrom(xg, yg, xs, ys, netNo, tempg0, tempg1, grid); // call the backtrack method
		    Transaction.commit();
                    committed = true;
		    if(DEBUG) System.out.println("Target (" + xg + ", " + yg + ")... FOUND!");
		} // print outcome of expansion method
		else {
		    // Transaction.commit();
		    if(DEBUG) System.out.println("Failed to route " + xs + " " + ys + " to " + xg
						 + "  " + yg);
		    failures++;
		}
		break;
	    } catch (CommitException e) {
		if (!conflictNoted) {
		    lt.conflicts++;
		    conflictNoted = true;
		}
		// will retry
		lt.restarts++;
	    } finally {
		if (!committed) {
		    Transaction.abort();
		}
	    }
	}
//         if(DEBUG || VIEW) {
//             dispGrid(grid, 0); // print the grid to screen
//             dispGrid(grid, 1); // print the grid to screen
//             view.repaint();
//         }
        return found;
    }
    
    public void dispGrid(Grid g, int z) {
        int laycol;
        if (z==0) laycol = magenta; else laycol=green;
        for (int y = GRID_SIZE-1; y>=0; y--) {
            for (int x = 0; x<GRID_SIZE; x++) {
                int gg = g.getPoint(x, y, z);
                if (gg==OCC) { view.point(x,y,cyan); continue; }
                if (gg==VIA) { view.point(x,y,yellow); continue; }
                if (gg==BVIA) { view.point(x,y,red); continue; }
                if (gg==TRACK) {view.point(x,y,laycol); continue; } //
            }
        }
    }
    
    
    public LeeThread createThread() {
        return createThread(0);
    }
    public LeeThread createThread(int which) {
        try {
            LeeThread leeThread = new LeeThread(this);
            return leeThread;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
    
    public void report() {
        //Open GUI view of PCB
        //view.display();
        //Print the PCB in ASCII, output to file
        //grid.printLayout(true);
        System.out.println("Total Tracks " + netNo + " Failures " + failures
                + " Vias " + num_vias + " Forced Vias " + forced_vias);
    }
    
    public void sanityCheck() {
        int found = 0, missing = 0;
        // Check debugGrid that the routes in debugQueue have been laid
        if(DEBUG) {
            System.out.println("DEBUG: Starting sanity check");
            while(debugQueue.next!=null) {
                WorkQueue n = debugQueue.deQueue();
                if(!grid.findTrack(n.x1, n.y1, n.x2, n.y2, n.nn)) {
                    System.out.println("ERROR: Missing track " +n.nn);
                    missing++;
                } else {
                    found++;
                }
            }
            System.out.println("DEBUG: found "+found+" missing "+missing);
        }
        
    }
    
    public static long initialTime;
    
    private static ExecutorService threadPool;

    public static void main(String [] args) {
        if(args.length!=2) {
            System.out.println("Params: [numthreads] [input-file]");
            System.exit(-1);
        }
        int numThreads = Integer.parseInt(args[0]);
        String filename = args[1];
        LeeRouter lr = new LeeRouter(filename, false, false, false);
        
        int numMillis = 600000;
        
        // setup the thread pool
        threadPool = Executors.newFixedThreadPool(10);

//		 Set up the benchmark
        long startTime = 0;
        long currentTime = 0;
        long watchdogInterval = 1000;
        boolean exitByTimeout = false;

        LeeThread[] thread = new LeeThread[numThreads];
// 	view.display();
        try {
            for (int i = 0; i < numThreads; i++)
                thread[i] = lr.createThread();
            startTime = System.currentTimeMillis();
	    initialTime = startTime;
            // lastSample = startTime;
            for (int i = 0; i < numThreads; i++)
                thread[i].start();
            currentTime = System.currentTimeMillis();
            exitByTimeout = monitorBenchmarkToEnd(numMillis, startTime, currentTime, watchdogInterval, exitByTimeout, thread);
            
            LeeThread.stop = true; // notify threads to stop
            for (int i = 0; i < numThreads; i++) {
                thread[i].join();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }
        long elapsedTime = startTime - currentTime;
        threadPool.shutdown();
        // int throughput = (int) (LeeRouter.netNo / elapsedTime);
        //System.out.println("Numthreads: " + numThreads);
        //System.out.println("Throughput:  " + throughput);
        //System.out.println("ElapsedTime: " + elapsedTime);
	// CommitStats.printAllStats();
        lr.report(numThreads,startTime,exitByTimeout, thread);
 	lr.sanityCheck();
        if(VIEW) {
            lr.dispGrid(lr.grid, 0); // print the grid to screen
            lr.dispGrid(lr.grid, 1); // print the grid to screen
            view.repaint();
	    view.display();
        }
    }
    
    private static boolean monitorBenchmarkToEnd(int numMillis, long startTime, long currentTime, long watchdogInterval, boolean exitByTimeout, LeeThread[] thread) throws InterruptedException {
        while (!exitByTimeout) {
            boolean exit = true;
            Thread.sleep(watchdogInterval);
            
            // Any threads with work left?
            for (LeeThread i : thread)
                if (i.finished != true) {
                    exit = false;
                    break;
                }
            if (!exit)
                currentTime = System.currentTimeMillis();
            else
                break;
            
            // Timeout?
            if (currentTime - startTime > numMillis) {
                exitByTimeout = true;
            }
        }
        return exitByTimeout;
    }
    
    private static void report(int numThreads, long startTime,
			       boolean timeout, LeeThread [] threads) {
        
        long stopTime = System.currentTimeMillis();
        long elapsed = stopTime - startTime;

        int roTransactionsSum = 0;
        int rwTransactionsSum = 0;
        int conflictsSum = 0;
        int restartsSum = 0;
        long longestTrackLayTime = 0;
        long shortestTrackLayTime = Long.MAX_VALUE;
        long totalWorkTime = 0;

        for (int i = 0; i < numThreads; i++) {
            roTransactionsSum+= threads[i].roTransactions;
            rwTransactionsSum+= threads[i].rwTransactions;
            conflictsSum+= threads[i].conflicts;
            restartsSum+= threads[i].restarts;
            if (threads[i].longestTrackLayTime > longestTrackLayTime) {
                longestTrackLayTime = threads[i].longestTrackLayTime;
            }
            if (threads[i].shortestTrackLayTime < shortestTrackLayTime) {
                shortestTrackLayTime = threads[i].shortestTrackLayTime;
            }
            totalWorkTime+= threads[i].totalWorkTime;
        }

        System.out.println(numThreads+", "+elapsed+", "+netNo+", "+failures+", "+roTransactionsSum+", "
                           +rwTransactionsSum+", "+conflictsSum+", "+restartsSum+", "+longestTrackLayTime+", "
                           +shortestTrackLayTime+", "+totalWorkTime);

        //benchmark.report();
        //             System.out.println("Elapsed time: " + elapsed + " seconds.");
        //             System.out.println("----------------------------------------");
    }
    
    static final TempGrid getCorrectTempg(TempGrid tempg0, TempGrid tempg1, int z) {
	return ((z == 0) ? tempg0 : tempg1);
    }


    static final class ExpansionTask extends NestedWorkUnit<ExpansionResult> {
        final Vector<Frontier> front;
        final int min, max, xGoal, yGoal;
        final TempGrid tempg0, tempg1;
        final LeeRouter lr;

        // front is shared, so can only acces to read.  Expand places in [min;max[
        ExpansionTask(Vector<Frontier> front, int min, int max, TempGrid tempg0, TempGrid tempg1, int xGoal, int yGoal,
                      LeeRouter lr) {
            this.front = front;
            this.min = min;
            this.max = max;
            this.tempg0 = tempg0;
            this.tempg1 = tempg1;
            this.xGoal = xGoal;
            this.yGoal = yGoal;
            this.lr = lr;
        }

        @Override
        public ExpansionResult execute() throws Throwable {
            // make nested transactions for each subset within [min;max[

            return lr.expand(front, min, max, tempg0, tempg1, xGoal, yGoal);
        }
    }

    static final class ExpansionResult {
        Vector<Frontier> places = new Vector<Frontier>();
        boolean reached = false;
    }
}
