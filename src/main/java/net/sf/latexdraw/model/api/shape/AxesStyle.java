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
import java.util.ResourceBundle;
import net.sf.latexdraw.view.pst.PSTricksConstants;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the different kinds of axes.
 * @author Arnaud Blouin
 */
public enum AxesStyle {
	AXES {
		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_AXES_STYLE_AXES;
		}

		@Override
		public boolean supportsArrows() {
			return true;
		}

		@Override
		public @NotNull String getLabel(final @NotNull ResourceBundle bundle) {
			return bundle.getString("axe"); //NON-NLS
		}
	}, FRAME {
		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_AXES_STYLE_FRAME;
		}

		@Override
		public boolean supportsArrows() {
			return false;
		}

		@Override
		public @NotNull String getLabel(final @NotNull ResourceBundle bundle) {
			return bundle.getString("frame"); //NON-NLS
		}
	}, NONE {
		@Override
		public @NotNull String getPSTToken() {
			return PSTricksConstants.TOKEN_AXES_STYLE_NONE;
		}

		@Override
		public boolean supportsArrows() {
			return false;
		}

		@Override
		public @NotNull String getLabel(final @NotNull ResourceBundle bundle) {
			return "None"; //NON-NLS
		}
	};

	/**
	 * @param style The PST token or the name of the style (e.g. AXES.toString()) corresponding to the style to get.
	 * @return The corresponding style or AXES.
	 */
	public static AxesStyle getStyle(final String style) {
		return Arrays.stream(values()).filter(it -> it.toString().equals(style) || it.getPSTToken().equals(style)).findFirst().orElse(AXES);
	}

	/**
	 * @return True if the axe style supports arrows.
	 */
	public abstract boolean supportsArrows();

	/**
	 * @return The PST token corresponding to the axe style.
	 */
	public abstract @NotNull String getPSTToken();

	/**
	 * @return The internationalised label of the axe style.
	 */
	public abstract @NotNull String getLabel(final @NotNull ResourceBundle bundle);
}
