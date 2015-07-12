package org.scilab.forge.jlatexmath;

import org.scilab.forge.jlatexmath.font.opentype.Opentype;
import org.scilab.forge.jlatexmath.platform.FactoryProvider;

import com.google.gwt.core.client.EntryPoint;

public class CreateLibrary implements EntryPoint {

	private JlmLib library;
	private Opentype opentype;

	@Override
	public void onModuleLoad() {
		FactoryProvider.INSTANCE = new FactoryProviderGWT();
		library = new JlmLib();
		opentype = Opentype.INSTANCE;
		exportLibrary(library, opentype);
	}

	private static native void exportLibrary(JlmLib library, Opentype opentype) /*-{
		$wnd.jlmlib = {};

		$wnd.jlmlib.initWith = $entry(function(str) {
			library.@org.scilab.forge.jlatexmath.JlmLib::initWith(Ljava/lang/String;)(str);
		});

		$wnd.jlmlib.SERIF = @org.scilab.forge.jlatexmath.TeXFormula::SERIF;
		$wnd.jlmlib.SANSSERIF = @org.scilab.forge.jlatexmath.TeXFormula::SANSSERIF;
		$wnd.jlmlib.BOLD = @org.scilab.forge.jlatexmath.TeXFormula::BOLD;
		$wnd.jlmlib.ITALIC = @org.scilab.forge.jlatexmath.TeXFormula::ITALIC;
		$wnd.jlmlib.ROMAN = @org.scilab.forge.jlatexmath.TeXFormula::ROMAN;
		$wnd.jlmlib.TYPEWRITER = @org.scilab.forge.jlatexmath.TeXFormula::TYPEWRITER;

		$wnd.jlmlib.drawLatex = $entry(function(opts) {
			//ctx, latex, size, style, x, y, fgColor, bgColor, cb
			if (!opts.context) {
				throw ("drawLatex(opts): opts.context must not be null");
			}
			if (!opts.latex) {
				throw ("drawLatex(opts): opts.latex must not be null");
			}
			var ctx = opts.context, 
			latex = opts.latex, 
			size = opts.size || 12, 
			type = opts.type || 0, 
			x = opts.x || 0, 
			y = opts.y || 0, 
			insets = opts.insets
					|| {
						top : 0,
						bottom : 0,
						left : 0,
						right : 0
					}, 
			topInset = insets.top || 0, 
			bottomInset = insets.bottom || 0, 
			leftInset = insets.left || 0, 
			rightInset = insets.right || 0, 
			fgColor = opts.foregroundColor || "#000000", 
			bgColor = opts.backgroundColor, // undefined === invisible
			cb = opts.callback;

			return library.@org.scilab.forge.jlatexmath.JlmLib::drawLatex(Lcom/google/gwt/canvas/dom/client/Context2d;Ljava/lang/String;FIIIIIIILjava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(ctx, latex, size, type, x, y, topInset, leftInset, bottomInset, rightInset, fgColor, bgColor, cb);
		});
		
		$wnd.jlmlib.setFontBaseUrl = $entry(function(url) {
			opentype.@org.scilab.forge.jlatexmath.font.opentype.Opentype::setFontBaseUrl(Ljava/lang/String;)(url);
		});
	}-*/;

}