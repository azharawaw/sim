//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.controller;

public interface DragConstants {

  // Motions
  static final int DRAG_ROTATE = 0;
  static final int DRAG_TRANSLATE = 1;
  static final int DRAG_SCALE = 2;
  static final int DRAG_FLY = 3;
  static final int DRAG_ZOOM = 4;
  static final int DRAG_ORBIT = 5;
  static final int DRAG_LOOK = 6;
  static final int DRAG_PICK = 7; // For testing picking

  // Actions
  static final int DRAG_ACTION = 0;
  static final int DRAG_ANNOTATE = 1;
  static final int DRAG_SELECT = 2;
}
