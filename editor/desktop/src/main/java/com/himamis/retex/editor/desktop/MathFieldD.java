package com.himamis.retex.editor.desktop;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JLabel;


import com.himamis.retex.editor.desktop.event.ClickListenerAdapter;
import com.himamis.retex.editor.desktop.event.FocusListenerAdapter;
import com.himamis.retex.editor.desktop.event.KeyListenerAdapter;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.IconHelper;
import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldD extends JLabel implements MathField {
	
	private static final MetaModel metaModel;
	
	static {
		metaModel = new MetaModel("octave.xml");
	}

	private static final long serialVersionUID = 1L;
	
	private MathFieldInternal mathFieldInternal;

	public MathFieldD() {
		setBackground(Color.white);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		mathFieldInternal = new MathFieldInternal();
		mathFieldInternal.setMathField(this);
		mathFieldInternal.setFormula(MathFormula.newFormula(metaModel));
	}
	
	public MathFieldD(String latex) {
		
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {
		setIcon(IconHelper.createIcon(icon));
		setFocusTraversalKeysEnabled(true);
		setFocusable(true);
		setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		addFocusListener(new FocusListenerAdapter(focusListener));
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		addMouseListener(new ClickListenerAdapter(clickListener));
	}

	@Override
	public void setKeyListener(KeyListener keyListener) {
		addKeyListener(new KeyListenerAdapter(keyListener));
	}

	@Override
	public boolean hasParent() {
		return getParent() != null;
	}

	@Override
	public void requestViewFocus() {
		requestFocus();
	}
}
