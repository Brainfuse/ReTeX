/* MathController.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
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
 *
 */
package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.meta.MetaArray;
import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.MetaFunction;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * This class represents controller and interprets input.
 * <p/>
 * Four editor modes are present:
 * a) Input mode = core editing
 * b) Operator toolbar = operators
 * c) Symbol toolbar = Greek letters
 * d) Function toolbar = functions
 *
 * @author Bea Petrovicova
 */
public abstract class MathController extends MathContext {

    private final char functionOpenKey = '('; // probably universal
    private final char functionCloseKey = ')';
    private final char squareBracketOpenKey = '[';
    private final char delimiterKey = ';';
    private final char apostropheKey = '"';

    private MetaModel metaModel;

    public void setFormula(MathFormula formula) {
        super.setFormula(formula);
        metaModel = formula.getMetaModel();
    }

    /**
     * Insert array.
     */
    public void newArray(int size, char arrayOpenKey) {
        // add braces
        MetaArray meta = metaModel.getArray(arrayOpenKey);
        MathArray array = new MathArray(meta, size);
        currentField.addArgument(currentOffset, array);

        // add sequence
        MathSequence field = new MathSequence();
        array.setArgument(0, field);

        for (int i = 1; i < size; i++) {
            // add sequence
            array.setArgument(i, new MathSequence());
        }

        // set current
        currentField = field;
        currentOffset = 0;
    }

    /**
     * Insert matrix.
     */
    public void newMatrix(int columns, int rows) {
        // add braces
        MetaArray meta = formula.getMetaModel().getMatrix();
        MathArray matrix = new MathArray(meta, columns, rows);
        currentField.addArgument(currentOffset, matrix);

        // add sequence
        MathSequence field = new MathSequence();
        matrix.setArgument(0, field);

        for (int i = 1; i < matrix.size(); i++) {
            // add sequence
            matrix.setArgument(i, new MathSequence());
        }

        // set current
        currentField = field;
        currentOffset = 0;
    }

    /**
     * Insert braces (), [], {}, "".
     */
    public void newBraces(char ch) {
        String casName = readCharacters();
        if (ch == functionOpenKey && formula.getMetaModel().isGeneral(casName)) {
            delCharacters(casName.length());
            newFunction(casName);

        } else if (ch == functionOpenKey && formula.getMetaModel().isFunction(casName)) {
            delCharacters(casName.length());
            newFunction(casName);

        } else {
            // TODO brace type
            newArray(1, ch);
        }
    }

    /**
     * Insert function by name.
     *
     * @param name function
     */
    public void newFunction(String name) {
        newFunction(name, 0);
    }

    /**
     * Insert function by name.
     *
     * @param name function
     */
    public void newFunction(String name, int initial) {

        // add extra braces for sqrt, nthroot and fraction
        if ("^".equals(name) && currentOffset > 0) {
            if (currentField.getArgument(currentOffset - 1) instanceof MathFunction) {
                MathFunction function = (MathFunction) currentField.getArgument(currentOffset - 1);
                if ("sqrt".equals(function.getName()) ||
                        "nthroot".equals(function.getName()) ||
                        "frac".equals(function.getName())) {

                    currentField.delArgument(currentOffset - 1);
                    // add braces
                    MathArray array = new MathArray(formula.getMetaModel().getArray(MetaArray.REGULAR), 1);
                    currentField.addArgument(currentOffset - 1, array);
                    // add sequence
                    MathSequence field = new MathSequence();
                    array.setArgument(0, field);
                    field.addArgument(0, function);
                }
            }
        }

        // add function
        MathFunction function;
        if (formula.getMetaModel().isGeneral(name)) {
            MetaFunction meta = formula.getMetaModel().getGeneral(name);
            function = new MathFunction(meta);

        } else {
            MetaFunction meta = formula.getMetaModel().getFunction(name);
            function = new MathFunction(meta);
        }

        // add sequences
        for (int i = 0; i < function.size(); i++) {
            MathSequence field = new MathSequence();
            function.setArgument(i, field);
        }

        // pass characters for fraction and factorial only
        if ("frac".equals(name) || "factorial".equals(name)) {
            passArgument(function);
        }
        currentField.addArgument(currentOffset, function);

        if (function.hasChildren()) {
            // set current sequence
            firstField((MathContainer) function.getArgument(initial));
            currentOffset = currentField.size();
        } else {
            currentOffset++;
        }
    }

    public void newScript(String script) {
        int offset = currentOffset;
        while (offset > 0 && currentField.getArgument(offset - 1) instanceof MathFunction) {

            MathFunction function = (MathFunction) currentField.getArgument(offset - 1);
            if (script.equals(function.getName())) {
                return;
            }
            if (!"^".equals(function.getName()) && !"_".equals(function.getName())) {
                break;
            }
            offset--;
        }
        offset = currentOffset;
        while (offset < currentField.size() && currentField.getArgument(offset) instanceof MathFunction) {

            MathFunction function = (MathFunction) currentField.getArgument(offset);
            if (script.equals(function.getName())) {
                return;
            }
            if (!"^".equals(function.getName()) && !"_".equals(function.getName())) {
                break;
            }
            offset++;
        }
        if (currentOffset > 0 && currentField.getArgument(currentOffset - 1) instanceof MathFunction) {
            MathFunction function = (MathFunction) currentField.getArgument(currentOffset - 1);
            if ("^".equals(function.getName()) && "_".equals(script)) {
                currentOffset--;
            }
        }
        if (currentOffset < currentField.size() && currentField.getArgument(currentOffset) instanceof MathFunction) {
            MathFunction function = (MathFunction) currentField.getArgument(currentOffset);
            if ("_".equals(function.getName()) && "^".equals(script)) {
                currentOffset++;
            }
        }
        newFunction(script);
    }

    /**
     * Insert operator.
     */
    public void newOperator(char op) {
        MetaCharacter meta = formula.getMetaModel().getOperator("" + op);
        currentField.addArgument(currentOffset, new MathCharacter(meta));
        currentOffset++;
    }

    /**
     * Insert symbol.
     */
    public void newSymbol(char sy) {
        MetaCharacter meta = formula.getMetaModel().getSymbol("" + sy);
        currentField.addArgument(currentOffset, new MathCharacter(meta));
        currentOffset++;
    }

    /**
     * Insert character.
     */
    public void newCharacter(char ch) {
        MetaCharacter meta = formula.getMetaModel().getCharacter("" + ch);
        currentField.addArgument(currentOffset, new MathCharacter(meta));
        currentOffset++;
    }

    /**
     * Insert character.
     */
    public void newCharacter(MetaCharacter meta) {
        currentField.addArgument(currentOffset, new MathCharacter(meta));
        currentOffset++;
    }

    /**
     * Insert field.
     */
    public void endField(char ch) {

        // first array specific ...
        if (currentField.getParent() instanceof MathArray) {
            MathArray parent = (MathArray) currentField.getParent();

            // if ',' typed within 1DArray or Vector ... add new field
            if (ch == parent.getFieldKey() && (parent.is1DArray() || parent.isVector())) {

                int index = currentField.getParentIndex();
                MathSequence field = new MathSequence();
                parent.addArgument(index + 1, field);
                while (currentField.size() > currentOffset) {
                    MathComponent component = currentField.getArgument(currentOffset);
                    currentField.delArgument(currentOffset);
                    field.addArgument(field.size(), component);
                }
                currentField = field;
                currentOffset = 0;

                // if ',' typed at the end of intermediate field of 2DArray or Matrix ... move to next field
            } else if (ch == parent.getFieldKey() && currentOffset == currentField.size() &&
                    parent.size() > currentField.getParentIndex() + 1 &&
                    (currentField.getParentIndex() + 1) % parent.columns() != 0) {

                currentField = (MathSequence) parent.getArgument(currentField.getParentIndex() + 1);
                currentOffset = 0;

                // if ';' typed at the end of last field ... add new row
            } else if (ch == parent.getRowKey() && currentOffset == currentField.size() &&
                    parent.size() == currentField.getParentIndex() + 1) {

                parent.addRow();
                currentField = parent.getArgument(parent.size() - parent.columns());
                currentOffset = 0;

                // if ';' typed at the end of (not last) row ... move to next field
            } else if (ch == parent.getRowKey() && currentOffset == currentField.size() &&
                    (currentField.getParentIndex() + 1) % parent.columns() == 0) {

                currentField = (MathSequence) parent.getArgument(currentField.getParentIndex() + 1);
                currentOffset = 0;

                // if ']' '}' typed at the end of last field ... move out of array
            } else if ((ch == parent.getCloseKey() && parent.isArray()) ||
                    (ch == parent.getCloseKey() && parent.isMatrix()) &&
                            parent.size() == currentField.getParentIndex() + 1 &&
                            currentOffset == currentField.size()) {

                currentOffset = parent.getParentIndex() + 1;
                currentField = (MathSequence) parent.getParent();
            }

            // now functions, braces, apostrophes ...
        } else if (currentField.getParent() != null) {
            MathContainer parent = currentField.getParent();

            // if ',' typed at the end of intermediate field of function ... move to next field
            if (ch == ',' && currentOffset == currentField.size() &&
                    parent instanceof MathFunction &&
                    parent.size() > currentField.getParentIndex() + 1) {

                currentField = (MathSequence) parent.getArgument(currentField.getParentIndex() + 1);
                currentOffset = 0;

                // if ')' typed at the end of last field of function ... move after closing character
            } else if (ch == functionCloseKey && currentOffset == currentField.size() &&
                    parent instanceof MathFunction &&
                    parent.size() == currentField.getParentIndex() + 1) {

                currentOffset = parent.getParentIndex() + 1;
                currentField = (MathSequence) parent.getParent();

                // if ')' typed at the end of last field of braces ... move after closing character
            } else {
                if (ch == ',') {
                    newCharacter(ch);
                    //update();
                }
            }

            // topmost container last ...
        } else {
            // if ';' typed and at the top level ... insert delimiter char
            if (ch == delimiterKey || ch == ',') {
                newCharacter(ch);
                //update();
            }
        }
    }

    /**
     * Insert symbol.
     */
    public void escSymbol() {
        String name = readCharacters();
        while (name.length() > 0) {
            if (formula.getMetaModel().isSymbol(name)) {
                delCharacters(name.length());
                MetaCharacter meta = formula.getMetaModel().getSymbol(name);
                currentField.addArgument(currentOffset, new MathCharacter(meta));
                currentOffset++;
                break;

            } else if (formula.getMetaModel().isOperator(name)) {
                delCharacters(name.length());
                MetaCharacter meta = formula.getMetaModel().getOperator(name);
                currentField.addArgument(currentOffset, new MathCharacter(meta));
                currentOffset++;
                break;

            } else {
                name = name.substring(1, name.length());
            }
        }
    }

    public void bkspContainer() {

        // if parent is function (cursor is at the beginning of the field)
        if (currentField.getParent() instanceof MathFunction) {
            MathFunction parent = (MathFunction) currentField.getParent();

            // fraction has operator like behavior
            if ("frac".equals(parent.getName())) {

                // if second operand is empty sequence
                if (currentField.getParentIndex() == 1 && currentField.size() == 0) {
                    int size = parent.getArgument(0).size();
                    delContaner(parent, parent.getArgument(0));
                    // move after included characters
                    currentOffset += size;

                    // if first operand is empty sequence
                } else if (currentField.getParentIndex() == 1 && parent.getArgument(0).size() == 0) {
                    delContaner(parent, currentField);
                }

            } else if (formula.getMetaModel().isGeneral(parent.getName())) {
                if (currentField.getParentIndex() == parent.getInsertIndex()) {
                    delContaner(parent, currentField);
                }

                // not a fraction, and cursor is right after the sign
            } else {
                if (currentField.getParentIndex() == 0) {
                    delContaner(parent, currentField);
                }
            }

            // if parent are empty array
        } else if (currentField.getParent() instanceof MathArray &&
                currentField.getParent().size() == 1 &&
                currentField.size() == 0) {

            MathArray parent = (MathArray) currentField.getParent();
            delContaner(parent, parent.getArgument(0));

            // if parent is 1DArray or Vector and cursor is at the beginning of intermediate the field
        } else if (currentField.getParent() instanceof MathArray &&
                (((MathArray) currentField.getParent()).is1DArray() ||
                        ((MathArray) currentField.getParent()).isVector()) &&
                currentField.getParentIndex() > 0) {

            int index = currentField.getParentIndex();
            MathArray parent = (MathArray) currentField.getParent();
            MathSequence field = parent.getArgument(index - 1);
            int size = field.size();
            currentOffset = 0;
            while (currentField.size() > 0) {

                MathComponent component = currentField.getArgument(0);
                currentField.delArgument(0);
                field.addArgument(field.size(), component);
            }
            parent.delArgument(index);
            currentField = field;
            currentOffset = size;
        }

        // we stop here for now
    }

    public void delContainer() {

        // if parent is function (cursor is at the end of the field)
        if (currentField.getParent() instanceof MathFunction) {
            MathFunction parent = (MathFunction) currentField.getParent();

            // fraction has operator like behavior
            if ("frac".equals(parent.getName())) {

                // first operand is current, second operand is empty sequence
                if (currentField.getParentIndex() == 0 && parent.getArgument(1).size() == 0) {
                    int size = parent.getArgument(0).size();
                    delContaner(parent, currentField);
                    // move after included characters
                    currentOffset += size;

                    // first operand is current, and first operand is empty sequence
                } else if (currentField.getParentIndex() == 0 && (currentField).size() == 0) {
                    delContaner(parent, parent.getArgument(1));
                }
            }

            // if parent are empty braces
        } else if (currentField.getParent() instanceof MathArray &&
                currentField.getParent().size() == 1 &&
                currentField.size() == 0) {
            MathArray parent = (MathArray) currentField.getParent();
            int size = parent.getArgument(0).size();
            delContaner(parent, parent.getArgument(0));
            // move after included characters
            currentOffset += size;

            // if parent is 1DArray or Vector and cursor is at the end of the field
        } else if (currentField.getParent() instanceof MathArray &&
                (((MathArray) currentField.getParent()).is1DArray() ||
                        ((MathArray) currentField.getParent()).isVector()) &&
                currentField.getParentIndex() + 1 < currentField.getParent().size()) {

            int index = currentField.getParentIndex();
            MathArray parent = (MathArray) currentField.getParent();
            MathSequence field = parent.getArgument(index + 1);
            int size = currentField.size();
            currentOffset = 0;
            while (currentField.size() > 0) {

                MathComponent component = currentField.getArgument(0);
                currentField.delArgument(0);
                field.addArgument(field.size(), component);
            }
            parent.delArgument(index);
            currentField = field;
            currentOffset = size;
        }

        // we stop here for now
    }

    public void bkspCharacter() {
        if (currentOffset > 0) {
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
        } else {
            bkspContainer();
        }
    }

    public void delCharacter() {
        if (currentOffset < currentField.size()) {
            currentField.delArgument(currentOffset);
        } else {
            delContainer();
        }
    }

    private void delContaner(MathContainer container, MathSequence operand) {
        if (container.getParent() instanceof MathSequence) {
            // when parent is sequence
            MathSequence parent = (MathSequence) container.getParent();
            int offset = container.getParentIndex();
            // delete container
            parent.delArgument(container.getParentIndex());
            // add content of operand
            while (operand.size() > 0) {
                MathComponent element = operand.getArgument(operand.size() - 1);
                operand.delArgument(operand.size() - 1);
                parent.addArgument(offset, element);
            }
            currentField = parent;
            currentOffset = offset;
        }
    }

    private void passArgument(MathContainer container) {
        // get pass to argument
        MathSequence field = (MathSequence) container.getArgument(container.getInsertIndex());

        // pass scripts first
        while (currentOffset > 0 && currentField.isScript(currentOffset - 1)) {
            MathFunction character = (MathFunction) currentField.getArgument(currentOffset - 1);
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
            field.addArgument(0, character);
        }

        if (currentOffset > 0) {
            // if previous sequence argument are braces pass their content
            if (currentField.getArgument(currentOffset - 1) instanceof MathArray) {

                MathArray array = (MathArray) currentField.getArgument(currentOffset - 1);
                currentField.delArgument(currentOffset - 1);
                currentOffset--;
                if (field.size() == 0) {
                    // here we already have sequence, just set it
                    container.setArgument(container.getInsertIndex(), array.getArgument(0));
                } else {
                    field.addArgument(0, array);
                }

                // if previous sequence argument is, function pass it
            } else if (currentField.getArgument(currentOffset - 1) instanceof MathFunction) {

                MathFunction function = (MathFunction) currentField.getArgument(currentOffset - 1);
                currentField.delArgument(currentOffset - 1);
                currentOffset--;
                field.addArgument(0, function);

                // otherwise pass character sequence
            } else {
                passCharacters(container);
            }
        }
    }

    private void passCharacters(MathContainer container) {
        // get pass to argument
        MathSequence field = (MathSequence) container.getArgument(container.getInsertIndex());

        while (currentOffset > 0 && currentField.getArgument(currentOffset - 1) instanceof MathCharacter) {

            MathCharacter character = (MathCharacter) currentField.getArgument(currentOffset - 1);
            if (character.isOperator()) {
                break;
            }
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
            field.addArgument(0, character);
        }
    }

    private String readCharacters() {
        StringBuffer buffer = new StringBuffer();
        int offset = currentOffset;
        while (offset > 0 && currentField.getArgument(offset - 1) instanceof MathCharacter) {

            MathCharacter character = (MathCharacter) currentField.getArgument(offset - 1);
            if (character.isOperator() || character.isSymbol()) {
                break;
            }
            offset--;
            buffer.insert(0, character.getName());
        }
        return buffer.toString();
    }

    private void delCharacters(int length) {
        while (length > 0 && currentOffset > 0 && currentField.getArgument(currentOffset - 1) instanceof MathCharacter) {

            MathCharacter character = (MathCharacter) currentField.getArgument(currentOffset - 1);
            if (character.isOperator() || character.isSymbol()) {
                break;
            }
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
            length--;
        }
    }

    public boolean keyTyped(char ch) {
        boolean handled = false;
        if (ch == ',' || ch == ';' ||
                metaModel.isArrayCloseKey(ch) ||
                ch == functionCloseKey || ch == delimiterKey) {
            endField(ch);
            update();
            handled = true;
        } else if (metaModel.isFunctionOpenKey(ch)) {
            newBraces(ch);
            update();
            handled = true;
        } else if (ch == '!') {
            newFunction("factorial");
            update();
            handled = true;
        } else if (ch == '^') {
            newScript("^");
            update();
            handled = true;
        } else if (ch == '_') {
            newScript("_");
            update();
            handled = true;
        } else if (ch == '\\') {
            newFunction("frac", 1);
            update();
            handled = true;
        } else if (metaModel.isArrayOpenKey(ch)) {
            newArray(1, ch);
            update();
            handled = true;
        } /*else if (ch == matrixOpenKey) { TODO fix matrix
            newMatrix(1, 1);
            update();
            handled = true;
        }*/ else if (formula.getMetaModel().isOperator("" + ch)) {
            newOperator(ch);
            update();
            handled = true;
        } else if (formula.getMetaModel().isSymbol("" + ch)) {
            newSymbol(ch);
            update();
            handled = true;
        } else if (formula.getMetaModel().isCharacter("" + ch)) {
            newCharacter(ch);
            update();
            handled = true;
        }
        return handled;
    }

    public void update() {
        update(false);
    }

    abstract public void update(boolean focusEvent);

}
