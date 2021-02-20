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
package net.sf.latexdraw.model.impl;

import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.api.shape.Color;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of a colour.
 * @author Arnaud Blouin
 */
class ColorImpl implements Color {
	private double r;
	private double g;
	private double b;
	private double o;

	ColorImpl(final double red, final double green, final double blue, final double opacity) {
		super();
		setR(red);
		setG(green);
		setB(blue);
		setO(opacity);
	}

	@Override
	public @NotNull javafx.scene.paint.Color toJFX() {
		return new javafx.scene.paint.Color(r, g, b, o);
	}

	@Override
	public @NotNull java.awt.Color toAWT() {
		return new java.awt.Color((float) r, (float) g, (float) b, (float) o);
	}

	@Override
	public double getR() {
		return r;
	}

	@Override
	public double getG() {
		return g;
	}

	@Override
	public double getB() {
		return b;
	}

	@Override
	public double getO() {
		return o;
	}

	private void checkChannel(final double val) {
		if(val < 0d || val > 1d || !MathUtils.INST.isValidCoord(val)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void setR(final double red) {
		checkChannel(red);
		r = red;
	}

	@Override
	public void setG(final double green) {
		checkChannel(green);
		g = green;
	}

	@Override
	public void setB(final double blue) {
		checkChannel(blue);
		b = blue;
	}

	@Override
	public void setO(final double opacity) {
		checkChannel(opacity);
		o = opacity;
	}

	@Override
	public @NotNull Color newColorWithOpacity(final double opacity) {
		return new ColorImpl(getR(), getG(), getB(), opacity);
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof ColorImpl)) {
			return false;
		}

		final ColorImpl color = (ColorImpl) obj;

		return MathUtils.INST.equalsDouble(color.getR(), getR(), 0.01) &&
			MathUtils.INST.equalsDouble(color.getG(), getG(), 0.01) &&
			MathUtils.INST.equalsDouble(color.getB(), getB(), 0.01) &&
			MathUtils.INST.equalsDouble(color.getO(), getO(), 0.01);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(getR());
		result = (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(getG());
		result = 31 * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(getB());
		result = 31 * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(getO());
		result = 31 * result + (int) (temp ^ temp >>> 32);
		return result;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d,%d,%d]", (int) Math.round(r * 255d), (int) Math.round(g * 255d), //NON-NLS
			(int) Math.round(b * 255d), (int) Math.round(o * 255d));
	}
}
