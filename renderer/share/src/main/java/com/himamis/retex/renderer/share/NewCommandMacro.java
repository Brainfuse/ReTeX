/* NewCommandMacro.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 * 
 * Copyright (C) 2009 DENIZET Calixte
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

package com.himamis.retex.renderer.share;

import java.util.HashMap;

import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.regex.RegexUtil;

public class NewCommandMacro implements Macro {

	protected static HashMap<String, String> macrocode = new HashMap<String, String>();
	protected static HashMap<String, String> macroreplacement = new HashMap<String, String>();

	public NewCommandMacro() {
	}

	public static void addNewCommand(String name, String code, int nbargs) throws ParseException {
		// if (macrocode.get(name) != null)
		// throw new ParseException("Command " + name +
		// " already exists ! Use renewcommand instead ...");
		macrocode.put(name, code);
		MacroInfo.Commands.put(name, new MacroInfo(new NewCommandMacro(), nbargs));
	}

	public static void addNewCommand(String name, String code, int nbargs, String def) throws ParseException {
		if (macrocode.get(name) != null)
			throw new ParseException("Command " + name + " already exists ! Use renewcommand instead ...");
		macrocode.put(name, code);
		macroreplacement.put(name, def);
		MacroInfo.Commands.put(name, new MacroInfo(new NewCommandMacro(), nbargs, 1));
	}

	public static boolean isMacro(String name) {
		return macrocode.containsKey(name);
	}

	public static void addReNewCommand(String name, String code, int nbargs) {
		if (macrocode.get(name) == null)
			throw new ParseException("Command " + name + " is not defined ! Use newcommand instead ...");
		macrocode.put(name, code);
		MacroInfo.Commands.put(name, new MacroInfo(new NewCommandMacro(), nbargs));
	}

	public String executeMacro(TeXParser tp, String[] args) {
		String code = macrocode.get(args[0]);
		String rep;
		int nbargs = args.length - 11;
		int dec = 0;

		if (args[nbargs + 1] != null) {
			dec = 1;
			rep = RegexUtil.quoteReplacement(args[nbargs + 1]);
			code = code.replaceAll("#1", rep);
		} else if (macroreplacement.get(args[0]) != null) {
			dec = 1;
			rep = RegexUtil.quoteReplacement(macroreplacement.get(args[0]));
			code = code.replaceAll("#1", rep);
		}

		for (int i = 1; i <= nbargs; i++) {
			rep = RegexUtil.quoteReplacement(args[i]);
			code = code.replaceAll("#" + (i + dec), rep);
		}

		return code;
	}
}