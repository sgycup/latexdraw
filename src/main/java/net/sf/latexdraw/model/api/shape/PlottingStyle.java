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
package net.sf.latexdraw.model.api.shape;

import java.util.Arrays;
import net.sf.latexdraw.view.pst.PSTricksConstants;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the different style of labels.
 * @author Arnaud Blouin
 */
public enum PlottingStyle {
	ALL {
		@Override
		public boolean isX() {
			return true;
		}

		@Override
		public boolean isY() {
			return true;
		}

		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_LABELS_DISPLAYED_ALL;
		}
	}, X {
		@Override
		public boolean isX() {
			return true;
		}

		@Override
		public boolean isY() {
			return false;
		}

		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_LABELS_DISPLAYED_X;
		}
	}, Y {
		@Override
		public boolean isX() {
			return false;
		}

		@Override
		public boolean isY() {
			return true;
		}

		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_LABELS_DISPLAYED_Y;
		}
	}, NONE {
		@Override
		public boolean isX() {
			return false;
		}

		@Override
		public boolean isY() {
			return false;
		}

		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_LABELS_DISPLAYED_NONE;
		}
	};

	/**
	 * The style that corresponds to the given string.
	 * @param style The style to check.
	 * @return The corresponding style or ALL.
	 */
	public static @NotNull PlottingStyle getStyle(final String style) {
		return Arrays.stream(values()).filter(it -> it.toString().equals(style)).findFirst().orElse(ALL);
	}

	@Override
	public String toString() {
		return getPSTToken();
	}

	/**
	 * @return The PST token corresponding to the labels style.
	 */
	public abstract @NotNull String getPSTToken();

	/**
	 * @return True if the current style supports the X-axis.
	 */
	public abstract boolean isX();

	/**
	 * @return True if the current style supports the Y-axis.
	 */
	public abstract boolean isY();
}
