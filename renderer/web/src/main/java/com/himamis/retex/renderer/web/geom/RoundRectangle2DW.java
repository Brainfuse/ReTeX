/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.himamis.retex.renderer.web.geom;

import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;

public class RoundRectangle2DW implements RoundRectangle2D {

	private double x;
	private double y;
	private double width;
	private double height;
	private double arcW;
	private double arcH;

	public RoundRectangle2DW(double x, double y, double width, double height, double arcw, double arch) {
		setRoundRectangle(x, y, width, height, arcw, arch);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getArcW() {
		return arcW;
	}

	public void setArcW(double arcW) {
		this.arcW = arcW;
	}

	public double getArcH() {
		return arcH;
	}

	public void setArcH(double arcH) {
		this.arcH = arcH;
	}

	@Override
	public void setRoundRectangle(double x, double y, double width, double height, double arcW, double arcH) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.arcW = arcW;
		this.arcH = arcH;
	}

}
