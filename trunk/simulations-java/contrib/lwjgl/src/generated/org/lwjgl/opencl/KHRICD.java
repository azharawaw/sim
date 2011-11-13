/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import org.lwjgl.*;
import java.nio.*;

public final class KHRICD {

	/**
	 * Accepted as &lt;param_name&gt; to the function clGetPlatformInfo 
	 */
	public static final int CL_PLATFORM_ICD_SUFFIX_KHR = 0x920;

	/**
	 * Returned by clGetPlatformIDs when no platforms are found 
	 */
	public static final int CL_PLATFORM_NOT_FOUND_KHR = 0xFFFFFC17;

	private KHRICD() {}

	public static int clIcdGetPlatformIDsKHR(PointerBuffer platforms, IntBuffer num_platforms) {
		long function_pointer = CLCapabilities.clIcdGetPlatformIDsKHR;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (platforms != null)
			BufferChecks.checkDirect(platforms);
		if (num_platforms != null)
			BufferChecks.checkBuffer(num_platforms, 1);
		int __result = nclIcdGetPlatformIDsKHR((platforms == null ? 0 : platforms.remaining()), platforms != null ? platforms.getBuffer() : null, platforms != null ? platforms.positionByte() : 0, num_platforms, num_platforms != null ? num_platforms.position() : 0, function_pointer);
		return __result;
	}
	static native int nclIcdGetPlatformIDsKHR(int platforms_num_entries, ByteBuffer platforms, int platforms_position, IntBuffer num_platforms, int num_platforms_position, long function_pointer);
}
