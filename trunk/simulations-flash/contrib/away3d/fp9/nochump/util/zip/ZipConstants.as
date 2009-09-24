/*
nochump.util.zip.ZipConstants
Copyright (C) 2007 David Chang (dchang@nochump.com)

This file is part of nochump.util.zip.

nochump.util.zip is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

nochump.util.zip is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/
package nochump.util.zip {
	
	internal class ZipConstants {
		
		/* The local file header */
		internal static const LOCSIG:uint = 0x04034b50;	// "PK\003\004"
		internal static const LOCHDR:uint = 30;	// LOC header size
		internal static const LOCVER:uint = 4;	// version needed to extract
		//internal static const LOCFLG:uint = 6; // general purpose bit flag
		//internal static const LOCHOW:uint = 8; // compression method
		//internal static const LOCTIM:uint = 10; // modification time
		//internal static const LOCCRC:uint = 14; // uncompressed file crc-32 value
		//internal static const LOCSIZ:uint = 18; // compressed size
		//internal static const LOCLEN:uint = 22; // uncompressed size
		internal static const LOCNAM:uint = 26; // filename length
		//internal static const LOCEXT:uint = 28; // extra field length
		
		/* The Data descriptor */
		internal static const EXTSIG:uint = 0x08074b50;	// "PK\007\008"
		internal static const EXTHDR:uint = 16;	// EXT header size
		//internal static const EXTCRC:uint = 4; // uncompressed file crc-32 value
		//internal static const EXTSIZ:uint = 8; // compressed size
		//internal static const EXTLEN:uint = 12; // uncompressed size
		
		/* The central directory file header */
		internal static const CENSIG:uint = 0x02014b50;	// "PK\001\002"
		internal static const CENHDR:uint = 46;	// CEN header size
		//internal static const CENVEM:uint = 4; // version made by
		internal static const CENVER:uint = 6; // version needed to extract
		//internal static const CENFLG:uint = 8; // encrypt, decrypt flags
		//internal static const CENHOW:uint = 10; // compression method
		//internal static const CENTIM:uint = 12; // modification time
		//internal static const CENCRC:uint = 16; // uncompressed file crc-32 value
		//internal static const CENSIZ:uint = 20; // compressed size
		//internal static const CENLEN:uint = 24; // uncompressed size
		internal static const CENNAM:uint = 28; // filename length
		//internal static const CENEXT:uint = 30; // extra field length
		//internal static const CENCOM:uint = 32; // comment length
		//internal static const CENDSK:uint = 34; // disk number start
		//internal static const CENATT:uint = 36; // internal file attributes
		//internal static const CENATX:uint = 38; // external file attributes
		internal static const CENOFF:uint = 42; // LOC header offset
		
		/* The entries in the end of central directory */
		internal static const ENDSIG:uint = 0x06054b50;	// "PK\005\006"
		internal static const ENDHDR:uint = 22; // END header size
		//internal static const ENDSUB:uint = 8; // number of entries on this disk
		internal static const ENDTOT:uint = 10;	// total number of entries
		//internal static const ENDSIZ:uint = 12; // central directory size in bytes
		internal static const ENDOFF:uint = 16; // offset of first CEN header
		//internal static const ENDCOM:uint = 20; // zip file comment length
		
		/* Compression methods */
		internal static const STORED:uint = 0;
		internal static const DEFLATED:uint = 8;
		
	}

}