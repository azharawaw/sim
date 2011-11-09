/*
 * Copyright (c) 2002-2011 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.opengl;

import org.lwjgl.util.generator.*;
import org.lwjgl.util.generator.opengl.*;

import java.nio.Buffer;

public interface ARB_base_instance {

	@Reuse("GL42")
	void glDrawArraysInstancedBaseInstance(@GLenum int mode,
	                                       int first,
	                                       @GLsizei int count,
	                                       @GLsizei int primcount,
	                                       @GLuint int baseinstance);

	@Reuse("GL42")
	void glDrawElementsInstancedBaseInstance(@GLenum int mode,
	                                         @AutoSize("indices") @GLsizei int count,
	                                         @AutoType("indices") @GLenum int type,
	                                         @Const
	                                         @BufferObject(BufferKind.ElementVBO)
	                                         @GLubyte
	                                         @GLushort
	                                         @GLuint Buffer indices,
	                                         @GLsizei int primcount,
	                                         @GLuint int baseinstance);

	@Reuse("GL42")
	void glDrawElementsInstancedBaseVertexBaseInstance(@GLenum int mode,
	                                                   @AutoSize("indices") @GLsizei int count,
	                                                   @AutoType("indices") @GLenum int type,
	                                                   @Const
	                                                   @BufferObject(BufferKind.ElementVBO)
	                                                   @GLubyte
	                                                   @GLushort
	                                                   @GLuint Buffer indices,
	                                                   @GLsizei int primcount,
	                                                   int basevertex,
	                                                   @GLuint int baseinstance);

}