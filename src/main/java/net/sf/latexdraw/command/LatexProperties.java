/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2020 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.command;

import net.sf.latexdraw.view.latex.VerticalPosition;
import org.jetbrains.annotations.Nullable;

/**
 * This enumeration defines the different LaTeX properties that can be modified.
 * @author Arnaud Blouin
 */
public enum LatexProperties {
	/** The scale of the drawing. */
	SCALE {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof Double;
		}
	},
	/** Modification of the comments. */
	COMMENT {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof String;
		}
	},
	/** Modification of the packages. */
	PACKAGES {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof String;
		}
	},
	/** Modification of the caption. */
	CAPTION {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof String;
		}
	},
	/** Modification of the label. */
	LABEL {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof String;
		}
	},
	/** Modification of the vertical position. */
	POSITION_VERTICAL {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof VerticalPosition;
		}
	},
	/** Modification of the horizontal position. */
	POSITION_HORIZONTAL {
		@Override
		public boolean isValueSupported(final @Nullable Object value) {
			return value instanceof Boolean;
		}
	};

	/**
	 * @param value The value to test.
	 * @return True: the given value corresponds to the excepted value of the property.
	 */
	public abstract boolean isValueSupported(final @Nullable Object value);
}
