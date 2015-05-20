package java.io;

/*This is an emulation class because gwt does not have it*/

   /*
    * Copyright 1996-2006 Sun Microsystems, Inc.  All Rights Reserved.
    * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
    *
    * This code is free software; you can redistribute it and/or modify it
    * under the terms of the GNU General Public License version 2 only, as
    * published by the Free Software Foundation.  Sun designates this
    * particular file as subject to the "Classpath" exception as provided
    * by Sun in the LICENSE file that accompanied this code.
    *
    * This code is distributed in the hope that it will be useful, but WITHOUT
    * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
    * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
14    * version 2 for more details (a copy is included in the LICENSE file that
15    * accompanied this code).
16    *
17    * You should have received a copy of the GNU General Public License version
18    * 2 along with this work; if not, write to the Free Software Foundation,
19    * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
20    *
21    * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
22    * CA 95054 USA or visit www.sun.com if you need additional information or
23    * have any questions.
24    */
 

  /**
30    * Abstract class for reading character streams.  The only methods that a
31    * subclass must implement are read(char[], int, int) and close().  Most
32    * subclasses, however, will override some of the methods defined here in order
33    * to provide higher efficiency, additional functionality, or both.
34    *
35    *
36    * @see BufferedReader
37    * @see   LineNumberReader
38    * @see CharArrayReader
39    * @see InputStreamReader
40    * @see   FileReader
41    * @see FilterReader
42    * @see   PushbackReader
43    * @see PipedReader
44    * @see StringReader
45    * @see Writer
46    *
47    * @author      Mark Reinhold
48    * @since       JDK1.1
49    */

 
public abstract class Reader /* implements Readable, Closeable */ {
 
       /**
54        * The object used to synchronize operations on this stream.  For
55        * efficiency, a character-stream object may use an object other than
56        * itself to protect critical sections.  A subclass should therefore use
57        * the object in this field rather than <tt>this</tt> or a synchronized
58        * method.
59        */
       protected Object lock;
       /**
63        * Creates a new character-stream reader whose critical sections will
64        * synchronize on the reader itself.
65        */
       protected Reader() {
           this.lock = this;
       }
  
       /**
71        * Creates a new character-stream reader whose critical sections will
72        * synchronize on the given object.
73        *
74        * @param lock  The Object to synchronize on.
        */
       protected Reader(Object lock) {
           if (lock == null) {
              throw new NullPointerException();
           }
          this.lock = lock;
       }
  
      /**
84        * Attempts to read characters into the specified character buffer.
85        * The buffer is used as a repository of characters as-is: the only
86        * changes made are the results of a put operation. No flipping or
87        * rewinding of the buffer is performed.
88        *
89        * @param target the buffer to read characters into
90        * @return The number of characters added to the buffer, or
91        *         -1 if this source of characters is at its end
92        * @throws IOException if an I/O error occurs
93        * @throws NullPointerException if target is null
94        * @throws ReadOnlyBufferException if target is a read only buffer
95        * @since 1.5
96        */
 
  /*     
       public int read(java.nio.CharBuffer target) throws IOException {
          int len = target.remaining();
           char[] cbuf = new char[len];
           int n = read(cbuf, 0, len);
           if (n > 0)
               target.put(cbuf, 0, n);
           return n;
       }
  
   */
       /**
107        * Reads a single character.  This method will block until a character is
108        * available, an I/O error occurs, or the end of the stream is reached.
109        *
110        * <p> Subclasses that intend to support efficient single-character input
111        * should override this method.
112        *
113        * @return     The character read, as an integer in the range 0 to 65535
114        *             (<tt>0x00-0xffff</tt>), or -1 if the end of the stream has
115        *             been reached
116        *
117        * @exception  IOException  If an I/O error occurs
118        */
       public int read() throws IOException {
           char cb[] = new char[1];
           if (read(cb, 0, 1) == -1)
               return -1;
           else
               return cb[0];
       }
   
       /**
128        * Reads characters into an array.  This method will block until some input
129        * is available, an I/O error occurs, or the end of the stream is reached.
130        *
131        * @param       cbuf  Destination buffer
132        *
133        * @return      The number of characters read, or -1
134        *              if the end of the stream
135        *              has been reached
136        *
137        * @exception   IOException  If an I/O error occurs
138        */
       public int read(char cbuf[]) throws IOException {
           return read(cbuf, 0, cbuf.length);
       }
  
       /**
144        * Reads characters into a portion of an array.  This method will block
145        * until some input is available, an I/O error occurs, or the end of the
146        * stream is reached.
147        *
148        * @param      cbuf  Destination buffer
149        * @param      off   Offset at which to start storing characters
150        * @param      len   Maximum number of characters to read
151        *
152        * @return     The number of characters read, or -1 if the end of the
153        *             stream has been reached
154        *
155        * @exception  IOException  If an I/O error occurs
156        */
       abstract public int read(char cbuf[], int off, int len) throws IOException;
   
       /** Maximum skip-buffer size */
       private static final int maxSkipBufferSize = 8192;
  
       /** Skip buffer, null until allocated */
       private char skipBuffer[] = null;
   
      /**
166        * Skips characters.  This method will block until some characters are
167        * available, an I/O error occurs, or the end of the stream is reached.
168        *
169        * @param  n  The number of characters to skip
170        *
171        * @return    The number of characters actually skipped
172        *
173        * @exception  IllegalArgumentException  If <code>n</code> is negative.
174        * @exception  IOException  If an I/O error occurs
175        */
       public long skip(long n) throws IOException {
           if (n < 0L)
               throw new IllegalArgumentException("skip value is negative");
           int nn = (int) Math.min(n, maxSkipBufferSize);
           synchronized (lock) {
               if ((skipBuffer == null) || (skipBuffer.length < nn))
                   skipBuffer = new char[nn];
               long r = n;
               while (r > 0) {
                   int nc = read(skipBuffer, 0, (int)Math.min(r, nn));
                  if (nc == -1)
                       break;
                   r -= nc;
               }
               return n - r;
           }
       }
   
      /**
195        * Tells whether this stream is ready to be read.
196        *
197        * @return True if the next read() is guaranteed not to block for input,
198        * false otherwise.  Note that returning false does not guarantee that the
199        * next read will block.
200        *
201        * @exception  IOException  If an I/O error occurs
202        */
       public boolean ready() throws IOException {
           return false;
       }
       /**
208        * Tells whether this stream supports the mark() operation. The default
209        * implementation always returns false. Subclasses should override this
210        * method.
211        *
212        * @return true if and only if this stream supports the mark operation.
213        */
       public boolean markSupported() {
           return false;
       }
   
       /**
219        * Marks the present position in the stream.  Subsequent calls to reset()
220        * will attempt to reposition the stream to this point.  Not all
221        * character-input streams support the mark() operation.
222        *
223        * @param  readAheadLimit  Limit on the number of characters that may be
224        *                         read while still preserving the mark.  After
225        *                         reading this many characters, attempting to
226        *                         reset the stream may fail.
227        *
228        * @exception  IOException  If the stream does not support mark(),
229        *                          or if some other I/O error occurs
230        */
       public void mark(int readAheadLimit) throws IOException {
           throw new IOException("mark() not supported");
       }
   
       /**
236        * Resets the stream.  If the stream has been marked, then attempt to
237        * reposition it at the mark.  If the stream has not been marked, then
238        * attempt to reset it in some way appropriate to the particular stream,
239        * for example by repositioning it to its starting point.  Not all
240        * character-input streams support the reset() operation, and some support
241        * reset() without supporting mark().
242        *
243        * @exception  IOException  If the stream has not been marked,
244        *                          or if the mark has been invalidated,
245        *                          or if the stream does not support reset(),
246        *                          or if some other I/O error occurs
247        */
      public void reset() throws IOException {
           throw new IOException("reset() not supported");
       }
      /**
253        * Closes the stream and releases any system resources associated with
254        * it.  Once the stream has been closed, further read(), ready(),
255        * mark(), reset(), or skip() invocations will throw an IOException.
256        * Closing a previously closed stream has no effect.
257        *
258        * @exception  IOException  If an I/O error occurs
259        */
       abstract public void close() throws IOException;
   
  }