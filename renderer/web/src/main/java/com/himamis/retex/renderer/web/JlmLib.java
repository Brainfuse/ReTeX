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
package com.himamis.retex.renderer.web;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.himamis.retex.renderer.share.ColorUtil;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;

public class JlmLib {

	private StringBuilder initString;

	public JlmLib() {
		initString = new StringBuilder();
	}

	public void initWith(String string) {
		initString.append(string);
	}

	public JavaScriptObject drawLatex(final Context2d ctx, final String latex,
			final float size, final int type, final int x, final int y,
			final int topInset, final int leftInset, final int bottomInset,
			final int rightInset, final String fgColorString,
			final String bgColorString, final JavaScriptObject callback) {

		// init jlm with the given string
		if (initString.length() > 0) {
			new TeXFormula(initString.toString());
			initString.setLength(0);
		}
		// create icon and graphics objects
		TeXIcon icon = createIcon(latex, size, type, new Insets(topInset, leftInset, bottomInset, rightInset));
		Graphics2DW g2 = new Graphics2DW(ctx);

		// fill the background color
		if (bgColorString != null && !bgColorString.equals("")) {
			final Color bgColor = ColorUtil.decode(bgColorString);
			g2.setColor(bgColor);
			g2.fillRect(x, y, icon.getIconWidth(), icon.getIconHeight());
		}

		// set the callback
		g2.setDrawingFinishedCallback(new DrawingFinishedCallback() {
			@Override
			public void onDrawingFinished() {
				callJavascriptCallback(callback);
			}
		});

		// paint the icon
		final Color fgColor = ColorUtil.decode(fgColorString);
		icon.paintIcon(new HasForegroundColor() {
			@Override
			public Color getForegroundColor() {
				return fgColor;
			}
		}, g2, x, y);
		g2.maybeNotifyDrawingFinishedCallback();

		// return {width, height}
		return createReturnValue(icon);
	}

	private static native void callJavascriptCallback(JavaScriptObject cb) /*-{
		if (cb != null) {
			cb();
		}
	}-*/;

	public static TeXIcon createIcon(final String latex, final float size,
			final int type, Insets insets) {
		TeXFormula formula = new TeXFormula(latex);
		TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setType(type)
				.setSize(size).build();
		icon.setInsets(insets);
		return icon;
	}
	
	private static JavaScriptObject createReturnValue(TeXIcon icon) {
		JSONObject object = new JSONObject();
		object.put("width", new JSONNumber(icon.getIconWidth()));
		object.put("height", new JSONNumber(icon.getIconHeight()));
		object.put("baseline", new JSONNumber(icon.getBaseLine()));
		return object.getJavaScriptObject();
	}

}
