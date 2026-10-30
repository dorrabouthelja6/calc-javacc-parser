package parser;

/* Author: Michaël Périn, Polytech Grenoble
 *  
 * LICENCE GPL
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. Dear USER, see the GNU General Public License for more details.
*/

import java.io.PrintStream;

public class Tracing {

  static PrintStream ps = System.out;

  // FONT

  static private final String DEFAULT = "0";

  static final String GRAY = "90";
  static final String RED = "91";
  static final String GREEN = "92";
  static final String YELLOW = "93";
  static final String BLUE = "94";
  static final String MAGENTA = "95";
  static final String CYAN = "96";
  static final String WHITE = "97";

  static final String RGB = "38;2;";
  static final String ORANGE = RGB + "255;165;0";
  static final String PINK = RGB + "170;0;170";

  static final String BOLD = ";1";
  static final String ITALIC = ";3";
  static final String FADED = ";2";
  static final String UNDERLINED = ";4";
  static final String INVERSE = ";7";
  static final String STRIKEOUT = ";9";

  static final String TOKEN = MAGENTA;
  static final String BARS = GRAY;
  static final String CALLER = CYAN;
  static final String RESULT = YELLOW;

  // == METHODS

  static private String current_font = DEFAULT;

  static private void setCurrent_font(String font) {
    current_font = font;
    ps.print(String.format("%c[%sm", (char) 033, current_font));
  }

  static private void resetFont() {
    if (!current_font.equals(DEFAULT))
      ps.print(String.format("%c[%sm", (char) 033, DEFAULT));
  }

  static private void updateFont(String font) {
    if (!current_font.equals(font)) {
      resetFont();
      setCurrent_font(font);
    }
  }

  // == INDENTATION

  static private void newline(int indentation) {
    ps.print("\n" + bars(indentation));
  }

  static private String bars(int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++)
      sb.append("| ");
    return sb.toString();
  }

  // == REQUIRED BY Parser

  static private boolean enable = false;

  static public void enable(boolean b) {
    enable = b;
  }

  static public void token(String category, String content) {
    if (!enable)
      return;
    updateFont(TOKEN);
    ps.print(" <" + category + ":");
    updateFont(GREEN);
    ps.print("\"" + content + "\"");
    updateFont(TOKEN);
    ps.print(">");
    resetFont();
  }

  static public void call(int depth, String caller) {
    if (!enable)
      return;
    updateFont(BARS);
    newline(depth);
    updateFont(CALLER + BOLD);
    ps.print(caller);
    resetFont();
  }

  static public void returns(int depth, String caller, String result) {
    if (!enable)
      return;
    updateFont(BARS);
    newline(depth);
    ps.print("↳");
    updateFont(CALLER + FADED);
    ps.print(caller + ": ");
    updateFont(RESULT);
    ps.print(result);
    resetFont();
  }

  // TEST

  static public void main(String _args[]) {
    enable(true);
    token("DIGIT", "1");
    token("DIGIT", "2");
    token("NAT", "12");
    call(1, "NAT");
    returns(1, "NAT", "12");
  }

}
