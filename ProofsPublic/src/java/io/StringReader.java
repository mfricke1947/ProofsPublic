package java.io;

/*This is an emulation class because gwt does not have it*/

   /*
2    * Copyright 1996-2005 Sun Microsystems, Inc.  All Rights Reserved.
3    * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
4    *
5    * This code is free software; you can redistribute it and/or modify it
6    * under the terms of the GNU General Public License version 2 only, as
7    * published by the Free Software Foundation.  Sun designates this
8    * particular file as subject to the "Classpath" exception as provided
9    * by Sun in the LICENSE file that accompanied this code.
10    *
11    * This code is distributed in the hope that it will be useful, but WITHOUT
12    * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
13    * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
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
30    * A character stream whose source is a string.
31    *
32    * @author      Mark Reinhold
33    * @since       JDK1.1
34    */

 

   public class StringReader extends Reader {

   
       private String str;
       private int length;
       private int next = 0;
       private int mark = 0;
   
       /**
44        * Creates a new string reader.
45        *
46        * @param s  String providing the character stream.
47        */
       public StringReader(String s) {
           this.str = s;
           this.length = s.length();
       }
   
       /** Check to make sure that the stream has not been closed */
       private void ensureOpen() throws IOException {
           if (str == null)
               throw new IOException("Stream closed");
       }
   
       /**
60        * Reads a single character.
61        *
62        * @return     The character read, or -1 if the end of the stream has been
63        *             reached
64        *
65        * @exception  IOException  If an I/O error occurs
66        */
       public int read() throws IOException {
           synchronized (lock) {
               ensureOpen();
               if (next >= length)
                   return -1;
               return str.charAt(next++);
           }
       }
   
      /**
77        * Reads characters into a portion of an array.
78        *
79        * @param      cbuf  Destination buffer
80        * @param      off   Offset at which to start writing characters
81        * @param      len   Maximum number of characters to read
82        *
83        * @return     The number of characters read, or -1 if the end of the
84        *             stream has been reached
85        *
86        * @exception  IOException  If an I/O error occurs
87        */
       public int read(char cbuf[], int off, int len) throws IOException {
           synchronized (lock) {
               ensureOpen();
               if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                   ((off + len) > cbuf.length) || ((off + len) < 0)) {
                   throw new IndexOutOfBoundsException();
               } else if (len == 0) {
                   return 0;
               }
               if (next >= length)
                   return -1;
               int n = Math.min(length - next, len);
              str.getChars(next, next + n, cbuf, off);
               next += n;
               return n;
           }
       }
  
       /**
107        * Skips the specified number of characters in the stream. Returns
108        * the number of characters that were skipped.
109        *
110        * <p>The <code>ns</code> parameter may be negative, even though the
111        * <code>skip</code> method of the {@link Reader} superclass throws
112        * an exception in this case. Negative values of <code>ns</code> cause the
113        * stream to skip backwards. Negative return values indicate a skip
114        * backwards. It is not possible to skip backwards past the beginning of
115        * the string.
116        *
117        * <p>If the entire string has been read or skipped, then this method has
118        * no effect and always returns 0.
119        *
120        * @exception  IOException  If an I/O error occurs
121        */
       public long skip(long ns) throws IOException {
           synchronized (lock) {
               ensureOpen();
               if (next >= length)
                   return 0;
               // Bound skip by beginning and end of the source
               long n = Math.min(length - next, ns);
               n = Math.max(-next, n);
               next += n;
               return n;
           }
       }
   
       /**
136        * Tells whether this stream is ready to be read.
137        *
138        * @return True if the next read() is guaranteed not to block for input
139        *
140        * @exception  IOException  If the stream is closed
141        */
       public boolean ready() throws IOException {
           synchronized (lock) {
           ensureOpen();
           return true;
           }
       }
   
       /**
150        * Tells whether this stream supports the mark() operation, which it does.
151        */
       public boolean markSupported() {
           return true;
       }
   
       /**
157        * Marks the present position in the stream.  Subsequent calls to reset()
158        * will reposition the stream to this point.
159        *
160        * @param  readAheadLimit  Limit on the number of characters that may be
161        *                         read while still preserving the mark.  Because
162        *                         the stream's input comes from a string, there
163        *                         is no actual limit, so this argument must not
164        *                         be negative, but is otherwise ignored.
165        *
166        * @exception  IllegalArgumentException  If readAheadLimit is < 0
167        * @exception  IOException  If an I/O error occurs
168        */
       public void mark(int readAheadLimit) throws IOException {
           if (readAheadLimit < 0){
               throw new IllegalArgumentException("Read-ahead limit < 0");
           }
           synchronized (lock) {
               ensureOpen();
               mark = next;
           }
       }
  
       /**
180        * Resets the stream to the most recent mark, or to the beginning of the
181        * string if it has never been marked.
182        *
183        * @exception  IOException  If an I/O error occurs
184        */
       public void reset() throws IOException {
           synchronized (lock) {
               ensureOpen();
               next = mark;
          }
       }
   
       /**
193        * Closes the stream and releases any system resources associated with
194        * it. Once the stream has been closed, further read(),
195        * ready(), mark(), or reset() invocations will throw an IOException.
196        * Closing a previously closed stream has no effect.
197        */
       public void close() {
           str = null;
       }
   }