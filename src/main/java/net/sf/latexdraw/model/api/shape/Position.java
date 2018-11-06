/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2018 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.model.api.shape;

import java.awt.geom.Rectangle2D;
import net.sf.latexdraw.model.ShapeFactory;

/**
 * The different cardinal points.
 * @author Arnaud Blouin
 */
public enum Position {
	NORTH {
		@Override
		public Position getOpposite() {
			return SOUTH;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getCenterX(), bound.getMinY());
		}
	}, SOUTH {
		@Override
		public Position getOpposite() {
			return NORTH;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getCenterX(), bound.getMaxY());
		}
	}, EAST {
		@Override
		public Position getOpposite() {
			return WEST;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getMaxX(), bound.getCenterY());
		}
	}, WEST {
		@Override
		public Position getOpposite() {
			return EAST;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getMinX(), bound.getCenterY());
		}
	}, NE {
		@Override
		public Position getOpposite() {
			return SW;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getMaxX(), bound.getMinY());
		}
	}, NW {
		@Override
		public Position getOpposite() {
			return SE;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getMinX(), bound.getMinY());
		}
	}, SE {
		@Override
		public Position getOpposite() {
			return NW;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getMaxX(), bound.getMaxY());
		}
	}, SW {
		@Override
		public Position getOpposite() {
			return NE;
		}

		@Override
		public Point getReferencePoint(final Rectangle2D bound) {
			return ShapeFactory.INST.createPoint(bound.getMinX(), bound.getMaxY());
		}
	};

	/**
	 * @return True if the given position is south oriented.
	 */
	public boolean isSouth() {
		return this == SOUTH || this == SE || this == SW;
	}

	/**
	 * @return True if the given position is north oriented.
	 */
	public boolean isNorth() {
		return this == NORTH || this == NE || this == NW;
	}

	/**
	 * @return True if the given position is east oriented.
	 */
	public boolean isEast() {
		return this == EAST || this == NE || this == SE;
	}

	/**
	 * @return True if the given position is west oriented.
	 */
	public boolean isWest() {
		return this == WEST || this == SW || this == NW;
	}

	/**
	 * @return The opposite position of the current position.
	 */
	public abstract Position getOpposite();

	public abstract Point getReferencePoint(final Rectangle2D bound);
}