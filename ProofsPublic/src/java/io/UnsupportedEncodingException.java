/*
2  * @(#)UnsupportedEncodingException.java 1.16 03/12/19
3  *
4  * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
5  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
6  */
package java.io;

 /**
10  * The Character Encoding is not supported.
11  *
12  * @author Asmus Freytag
13  * @version 1.16, 12/19/03
14  * @since JDK1.1
15  */
 public class UnsupportedEncodingException
     extends IOException  
 {
     /**
20      * Constructs an UnsupportedEncodingException without a detail message.
21      */
     public UnsupportedEncodingException() {
         super();
     }
 
     /**
27      * Constructs an UnsupportedEncodingException with a detail message.
28      * @param s Describes the reason for the exception.
29      */
     public UnsupportedEncodingException(String   s) {
         super(s);
     }
 }

